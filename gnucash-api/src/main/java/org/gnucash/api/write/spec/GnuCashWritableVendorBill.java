package org.gnucash.api.write.spec;

import java.time.LocalDate;

import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashVendor;
import org.gnucash.api.read.IllegalTransactionSplitActionException;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.impl.aux.WrongOwnerTypeException;
import org.gnucash.api.read.spec.GnuCashVendorBill;
import org.gnucash.api.write.GnuCashWritableGenerInvoice;
import org.gnucash.base.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.numbers.FixedPointNumber;

/**
 * Vendor bill that can be modified if {@link #isModifiable()} returns true.
 * 
 * @see GnuCashVendorBill
 * 
 * @see GnuCashWritableCustomerInvoice
 * @see GnuCashWritableEmployeeVoucher
 * @see GnuCashWritableJobInvoice
 */
public interface GnuCashWritableVendorBill extends GnuCashWritableGenerInvoice {

    GnuCashWritableVendorBillEntry getWritableEntryByID(GCshID id);
    
    // ---------------------------------------------------------------

    /**
     * Will throw an IllegalStateException if there are bills for this vendor.<br/>
     * 
     * @param vend the vendor who sent an invoice to us.
* 
     */
    void setVendor(GnuCashVendor vend);

    // ---------------------------------------------------------------

    GnuCashWritableVendorBillEntry createEntry(
	    GnuCashAccount acct, 
	    FixedPointNumber singleUnitPrice,
	    FixedPointNumber quantity) throws TaxTableNotFoundException, IllegalTransactionSplitActionException, NumberFormatException;

    GnuCashWritableVendorBillEntry createEntry(
	    GnuCashAccount acct, 
	    FixedPointNumber singleUnitPrice,
	    FixedPointNumber quantity, 
	    String taxTabName)
	    throws TaxTableNotFoundException, IllegalTransactionSplitActionException, NumberFormatException;

    GnuCashWritableVendorBillEntry createEntry(
	    GnuCashAccount acct, 
	    FixedPointNumber singleUnitPrice,
	    FixedPointNumber quantity, 
	    GCshTaxTable taxTab)
	    throws TaxTableNotFoundException, IllegalTransactionSplitActionException, NumberFormatException;

    // ---------------------------------------------------------------
    
    void post(GnuCashAccount expensesAcct,
	      GnuCashAccount payablAcct,
	      LocalDate postDate,
	      LocalDate dueDate) throws WrongOwnerTypeException, NumberFormatException, IllegalTransactionSplitActionException;

}
