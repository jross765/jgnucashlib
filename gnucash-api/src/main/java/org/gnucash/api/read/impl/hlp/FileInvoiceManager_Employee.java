package org.gnucash.api.read.impl.hlp;

import java.util.ArrayList;
import java.util.Collection;

import org.gnucash.api.read.GnucashEmployee;
import org.gnucash.api.read.GnucashGenerInvoice;
import org.gnucash.api.read.UnknownAccountTypeException;
import org.gnucash.api.read.impl.spec.GnucashEmployeeVoucherImpl;
import org.gnucash.api.read.spec.GnucashEmployeeVoucher;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FileInvoiceManager_Employee {

	protected static final Logger LOGGER = LoggerFactory.getLogger(FileInvoiceManager_Employee.class);

	// ---------------------------------------------------------------

	public static Collection<GnucashEmployeeVoucher> getVouchers(final FileInvoiceManager invcMgr,
			final GnucashEmployee empl) throws WrongInvoiceTypeException {
		Collection<GnucashEmployeeVoucher> retval = new ArrayList<GnucashEmployeeVoucher>();

		for ( GnucashGenerInvoice invc : invcMgr.getGenerInvoices() ) {
			if ( invc.getOwnerID(GnucashGenerInvoice.ReadVariant.DIRECT).equals(empl.getID()) ) {
				try {
					retval.add(new GnucashEmployeeVoucherImpl(invc));
				} catch (WrongInvoiceTypeException e) {
					LOGGER.error("getVouchers: Cannot instantiate GnucashEmployeeVoucherImpl");
				}
			}
		}

		return retval;
	}

	public static Collection<GnucashEmployeeVoucher> getPaidVouchers(final FileInvoiceManager invcMgr,
			final GnucashEmployee empl)
			throws WrongInvoiceTypeException, UnknownAccountTypeException {
		Collection<GnucashEmployeeVoucher> retval = new ArrayList<GnucashEmployeeVoucher>();

		for ( GnucashGenerInvoice invc : invcMgr.getPaidGenerInvoices() ) {
			if ( invc.getOwnerID(GnucashGenerInvoice.ReadVariant.DIRECT).equals(empl.getID()) ) {
				try {
					retval.add(new GnucashEmployeeVoucherImpl(invc));
				} catch (WrongInvoiceTypeException e) {
					LOGGER.error("getPaidVouchers: Cannot instantiate GnucashEmployeeVoucherImpl");
				}
			}
		}

		return retval;
	}

	public static Collection<GnucashEmployeeVoucher> getUnpaidVouchers(final FileInvoiceManager invcMgr,
			final GnucashEmployee empl)
			throws WrongInvoiceTypeException, UnknownAccountTypeException {
		Collection<GnucashEmployeeVoucher> retval = new ArrayList<GnucashEmployeeVoucher>();

		for ( GnucashGenerInvoice invc : invcMgr.getUnpaidGenerInvoices() ) {
			if ( invc.getOwnerID(GnucashGenerInvoice.ReadVariant.DIRECT).equals(empl.getID()) ) {
				try {
					retval.add(new GnucashEmployeeVoucherImpl(invc));
				} catch (WrongInvoiceTypeException e) {
					LOGGER.error("getUnpaidVouchers: Cannot instantiate GnucashEmployeeVoucherImpl");
				}
			}
		}

		return retval;
	}

}
