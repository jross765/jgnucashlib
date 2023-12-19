package org.gnucash.api.read.spec;

import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.numbers.FixedPointNumber;
import org.gnucash.api.read.GnucashGenerInvoiceEntry;

public interface GnucashEmployeeVoucherEntry extends GnucashGenerInvoiceEntry 
{
  GCshID getVoucherID();

  GnucashEmployeeVoucher getVoucher() throws WrongInvoiceTypeException, IllegalArgumentException;
  
  // -----------------------------------------------------------------

  FixedPointNumber getPrice() throws WrongInvoiceTypeException;

  String getPriceFormatted() throws WrongInvoiceTypeException;
  
}
