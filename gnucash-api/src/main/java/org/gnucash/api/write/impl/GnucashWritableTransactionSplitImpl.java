package org.gnucash.api.write.impl;

import java.text.ParseException;

import org.gnucash.api.Const;
import org.gnucash.api.basetypes.complex.GCshCmdtyCurrID;
import org.gnucash.api.basetypes.complex.InvalidCmdtyCurrIDException;
import org.gnucash.api.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.numbers.FixedPointNumber;
import org.gnucash.api.read.GnucashAccount;
import org.gnucash.api.read.GnucashTransactionSplit;
import org.gnucash.api.read.IllegalTransactionSplitActionException;
import org.gnucash.api.read.impl.GnucashTransactionSplitImpl;
import org.gnucash.api.write.GnucashWritableFile;
import org.gnucash.api.write.GnucashWritableObject;
import org.gnucash.api.write.GnucashWritableTransaction;
import org.gnucash.api.write.GnucashWritableTransactionSplit;
import org.gnucash.api.generated.GncTransaction;
import org.gnucash.api.generated.ObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Transaction-Split that can be newly created or removed from it's transaction.
 */
public class GnucashWritableTransactionSplitImpl extends GnucashTransactionSplitImpl 
                                                 implements GnucashWritableTransactionSplit 
{
    private static final Logger LOGGER = LoggerFactory.getLogger(GnucashWritableTransactionSplitImpl.class);

    	/**
	 * Our helper to implement the GnucashWritableObject-interface.
	 */
	private final GnucashWritableObjectImpl helper = new GnucashWritableObjectImpl(this);

	// -----------------------------------------------------------
	
	/**
	 * @param jwsdpPeer   the JWSDP-object we are facading.
	 * @param transaction the transaction we belong to
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws ClassNotFoundException 
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 */
	@SuppressWarnings("exports")
	public GnucashWritableTransactionSplitImpl(
		final GncTransaction.TrnSplits.TrnSplit jwsdpPeer, 
		final GnucashWritableTransaction transaction,
		final boolean addSpltToAcct,
		final boolean addSpltToInvc) throws NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
		super(jwsdpPeer, transaction, 
                      addSpltToAcct, addSpltToInvc);
	}

	/**
	 * create a new split and and add it to the given transaction.
	 *
	 * @param trx transaction the transaction we will belong to
	 * @param acct     the account we take money (or other things) from or give it to
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws ClassNotFoundException 
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 */
	public GnucashWritableTransactionSplitImpl(
		final GnucashWritableTransactionImpl trx, 
		final GnucashAccount acct) throws NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
		super(createTransactionSplit(trx, acct, 
				             trx.getWritableFile().createGUID()), 
		      trx, 
		      true, true);

		// this is a workaround.
		// if super does account.addSplit(this) it adds an instance on GnucashTransactionSplitImpl that is "!=
		// (GnucashWritableTransactionSplitImpl)this";
		// thus we would get warnings about duplicate split-ids and can no longer compare splits by instance.
		//        if(account!=null)
		//            ((GnucashAccountImpl)account).replaceTransactionSplit(account.getTransactionSplitByID(getId()),
		// GnucashWritableTransactionSplitImpl.this);

		trx.addSplit(this);
	}

	public GnucashWritableTransactionSplitImpl(GnucashTransactionSplit split) throws NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	    super(split.getJwsdpPeer(), split.getTransaction(), 
		  true, true);
	}

	// -----------------------------------------------------------
	
	/**
	 * @see GnucashWritableObject#setUserDefinedAttribute(java.lang.String, java.lang.String)
	 */
	public void setUserDefinedAttribute(final String name, final String value) {
		helper.setUserDefinedAttribute(name, value);
	}

	/**
	 * @see GnucashTransactionSplitImpl#getTransaction()
	 */
	@Override
	public GnucashWritableTransaction getTransaction() {
		return (GnucashWritableTransaction) super.getTransaction();
	}

	/**
	 * Creates a new Transaction and add's it to the given gnucash-file
	 * Don't modify the ID of the new transaction!
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws ClassNotFoundException 
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 */
	protected static GncTransaction.TrnSplits.TrnSplit createTransactionSplit(
		final GnucashWritableTransactionImpl transaction,
		final GnucashAccount account,
		final String pSplitID) throws NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {

		if (transaction == null) {
			throw new IllegalArgumentException("null transaction given");
		}

		if (account == null) {
			throw new IllegalArgumentException("null account given");
		}

		if (pSplitID == null || pSplitID.trim().length() == 0) {
			throw new IllegalArgumentException("null or empty pSplitID given");
		}

		// this is needed because transaction.addSplit() later
		// must have an already build List of splits.
		// if not it will create the list from the JAXB-Data
		// thus 2 instances of this GnucashWritableTransactionSplitImpl
		// will exist. One created in getSplits() from this JAXB-Data
		// the other is this object.
		transaction.getSplits();

		GnucashWritableFileImpl gnucashFileImpl = transaction.getWritableFile();
		ObjectFactory factory = gnucashFileImpl.getObjectFactory();

		GncTransaction.TrnSplits.TrnSplit split = gnucashFileImpl.createGncTransactionTypeTrnSplitsTypeTrnSplitType();
		{
			GncTransaction.TrnSplits.TrnSplit.SplitId id = factory.createGncTransactionTrnSplitsTrnSplitSplitId();
			id.setType(Const.XML_DATA_TYPE_GUID);
			id.setValue(pSplitID);
			split.setSplitId(id);
		}

		split.setSplitReconciledState(GnucashTransactionSplit.ReconStatus.NREC.getCode());

		split.setSplitQuantity("0/100");
		split.setSplitValue("0/100");
		{
			GncTransaction.TrnSplits.TrnSplit.SplitAccount splitaccount = factory.createGncTransactionTrnSplitsTrnSplitSplitAccount();
			splitaccount.setType(Const.XML_DATA_TYPE_GUID);
			splitaccount.setValue(account.getId().toString());
			split.setSplitAccount(splitaccount);
		}
		return split;
	}

	/**
	 * remove this split from it's transaction.
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws ClassNotFoundException 
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 */
	public void remove() throws NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
		getTransaction().remove(this);
	}

	/**
	 * @see GnucashWritableTransactionSplit#setAccount(GnucashAccount)
	 */
	public void setAccountID(final GCshID accountId) {
		setAccount(getTransaction().getGnucashFile().getAccountByID(accountId));
	}

	/**
	 * @see GnucashWritableTransactionSplit#setAccount(GnucashAccount)
	 */
	public void setAccount(final GnucashAccount account) {
		if (account == null) {
			throw new NullPointerException("null account given");
		}
		String old = (getJwsdpPeer().getSplitAccount() == null ? null
				:
						getJwsdpPeer().getSplitAccount().getValue());
		getJwsdpPeer().getSplitAccount().setType(Const.XML_DATA_TYPE_GUID);
		getJwsdpPeer().getSplitAccount().setValue(account.getId().toString());
		((GnucashWritableFile) getGnucashFile()).setModified(true);

		if (old == null || !old.equals(account.getId())) {
			if (getPropertyChangeSupport() != null) {
				getPropertyChangeSupport().firePropertyChange("accountID", old, account.getId());
			}
		}

	}

	/**
	 * @throws InvalidCmdtyCurrTypeException 
	 * @see GnucashWritableTransactionSplit#setQuantity(FixedPointNumber)
	 */
	public void setQuantity(final String n) throws InvalidCmdtyCurrTypeException {
		try {
			this.setQuantity(new FixedPointNumber(n.toLowerCase().replaceAll("&euro;", "").replaceAll("&pound;", "")));
		}
		catch (NumberFormatException e) {
			try {
				Number parsed = this.getQuantityCurrencyFormat().parse(n);
				this.setQuantity(new FixedPointNumber(parsed.toString()));
			}
			catch (NumberFormatException e1) {
				throw e;
			}
			catch (ParseException e1) {
				throw e;
			}
		}
	}

	/**
	 * @return true if the currency of transaction and account match
	 * @throws InvalidCmdtyCurrTypeException 
	 */
	private boolean isCurrencyMatching() throws InvalidCmdtyCurrTypeException {
		GnucashAccount acct = getAccount();
		if (acct == null) {
			return false;
		}
		GnucashWritableTransaction transaction = getTransaction();
		if (transaction == null) {
			return false;
		}
		GCshCmdtyCurrID acctCmdtyCurrID = acct.getCmdtyCurrID();
		if (acctCmdtyCurrID == null) {
			return false;
		}
	
		// Important: Don't forget to cast the IDs to their most basic type
		return ((GCshCmdtyCurrID) acctCmdtyCurrID).equals((GCshCmdtyCurrID) transaction.getCmdtyCurrID());
	}

	/**
	 * @throws InvalidCmdtyCurrTypeException 
	 * @throws NumberFormatException 
	 * @see GnucashWritableTransactionSplit#setQuantity(FixedPointNumber)
	 */
	public void setQuantity(final FixedPointNumber n) throws NumberFormatException, InvalidCmdtyCurrTypeException {
		if (n == null) {
			throw new NullPointerException("null quantity given");
		}

		String old = getJwsdpPeer().getSplitQuantity();
		getJwsdpPeer().setSplitQuantity(n.toGnucashString());
		((GnucashWritableFile) getGnucashFile()).setModified(true);
		if (isCurrencyMatching()) {
			String oldvalue = getJwsdpPeer().getSplitValue();
			getJwsdpPeer().setSplitValue(n.toGnucashString());
			if (old == null || !old.equals(n.toGnucashString())) {
				if (getPropertyChangeSupport() != null) {
					getPropertyChangeSupport().firePropertyChange("value", new FixedPointNumber(oldvalue), n);
				}
			}
		}

		if (old == null || !old.equals(n.toGnucashString())) {
			if (getPropertyChangeSupport() != null) {
				getPropertyChangeSupport().firePropertyChange("quantity", new FixedPointNumber(old), n);
			}
		}
	}

	/**
	 * @throws InvalidCmdtyCurrTypeException 
	 * @see GnucashWritableTransactionSplit#setValue(FixedPointNumber)
	 */
	public void setValue(final String n) throws InvalidCmdtyCurrTypeException {
		try {
			this.setValue(new FixedPointNumber(n.toLowerCase().replaceAll("&euro;", "").replaceAll("&pound;", "")));
		}
		catch (NumberFormatException e) {
			try {
				Number parsed = this.getValueCurrencyFormat().parse(n);
				this.setValue(new FixedPointNumber(parsed.toString()));
			} catch (NumberFormatException e1) {
				throw e;
			} catch (ParseException e1) {
				throw e;
			} catch (InvalidCmdtyCurrIDException e1) {
				throw e;
			}
		}
	}

	/**
	 * @throws InvalidCmdtyCurrTypeException 
	 * @throws NumberFormatException 
	 * @see GnucashWritableTransactionSplit#setValue(FixedPointNumber)
	 */
	public void setValue(final FixedPointNumber n) throws NumberFormatException, InvalidCmdtyCurrTypeException {
		if (n == null) {
			throw new NullPointerException("null value given");
		}
		String old = getJwsdpPeer().getSplitValue();
		getJwsdpPeer().setSplitValue(n.toGnucashString());
		((GnucashWritableFile) getGnucashFile()).setModified(true);
		if (isCurrencyMatching()) {
			String oldquantity = getJwsdpPeer().getSplitQuantity();
			getJwsdpPeer().setSplitQuantity(n.toGnucashString());
			if (old == null || !old.equals(n.toGnucashString())) {
				if (getPropertyChangeSupport() != null) {
					getPropertyChangeSupport().firePropertyChange("quantity", new FixedPointNumber(oldquantity), n);
				}
			}
		}

		if (old == null || !old.equals(n.toGnucashString())) {
			if (getPropertyChangeSupport() != null) {
				getPropertyChangeSupport().firePropertyChange("value", new FixedPointNumber(old), n);
			}
		}
	}

	/**
	 * Set the description-text.
	 *
	 * @param desc the new description
	 */
	public void setDescription(final String desc) {
		if (desc == null) {
			throw new IllegalArgumentException("null description given! Please use the empty string instead of null for an empty description");
		}

		String old = getJwsdpPeer().getSplitMemo();
		getJwsdpPeer().setSplitMemo(desc);
		((GnucashWritableFile) getGnucashFile()).setModified(true);

		if (old == null || !old.equals(desc)) {
			if (getPropertyChangeSupport() != null) {
				getPropertyChangeSupport().firePropertyChange("description", old, desc);
			}
		}
	}

	/**
	 * Set the type of association this split has with
	 * an invoice's lot.
	 *
	 * @param action null, or one of the defined ACTION_xyz values
	 * @throws IllegalTransactionSplitActionException 
	 */
	public void setAction(final String action) throws IllegalTransactionSplitActionException {
//		if ( action != null &&
//             ! action.equals(ACTION_PAYMENT) &&
//             ! action.equals(ACTION_INVOICE) &&
//             ! action.equals(ACTION_BILL) && 
//             ! action.equals(ACTION_BUY) && 
//             ! action.equals(ACTION_SELL) ) {
//                throw new IllegalSplitActionException();
//		}

		String old = getJwsdpPeer().getSplitAction();
		getJwsdpPeer().setSplitAction(action);
		((GnucashWritableFile) getGnucashFile()).setModified(true);

		if (old == null || !old.equals(action)) {
			if (getPropertyChangeSupport() != null) {
				getPropertyChangeSupport().firePropertyChange("splitAction", old, action);
			}
		}
	}

	public void setLotID(final String lotID) {

		GnucashWritableTransactionImpl trx = (GnucashWritableTransactionImpl) getTransaction();
		GnucashWritableFileImpl writingFile = trx.getWritableFile();
		ObjectFactory factory = writingFile.getObjectFactory();

		if (getJwsdpPeer().getSplitLot() == null) {
			GncTransaction.TrnSplits.TrnSplit.SplitLot lot = factory.createGncTransactionTrnSplitsTrnSplitSplitLot();
			getJwsdpPeer().setSplitLot(lot);
		}
		getJwsdpPeer().getSplitLot().setValue(lotID);
		getJwsdpPeer().getSplitLot().setType(Const.XML_DATA_TYPE_GUID);

		// if we have a lot, and if we are a paying transaction, then check the slots
		// ::TODO ::CHECK
		// 09.10.2023: This code, in the current setting, generates wrong
		// output (a closing split slot tag without an opening one, and 
                // we don't (always?) need a split slot anyway.
//		SlotsType slots = getJwsdpPeer().getSplitSlots();
//		if (slots == null) {
//			slots = factory.createSlotsType();
//			getJwsdpPeer().setSplitSlots(slots);
//		}
//		if (slots.getSlot() == null) {
//			Slot slot = factory.createSlot();
//			slot.setSlotKey("trans-txn-type");
//			SlotValue value = factory.createSlotValue();
//			value.setType("string");
//			value.getContent().add(GnucashTransaction.TYPE_PAYMENT);
//			slot.setSlotValue(value);
//			slots.getSlot().add(slot);
//		}

	}

	// --------------------- support for propertyChangeListeners ---------------

	/**
	 * @throws InvalidCmdtyCurrTypeException 
	 * @see GnucashWritableTransactionSplit#setQuantityFormattedForHTML(java.lang.String)
	 */
	public void setQuantityFormattedForHTML(final String n) throws InvalidCmdtyCurrTypeException {
		this.setQuantity(n);
	}

	/**
	 * @throws InvalidCmdtyCurrTypeException 
	 * @see GnucashWritableTransactionSplit#setValueFormattedForHTML(java.lang.String)
	 */
	public void setValueFormattedForHTML(final String n) throws InvalidCmdtyCurrTypeException {
		this.setValue(n);
	}

	/**
	 * ${@inheritDoc}.
	 */
	public GnucashWritableFile getWritableGnucashFile() {
		return (GnucashWritableFile) getGnucashFile();
	}
}
