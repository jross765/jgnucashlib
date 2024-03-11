package org.gnucash.api.read.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.InputStream;
import java.util.Locale;

import org.gnucash.api.ConstTest;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.GnuCashGenerInvoiceEntry;
import org.gnucash.api.read.impl.aux.GCshFileStats;
import org.junit.Before;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

public class TestGnuCashGenerInvoiceEntryImpl {
	public static final GCshID INVCENTR_1_ID = new GCshID("513589a11391496cbb8d025fc1e87eaa");
	public static final GCshID INVCENTR_2_ID = new GCshID("0041b8d397f04ae4a2e9e3c7f991c4ec");
	public static final GCshID INVCENTR_3_ID = new GCshID("83e78ce224d94c3eafc55e33d3d5f3e6");

	// -----------------------------------------------------------------

	private GnuCashFileImpl gcshFile = null;
	private GCshFileStats gcshFileStats = null;
	private GnuCashGenerInvoiceEntry invcEntr = null;

	// -----------------------------------------------------------------

	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(suite());
	}

	@SuppressWarnings("exports")
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestGnuCashGenerInvoiceEntryImpl.class);
	}

	@Before
	public void initialize() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		// URL gcshFileURL = classLoader.getResource(Const.GCSH_FILENAME);
		// System.err.println("GnuCash test file resource: '" + gcshFileURL + "'");
		InputStream gcshFileStream = null;
		try {
			gcshFileStream = classLoader.getResourceAsStream(ConstTest.GCSH_FILENAME);
		} catch (Exception exc) {
			System.err.println("Cannot generate input stream from resource");
			return;
		}

		try {
			gcshFile = new GnuCashFileImpl(gcshFileStream);
		} catch (Exception exc) {
			System.err.println("Cannot parse GnuCash file");
			exc.printStackTrace();
		}

		gcshFileStats = new GCshFileStats(gcshFile);
	}

	// -----------------------------------------------------------------

	// redundant:
	//  @Test
	//  public void test01() throws Exception
	//  {
	//      assertEquals(ConstTest.NOF_INVC_ENTR, gcshFileStats.getNofEntriesGenerInvoiceEntries(GCshFileStats.Type.RAW));
	//      assertEquals(ConstTest.NOF_INVC_ENTR, gcshFileStats.getNofEntriesGenerInvoiceEntries(GCshFileStats.Type.COUNTER));
	//      assertEquals(ConstTest.NOF_INVC_ENTR, gcshFileStats.getNofEntriesGenerInvoiceEntries(GCshFileStats.Type.CACHE));
	//  }

	@Test
	public void test02_1() throws Exception {
		invcEntr = gcshFile.getGenerInvoiceEntryByID(INVCENTR_1_ID);
		assertNotEquals(null, invcEntr);

		assertEquals(INVCENTR_1_ID, invcEntr.getID());
		assertEquals(GnuCashGenerInvoice.TYPE_VENDOR, invcEntr.getType());
		assertEquals("286fc2651a7848038a23bb7d065c8b67", invcEntr.getGenerInvoiceID().toString());
		assertEquals(null, invcEntr.getAction());
		assertEquals("Item 1", invcEntr.getDescription());

		assertEquals(true, invcEntr.isVendBllTaxable());
		assertEquals(0.19, invcEntr.getVendBllApplicableTaxPercent().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(12.50, invcEntr.getVendBllPrice().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(3, invcEntr.getQuantity().intValue());
	}

	@Test
	public void test02_2() throws Exception {
		invcEntr = gcshFile.getGenerInvoiceEntryByID(INVCENTR_2_ID);
		assertNotEquals(null, invcEntr);

		assertEquals(INVCENTR_2_ID, invcEntr.getID());
		assertEquals(GnuCashGenerInvoice.TYPE_VENDOR, invcEntr.getType());
		assertEquals("4eb0dc387c3f4daba57b11b2a657d8a4", invcEntr.getGenerInvoiceID().toString());
		assertEquals(GnuCashGenerInvoiceEntry.Action.HOURS, invcEntr.getAction());
		assertEquals("Gefälligkeiten", invcEntr.getDescription());

		assertEquals(true, invcEntr.isVendBllTaxable());
		// Following: sic, because there is n o tax table entry assigned
		// (this is an error in real life, but we have done it on purpose here
		// for the tests).
		assertEquals(0.00, invcEntr.getVendBllApplicableTaxPercent().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(13.80, invcEntr.getVendBllPrice().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(3, invcEntr.getQuantity().intValue());
	}

	@Test
	public void test02_3() throws Exception {
		invcEntr = gcshFile.getGenerInvoiceEntryByID(INVCENTR_3_ID);
		assertNotEquals(null, invcEntr);

		assertEquals(INVCENTR_3_ID, invcEntr.getID());
		assertEquals(GnuCashGenerInvoice.TYPE_CUSTOMER, invcEntr.getType());
		assertEquals("6588f1757b9e4e24b62ad5b37b8d8e07", invcEntr.getGenerInvoiceID().toString());
		assertEquals(GnuCashGenerInvoiceEntry.Action.MATERIAL, invcEntr.getAction());
		assertEquals("Posten 3", invcEntr.getDescription());

		assertEquals(true, invcEntr.isCustInvcTaxable());
		assertEquals(0.19, invcEntr.getCustInvcApplicableTaxPercent().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(120.00, invcEntr.getCustInvcPrice().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(10, invcEntr.getQuantity().intValue());
	}

	@Test
	public void test03() throws Exception {
		// Works only in German locale:
		// assertEquals("Material",
		// GnuCashGenerInvoiceEntry.Action.MATERIAL.getLocaleString());

		assertEquals("Hours", GnuCashGenerInvoiceEntry.Action.HOURS.getLocaleString(Locale.ENGLISH));
		assertEquals("Stunden", GnuCashGenerInvoiceEntry.Action.HOURS.getLocaleString(Locale.GERMAN));
		assertEquals("Heures", GnuCashGenerInvoiceEntry.Action.HOURS.getLocaleString(Locale.FRENCH));
		// Locale.SPANISH does not exist (funny...)
		// assertEquals("Horas",
		// GnuCashGenerInvoiceEntry.Action.HOURS.getLocaleString(Locale.SPANISH));

		assertEquals("Hours", GnuCashGenerInvoiceEntry.Action.HOURS.getLocaleString(Locale.forLanguageTag("EN")));
		assertEquals("Stunden", GnuCashGenerInvoiceEntry.Action.HOURS.getLocaleString(Locale.forLanguageTag("DE")));
		assertEquals("Heures", GnuCashGenerInvoiceEntry.Action.HOURS.getLocaleString(Locale.forLanguageTag("FR")));
		assertEquals("Horas", GnuCashGenerInvoiceEntry.Action.HOURS.getLocaleString(Locale.forLanguageTag("ES")));
	}

}