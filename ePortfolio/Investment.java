package ePortfolio;

import java.util.Objects;

/**
 * Abstract super class of Stock and MutualFund.
 */
public abstract class Investment
{
    /**
     * Constructor for Investment.
     * @param symbol The symbol of the Investment.
     * @param name The name of the Investment.
     * @param quantity The quantity of the Investment.
     * @param price The price of the Investment.
     * @param bookValue The computed book value of the Investment.
     */
    Investment(String symbol, String name, int quantity, float price, float bookValue)
    {
        this.symbol = symbol;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.bookValue = bookValue;
    }

    private String symbol;
    private String name;
    private int quantity;
    private float price;
    private float bookValue;

    //  region <Getter, Setter>

    public String getSymbol()
    {
        return symbol;
    }

    public void setSymbol(String symbol)
    {
        this.symbol = symbol;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getQuantity()
    {
        return quantity;
    }

    public void setQuantity(int quantity)
    {
        this.quantity = quantity;
    }

    public float getPrice()
    {
        return price;
    }

    public void setPrice(float price)
    {
        this.price = price;
    }

    public float getBookValue()
    {
        return bookValue;
    }

    public void setBookValue(float bookValue)
    {
        this.bookValue = bookValue;
    }

//  endregion

    /**
     * Formats variables of investment into a String
     * to allow for writing to a file.
     * @return The formatted String.
     */
    public String toFileString()
    {
        return  "type=\"" + (this instanceof Stock ? "stock\"\n" : "mutualfund\"\n") +
                "symbol=\""+getSymbol()+"\"\n" +
                "name=\""+getName()+"\"\n" +
                "quantity=\""+getQuantity()+"\"\n" +
                "price=\""+getPrice()+"\"\n" +
                "bookValue=\""+getBookValue()+"\"\n";
    }

    /**
     * Converts the variables of the Investment into
     * a readable String format.
     * @return The formatted String of Investment variables.
     */
    @Override
    public String toString() {
        return String.format("'%s'  |  '%s'\n%s: %d  |  Price: $%.2f\nBook Value: $%.2f\n",
                symbol,
                name,
                (this instanceof Stock ? "Shares" : "Units"),
                quantity,
                price,
                bookValue);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        Investment that = (Investment) o;

        return
                quantity == that.quantity &&
                        that.price == price &&
                        that.bookValue == bookValue &&
                        symbol.equalsIgnoreCase(that.symbol) &&
                        name.equalsIgnoreCase(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol, name, quantity, price, bookValue);
    }
}
