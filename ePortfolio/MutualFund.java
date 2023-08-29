package ePortfolio;

/**
 * An extension of Investment that can be
 * bought, sold, updated and searched for
 * in a Portfolio.
 *
 * Includes a 'redemption' fee of $45 when sold.
 */
public class MutualFund extends Investment
{
    /**
     * Constructor for MutualFund.
     *
     * @param symbol    The symbol of the MutualFund.
     * @param name      The name of the MutualFund.
     * @param quantity  The quantity of the MutualFund.
     * @param price     The price of the MutualFund.
     * @param bookValue The computed book value of the MutualFund.
     */
    MutualFund(String symbol, String name, int quantity, float price, float bookValue)
    {
        super(symbol, name, quantity, price, bookValue);
    }

    /**
     * Compares the MutualFund with another Object
     * for absolute equality.
     * @param o The object to compare.
     * @return Whether the Object and MutualFund are equal.
     */
    @Override
    public boolean equals(Object o) {
        // Check for referential equality.
        if (this == o) return true;

        // Check for null value and class inequality.
        if (o == null || getClass() != o.getClass()) return false;

        // Cast Object to MutualFund.
        MutualFund mutualFund = (MutualFund) o;

        // Compare all variables.
        return
                getQuantity() == mutualFund.getQuantity()
                        && mutualFund.getPrice() == getPrice()
                        && mutualFund.getBookValue() == getBookValue()
                        && getSymbol().equalsIgnoreCase(mutualFund.getSymbol())
                        && getName().equalsIgnoreCase(mutualFund.getName());
    }
}
