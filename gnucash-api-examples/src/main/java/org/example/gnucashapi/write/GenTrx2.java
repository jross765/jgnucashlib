package org.example.gnucashapi.write;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.gnucash.api.basetypes.complex.GCshCurrID;
import org.gnucash.api.numbers.FixedPointNumber;
import org.gnucash.api.read.GnucashAccount;
import org.gnucash.api.read.GnucashTransaction;
import org.gnucash.api.read.GnucashTransactionSplit;
import org.gnucash.api.read.NoEntryFoundException;
import org.gnucash.api.read.TooManyEntriesFoundException;
import org.gnucash.api.write.GnucashWritableTransaction;
import org.gnucash.api.write.GnucashWritableTransactionSplit;
import org.gnucash.api.write.impl.GnucashWritableFileImpl;

/**
 * Created by Deniss Larka
 */
public class GenTrx2 {
    // BEGIN Example data -- adapt to your needs
    private static String gcshInFileName  = "example_in.gnucash";
    private static String gcshOutFileName = "example_out.gnucash";
    private static String accountName     = "Root Account::Erträge::Honorar";
    // END Example data

    // -----------------------------------------------------------------

    public static void main(String[] args) throws IOException {
	try {
	    GenTrx2 tool = new GenTrx2();
	    tool.kernel();
	} catch (Exception exc) {
	    System.err.println("Execution exception. Aborting.");
	    exc.printStackTrace();
	    System.exit(1);
	}
    }

    private void kernel() throws Exception {
	GnucashWritableFileImpl gnucashFile = new GnucashWritableFileImpl(new File(gcshInFileName));
	Collection<GnucashAccount> accounts = gnucashFile.getAccounts();
	for (GnucashAccount account : accounts) {
	    System.out.println(account.getQualifiedName());
	}

	GnucashWritableTransaction writableTransaction = gnucashFile.createWritableTransaction();
	writableTransaction.setDescription("check");
	writableTransaction.setCmdtyCurrID(new GCshCurrID("EUR"));
	writableTransaction.setDateEntered(LocalDateTime.now());

	GnucashAccount acct = null;
	try {
	    acct = gnucashFile.getAccountByNameUniq(accountName, true);
	} catch ( NoEntryFoundException exc ) {
	    System.err.println("Found no account with ");
	    System.exit(1);
	} catch ( TooManyEntriesFoundException exc ) {
	    System.err.println("Found several accounts with that name");
	    System.exit(1);
	}
	
	GnucashWritableTransactionSplit writingSplit = writableTransaction.createWritableSplit(acct);
	writingSplit.setValue(new FixedPointNumber(100));
	writingSplit.setDescription("Generated by GenTrx2 " + LocalDateTime.now().toString());

	Collection<? extends GnucashTransaction> transactions = gnucashFile.getTransactions();
	for (GnucashTransaction transaction : transactions) {
	    System.out.println(transaction.getDatePosted());
	    List<GnucashTransactionSplit> splits = transaction.getSplits();
	    for (GnucashTransactionSplit split : splits) {
		System.out.println("\t" + split.getQuantity());
	    }
	}

	// Caution: output file will always be in uncompressed XML format,
	// regardless of whether the input file was compressed or not.
	gnucashFile.writeFile(new File(gcshOutFileName));
	System.out.println("OK");
    }
}
