package org.gnucash.api.read.impl.hlp;

import java.util.ArrayList;
import java.util.List;

import org.gnucash.api.read.GnuCashEmployee;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.impl.spec.GnuCashEmployeeVoucherImpl;
import org.gnucash.api.read.spec.GnuCashEmployeeVoucher;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FileInvoiceManager_Employee {

	protected static final Logger LOGGER = LoggerFactory.getLogger(FileInvoiceManager_Employee.class);

	// ---------------------------------------------------------------

	public static List<GnuCashEmployeeVoucher> getVouchers(final FileInvoiceManager invcMgr,
			final GnuCashEmployee empl) {
		List<GnuCashEmployeeVoucher> retval = new ArrayList<GnuCashEmployeeVoucher>();

		for ( GnuCashGenerInvoice invc : invcMgr.getGenerInvoices() ) {
			if ( invc.getOwnerID(GnuCashGenerInvoice.ReadVariant.DIRECT).equals(empl.getID()) ) {
				try {
					retval.add(new GnuCashEmployeeVoucherImpl(invc));
				} catch (WrongInvoiceTypeException e) {
					LOGGER.error("getVouchers: Cannot instantiate GnuCashEmployeeVoucherImpl");
				}
			}
		}

		return retval;
	}

	public static List<GnuCashEmployeeVoucher> getPaidVouchers(final FileInvoiceManager invcMgr,
			final GnuCashEmployee empl) {
		List<GnuCashEmployeeVoucher> retval = new ArrayList<GnuCashEmployeeVoucher>();

		for ( GnuCashGenerInvoice invc : invcMgr.getPaidGenerInvoices() ) {
			if ( invc.getOwnerID(GnuCashGenerInvoice.ReadVariant.DIRECT).equals(empl.getID()) ) {
				try {
					retval.add(new GnuCashEmployeeVoucherImpl(invc));
				} catch (WrongInvoiceTypeException e) {
					LOGGER.error("getPaidVouchers: Cannot instantiate GnuCashEmployeeVoucherImpl");
				}
			}
		}

		return retval;
	}

	public static List<GnuCashEmployeeVoucher> getUnpaidVouchers(final FileInvoiceManager invcMgr,
			final GnuCashEmployee empl) {
		List<GnuCashEmployeeVoucher> retval = new ArrayList<GnuCashEmployeeVoucher>();

		for ( GnuCashGenerInvoice invc : invcMgr.getUnpaidGenerInvoices() ) {
			if ( invc.getOwnerID(GnuCashGenerInvoice.ReadVariant.DIRECT).equals(empl.getID()) ) {
				try {
					retval.add(new GnuCashEmployeeVoucherImpl(invc));
				} catch (WrongInvoiceTypeException e) {
					LOGGER.error("getUnpaidVouchers: Cannot instantiate GnuCashEmployeeVoucherImpl");
				}
			}
		}

		return retval;
	}

}
