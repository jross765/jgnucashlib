package org.gnucash.api.read;

import java.lang.reflect.Field;
import java.time.ZonedDateTime;
import java.util.Locale;

import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.generated.GncGncEntry;
import org.gnucash.api.numbers.FixedPointNumber;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.api.read.hlp.GnucashGenerInvoiceEntry_Cust;
import org.gnucash.api.read.hlp.GnucashGenerInvoiceEntry_Empl;
import org.gnucash.api.read.hlp.GnucashGenerInvoiceEntry_Job;
import org.gnucash.api.read.hlp.GnucashGenerInvoiceEntry_Vend;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;

/**
 * Entry-Line in an invoice stating one position
 * with it's name, single-unit-price and count.
 */
public interface GnucashGenerInvoiceEntry extends Comparable<GnucashGenerInvoiceEntry>,
                                                  GnucashGenerInvoiceEntry_Cust,
                                                  GnucashGenerInvoiceEntry_Vend,
                                                  GnucashGenerInvoiceEntry_Empl,
                                                  GnucashGenerInvoiceEntry_Job
{

    // For the following enumerations cf.:
    // https://github.com/Gnucash/gnucash/blob/stable/libgnucash/engine/gncEntry.h  
    public enum Action {
      
	// ::MAGIC (actually kind of "half-magic")
	JOB      ("INVC_ENTR_ACTION_JOB"),
	MATERIAL ("INVC_ENTR_ACTION_MATERIAL"),
	HOURS    ("INVC_ENTR_ACTION_HOURS");
      
	// ---

	private String code = "UNSET";
	
	// ---
	
	Action(String code) {
	    this.code = code;
	}

	// ---
	
	public String getCode() {
	    return code;
	}
	
	public String getLocaleString() throws IllegalArgumentException {
	    return getLocaleString(Locale.getDefault());
	}

	public String getLocaleString(Locale lcl) throws IllegalArgumentException {
	    try {
		Class<?> cls = Class.forName("org.gnucash.api.Const_" + lcl.getLanguage().toUpperCase());
		Field fld = cls.getDeclaredField(code);
		return (String) fld.get(null);
	    } catch ( Exception exc ) {
		throw new MappingException("Could not map string '" + code + "' to locale-specific string");
	    }
	}
		
	// no typo!
	public static Action valueOff(String code) throws IllegalArgumentException {
	    for ( Action val : values() ) {
		if ( val.getLocaleString().equals(code) ) {
		    return val;
		}
	    }

	    return null;
	}
    }
  
  // Not yet, for future releases:
//  public static final String ENTRY_DATE          = "date";
//  public static final String ENTRY_DATE_ENTERED  = "date-entered";
//  public static final String ENTRY_DESC          = "desc";
//  public static final String ENTRY_ACTION        = "action";
//  public static final String ENTRY_NOTES         = "notes";
//  public static final String ENTRY_QTY           = "qty";
//
//  public static final String ENTRY_IPRICE        = "iprice";
//  public static final String ENTRY_IACCT         = "invoice-account";
//  public static final String ENTRY_BACCT         = "bill-account";
//  public static final String ENTRY_BPRICE        = "bprice";
//  public static final String ENTRY_BILLABLE      = "billable?";
//  public static final String ENTRY_BILLTO        = "bill-to";
//
//  public static final String ENTRY_ORDER         = "order";
//  public static final String ENTRY_INVOICE       = "invoice";
//  public static final String ENTRY_BILL          = "bill";
//
//  public static final String ENTRY_INV_DISC_TYPE = "discount-type";
//  public static final String ENTRY_INV_DISC_HOW  = "discount-method";
//
//  public static final String ENTRY_INV_TAXABLE   = "invoice-taxable";
//  public static final String ENTRY_BILL_TAXABLE  = "bill-taxable";
//  public static final String ENTRY_INV_TAX_INC   = "invoice-tax-included";
//  public static final String ENTRY_BILL_TAX_INC  = "bill-tax-included";
//  public static final String ENTRY_INV_DISCOUNT  = "invoice-discount";
//  public static final String ENTRY_BILL_PAY_TYPE = "bill-payment-type";
  
  // -----------------------------------------------------------------

  /**
   * @return the unique-id to identify this object with across name- and
   *         hirarchy-changes
   */
  GCshID getID();

  /**
   * @return the type of the customer/vendor invoice entry, i.e. the owner type of
   *         the entry's invoice
   * @throws WrongInvoiceTypeException
   */
  GCshOwner.Type getType() throws WrongInvoiceTypeException;

  /**
   *
   * @return the unique-id of the invoice we belong to to
   * @see GnucashGenerInvoice#getID()
   */
  GCshID getGenerInvoiceID();

  /**
   * @return the invoice this entry belongs to
   */
  GnucashGenerInvoice getGenerInvoice();

  // ---------------------------------------------------------------

  /**
   * The returned text is saved locale-specific. E.g. "Stunden" instead of "hours"
   * for Germany.
   * 
   * @return HOURS or ITEMS, ....
   * @throws IllegalArgumentException
   * @throws SecurityException
   */
  Action getAction() throws IllegalArgumentException;

  /**
   * @return the number of items of price ${@link #getCustInvcPrice()} and type
   *         ${@link #getAction()}.
   */
  FixedPointNumber getQuantity();

  /**
   * @return the number of items of price ${@link #getCustInvcPrice()} and type
   *         ${@link #getAction()}.
   */
  String getQuantityFormatted();

  /**
   * @return the user-defined date
   */
  ZonedDateTime getDate();

  /**
   * @return the user-defined date
   */
  String getDateFormatted();

  /**
   * @return the user-defined description for this object (may contain multiple
   *         lines and non-ascii-characters)
   */
  String getDescription();

  // ---------------------------------------------------------------

  @SuppressWarnings("exports")
  GncGncEntry getJwsdpPeer();
}
