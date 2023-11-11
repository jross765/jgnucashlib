package org.gnucash.read.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.TreeSet;

import org.gnucash.ConstTest;
import org.gnucash.read.GnucashFile;
import org.gnucash.read.GnucashGenerInvoice;
import org.gnucash.read.GnucashGenerInvoiceEntry;
import org.gnucash.read.GnucashTransaction;
import org.gnucash.read.aux.GCshOwner;
import org.junit.Before;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

public class TestGnucashGenerInvoiceImpl
{
  private GnucashFile         gcshFile = null;
  private GnucashGenerInvoice invc     = null;
  
  public static final String INVC_1_ID = "d9967c10fdf1465e9394a3e4b1e7bd79";
  public static final String INVC_2_ID = "286fc2651a7848038a23bb7d065c8b67";
  public static final String INVC_3_ID = "b1e981f796b94ca0b17a9dccb91fedc0";
  public static final String INVC_4_ID = "4eb0dc387c3f4daba57b11b2a657d8a4";
  public static final String INVC_5_ID = "169331c9860642cf84b04f3e3151058a";
  public static final String INVC_6_ID = "6588f1757b9e4e24b62ad5b37b8d8e07";

  // -----------------------------------------------------------------
  
  public static void main(String[] args) throws Exception
  {
    junit.textui.TestRunner.run(suite());
  }

  @SuppressWarnings("exports")
  public static junit.framework.Test suite() 
  {
    return new JUnit4TestAdapter(TestGnucashGenerInvoiceImpl.class);  
  }
  
  @Before
  public void initialize() throws Exception
  {
    ClassLoader classLoader = getClass().getClassLoader();
    // URL gcshFileURL = classLoader.getResource(Const.GCSH_FILENAME);
    // System.err.println("GnuCash test file resource: '" + gcshFileURL + "'");
    InputStream gcshFileStream = null;
    try 
    {
      gcshFileStream = classLoader.getResourceAsStream(ConstTest.GCSH_FILENAME);
    } 
    catch ( Exception exc ) 
    {
      System.err.println("Cannot generate input stream from resource");
      return;
    }
    
    try
    {
      gcshFile = new GnucashFileImpl(gcshFileStream);
    }
    catch ( Exception exc )
    {
      System.err.println("Cannot parse GnuCash file");
      exc.printStackTrace();
    }
  }

  // -----------------------------------------------------------------

  @Test
  public void test01() throws Exception
  {
    assertEquals(6, gcshFile.getNofEntriesGenerInvoiceMap());
  }

  // -----------------------------------------------------------------

  @Test
  public void testCust01_1() throws Exception
  {
    invc = gcshFile.getGenerInvoiceByID(INVC_1_ID);
    assertNotEquals(null, invc);
    
    assertEquals(INVC_1_ID, invc.getId());
    assertEquals(GCshOwner.Type.CUSTOMER, invc.getOwnerType(GnucashGenerInvoice.ReadVariant.DIRECT));
    assertEquals("R1730", invc.getNumber());
    assertEquals("Alles ohne Steuern / voll bezahlt", invc.getDescription());

    assertEquals("2023-07-29T10:59Z", invc.getDateOpened().toString());
    assertEquals("2023-07-29T10:59Z", invc.getDatePosted().toString());
  }

  @Test
  public void testCust02_1() throws Exception
  {
      invc = gcshFile.getGenerInvoiceByID(INVC_1_ID);
      assertNotEquals(null, invc);

    assertEquals(2, invc.getGenerEntries().size());

    TreeSet entrList = new TreeSet(); // sort elements of HashSet
    entrList.addAll(invc.getGenerEntries());
    assertEquals("92e54c04b66f4682a9afb48e27dfe397", 
                 ((GnucashGenerInvoiceEntry) entrList.toArray()[0]).getId());
    assertEquals("3c67a99b5fe34387b596bb1fbab21a74", 
                 ((GnucashGenerInvoiceEntry) entrList.toArray()[1]).getId());
  }

  @Test
  public void testCust03_1() throws Exception
  {
      invc = gcshFile.getGenerInvoiceByID(INVC_1_ID);
      assertNotEquals(null, invc);

    assertEquals(1327.60, invc.getInvcAmountWithoutTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
    
    assertEquals(1327.60, invc.getInvcAmountWithTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
  }

  @Test
  public void testCust04_1() throws Exception
  {
      invc = gcshFile.getGenerInvoiceByID(INVC_1_ID);
      assertNotEquals(null, invc);

    assertEquals("c97032ba41684b2bb5d1391c9d7547e9", invc.getPostTransaction().getId());
    assertEquals(1, invc.getPayingTransactions().size());

    LinkedList<GnucashTransaction> trxList = (LinkedList<GnucashTransaction>) invc.getPayingTransactions();
    Collections.sort(trxList);
    assertEquals("29557cfdf4594eb68b1a1b710722f991", 
                 ((GnucashTransaction) trxList.toArray()[0]).getId());

    assertEquals(true, invc.isInvcFullyPaid());
  }

  // -----------------------------------------------------------------

  @Test
  public void testVend01_1() throws Exception
  {
    invc = gcshFile.getGenerInvoiceByID(INVC_4_ID);
    assertNotEquals(null, invc);
    
    assertEquals(INVC_4_ID, invc.getId());
    assertEquals(GCshOwner.Type.VENDOR, invc.getOwnerType(GnucashGenerInvoice.ReadVariant.DIRECT));
    assertEquals("1730-383/2", invc.getNumber());
    assertEquals("Sie wissen schon: Gefälligkeiten, ne?", invc.getDescription());

    assertEquals("2023-08-31T10:59Z", invc.getDateOpened().toString());
    // ::TODO
    assertEquals("2023-08-31T10:59Z", invc.getDatePosted().toString());
  }

  @Test
  public void testVend01_2() throws Exception
  {
    invc = gcshFile.getGenerInvoiceByID(INVC_2_ID);
    assertNotEquals(null, invc);
    
    assertEquals(INVC_2_ID, invc.getId());
    assertEquals(GCshOwner.Type.VENDOR, invc.getOwnerType(GnucashGenerInvoice.ReadVariant.DIRECT));
    assertEquals("2740921", invc.getNumber());
    assertEquals("Dat isjamaol eine schöne jepflejgte Reschnung!", invc.getDescription());

    assertEquals("2023-08-30T10:59Z", invc.getDateOpened().toString());
    // ::TODO
    assertEquals("2023-08-30T10:59Z", invc.getDatePosted().toString());
  }

  @Test
  public void testVend02_1() throws Exception
  {
    invc = gcshFile.getGenerInvoiceByID(INVC_4_ID);
    assertNotEquals(null, invc);

    assertEquals(1, invc.getGenerEntries().size());

    TreeSet entrList = new TreeSet(); // sort elements of HashSet
    entrList.addAll(invc.getGenerEntries());
    assertEquals("0041b8d397f04ae4a2e9e3c7f991c4ec", 
                 ((GnucashGenerInvoiceEntry) entrList.toArray()[0]).getId());
  }

  @Test
  public void testVend02_2() throws Exception
  {
    invc = gcshFile.getGenerInvoiceByID(INVC_2_ID);
    assertNotEquals(null, invc);

    assertEquals(2, invc.getGenerEntries().size());

    TreeSet entrList = new TreeSet(); // sort elements of HashSet
    entrList.addAll(invc.getGenerEntries());
    assertEquals("513589a11391496cbb8d025fc1e87eaa", 
                 ((GnucashGenerInvoiceEntry) entrList.toArray()[1]).getId());
    assertEquals("dc3c53f07ff64199ad4ea38988b3f40a", 
                 ((GnucashGenerInvoiceEntry) entrList.toArray()[0]).getId());
  }

  @Test
  public void testVend03_1() throws Exception
  {
    invc = gcshFile.getGenerInvoiceByID(INVC_4_ID);
    assertNotEquals(null, invc);

    assertEquals(41.40, invc.getBillAmountWithoutTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
    // Note: due to (purposefully) incorrect booking, the gross amount
    // of this bill is *not* 49.27 EUR, but 41.40 EUR (its net amount).
    assertEquals(41.40, invc.getBillAmountWithTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
  }

  @Test
  public void testVend03_2() throws Exception
  {
    invc = gcshFile.getGenerInvoiceByID(INVC_2_ID);
    assertNotEquals(null, invc);

    assertEquals(79.11, invc.getBillAmountWithoutTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
    assertEquals(94.14, invc.getBillAmountWithTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
  }

  @Test
  public void testVend04_1() throws Exception
  {
    invc = gcshFile.getGenerInvoiceByID(INVC_4_ID);
    assertNotEquals(null, invc);

//    assertEquals("xxx", invc.getPostTransaction());
    
    // ::TODO
    assertEquals(0, invc.getPayingTransactions().size());
    
//    LinkedList<GnucashTransaction> trxList = (LinkedList<GnucashTransaction>) bllSpec.getPayingTransactions();
//    Collections.sort(trxList);
//    assertEquals("xxx", 
//                 ((GnucashTransaction) bllSpec.getPayingTransactions().toArray()[0]).getId());

    assertEquals(false, invc.isBillFullyPaid());
  }

  @Test
  public void testVend04_2() throws Exception
  {
    invc = gcshFile.getGenerInvoiceByID(INVC_2_ID);
    assertNotEquals(null, invc);

    assertEquals("aa64d862bb5e4d749eb41f198b28d73d", invc.getPostTransaction().getId());   
    assertEquals(1, invc.getPayingTransactions().size());
    
    LinkedList<GnucashTransaction> trxList = (LinkedList<GnucashTransaction>) invc.getPayingTransactions();
    Collections.sort(trxList);
    assertEquals("ccff780b18294435bf03c6cb1ac325c1", 
                 ((GnucashTransaction) trxList.toArray()[0]).getId());
    
    assertEquals(true, invc.isBillFullyPaid());
  }

  @Test
  public void test06_1() throws Exception {
      invc = gcshFile.getGenerInvoiceByID(INVC_4_ID);
      assertNotEquals(null, invc);
      assertEquals("https://my.vendor.bill.link.01", invc.getURL());
  }

  @Test
  public void test06_2() throws Exception {
      invc = gcshFile.getGenerInvoiceByID(INVC_5_ID);
      assertNotEquals(null, invc);
      assertEquals("https://my.job.invoice.link.01", invc.getURL());
  }
  
  @Test
  public void test06_3() throws Exception {
      invc = gcshFile.getGenerInvoiceByID(INVC_6_ID);
      assertNotEquals(null, invc);
      assertEquals("https://my.customer.invoice.link.01", invc.getURL());
  }
}
