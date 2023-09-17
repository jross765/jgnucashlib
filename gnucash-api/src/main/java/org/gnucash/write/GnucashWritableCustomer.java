package org.gnucash.write;

import org.gnucash.read.GnucashCustomer;
import org.gnucash.read.GnucashObject;
import org.gnucash.read.aux.Address;
import org.gnucash.write.aux.GnucashWritableAddress;

/**
 * Customer that can be modified
 */
public interface GnucashWritableCustomer extends GnucashCustomer, 
                                                 GnucashWritableObject 
{

	void remove();


	/**
	 * @see {@link GnucashCustomer#getNumber()}
	 * @param number the user-assigned number of this customer (may contain non-digits)
	 */
	void setNumber(String number);

	void setDiscount(String discount);

	/**
	 * @param notes user-defined notes about the customer (may be null)
	 */
	void setNotes(String notes);


	void setName(String name);

	void setAddress(Address adr);

	void setShippingAddress(Address adr);

	GnucashWritableAddress getWritableAddress();

	GnucashWritableAddress getWritableShippingAddress();

	GnucashWritableAddress getAddress();

	GnucashWritableAddress getShippingAddress();


	/**
	 * @param name the name of the user-defined attribute
	 * @param value the value or null if not set
	 * @see {@link GnucashObject#getUserDefinedAttribute(String)}
	 */
	void setUserDefinedAttribute(final String name, final String value);
}
