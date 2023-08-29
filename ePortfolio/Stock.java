package ePortfolio;

/**
 * An extension of Investment that can be
 * bought, sold, updated and searched for
 * in a Portfolio.
 *
 * Includes a 'commission' of $9.99 when bought and sold.
 */
public class Stock extends Investment
{
    /**
     * Constructor for Stock.
     * @param symbol The symbol of the Stock.
     * @param name The name of the Stock.
     * @param quantity The quantity of the Stock.
     * @param price The price of the Stock.
     * @param bookValue The computed book value of the stock.
     */
    Stock(String symbol, String name, int quantity, float price, float bookValue)
    {
        super(symbol, name, quantity, price, bookValue);
    }

    /**
     * Compares the Stock with another Object
     * for absolute equality.
     * @param o The object to compare.
     * @return Whether the Object and Stock are equal.
     */
    @Override
    public boolean equals(Object o) {
        // Check for referential equality.
        if (this == o) return true;

        // Check for null value and class inequality.
        if (o == null || getClass() != o.getClass()) return false;

        // Cast Object to Stock.
        Stock stock = (Stock) o;

        // Compare all variables.
        return
                getQuantity() == stock.getQuantity()
                        && stock.getPrice() == getPrice()
                        && stock.getBookValue() == getBookValue()
                        && getSymbol().equalsIgnoreCase(stock.getSymbol())
                        && getName().equalsIgnoreCase(stock.getName());
    }
}
