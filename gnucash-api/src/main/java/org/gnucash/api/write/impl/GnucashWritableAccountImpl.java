package org.gnucash.api.write.impl;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;

import org.gnucash.api.Const;
import org.gnucash.api.basetypes.complex.GCshCmdtyCurrID;
import org.gnucash.api.basetypes.complex.GCshCmdtyCurrNameSpace;
import org.gnucash.api.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.generated.GncAccount;
import org.gnucash.api.generated.ObjectFactory;
import org.gnucash.api.generated.Slot;
import org.gnucash.api.generated.SlotValue;
import org.gnucash.api.generated.SlotsType;
import org.gnucash.api.numbers.FixedPointNumber;
import org.gnucash.api.read.GnucashAccount;
import org.gnucash.api.read.GnucashFile;
import org.gnucash.api.read.GnucashTransactionSplit;
import org.gnucash.api.read.UnknownAccountTypeException;
import org.gnucash.api.read.impl.GnucashAccountImpl;
import org.gnucash.api.read.impl.GnucashFileImpl;
import org.gnucash.api.write.GnucashWritableAccount;
import org.gnucash.api.write.GnucashWritableFile;
import org.gnucash.api.write.GnucashWritableObject;
import org.gnucash.api.write.GnucashWritableTransactionSplit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extension of GnucashAccountImpl to allow read-write access instead of
 * read-only access.
 */
public class GnucashWritableAccountImpl extends GnucashAccountImpl 
                                        implements GnucashWritableAccount 
{
    /**
     * Our logger for debug- and error-ourput.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(GnucashWritableAccountImpl.class);

    // ---------------------------------------------------------------

    /**
     * Our helper to implement the GnucashWritableObject-interface.
     */
    private GnucashWritableObjectImpl helper;
    
    /**
     * Used by ${@link #getBalance()} to cache the result.
     */
    private FixedPointNumber myBalanceCached = null;

    /**
     * Used by ${@link #getBalance()} to cache the result.
     */
    private PropertyChangeListener myBalanceCachedInvalidtor = null;
    
    // ---------------------------------------------------------------

    /**
     * @param jwsdpPeer 
     * @param file 
     * @see GnucashAccountImpl#GnucashAccountImpl(GncAccount, GnucashFile)
     */
    @SuppressWarnings("exports")
    public GnucashWritableAccountImpl(final GncAccount jwsdpPeer, final GnucashFileImpl file) {
	super(jwsdpPeer, file);
    }

    /**
     * @param file 
     * @see GnucashAccountImpl#GnucashAccountImpl(GncAccount, GnucashFile) )
     */
    public GnucashWritableAccountImpl(final GnucashWritableFileImpl file) {
	super(createAccount_int(file, GCshID.getNew()), file);
    }

    public GnucashWritableAccountImpl(final GnucashAccount acct, final boolean addSplits)
	    throws UnknownAccountTypeException {
	super(acct.getJwsdpPeer(), acct.getGnucashFile());

	if (addSplits) {
	    // acct.getGnucashFile()).getTransactionSplitsRaw().size());
	    // for ( GnucashTransactionSplit splt :
	    // acct.getGnucashFile().getTransactionSplits() ) {
	    for (GnucashTransactionSplit splt : ((GnucashFileImpl) acct.getGnucashFile())
		    .getTransactionSplits_readAfresh()) {
		if (acct.getType() != Type.ROOT && splt.getAccountID().equals(acct.getID())) {
		    super.addTransactionSplit(splt);
		    // NO:
//			    addTransactionSplit(new GnucashTransactionSplitImpl(splt.getJwsdpPeer(), splt.getTransaction(), 
//	                            false, false));
		}
	    }
	}
    }

    // ---------------------------------------------------------------

    /**
     * @param file
     * @return
     */
    private static GncAccount createAccount_int(final GnucashWritableFileImpl file, final GCshID acctID) {
	if (!acctID.isSet()) {
	    throw new IllegalArgumentException("GUID not set!");
	}

	ObjectFactory factory = file.getObjectFactory();

	GncAccount jwsdpAcct = factory.createGncAccount();
	// left unset account.setActCode();
	jwsdpAcct.setActCommodityScu(100); // x,yz
	jwsdpAcct.setActDescription("no description yet");
	// left unset account.setActLots();
	jwsdpAcct.setActName("UNNAMED");
	// left unset account.setActNonStandardScu();
	// left unset account.setActParent())
	jwsdpAcct.setActType(GnucashAccount.Type.BANK.toString());

	jwsdpAcct.setVersion(Const.XML_FORMAT_VERSION);

	{
	    GncAccount.ActCommodity currency = factory.createGncAccountActCommodity();
	    currency.setCmdtyId(file.getDefaultCurrencyID());
	    currency.setCmdtySpace(GCshCmdtyCurrNameSpace.CURRENCY);
	    jwsdpAcct.setActCommodity(currency);
	}

	{
	    GncAccount.ActId guid = factory.createGncAccountActId();
	    guid.setType(Const.XML_DATA_TYPE_GUID);
	    guid.setValue(acctID.toString());
	    jwsdpAcct.setActId(guid);
	}

	{
	    SlotsType slots = factory.createSlotsType();
	    jwsdpAcct.setActSlots(slots);
	}

	{
	    Slot slot = factory.createSlot();
	    slot.setSlotKey(Const.SLOT_KEY_ACCT_PLACEHOLDER);
	    SlotValue slottype = factory.createSlotValue();
	    slottype.setType(Const.XML_DATA_TYPE_STRING);
	    slottype.getContent().add("false");
	    slot.setSlotValue(slottype);
	    jwsdpAcct.getActSlots().getSlot().add(slot);
	}

	{
	    Slot slot = factory.createSlot();
	    slot.setSlotKey(Const.SLOT_KEY_ACCT_NOTES);
	    SlotValue slottype = factory.createSlotValue();
	    slottype.setType(Const.XML_DATA_TYPE_STRING);
	    slottype.getContent().add("");
	    slot.setSlotValue(slottype);
	    jwsdpAcct.getActSlots().getSlot().add(slot);
	}

	file.getRootElement().getGncBook().getBookElements().add(jwsdpAcct);
	file.setModified(true);

	LOGGER.debug("createAccount_int: Created new account (core): " + jwsdpAcct.getActId().getValue());

	return jwsdpAcct;
    }

    /**
     * Remove this account from the sytem.<br/>
     * Throws IllegalStateException if this account has splits or childres.
     */
    public void remove() {
	if ( getTransactionSplits().size() > 0 ) {
	    throw new IllegalStateException("cannot remove account while it contains transaction-splits!");
	}
	
	if ( this.getChildren().size() > 0 ) {
	    throw new IllegalStateException("cannot remove account while it contains child-accounts!");
	}

	getWritableGnucashFile().getRootElement().getGncBook().getBookElements().remove(getJwsdpPeer());
	getWritableGnucashFile().removeAccount(this);
    }

    /**
     * The gnucash-file is the top-level class to contain everything.
     *
     * @return the file we are associated with
     */
    public GnucashWritableFile getWritableGnucashFile() {
	return (GnucashWritableFileImpl) getGnucashFile();
    }
    
    // ---------------------------------------------------------------

    /**
     * @see GnucashAccount#addTransactionSplit(GnucashTransactionSplit)
     */
    @Override
    public void addTransactionSplit(final GnucashTransactionSplit split) {
	super.addTransactionSplit(split);

	setIsModified();
	// <<insert code to react further to this change here
	PropertyChangeSupport propertyChangeFirer = getPropertyChangeSupport();
	if (propertyChangeFirer != null) {
	    propertyChangeFirer.firePropertyChange("transactionSplits", null, getTransactionSplits());
	}
    }

    /**
     * @param impl the split to remove
     */
    protected void removeTransactionSplit(final GnucashWritableTransactionSplit impl) {
	List<GnucashTransactionSplit> transactionSplits = getTransactionSplits();
	transactionSplits.remove(impl);

	setIsModified();
	// <<insert code to react further to this change here
	PropertyChangeSupport propertyChangeFirer = getPropertyChangeSupport();
	if (propertyChangeFirer != null) {
	    propertyChangeFirer.firePropertyChange("transactionSplits", null, transactionSplits);
	}
    }

    // ---------------------------------------------------------------

    /**
     * @see GnucashWritableAccount#setName(java.lang.String)
     */
    public void setName(final String name) {
	if ( name == null ) {
	    throw new IllegalArgumentException("null name given!");
	}

	if ( name.trim().length() == 0 ) {
	    throw new IllegalArgumentException("empty name given!");
	}

	String oldName = getName();
	if (oldName == name) {
	    return; // nothing has changed
	}
	this.getJwsdpPeer().setActName(name);
	setIsModified();
	// <<insert code to react further to this change here
	PropertyChangeSupport propertyChangeFirer = getPropertyChangeSupport();
	if (propertyChangeFirer != null) {
	    propertyChangeFirer.firePropertyChange("name", oldName, name);
	}
    }

    /**
     * @see GnucashWritableAccount#setAccountCode(java.lang.String)
     */
    public void setAccountCode(final String code) {
	if ( code == null ) {
	    throw new IllegalArgumentException("null code given!");
	}

	if ( code.trim().length() == 0 ) {
	    throw new IllegalArgumentException("empty code given!");
	}

	String oldCode = getCode();
	if (oldCode == code) {
	    return; // nothing has changed
	}
	this.getJwsdpPeer().setActCode(code);
	setIsModified();
	// <<insert code to react further to this change here
	PropertyChangeSupport propertyChangeFirer = getPropertyChangeSupport();
	if (propertyChangeFirer != null) {
	    propertyChangeFirer.firePropertyChange("code", oldCode, code);
	}
    }

    /**
     * @param currNameSpace the new namespace
     * @throws InvalidCmdtyCurrTypeException
     * @see {@link GnucashAccount#getCurrencyNameSpace()}
     */
    private void setCmdtyCurrNameSpace(final String currNameSpace) throws InvalidCmdtyCurrTypeException {
	if ( currNameSpace == null ) {
	    throw new IllegalArgumentException("null currencyNameSpace given!");
	}

	if ( currNameSpace.trim().length() == 0 ) {
	    throw new IllegalArgumentException("empty currencyNameSpace given!");
	}

	String oldCurrNameSpace = getCmdtyCurrID().getNameSpace();
	if (oldCurrNameSpace == currNameSpace) {
	    return; // nothing has changed
	}
	this.getJwsdpPeer().getActCommodity().setCmdtySpace(currNameSpace);
	setIsModified();
	// <<insert code to react further to this change here
	PropertyChangeSupport propertyChangeFirer = getPropertyChangeSupport();
	if (propertyChangeFirer != null) {
	    propertyChangeFirer.firePropertyChange("currencyNameSpace", oldCurrNameSpace, currNameSpace);
	}
    }

    public void setCmdtyCurrID(final GCshCmdtyCurrID cmdtyCurrID) throws InvalidCmdtyCurrTypeException {
	setCmdtyCurrNameSpace(cmdtyCurrID.getNameSpace());
	setCmdtyCurrCode(cmdtyCurrID.getCode());
    }

    /**
     * @param currID the new currency
     * @throws InvalidCmdtyCurrTypeException
     * @see #setCurrencyNameSpace(String)
     * @see {@link GnucashAccount#getCurrencyID()}
     */
    private void setCmdtyCurrCode(final String currID) throws InvalidCmdtyCurrTypeException {
	if ( currID == null ) {
	    throw new IllegalArgumentException("null currencyID given!");
	}

	if ( currID.trim().length() == 0 ) {
	    throw new IllegalArgumentException("empty currencyID given!");
	}

	String oldCurrencyId = getCmdtyCurrID().getCode();
	if (oldCurrencyId == currID) {
	    return; // nothing has changed
	}
	this.getJwsdpPeer().getActCommodity().setCmdtyId(currID);
	setIsModified();
	// <<insert code to react further to this change here
	PropertyChangeSupport propertyChangeFirer = getPropertyChangeSupport();
	if (propertyChangeFirer != null) {
	    propertyChangeFirer.firePropertyChange("currencyID", oldCurrencyId, currID);
	}
    }

    /**
     * set getWritableFile().setModified(true).
     */
    protected void setIsModified() {
	GnucashWritableFile writableFile = getWritableGnucashFile();
	writableFile.setModified(true);
    }

    /**
     * @see GnucashWritableAccount#setName(java.lang.String)
     */
    public void setDescription(final String descr) {
	if ( descr == null ) {
	    throw new IllegalArgumentException("null description given!");
	}

	// Caution: empty string allowed here
//	if ( descr.trim().length() == 0 ) {
//	    throw new IllegalArgumentException("empty description given!");
//	}

	String oldDescr = getDescription();
	if (oldDescr == descr) {
	    return; // nothing has changed
	}
	getJwsdpPeer().setActDescription(descr);
	setIsModified();
	// <<insert code to react further to this change here
	PropertyChangeSupport propertyChangeFirer = getPropertyChangeSupport();
	if (propertyChangeFirer != null) {
	    propertyChangeFirer.firePropertyChange("description", oldDescr, descr);
	}
    }

    /**
     * @throws UnknownAccountTypeException
     * @see GnucashWritableAccount#setInvcType(java.lang.String)
     */
    public void setType(final Type type) throws UnknownAccountTypeException {
	Type oldType = getType();
	if (oldType == type) {
	    return; // nothing has changed
	}
	
	getJwsdpPeer().setActType(type.toString());
	setIsModified();
	// <<insert code to react further to this change here
	PropertyChangeSupport propertyChangeFirer = getPropertyChangeSupport();
	if (propertyChangeFirer != null) {
	    propertyChangeFirer.firePropertyChange("type", oldType, type);
	}
    }

    /**
     * @see GnucashWritableAccount#setParentAccount(GnucashAccount)
     */
    public void setParentAccountID(final GCshID prntAcctID) {
	if (prntAcctID == null) {
	    setParentAccount(null);
	} else if (!prntAcctID.isSet()) {
	    setParentAccount(null);
	} else {
	    setParentAccount(getGnucashFile().getAccountByID(prntAcctID));
	}
    }

    /**
     * @see GnucashWritableAccount#setParentAccount(GnucashAccount)
     */
    public void setParentAccount(final GnucashAccount prntAcct) {

	if (prntAcct == null) {
	    this.getJwsdpPeer().setActParent(null);
	    return;
	}

	if (prntAcct == this) {
	    throw new IllegalArgumentException("I cannot be my own parent!");
	}

	// check if newparent is a child-account recusively
	if (isChildAccountRecursive(prntAcct)) {
	    throw new IllegalArgumentException("I cannot be my own (grand-)parent!");
	}

	GnucashAccount oldPrntAcct = null;
	GncAccount.ActParent parent = getJwsdpPeer().getActParent();
	if (parent == null) {
	    parent = ((GnucashWritableFileImpl) getWritableGnucashFile()).getObjectFactory()
		    .createGncAccountActParent();
	    parent.setType(Const.XML_DATA_TYPE_GUID);
	    parent.setValue(prntAcct.getID().toString());
	    getJwsdpPeer().setActParent(parent);

	} else {
	    oldPrntAcct = getParentAccount();
	    parent.setValue(prntAcct.getID().toString());
	}
	setIsModified();

	// <<insert code to react further to this change here
	PropertyChangeSupport propertyChangeFirer = getPropertyChangeSupport();
	if (propertyChangeFirer != null) {
	    propertyChangeFirer.firePropertyChange("parentAccount", oldPrntAcct, prntAcct);
	}
    }

    // ---------------------------------------------------------------

    /**
     * same as getBalance(new Date()).<br/>
     * ignores transactions after the current date+time<br/>
     * This implementation caches the result.<br/>
     * We assume that time does never move backwards
     *
     * @see #getBalance(LocalDate)
     */
    @Override
    public FixedPointNumber getBalance() {

	if (myBalanceCached != null) {
	    return myBalanceCached;
	}

	Collection<GnucashTransactionSplit> after = new ArrayList<GnucashTransactionSplit>();
	FixedPointNumber balance = getBalance(LocalDate.now(), after);

	if (after.isEmpty()) {
	    myBalanceCached = balance;

	    // add a listener to keep the cache up to date
	    if (myBalanceCachedInvalidtor != null) {
		myBalanceCachedInvalidtor = new PropertyChangeListener() {
		    private final Collection<GnucashTransactionSplit> splitsWeAreAddedTo = new HashSet<GnucashTransactionSplit>();

		    public void propertyChange(final PropertyChangeEvent evt) {
			myBalanceCached = null;

			// we don't handle the case of removing an account
			// because that happenes seldomly enough

			if ( evt.getPropertyName().equals("account") && 
			     evt.getSource() instanceof GnucashWritableTransactionSplit ) {
			    GnucashWritableTransactionSplit splitw = (GnucashWritableTransactionSplit) evt.getSource();
			    if (splitw.getAccount() != GnucashWritableAccountImpl.this) {
				splitw.removePropertyChangeListener("account", this);
				splitw.removePropertyChangeListener("quantity", this);
				splitw.getTransaction().removePropertyChangeListener("datePosted", this);
				splitsWeAreAddedTo.remove(splitw);

			    }

			}
			
			if ( evt.getPropertyName().equals("transactionSplits") ) {
			    Collection<GnucashTransactionSplit> splits = (Collection<GnucashTransactionSplit>) evt.getNewValue();
			    for (GnucashTransactionSplit split : splits) {
				if ( ! (split instanceof GnucashWritableTransactionSplit) || 
				     splitsWeAreAddedTo.contains(split)) {
				    continue;
				}
				GnucashWritableTransactionSplit splitw = (GnucashWritableTransactionSplit) split;
				splitw.addPropertyChangeListener("account", this);
				splitw.addPropertyChangeListener("quantity", this);
				splitw.getTransaction().addPropertyChangeListener("datePosted", this);
				splitsWeAreAddedTo.add(splitw);
			    }
			}
		    }
		};
		
		addPropertyChangeListener("currencyID", myBalanceCachedInvalidtor);
		addPropertyChangeListener("currencyNameSpace", myBalanceCachedInvalidtor);
		addPropertyChangeListener("transactionSplits", myBalanceCachedInvalidtor);
	    }
	}

	return balance;
    }

    /**
     * Get the sum of all transaction-splits affecting this account in the given
     * time-frame.
     *
     * @param from when to start, inclusive
     * @param to   when to stop, exlusive.
     * @return the sum of all transaction-splits affecting this account in the given
     *         time-frame.
     */
    public FixedPointNumber getBalanceChange(final LocalDate from, final LocalDate to) {
	FixedPointNumber retval = new FixedPointNumber();

	for ( GnucashTransactionSplit splt : getTransactionSplits() ) {
	    LocalDateTime whenHappened = splt.getTransaction().getDatePosted().toLocalDateTime();
	    
	    if ( ! whenHappened.isBefore(to.atStartOfDay()) ) {
		continue;
	    }
	    
	    if ( whenHappened.isBefore(from.atStartOfDay()) ) {
		continue;
	    }
	    
	    retval = retval.add(splt.getQuantity());
	}

	return retval;
    }

    // ---------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * @see GnucashWritableObject#setUserDefinedAttribute(java.lang.String,
     *      java.lang.String)
     */
	public void setUserDefinedAttribute(final String name, final String value) {
		if ( helper == null ) {
			helper = new GnucashWritableObjectImpl(super.helper);
		}
		LOGGER.debug("setUserDefinedAttribute: [account-id=" + getID() + "] name=" + getName() + "] (name=" + name
				+ ", value=" + value + ")");
		helper.setUserDefinedAttribute(name, value);
	}

	public void clean() {
		if ( helper == null ) {
			helper = new GnucashWritableObjectImpl(super.helper);
		}
		LOGGER.debug("clean: [account-id=" + getID() + "]");
		helper.cleanSlots();
	}

    // ---------------------------------------------------------------

    @Override
    public String toString() {
	StringBuffer buffer = new StringBuffer();
	buffer.append("GnucashWritableAccountImpl [");

	buffer.append("id=");
	buffer.append(getID());

	buffer.append(", code='");
	buffer.append(getCode() + "'");

	buffer.append(", type=");
	try {
	    buffer.append(getType());
	} catch (UnknownAccountTypeException e) {
	    buffer.append("ERROR");
	}

	buffer.append(", qualif-name='");
	buffer.append(getQualifiedName() + "'");

	buffer.append(", commodity/currency='");
	try {
	    buffer.append(getCmdtyCurrID() + "'");
	} catch (InvalidCmdtyCurrTypeException e) {
	    buffer.append("ERROR");
	}

	buffer.append("]");

	return buffer.toString();
    }

}
