package org.gnucash.api.read.spec;

import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.numbers.FixedPointNumber;
import org.gnucash.api.read.GnucashGenerInvoiceEntry;

public interface GnucashJobInvoiceEntry extends GnucashGenerInvoiceEntry 
{
  GCshID getInvoiceID();

  GnucashJobInvoice getInvoice() throws WrongInvoiceTypeException;
  
  // -----------------------------------------------------------------

  FixedPointNumber getPrice() throws WrongInvoiceTypeException;

  String getPriceFormatted() throws WrongInvoiceTypeException;
  
}
