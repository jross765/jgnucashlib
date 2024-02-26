package org.gnucash.api.read;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.gnucash.base.basetypes.complex.GCshCmdtyCurrID;
import org.gnucash.base.basetypes.complex.InvalidCmdtyCurrIDException;
import org.gnucash.base.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.numbers.FixedPointNumber;
import org.gnucash.api.generated.GncTransaction;
import org.gnucash.api.read.hlp.GnucashObject;
import org.gnucash.api.read.hlp.HasAttachment;
import org.gnucash.api.read.hlp.HasUserDefinedAttributes;

/**
 * A financial transaction between two or more accounts.
 * <br>
 * A transaction has two or more transaction splits ({@link GnucashTransactionSplit})
 * whose values normally add up to zero.
 * <br>
 * Cf. <a href="https://gnucash.org/docs/v5/C/gnucash-manual//ch_Common_Trans_Ops.html">GnuCash manual</a>
 * 
 * @see GnucashTransactionSplit
 */
public interface GnucashTransaction extends Comparable<GnucashTransaction>,
                                            GnucashObject,
                                            HasAttachment,
                                            HasUserDefinedAttributes
{

    // For the following types cf.:
    // https://github.com/Gnucash/gnucash/blob/stable/libgnucash/engine/Transaction.h
    public enum Type {

	// ::MAGIC
	NONE    ( "" ), 
	INVOICE ( "I" ), 
	PAYMENT ( "P" ), 
	LINK    ( "L" );

	// ---

	// Note: In theory, the code should be a char, not a String.
	// However, if we use a char, we would have to convert it to a String
	// anyway when actually using this Type (or else, we have weird
	// errors writing the GnuCash file).
	private String code = "X";

	// ---

	Type(String code) {
	    this.code = code;
	}

	// ---

	public String getCode() {
	    return code;
	}

	// no typo!
	public static Type valueOff(String code) {
	    for (Type type : values()) {
		if (type.getCode().equals(code)) {
		    return type;
		}
	    }

	    return null;
	}
    }

    // -----------------------------------------------------------------

    /**
     *
     * @return the unique-id to identify this object with across name- and hirarchy-changes
     */
    GCshID getID();

    /**
     * @return the user-defined description for this object (may contain multiple lines and non-ascii-characters)
     */
    String getDescription();
    
    /**
     * 
     * @return the transaction-number.
     */
    String getTransactionNumber();

    // ----------------------------

    @SuppressWarnings("exports")
    GncTransaction getJwsdpPeer();

    // ----------------------------

    /**
     * Do not modify the returned collection!
     * @return all splits of this transaction.
     *  
     */
    List<GnucashTransactionSplit> getSplits();

    /**
     * Get a split of this transaction it's id.
     * @param id the id to look for
     * @return null if not found
     *  
     */
    GnucashTransactionSplit getSplitByID(GCshID id) throws IllegalArgumentException;

    /**
     *
     * @return the first split of this transaction or null.
     * @throws SplitNotFoundException 
     *  
     */
    GnucashTransactionSplit getFirstSplit() throws SplitNotFoundException;

    /**
     * @return the second split of this transaction or null.
     * @throws SplitNotFoundException 
     *  
     */
    GnucashTransactionSplit getSecondSplit() throws SplitNotFoundException;

    /**
     *
     * @return the number of splits in this transaction.
     *  
     */
    int getSplitsCount();

    /**
     *
     * @return the date the transaction was entered into the system
     */
    ZonedDateTime getDateEntered();

    /**
     *
     * @return the date the transaction happened
     */
    ZonedDateTime getDatePosted();

    /**
     *
     * @return date the transaction happened
     */
    String getDatePostedFormatted();

    /**
     *
     * @return true if the sum of all splits adds up to zero.
     *  
     */
    boolean isBalanced() throws IllegalArgumentException;

    GCshCmdtyCurrID getCmdtyCurrID() throws InvalidCmdtyCurrTypeException;

    /**
     * The result is in the currency of the transaction.<br/>
     * if the transaction is unbalanced, get sum of all split-values.
     * @return the sum of all splits
     *  
     * @see #isBalanced()
     */
    FixedPointNumber getBalance() throws IllegalArgumentException;
    /**
     * The result is in the currency of the transaction.
     * @return 
     * @throws InvalidCmdtyCurrIDException 
     * @throws InvalidCmdtyCurrTypeException 
     *  
     * @see GnucashTransaction#getBalance()
     */
    String getBalanceFormatted() throws InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException;
    
    /**
     * The result is in the currency of the transaction.
     * @return 
     * @throws InvalidCmdtyCurrIDException 
     * @throws InvalidCmdtyCurrTypeException 
     *  
     * @see GnucashTransaction#getBalance()
     */
    String getBalanceFormatted(Locale lcl) throws InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException;

    /**
     * The result is in the currency of the transaction.<br/>
     * if the transaction is unbalanced, get the missing split-value to balance it.
     * @return the sum of all splits
     *  
     * @see #isBalanced()
     */
    FixedPointNumber getNegatedBalance() throws IllegalArgumentException;
    
    /**
     * The result is in the currency of the transaction.
     * @return 
     * @throws InvalidCmdtyCurrIDException 
     * @throws InvalidCmdtyCurrTypeException 
     * @throws NumberFormatException 
     *  
     * @see GnucashTransaction#getNegatedBalance()
     */
    String getNegatedBalanceFormatted() throws NumberFormatException, InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException;
    
    /**
     * The result is in the currency of the transaction.
     * @return 
     * @throws InvalidCmdtyCurrIDException 
     * @throws InvalidCmdtyCurrTypeException 
     * @throws NumberFormatException 
     *  
     * @see GnucashTransaction#getNegatedBalance()
     */
    String getNegatedBalanceFormatted(Locale lcl) throws NumberFormatException, InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException;

}
