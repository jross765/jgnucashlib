package org.example.gnucashapi.read;

import java.io.File;
import java.util.Collection;

import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.read.GnucashCustomer;
import org.gnucash.api.read.GnucashGenerInvoice;
import org.gnucash.api.read.NoEntryFoundException;
import org.gnucash.api.read.UnknownAccountTypeException;
import org.gnucash.api.read.aux.GCshBillTerms;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.impl.GnucashFileImpl;
import org.gnucash.api.read.spec.GnucashCustomerInvoice;
import org.gnucash.api.read.spec.GnucashCustomerJob;
import org.gnucash.api.read.spec.GnucashJobInvoice;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;

public class GetCustInfo {
    // BEGIN Example data -- adapt to your needs
    private static String gcshFileName = "example_in.gnucash";
    private static Helper.Mode mode    = Helper.Mode.ID;
    private static GCshID custID       = new GCshID("xyz");
    private static String custName     = "abc";
    // END Example data

    // -----------------------------------------------------------------

    public static void main(String[] args) {
	try {
	    GetCustInfo tool = new GetCustInfo();
	    tool.kernel();
	} catch (Exception exc) {
	    System.err.println("Execution exception. Aborting.");
	    exc.printStackTrace();
	    System.exit(1);
	}
    }

    protected void kernel() throws Exception {
	GnucashFileImpl gcshFile = new GnucashFileImpl(new File(gcshFileName));

	GnucashCustomer cust = gcshFile.getCustomerByID(custID);
	if ( mode == Helper.Mode.ID ) {
	    cust = gcshFile.getCustomerByID(custID);
	    if (cust == null) {
		System.err.println("Found no account with that ID");
		throw new NoEntryFoundException();
	    }
	} else if ( mode == Helper.Mode.NAME ) {
	    Collection<GnucashCustomer> custList = null;
	    custList = gcshFile.getCustomersByName(custName, true);
	    if (custList.size() == 0) {
		System.err.println("Found no account with that name.");
		throw new NoEntryFoundException();
	    } else if (custList.size() > 1) {
		System.err.println("Found several accounts with that name.");
		System.err.println("Taking first one.");
	    }
	    cust = custList.iterator().next(); // first element
	}
	
	// ------------------------

	try {
	    System.out.println("ID:                " + cust.getId());
	} catch (Exception exc) {
	    System.out.println("ID:                " + "ERROR");
	}

	try {
	    System.out.println("Number:            '" + cust.getNumber() + "'");
	} catch (Exception exc) {
	    System.out.println("Number:            " + "ERROR");
	}

	try {
	    System.out.println("Name:              '" + cust.getName() + "'");
	} catch (Exception exc) {
	    System.out.println("Name:              " + "ERROR");
	}

	try {
	    System.out.println("Address:           '" + cust.getAddress() + "'");
	} catch (Exception exc) {
	    System.out.println("Address:           " + "ERROR");
	}

	System.out.println("");
	try {
	    System.out.println("Discount:          " + cust.getDiscount());
	} catch (Exception exc) {
	    System.out.println("Discount:          " + "ERROR");
	}

	try {
	    System.out.println("Credit:            " + cust.getCredit());
	} catch (Exception exc) {
	    System.out.println("Credit:            " + "ERROR");
	}

	System.out.println("");
	try {
	    GCshID taxTabID = cust.getTaxTableID();
	    System.out.println("Tax table ID:      " + taxTabID);

	    if (cust.getTaxTableID() != null) {
		try {
		    GCshTaxTable taxTab = gcshFile.getTaxTableByID(taxTabID);
		    System.out.println("Tax table:        " + taxTab.toString());
		} catch (Exception exc2) {
		    System.out.println("Tax table:        " + "ERROR");
		}
	    }
	} catch (Exception exc) {
	    System.out.println("Tax table ID:      " + "ERROR");
	}

	System.out.println("");
	try {
	    GCshID bllTrmID = cust.getTermsID();
	    System.out.println("Bill terms ID:     " + bllTrmID);

	    if (cust.getTermsID() != null) {
		try {
		    GCshBillTerms bllTrm = gcshFile.getBillTermsByID(bllTrmID);
		    System.out.println("Bill Terms:        " + bllTrm.toString());
		} catch (Exception exc2) {
		    System.out.println("Bill Terms:        " + "ERROR");
		}
	    }
	} catch (Exception exc) {
	    System.out.println("Bill terms ID:     " + "ERROR");
	}

	System.out.println("");
	System.out.println("Income generated:");
	try {
	    System.out
		    .println(" - direct:  " + cust.getIncomeGeneratedFormatted(GnucashGenerInvoice.ReadVariant.DIRECT));
	} catch (Exception exc) {
	    System.out.println(" - direct:  " + "ERROR");
	}

	try {
	    System.out.println(
		    " - via all jobs:  " + cust.getIncomeGeneratedFormatted(GnucashGenerInvoice.ReadVariant.VIA_JOB));
	} catch (Exception exc) {
	    System.out.println(" - via all jobs:  " + "ERROR");
	}

	System.out.println("Outstanding value:");
	try {
	    System.out
		    .println(" - direct: " + cust.getOutstandingValueFormatted(GnucashGenerInvoice.ReadVariant.DIRECT));
	} catch (Exception exc) {
	    System.out.println(" - direct: " + "ERROR");
	}

	try {
	    System.out.println(
		    " - via all jobs: " + cust.getOutstandingValueFormatted(GnucashGenerInvoice.ReadVariant.VIA_JOB));
	} catch (Exception exc) {
	    System.out.println(" - via all jobs: " + "ERROR");
	}

	// ---

	showJobs(cust);
	showInvoices(cust);
    }

    // -----------------------------------------------------------------

    private void showJobs(GnucashCustomer cust) throws WrongInvoiceTypeException {
	System.out.println("");
	System.out.println("Jobs:");
	for (GnucashCustomerJob job : cust.getJobs()) {
	    System.out.println(" - " + job.toString());
	}
    }

    private void showInvoices(GnucashCustomer cust) throws WrongInvoiceTypeException, UnknownAccountTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	System.out.println("");
	System.out.println("Invoices:");

	System.out.println("Number of open invoices: " + cust.getNofOpenInvoices());

	System.out.println("");
	System.out.println("Paid invoices (direct):");
	for (GnucashCustomerInvoice invc : cust.getPaidInvoices_direct()) {
	    System.out.println(" - " + invc.toString());
	}

	System.out.println("");
	System.out.println("Paid invoices (via all jobs):");
	for (GnucashJobInvoice invc : cust.getPaidInvoices_viaAllJobs()) {
	    System.out.println(" - " + invc.toString());
	}

	System.out.println("");
	System.out.println("Unpaid invoices (direct):");
	for (GnucashCustomerInvoice invc : cust.getUnpaidInvoices_direct()) {
	    System.out.println(" - " + invc.toString());
	}

	System.out.println("");
	System.out.println("Unpaid invoices (via all jobs):");
	for (GnucashJobInvoice invc : cust.getUnpaidInvoices_viaAllJobs()) {
	    System.out.println(" - " + invc.toString());
	}
    }
}