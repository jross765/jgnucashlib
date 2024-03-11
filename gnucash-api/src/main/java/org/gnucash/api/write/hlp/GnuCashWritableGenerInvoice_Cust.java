package org.gnucash.api.write.hlp;

import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashCustomer;
import org.gnucash.api.read.IllegalTransactionSplitActionException;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.write.spec.GnuCashWritableCustomerInvoiceEntry;
import org.gnucash.base.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.base.numbers.FixedPointNumber;

public interface GnuCashWritableGenerInvoice_Cust {

    void setCustomer(GnuCashCustomer cust);

    // ---------------------------------------------------------------

    /**
     * create and add a new entry.<br/>
     * The entry will use the accounts of the SKR03.
     * @param acct 
     * @param singleUnitPrice 
     * @param quantity 
     * @return 
     * 
     * @throws TaxTableNotFoundException
     * @throws IllegalTransactionSplitActionException
     */
    GnuCashWritableCustomerInvoiceEntry createCustInvcEntry(
    		GnuCashAccount acct,
    		FixedPointNumber singleUnitPrice, 
    		FixedPointNumber quantity)
	    throws TaxTableNotFoundException, IllegalTransactionSplitActionException;

    /**
     * create and add a new entry.<br/>
     * The entry will use the accounts of the SKR03.
     * @param acct 
     * @param singleUnitPrice 
     * @param quantity 
     * @param taxTabName 
     * @return 
     * 
     * @throws TaxTableNotFoundException
     * @throws IllegalTransactionSplitActionException
     */
    GnuCashWritableCustomerInvoiceEntry createCustInvcEntry(
    		GnuCashAccount acct,
    		FixedPointNumber singleUnitPrice, 
    		FixedPointNumber quantity, 
    		String taxTabName)
	    throws TaxTableNotFoundException, IllegalTransactionSplitActionException;

    /**
     * create and add a new entry.<br/>
     * @param acct 
     * @param singleUnitPrice 
     * @param quantity 
     * @param taxTab 
     *
     * @return an entry using the given Tax-Table
     * @throws TaxTableNotFoundException
     * @throws IllegalTransactionSplitActionException
     */
    GnuCashWritableCustomerInvoiceEntry createCustInvcEntry(
    		GnuCashAccount acct,
    		FixedPointNumber singleUnitPrice,
    		FixedPointNumber quantity,
    		GCshTaxTable taxTab)
	    throws TaxTableNotFoundException, IllegalTransactionSplitActionException,
	    NumberFormatException;
}