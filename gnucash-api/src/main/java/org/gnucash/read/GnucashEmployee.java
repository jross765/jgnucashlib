package org.gnucash.read;

import java.util.Collection;
import java.util.Locale;

import org.gnucash.basetypes.simple.GCshID;
import org.gnucash.numbers.FixedPointNumber;
import org.gnucash.read.aux.GCshAddress;
import org.gnucash.read.spec.GnucashEmployeeJob;
import org.gnucash.read.spec.GnucashEmployeeVoucher;
import org.gnucash.read.spec.GnucashJobInvoice;
import org.gnucash.read.spec.WrongInvoiceTypeException;

/**
 * An employee that can hand in expense vouchers and, obviously, receive
 * a salary
 *
 * @see GnucashEmployeeVoucher
 */
public interface GnucashEmployee extends GnucashObject {

    /**
     * The gnucash-file is the top-level class to contain everything.
     * 
     * @return the file we are associated with
     */
    GnucashFile getGnucashFile();

    // ------------------------------------------------------------

    /**
     * @return the unique-id to identify this object with across name- and
     *         hirarchy-changes
     */
    GCshID getId();

    /**
     *
     * @return the user-assigned number of this employee (may contain non-digits)
     */
    String getNumber();

    /**
     *
     * @return the user name of the employee
     */
    String getUserName();

    /**
     *
     * @return the name of the employee
     */
    String getName();

    /**
     * @return the address including the name
     */
    GCshAddress getAddress();

    /**
     * @return user-defined notes about the employee (may be null)
     */
    String getLanguage();

    /**
     * @return user-defined notes about the employee (may be null)
     */
    String getNotes();

    // ------------------------------------------------------------

//    /**
//     * The id of the default tax table to use with this employee (may be null).
//     * 
//     * @see {@link #getTaxTable()}
//     */
//    String getTaxTableID();
//
//    /**
//     * The default tax table to use with this employee (may be null).
//     * 
//     * @see {@link #getTaxTableID()}
//     */
//    GCshTaxTable getTaxTable();

    // ------------------------------------------------------------

//    /**
//     * The id of the default terms to use with this customer (may be null).
//     * 
//     * @see {@link #getTaxTable()}
//     */
//    String getTermsID();
//
//    /**
//     * The default terms to use with this customer (may be null).
//     * 
//     * @see {@link #getTaxTableID()}
//     */
//    GCshBillTerms getTerms();

    // ------------------------------------------------------------

    /**
     * Date is not checked so invoiced that have entered payments in the future are
     * considered Paid.
     * 
     * @return the current number of Unpaid invoices
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException 
     */
    int getNofOpenVouchers() throws WrongInvoiceTypeException, UnknownAccountTypeException;

    // -------------------------------------

    /**
     * @return the sum of payments for invoices to this client
     * @throws UnknownAccountTypeException 
     * @throws WrongInvoiceTypeException
     */
    FixedPointNumber getExpensesGenerated(GnucashGenerInvoice.ReadVariant readVar) throws UnknownAccountTypeException;

    /**
     * @return the sum of payments for invoices to this client
     * @throws UnknownAccountTypeException 
     * @throws WrongInvoiceTypeException
     */
    FixedPointNumber getExpensesGenerated_direct() throws UnknownAccountTypeException;

    /**
     * @throws UnknownAccountTypeException 
     * @throws WrongInvoiceTypeException
     * @see #getIncomeGenerated() Formatted according to the current locale's
     *      currency-format
     */
    String getExpensesGeneratedFormatted(GnucashGenerInvoice.ReadVariant readVar) throws UnknownAccountTypeException;

    /**
     * @throws UnknownAccountTypeException 
     * @throws WrongInvoiceTypeException
     * @see #getIncomeGenerated() Formatted according to the given locale's
     *      currency-format
     */
    String getExpensesGeneratedFormatted(GnucashGenerInvoice.ReadVariant readVar, Locale lcl) throws UnknownAccountTypeException;

    // -------------------------------------

    /**
     * @return the sum of left to pay Unpaid invoiced
     * @throws UnknownAccountTypeException 
     * @throws WrongInvoiceTypeException
     */
    FixedPointNumber getOutstandingValue(GnucashGenerInvoice.ReadVariant readVar) throws UnknownAccountTypeException, WrongInvoiceTypeException;

    /**
     * @return the sum of left to pay Unpaid invoiced
     * @throws UnknownAccountTypeException 
     * @throws WrongInvoiceTypeException
     */
    FixedPointNumber getOutstandingValue_direct() throws UnknownAccountTypeException, WrongInvoiceTypeException;

    /**
     * @throws UnknownAccountTypeException 
     * @throws WrongInvoiceTypeException
     * @see #getOutstandingValue() Formatted according to the current locale's
     *      currency-format
     */
    String getOutstandingValueFormatted(GnucashGenerInvoice.ReadVariant readVar) throws UnknownAccountTypeException, WrongInvoiceTypeException;

    /**
     *
     * @throws UnknownAccountTypeException 
     * @throws WrongInvoiceTypeException
     * @see #getOutstandingValue() Formatted according to the given locale's
     *      currency-format
     */
    String getOutstandingValueFormatted(GnucashGenerInvoice.ReadVariant readVar, Locale lcl) throws UnknownAccountTypeException, WrongInvoiceTypeException;

    // ------------------------------------------------------------

    /**
     * @return the UNMODIFIABLE collection of jobs that have this employee associated
     *         with them.
     * @throws WrongInvoiceTypeException
     */
    Collection<GnucashEmployeeJob> getJobs() throws WrongInvoiceTypeException;

    // ------------------------------------------------------------

    Collection<GnucashGenerInvoice>    getVouchers() throws WrongInvoiceTypeException;

    Collection<GnucashEmployeeVoucher> getPaidVouchers_direct() throws WrongInvoiceTypeException, UnknownAccountTypeException;

    Collection<GnucashJobInvoice>      getPaidVouchers_viaAllJobs() throws WrongInvoiceTypeException, UnknownAccountTypeException;

    Collection<GnucashEmployeeVoucher> getUnpaidVouchers_direct() throws WrongInvoiceTypeException, UnknownAccountTypeException;

    Collection<GnucashJobInvoice>      getUnpaidVouchers_viaAllJobs() throws WrongInvoiceTypeException, UnknownAccountTypeException;

    // ------------------------------------------------------------

    public static int getHighestNumber(GnucashEmployee empl) {
	return empl.getGnucashFile().getHighestCustomerNumber();
    }

    public static String getNewNumber(GnucashEmployee cust) {
	return cust.getGnucashFile().getNewCustomerNumber();
    }

}