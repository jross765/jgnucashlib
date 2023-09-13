package org.gnucash.read.impl;

import java.text.NumberFormat;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.gnucash.generated.GncV2;
import org.gnucash.generated.ObjectFactory;
import org.gnucash.numbers.FixedPointNumber;
import org.gnucash.read.GnucashCustVendInvoice;
import org.gnucash.read.GnucashCustomer;
import org.gnucash.read.GnucashFile;
import org.gnucash.read.GnucashJob;
import org.gnucash.read.GnucashTaxTable;
import org.gnucash.read.spec.GnucashCustomerInvoice;
import org.gnucash.read.spec.GnucashCustomerJob;
import org.gnucash.read.spec.WrongInvoiceTypeException;

public class GnucashCustomerImpl extends GnucashObjectImpl implements GnucashCustomer {

	/**
	 * the JWSDP-object we are facading.
	 */
	private final GncV2.GncBook.GncGncCustomer jwsdpPeer;

    /**
     * The file we belong to.
     */
    private final GnucashFile file;
    
    // ---------------------------------------------------------------

	/**
	 * @param peer    the JWSDP-object we are facading.
	 * @param gncFile the file to register under
	 */
	protected GnucashCustomerImpl(final GncV2.GncBook.GncGncCustomer peer, final GnucashFile gncFile) {
		super((peer.getCustSlots() == null) ? new ObjectFactory().createSlotsType() : peer.getCustSlots(), gncFile);
		if (peer.getCustSlots() == null) {
			peer.setCustSlots(getSlots());
		}
		jwsdpPeer = peer;
        file = gncFile;
	}

    // ---------------------------------------------------------------

	/**
	 * @return the JWSDP-object we are wrapping.
	 */
	public GncV2.GncBook.GncGncCustomer getJwsdpPeer() {
		return jwsdpPeer;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getId() {
		return jwsdpPeer.getCustGuid().getValue();
	}

	/**
	 * @return the jobs that have this customer associated with them.
	 * @see GnucashCustomer#getJobs()
	 */
	public java.util.Collection<GnucashCustomerJob> getJobs() {

		List<GnucashCustomerJob> retval = new LinkedList<GnucashCustomerJob>();

		for (GnucashJob job : getGnucashFile().getJobs()) {
		  if ( job instanceof GnucashCustomerJob ) {
            if ( ((GnucashCustomerJob) job).getCustomerId().equals(getId()) ) {
              retval.add((GnucashCustomerJob) job);
            }
		  }		    
		}

		return retval;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getDiscount() {
		return jwsdpPeer.getCustDiscount();
	}

	/**
	 * {@inheritDoc}
	 */
	public String getNotes() {
		return jwsdpPeer.getCustNotes();
	}

	/**
	 * date is not checked so invoiced that have entered payments in
	 * the future are considered Paid.
	 *
	 * @return the current number of Unpaid invoices
	 * @throws WrongInvoiceTypeException 
	 */
	public int getNofOpenInvoices() throws WrongInvoiceTypeException {
      return getGnucashFile().getUnpaidInvoicesForCustomer_direct(this).size();
    }

	/**
	 * @return the sum of payments for invoices to this client
	 * @throws WrongInvoiceTypeException 
	 */
	public FixedPointNumber getIncomeGenerated() throws WrongInvoiceTypeException {
		FixedPointNumber retval = new FixedPointNumber();

		for (GnucashCustVendInvoice invoice : getGnucashFile().getInvoices()) {
		  if ( invoice instanceof GnucashCustomerInvoice ) {
            if ( ((GnucashCustomerInvoice) invoice).getCustomer() != this ) {
              continue;
            }
            retval.add(invoice.getInvcAmountWithoutTaxes());
		  }
		}

		return retval;
	}

	/**
	 * The currencyFormat to use for default-formating.<br/>
	 * Please access only using {@link #getCurrencyFormat()}.
	 *
	 * @see #getCurrencyFormat()
	 */
	private NumberFormat currencyFormat = null;

	/**
	 * @return formated acording to the current locale's currency-format
	 * @throws WrongInvoiceTypeException 
	 * @see #getIncomeGenerated()
	 */
	public String getIncomeGeneratedFormatted() throws WrongInvoiceTypeException {
		return getCurrencyFormat().format(getIncomeGenerated());

	}

	/**
	 * @param l the locale to format for
	 * @return formated acording to the given locale's currency-format
	 * @throws WrongInvoiceTypeException 
	 * @see #getIncomeGenerated()
	 */
	public String getIncomeGeneratedFormatted(final Locale l) throws WrongInvoiceTypeException {
		return NumberFormat.getCurrencyInstance(l).format(getIncomeGenerated());
	}

	/**
	 * @return the sum of left to pay Unpaid invoiced
	 * @throws WrongInvoiceTypeException 
	 */
	public FixedPointNumber getOutstandingValue() throws WrongInvoiceTypeException {
		FixedPointNumber retval = new FixedPointNumber();

		for (GnucashCustVendInvoice invoice : getGnucashFile().getInvoices()) {
		  if ( invoice instanceof GnucashCustomerInvoice ) {
            if ( ((GnucashCustomerInvoice) invoice).getCustomer() != this ) {
              continue;
            }
            retval.add(invoice.getInvcAmountUnpaidWithTaxes());
		  }
		}

		return retval;
	}

	/**
	 * @return Formatted acording to the current locale's currency-format
	 * @throws WrongInvoiceTypeException 
	 * @see #getOutstandingValue()
	 */
	public String getOutstandingValueFormatted() throws WrongInvoiceTypeException {
		return getCurrencyFormat().format(getOutstandingValue());
	}

	/**
	 * @throws WrongInvoiceTypeException 
	 * @see #getOutstandingValue()
	 * Formatted acording to the given locale's currency-format
	 */
	public String getOutstandingValueFormatted(final Locale l) throws WrongInvoiceTypeException {
		return NumberFormat.getCurrencyInstance(l).format(getOutstandingValue());
	}

	/**
	 * {@inheritDoc}
	 */
	public String getNumber() {
		return jwsdpPeer.getCustId();
	}

	/**
	 * {@inheritDoc}
	 */
	public String getCustomerTaxTableID() {
		GncV2.GncBook.GncGncCustomer.CustTaxtable custTaxtable = jwsdpPeer.getCustTaxtable();
		if (custTaxtable == null) {
			return null;
		}
		return custTaxtable.getValue();
	}

	/**
	 * {@inheritDoc}
	 */
	public GnucashTaxTable getCustomerTaxTable() {
		String id = getCustomerTaxTableID();
		if (id == null) {
			return null;
		}
		return getGnucashFile().getTaxTableByID(id);
	}

	/**
	 * {@inheritDoc}
	 */
	public String getName() {
		return jwsdpPeer.getCustName();
	}

	/**
	 * {@inheritDoc}
	 */
	public GnucashCustomer.Address getAddress() {
		return new AddressImpl(jwsdpPeer.getCustAddr());
	}

	/**
	 * {@inheritDoc}
	 */
	public GnucashCustomer.Address getShippingAddress() {
		return new AddressImpl(jwsdpPeer.getCustShipaddr());
	}

	/**
	 * (c) 2005 by Wolschon Softwaredesign und Beratung.<br/>
	 * Project: gnucashReader<br/>
	 * GnucashCustomerImpl.java<br/>
	 *
	 * @author <a href="Marcus@Wolschon.biz">Marcus Wolschon</a>
	 * @see Address
	 */
	public static class AddressImpl implements Address {

		/**
		 * The JWSDP-object we are wrapping.
		 */
		private final org.gnucash.generated.Address jwsdpPeer;

		/**
		 * @param newPeer the JWSDP-object we are wrapping.
		 */
		public AddressImpl(final org.gnucash.generated.Address newPeer) {
			super();
			jwsdpPeer = newPeer;
		}

		/**
		 * @return The JWSDP-object we are wrapping.
		 */
		public org.gnucash.generated.Address getJwsdpPeer() {
			return jwsdpPeer;
		}

		/**
		 * @see GnucashCustomer.Address#getAddressName()
		 */
		public String getAddressName() {
			if (jwsdpPeer.getAddrName() == null) {
				return "";
			}
			return jwsdpPeer.getAddrName();
		}

		/**
		 * @see GnucashCustomer.Address#getAddressLine1()
		 */
		public String getAddressLine1() {
			if (jwsdpPeer.getAddrAddr1() == null) {
				return "";
			}
			return jwsdpPeer.getAddrAddr1();
		}

		/**
		 * @see GnucashCustomer.Address#getAddressLine2()
		 */
		public String getAddressLine2() {
			if (jwsdpPeer.getAddrAddr2() == null) {
				return "";
			}
			return jwsdpPeer.getAddrAddr2();
		}

		/**
		 * @return third and last line below the name
		 */
		public String getAddressLine3() {
			if (jwsdpPeer.getAddrAddr3() == null) {
				return "";
			}
			return jwsdpPeer.getAddrAddr3();
		}

		/**
		 * @return fourth and last line below the name
		 */
		public String getAddressLine4() {
			if (jwsdpPeer.getAddrAddr4() == null) {
				return "";
			}
			return jwsdpPeer.getAddrAddr4();
		}

		/**
		 * @return telephone
		 */
		public String getTel() {
			if (jwsdpPeer.getAddrPhone() == null) {
				return "";
			}
			return jwsdpPeer.getAddrPhone();
		}

		/**
		 * @return Fax
		 */
		public String getFax() {
			if (jwsdpPeer.getAddrFax() == null) {
				return "";
			}
			return jwsdpPeer.getAddrFax();
		}

		/**
		 * @return Email
		 */
		public String getEmail() {
			if (jwsdpPeer.getAddrEmail() == null) {
				return "";
			}
			return jwsdpPeer.getAddrEmail();
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return getAddressName() + "\n"
					+ getAddressLine1() + "\n"
					+ getAddressLine2();
		}
	}

	/**
	 * @return the currency-format to use if no locale is given.
	 */
	protected NumberFormat getCurrencyFormat() {
		if (currencyFormat == null) {
			currencyFormat = NumberFormat.getCurrencyInstance();
		}

		return currencyFormat;
	}
	
  // -----------------------------------------------------------------

  @Override
  public Collection<GnucashCustomerInvoice> getPaidInvoices(GnucashCustVendInvoice.ReadVariant readVar) throws WrongInvoiceTypeException
  {
    if ( readVar == GnucashCustVendInvoice.ReadVariant.DIRECT )
      return file.getPaidInvoicesForCustomer_direct(this);
    else if ( readVar == GnucashCustVendInvoice.ReadVariant.VIA_JOB )
      return file.getPaidInvoicesForCustomer_viaJob(this);

    return null; // Compiler happy
  }

  @Override
  public Collection<GnucashCustomerInvoice> getUnpaidInvoices(GnucashCustVendInvoice.ReadVariant readVar) throws WrongInvoiceTypeException
  {
    if ( readVar == GnucashCustVendInvoice.ReadVariant.DIRECT )
      return file.getUnpaidInvoicesForCustomer_direct(this);
    else if ( readVar == GnucashCustVendInvoice.ReadVariant.VIA_JOB )
      return file.getUnpaidInvoicesForCustomer_viaJob(this);
    
    return null; // Compiler happy
  }

  // ----------------------------
  
  public static int getHighestNumber(GnucashCustomer cust)
  {
    return cust.getGnucashFile().getHighestCustomerNumber();
  }

}
