package org.gnucash.api.write.impl;

import java.beans.PropertyChangeSupport;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.gnucash.api.Const;
import org.gnucash.api.basetypes.complex.GCshCmdtyCurrID;
import org.gnucash.api.basetypes.complex.GCshCmdtyCurrNameSpace;
import org.gnucash.api.basetypes.complex.GCshCmdtyID;
import org.gnucash.api.basetypes.complex.GCshCurrID;
import org.gnucash.api.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.generated.GncV2;
import org.gnucash.api.generated.ObjectFactory;
import org.gnucash.api.numbers.FixedPointNumber;
import org.gnucash.api.read.GnucashCommodity;
import org.gnucash.api.read.impl.GnucashPriceImpl;
import org.gnucash.api.write.GnucashWritablePrice;
import org.gnucash.api.write.GnucashWritableFile;
import org.gnucash.api.write.GnucashWritableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extension of GCshPriceImpl to allow read-write access instead of
 * read-only access.
 */
public class GnucashWritablePriceImpl extends GnucashPriceImpl 
                                      implements GnucashWritablePrice 
{
    private static final Logger LOGGER = LoggerFactory.getLogger(GnucashWritablePriceImpl.class);

    // ---------------------------------------------------------------

    /**
     * Our helper to implement the GnucashWritableObject-interface.
     */
    private final GnucashWritableObjectImpl helper = new GnucashWritableObjectImpl(this);

    // ---------------------------------------------------------------

    @SuppressWarnings("exports")
    public GnucashWritablePriceImpl(
	    final GncV2.GncBook.GncPricedb.Price jwsdpPeer,
	    final GnucashWritableFile file) {
	super(jwsdpPeer, file);
    }

    public GnucashWritablePriceImpl(final GnucashWritableFileImpl file) {
	super(createPrice(file, GCshID.getNew()), file);
    }

    public GnucashWritablePriceImpl(GnucashPriceImpl prc) {
	super(prc.getJwsdpPeer(), prc.getGnucashFile());
    }

    // ---------------------------------------------------------------

    /**
     * The gnucash-file is the top-level class to contain everything.
     *
     * @return the file we are associated with
     */
    @Override
    public GnucashWritableFileImpl getWritableGnucashFile() {
	return (GnucashWritableFileImpl) super.getGnucashFile();
    }

    /**
     * The gnucash-file is the top-level class to contain everything.
     *
     * @return the file we are associated with
     */
    @Override
    public GnucashWritableFileImpl getGnucashFile() {
	return (GnucashWritableFileImpl) super.getGnucashFile();
    }

    // ---------------------------------------------------------------
    
    // ::TODO
//  public GCshWritablePrice createWritablePrice(final GCshPrice prc) {
//	GCshWritablePriceImpl splt = new GCshWritablePriceImpl(this, prc);
//	addPrice(splt);
//	return splt;
//  }

    private static GncV2.GncBook.GncPricedb.Price createPrice(
	    final GnucashWritableFileImpl file, 
	    final GCshID prcID) {
	
        ObjectFactory factory = file.getObjectFactory();
        
        GncV2.GncBook.GncPricedb.Price prc = file.createGncGncPricedbPriceType();
    
        {
            GncV2.GncBook.GncPricedb.Price.PriceId gncPrcID = factory.createGncV2GncBookGncPricedbPricePriceId();
            gncPrcID.setType(Const.XML_DATA_TYPE_GUID);
            gncPrcID.setValue(prcID.toString());
            prc.setPriceId(gncPrcID);
        }
        
        {
            GncV2.GncBook.GncPricedb.Price.PriceCommodity cmdty = factory.createGncV2GncBookGncPricedbPricePriceCommodity();
            cmdty.setCmdtySpace("xxx");
            cmdty.setCmdtyId("yyy");
            prc.setPriceCommodity(cmdty);
        }
    
        {
            GncV2.GncBook.GncPricedb.Price.PriceCurrency curr = factory.createGncV2GncBookGncPricedbPricePriceCurrency();
            curr.setCmdtySpace(GCshCmdtyCurrNameSpace.CURRENCY);
            curr.setCmdtyId(file.getDefaultCurrencyID());
            prc.setPriceCurrency(curr);
        }
        
        {
            GncV2.GncBook.GncPricedb.Price.PriceTime prcTim = factory.createGncV2GncBookGncPricedbPricePriceTime();
            LocalDate tsDate = LocalDate.now(); // ::TODO
            prcTim.setTsDate(tsDate.toString());
            prc.setPriceTime(prcTim);
        }
        
        prc.setPriceType(Type.LAST.getCode());
        prc.setPriceSource(Source.USER_PRICE.getCode());
        prc.setPriceValue("1");
        
        // file.getRootElement().getGncBook().getBookElements().add(prc);
	GncV2.GncBook.GncPricedb priceDB = file.getPrcMgr().getPriceDB();
	priceDB.getPrice().add(prc);
        file.setModified(true);
    
        return prc;
    }

    // ---------------------------------------------------------------

    @Override
    public void setFromCmdtyCurrQualifID(GCshCmdtyCurrID qualifID) {
	jwsdpPeer.getPriceCommodity().setCmdtySpace(qualifID.getNameSpace());
	jwsdpPeer.getPriceCommodity().setCmdtyId(qualifID.getCode());
	getWritableGnucashFile().setModified(true);
    }

    @Override
    public void setFromCommodityQualifID(GCshCmdtyID qualifID) {
	jwsdpPeer.getPriceCommodity().setCmdtySpace(qualifID.getNameSpace());
	jwsdpPeer.getPriceCommodity().setCmdtyId(qualifID.getCode());
	getWritableGnucashFile().setModified(true);
    }

    @Override
    public void setFromCurrencyQualifID(GCshCurrID qualifID) {
	jwsdpPeer.getPriceCommodity().setCmdtySpace(qualifID.getNameSpace());
	jwsdpPeer.getPriceCommodity().setCmdtyId(qualifID.getCode());
	getWritableGnucashFile().setModified(true);
    }

    @Override
    public void setFromCommodity(GnucashCommodity cmdty) {
	setFromCmdtyCurrQualifID(cmdty.getQualifID());
    }

    @Override
    public void setFromCurrencyCode(String code) {
	setFromCurrencyQualifID(new GCshCurrID(code));
    }

    @Override
    public void setFromCurrency(GnucashCommodity curr) {
	setFromCommodity(curr);	
    }
    
    // ----------------------------

    @Override
    public void setToCurrencyQualifID(GCshCmdtyCurrID qualifID) {
	if ( ! qualifID.getNameSpace().equals(GCshCmdtyCurrNameSpace.CURRENCY) )
	    throw new InvalidCmdtyCurrTypeException("Is not a currency: " + qualifID.toString());
	
	jwsdpPeer.getPriceCurrency().setCmdtySpace(qualifID.getNameSpace());
	jwsdpPeer.getPriceCurrency().setCmdtyId(qualifID.getCode());
	getWritableGnucashFile().setModified(true);
    }

    @Override
    public void setToCurrencyQualifID(GCshCurrID qualifID) {
	jwsdpPeer.getPriceCurrency().setCmdtySpace(qualifID.getNameSpace());
	jwsdpPeer.getPriceCurrency().setCmdtyId(qualifID.getCode());
	getWritableGnucashFile().setModified(true);
    }

    @Override
    public void setToCurrencyCode(String code) {
	setToCurrencyQualifID(new GCshCurrID(code));
    }

    @Override
    public void setToCurrency(GnucashCommodity curr) {
	setToCurrencyQualifID(curr.getQualifID());
    }
    
    // ----------------------------

    @Override
    public void setDate(LocalDate date) {
	LocalDate oldDate = getDate();
	this.dateTime = ZonedDateTime.of(date, LocalTime.MIN, ZoneId.systemDefault());
	String datePostedStr = this.dateTime.format(DATE_FORMAT);
	jwsdpPeer.getPriceTime().setTsDate(datePostedStr);
	getWritableGnucashFile().setModified(true);

	PropertyChangeSupport propertyChangeSupport = getPropertyChangeSupport();
	if (propertyChangeSupport != null) {
	    propertyChangeSupport.firePropertyChange("price", oldDate, date);
	}
    }

    @Override
    public void setDateTime(LocalDateTime dateTime) {
	LocalDate oldDate = getDate();
	this.dateTime = ZonedDateTime.of(dateTime, ZoneId.systemDefault());
	String datePostedStr = this.dateTime.format(DATE_FORMAT);
	jwsdpPeer.getPriceTime().setTsDate(datePostedStr);
	getWritableGnucashFile().setModified(true);

	PropertyChangeSupport propertyChangeSupport = getPropertyChangeSupport();
	if (propertyChangeSupport != null) {
	    propertyChangeSupport.firePropertyChange("price", oldDate, dateTime);
	}
    }

    @Override
    public void setSource(Source src) {
	setSourceStr(src.getCode());
    }

    public void setSourceStr(String str) {
	jwsdpPeer.setPriceSource(str);
	getWritableGnucashFile().setModified(true);
    }

    @Override
    public void setType(Type type) {
	setTypeStr(type.getCode());
    }

    public void setTypeStr(String typeStr) {
	jwsdpPeer.setPriceType(typeStr);
	getWritableGnucashFile().setModified(true);
    }

    @Override
    public void setValue(FixedPointNumber val) {
	jwsdpPeer.setPriceValue(val.toGnucashString());
	getWritableGnucashFile().setModified(true);
    }

    // ---------------------------------------------------------------

    /**
     * @see GnucashWritableObject#setUserDefinedAttribute(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public void setUserDefinedAttribute(final String name, final String value) {
	helper.setUserDefinedAttribute(name, value);
    }

    // ---------------------------------------------------------------
    
    @Override
    public String toString() {
	String result = "GCshWritablePriceImpl [";
	
	result += "id=" + getID();
	
	try {
	    result += ", cmdty-qualif-id='" + getFromCmdtyCurrQualifID() + "'";
	} catch (InvalidCmdtyCurrTypeException e) {
	    result += ", cmdty-qualif-id=" + "ERROR";
	}
	
	try {
	    result += ", curr-qualif-id='" + getToCurrencyQualifID() + "'";
	} catch (Exception e) {
	    result += ", curr-qualif-id=" + "ERROR";
	}
	
	result += ", date=" + getDate(); 
	
	try {
	    result += ", value=" + getValueFormatted();
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    result += ", value=" + "ERROR";
	}
	
	result += ", type=" + getType();
	result += ", source=" + getSource(); 

	result += "]"; 

	return result;
    }
    
}