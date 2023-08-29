package ePortfolio;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Contains the investments bought by the user.
 */
public class Portfolio
{

    /**
     * Constructor for Portfolio.
     * Initializes ArrayList and HashMap.
     */
    Portfolio()
    {
        investments = new ArrayList<>();
        hashMap = new HashMap<>();
    }

    /**
     * Constructor for Portfolio.
     * Loads investments from a file.
     * @param args Array of Strings provided by the user at program execution.
     */
    Portfolio(String[] args)
    {
        // Initialize objects.
        investments = new ArrayList<>();
        hashMap = new HashMap<>();

        // Check that user entered input on running the program.
        if (args[0] == null || args[0].isBlank())
        {
            System.err.println("ERROR: No filename was entered.");
            System.out.println("Continuing program with empty portfolio.");

            return;
        }

        // Load investments using command line input.
        Load(args[0]);
    }

    private static ArrayList<Investment> investments = null;

    private static HashMap<String, ArrayList<Integer>> hashMap = null;

    /**
     * Default file name if the user does not provide one.
     */
    private static final String defaultFilename = "output.txt";
    private static String inFilename = null;

    public static void main(String[] args)
    {
        // Check that arguments were included in running of program.
        if (args == null || args.length == 0)
        {
            System.err.println("ERROR: No arguments were provided when running the program.");

            File f;

            if (!(f = new File (defaultFilename)).exists() || f.length() == 0)
            {
                System.out.println("Continuing program with empty portfolio.");

                // Create portfolio with no prior investments.
                new Portfolio();
            }
            else
            {
                // Create portfolio with investments from file named with defaultFileName.
                new Portfolio(new String[] {defaultFilename});

                // Update the hash map to assign keywords to indices.
                UpdateHashMap(null);
            }
        }
        else
        {
            // Create portfolio with investments based on argument(s) included in running of program.
            new Portfolio(args);

            // Update the hash map to assign keywords to indices.
            UpdateHashMap(null);
        }

        // Create the window after attempting to load from the file.
        CreateWindow();
    }

    /**
     * Creates the Window and makes it visible.
     */
    private static void CreateWindow()
    {
        Window w = new Window();
        w.setVisible(true);
    }

    /**
     * Allows the user to buy new or preexisting investments.
     * Searches for if investment exists according to user
     * input.
     *
     * If investment exists, user is allowed to purchase
     * additional shares/units, if the type inputted is
     * the same as the investments.
     *
     * Else, the inputted name is used for the new
     * investment and the hashmap is updated.
     *
     * @param inType The type of Investment to buy/add to.
     * @param inSymbol The symbol of the new/existing Investment.
     * @param inName The name of the new/existing Investment.
     * @param inQuantity The quantity to buy of the Investment.
     * @param inPrice The price to buy an Investment at.
     * @return A string representing the results of the operation.
     * @throws Exception Possible exceptions can occur from no
     * investments existing within the portfolio or from input
     * values being invalid.
     *
     * An exception is thrown with a WARNING message if the user
     * wished to purchase a Stock/Mutual Fund but there was an
     * existing Investment with a differing type.
     */
    public static String Buy
    (String inType,
     String inSymbol,
     String inName,
     String inQuantity,
     String inPrice) throws Exception
    {
        String retMsg = "";

        int type;

        if (inType.equalsIgnoreCase("stock"))
            type = 1;
        else if (inType.equalsIgnoreCase("mutual fund"))
            type = 2;
        else
            throw new Exception(String.format("ERROR: Cannot add investment of type '%s'.\n", inType));

        if (inSymbol.isEmpty())
            throw new Exception("ERROR: A symbol must be provided when adding an investment.\n");

        String symbol = inSymbol.trim();

        String name = inName.trim();

        int quantity;

        try
        {
            quantity = Integer.parseInt(inQuantity.trim());

            if (quantity <= 0)
                throw new Exception("ERROR: Please enter a positive value for quantity.\n");
        }
        catch (Exception e)
        {
            throw new Exception("ERROR: Please enter a positive value for quantity.\n");
        }

        float price;

        try
        {
            price = Float.parseFloat(inPrice.trim());

            if (price <= 0)
                throw new Exception("ERROR: Please enter a positive value for price.\n");
        }
        catch (Exception e)
        {
            throw new Exception("ERROR: Please enter a positive value for price.\n");
        }

        int foundAt = GetBySymbol(inSymbol);

        // If an Investment was found.
        if (foundAt != -1)
        {
            if (type == 1 && investments.get(foundAt) instanceof MutualFund)
            {
                throw new Exception(String.format("WARNING: Mutual Fund currently exists with symbol '%s'.\n", symbol));
            }
            else if (type == 2 && investments.get(foundAt) instanceof Stock)
            {
                throw new Exception(String.format("WARNING: Stock currently exists with symbol '%s'.\n", symbol));
            }
        }

        // If no investment was found in the ArrayList.
        if (foundAt == -1)
        {
            if (inName.isEmpty())
                throw new Exception("ERROR: A name must be provided when buying a new investment.\n");

            // Create a new Stock object, and add to 'investments' ArrayList.
            if (type == 1)
            {
                Stock s = new Stock(symbol,
                        name,
                        quantity,
                        price,
                        quantity * price + 9.99f);

                investments.add(s);

                retMsg += (String.format("Purchased %d shares of '%s' [%s] at $%.2f.\n",
                        quantity,
                        symbol,
                        name,
                        price));

                retMsg += s;
            }
            // Create a new MutualFund object and add to 'investments' ArrayList.
            else
            {
                MutualFund mf = new MutualFund(symbol,
                        name,
                        quantity,
                        price,
                        quantity * price);

                investments.add(mf);

                retMsg += String.format("Purchased %d units of '%s' [%s] at $%.2f.\n",
                        quantity,
                        symbol,
                        name,
                        price);

                retMsg += mf;
            }

            UpdateHashMap(null);
        }
        // If an investment was found.
        else
        {
            Investment i = investments.get(foundAt);

            i.setQuantity(quantity + i.getQuantity());

            i.setPrice(price);

            i.setBookValue(i.getBookValue() + quantity * price + (i instanceof Stock ? 9.99f : 0));

            retMsg += String.format("Purchased %d additional %s of '%s' [%s] at $%.2f.\n",
                    quantity,
                    (i instanceof Stock ? "shares" : "units"),
                    symbol,
                    i.getName(),
                    price);

            // Print out the updated Object.
            retMsg += i;
        }

        return retMsg;
    }

    /**
     *
     * Allows the user to sell an existing investment.
     * Searches for if inputted symbol exists, and sells
     * if all other input info is valid.
     *
     * Updates hashmap if investment was completely sold.
     * @param inSymbol The symbol of the Investment to sell.
     * @param inQuantity The amount of the Investment to sell.
     * @param inPrice The price to sell the Investment at.
     * @return A string representing the results of the operation.
     * @throws Exception Possible exceptions can occur from no investments
     * existing within the portfolio, invalid inputs for the parameters,
     * or cases where quantity to sell is larger than current quantity.
     */
    public static String Sell(String inSymbol, String inQuantity, String inPrice) throws Exception
    {
        if (!InvestmentExists())
            throw new Exception("ERROR: No investments exist within the portfolio.\n");

        String retMsg = "";

        String symbol;

        if (inSymbol.isEmpty())
            throw new Exception("ERROR: A symbol must be provided when selling an investment.\n");

        symbol = inSymbol;

        float quantity;

        try
        {
            quantity = Integer.parseInt(inQuantity);

            if (quantity <= 0)
                throw new Exception("ERROR: Please enter a positive value for quantity.\n");
        }
        catch (Exception e)
        {
            throw new Exception("ERROR: Please enter a positive value for quantity.\n");
        }

        float price;

        try
        {
            price = Float.parseFloat(inPrice);

            if (price <= 0)
                throw new Exception("ERROR: Please enter a positive value for price.\n");
        }
        catch (Exception e)
        {
            throw new Exception("ERROR: Please enter a positive value for price.\n");
        }

        int foundAt = GetBySymbol(inSymbol);

        // Check whether inputted symbol exists in portfolio.
        if (foundAt != -1)
        {
            Investment i = investments.get(foundAt);

            if (i.getQuantity() < quantity)
            {
                // Print an error to the user.
                throw new Exception(String.format("ERROR: Quantity entered [%.0f] is greater than quantity currently held [%d].\n",
                        quantity,
                        i.getQuantity())
                );
            }

            // Determine the payment received from selling the investment.
            float payment = price * quantity - (i instanceof Stock ? 9.99f : 45f);

            // Calculate the new book value and store it in the investment.
            i.setBookValue(i.getBookValue() * ((i.getQuantity() - quantity) / (float) i.getQuantity()));

            // Calculate the new quantity and store it in the investment.
            i.setQuantity(i.getQuantity() - (int)quantity);

            // Set the new price of the investment.
            i.setPrice(price);

            retMsg += String.format("Sold %.0f %s of '%s' [%s] at $%.2f.\n",
                    quantity,
                    (i instanceof Stock ? "shares" : "units"),
                    symbol,
                    i.getName(),
                    price);

            retMsg += String.format("Payment received: $%.2f.\n", payment);

            if (i.getQuantity() == 0)
            {
                // Remove the investment if all of its quantity was sold.
                investments.remove(i);

                // Update the hashmap with the removed investment.
                UpdateHashMap(i.getName());

                retMsg += String.format("'%s' [%s] removed from portfolio.\n", symbol, i.getName());
            }
            else
            {
                retMsg += i;
            }

            return retMsg;
        }

        return "No investment was found with the symbol '" + symbol + "'.\n";
    }

    /**
     * Allows the user to update the prices of
     * an investment within their portfolio.
     * @param symbol The symbol of the Investment to update.
     * @param name The name of the Investment to update.
     * @param inPrice The new price for the Investment.
     * @return A string representing the results of the operation.
     * @throws Exception Possible exceptions can occur if no investments
     * exist within the portfolio, or if an invalid input is received for
     * the symbol, name (being blank) or inPrice (i.e., negative, or non-numeric values).
     */
    public static String Update(String symbol, String name, String inPrice) throws Exception
    {
        if (!InvestmentExists())
            throw new Exception("ERROR: No investments exist within the portfolio.\n");

        if (symbol.isEmpty())
            throw new Exception("ERROR: A symbol must be provided when updating an investment.\n");

        if (name.isEmpty())
            throw new Exception("ERROR: A name must be provided when updating an investment.\n");

        float price;

        try
        {
            price = Float.parseFloat(inPrice);

            if (price <= 0)
                throw new Exception("ERROR: Please enter a positive floating-point value for price.\n");
        }
        catch (Exception e)
        {
            throw new Exception("ERROR: Please enter a positive floating-point value for price.\n");
        }

        Investment i = investments.get(GetBySymbol(symbol));

        if (price == i.getPrice())
            return String.format("WARNING: No change to price of '%s' [%s].\n", symbol, name);

        i.setPrice(price);

        return String.format("'%s' [%s] updated with new price $%.2f.\n", symbol, name, price);
    }

    /**
     * Calculates the amount of payment received
     * provided that the user were to sell all investments
     * currently held at their current price.
     * @return The total gain from selling all investments.
     * @throws Exception An exception can be thrown if no
     * investments exist within the portfolio.
     */
    public static float GetGain() throws Exception
    {
        // Gain would be 0 if no investment has been bought or sold.
        if (!InvestmentExists())
            throw new Exception("ERROR: No investments exist within the portfolio.\n");

        float localGain = 0;

        // Add the payment of each Stock into the temporary gain variable.
        for (Investment i : investments)
        {
            localGain += i.getQuantity() * i.getPrice() - i.getBookValue();

            if (i instanceof Stock)
                localGain -= 9.99f;
            else
                localGain -= 45;
        }

        return localGain;
    }

    /**
     * Calculates the amount of payment received
     * provided that the user were to sell the Investment
     * at index.
     * @param index The index of the Investment to calculate.
     * @return The gain from selling the Investment at index.
     * @throws Exception An exception can be thrown if no
     * investments exist within the portfolio or if the passed
     * index exceeds the bounds of the ArrayList.
     */
    public static float GetGain(int index) throws Exception
    {
        // Gain would be 0 if no investment has been bought or sold.
        if (!InvestmentExists())
            throw new Exception("ERROR: No investments exist within the portfolio.\n");

        if (index < 0 || index >= investments.size())
            throw new Exception("ERROR: Attempt to retrieve investment beyond list bounds.\n");

        var i = investments.get(index);

        return i.getQuantity() * i.getPrice() - i.getBookValue() - (i instanceof Stock ? 9.99f : 45);
    }

    /**
     * Allows the user to search for investments within
     * their portfolio by symbol, name, or price.
     * @param inSymbol The symbol of the Investment to search for.
     * @param inName The name of the Investment to search for.
     * @param inLPrice The lower bound to search for.
     * @param inHPrice The higher bound to search for.
     * @return A string representing the result of the operation.
     * @throws Exception Possible exceptions can occur if there are not
     * existing investments, or if inputs were invalid (such as inputting
     * a String of non-numeric values for inLPrice or inHPrice).
     */
    public static String Search(String inSymbol, String inName, String inLPrice, String inHPrice) throws Exception
    {
        if (!InvestmentExists())
            throw new Exception("ERROR: No investments exist within the portfolio.\n");

        StringBuilder retMsg = new StringBuilder();

        boolean searchWithSymbol = !inSymbol.isBlank();
        boolean searchWithName = !inName.isBlank();
        boolean searchWithRange = (!inLPrice.isBlank() || !inHPrice.isBlank());

        if (searchWithName)
        {
            // Split the keywords entered by space.
            String[] regex = inName.split(" ");

            // Create an ArrayList<Integer> to track index
            // matches between keywords.
            ArrayList<Integer> allSets = null;

            for (String keyword : regex)
            {
                // Get the indices (of investments) that the keyword is mapped to.
                var set = hashMap.get(keyword);

                // Check if the keyword was not associated with any investment.
                if (set == null)
                {
                    // Clear allSets if not null, indicating that search by keyword
                    // returns no investment indices.
                    if (allSets != null)
                        allSets.clear();

                    break;
                }

                // Initialize allSets if null, else remove any indices
                // not present in both ArrayLists.
                if (allSets == null)
                    allSets = new ArrayList<>(set);
                else
                    allSets.removeIf(v -> !set.contains(v));

                // No reason to continue iterating if two keywords
                // already had no matches.
                if (allSets.isEmpty())
                    break;
            }

            // Check if allSets is null or if there were no indices found.
            if (allSets == null || allSets.isEmpty())
                return String.format("No investment was found when searching by keyword '%s'.\n", inName);

            if (searchWithSymbol)
            {
                int foundAt = -1;

                // Only search indices found matched with keywords.
                for (int v : allSets)
                {
                    if (investments.get(v).getSymbol().equalsIgnoreCase(inSymbol))
                    {
                        // Symbols are unique, therefore only one investment can be found.
                        foundAt = v;
                        break;
                    }
                }

                if (foundAt == -1)
                {
                    return String.format("No investment was found when searching by keyword '%s' and symbol '%s'.\n", inName, inSymbol);
                }
                else if (!searchWithRange)
                {
                    Investment i = investments.get(foundAt);

                    retMsg.append(String.format("Found %s with symbol '%s', keyword(s) '%s':\n\n",
                            i.getClass().getSimpleName(),
                            inSymbol,
                            inName));

                    retMsg.append(i);

                    return retMsg.toString();
                }

                // Remove all indices from allSets, and add the
                // index at which the investment was found-by-symbol.
                allSets.clear();
                allSets.add(foundAt);
            }

            if (searchWithRange)
            {
                boolean foundAny = false;

                retMsg.append(String.format("Found investments by keyword(s) '%s'%sand range '%s-%s':\n\n",
                        inName,
                        (searchWithSymbol ? (", symbol '" + inSymbol + "', ") : " "),
                        inLPrice,
                        inHPrice));

                float lowBound;
                try
                {
                    if (inLPrice.isEmpty())
                    {
                        lowBound = 0;
                    }
                    else
                    {
                        lowBound = Float.parseFloat(inLPrice);

                        if (lowBound < 0)
                            throw new Exception("Please enter a positive floating-point value (or empty) for low price");
                    }
                }
                catch (Exception e)
                {
                    throw new Exception("Please enter a positive floating-point value (or empty) for low price");
                }

                float highBound;
                try
                {
                    if (inHPrice.isEmpty())
                        highBound = Float.MAX_VALUE;
                    else
                    {
                        highBound = Float.parseFloat(inHPrice);

                        if (highBound < 0)
                            throw new Exception("Please enter a positive floating-point value (or empty) for high price");
                    }
                }
                catch (Exception e)
                {
                    throw new Exception("Please enter a positive floating-point value (or empty) for high price");
                }

                if (lowBound > highBound)
                    throw new Exception("WARNING: Low price is higher than high price.");

                for (int v : allSets)
                {
                    if (investments.get(v).getPrice() >= lowBound && investments.get(v).getPrice() <= highBound)
                    {
                        retMsg.append(investments.get(v));
                        foundAny = true;
                    }
                }

                if (!foundAny)
                {
                    return String.format("N/A. No investments were found by keyword(s) '%s'%sand range '%s-%s'\n",
                            inName,
                            (searchWithSymbol ? (", symbol " + inSymbol + " ") : " "),
                            inLPrice,
                            inHPrice);
                }

                return retMsg.toString();
            }

            // In the event that the user only wished to search by keyword.
            retMsg.append(String.format("Found investments by keyword(s) '%s':\n\n", inName));

            for (var v : allSets)
            {
                // Print out the Investment object.
                retMsg.append(investments.get(v)).append("\n");
            }

            return retMsg.toString();
        }

        if (searchWithSymbol)
        {
            int index = GetBySymbol(inSymbol);

            if (index == -1)
                return String.format("No investment was found when searching by symbol '%s'.\n", inSymbol);

            if (searchWithRange)
            {
                float lowBound;
                try
                {
                    if (inLPrice.isEmpty())
                    {
                        lowBound = 0;
                    }
                    else
                    {
                        lowBound = Float.parseFloat(inLPrice);

                        if (lowBound < 0)
                            throw new Exception("Please enter a positive floating-point value (or empty) for low price");
                    }
                }
                catch (Exception e)
                {
                    throw new Exception("Please enter a positive floating-point value (or empty) for low price");
                }

                float highBound;
                try
                {
                    if (inHPrice.isEmpty())
                        highBound = Float.MAX_VALUE;
                    else
                    {
                        highBound = Float.parseFloat(inHPrice);

                        if (highBound < 0)
                            throw new Exception("Please enter a positive floating-point value (or empty) for high price");
                    }
                }
                catch (Exception e)
                {
                    throw new Exception("Please enter a positive floating-point value (or empty) for high price");
                }

                if (lowBound > highBound)
                    throw new Exception("WARNING: Low price is higher than high price.");

                if (investments.get(index).getPrice() >= lowBound && investments.get(index).getPrice() <= highBound)
                {
                    retMsg.append(String.format("Found %s with symbol '%s', and price range '%s-%s':\n\n",
                            investments.get(index).getClass().getSimpleName(),
                            inSymbol,
                            inLPrice,
                            inHPrice));

                    // Display the Investment object.
                    retMsg.append(investments.get(index));

                    // Return to menu.
                    return retMsg.toString();
                }

                // In the event that an investment was not found-by-symbol, found-by-name, or found-by-range.
                return String.format("Did not find investment with symbol '%s' and price range '%s-%s'.\n",
                        inSymbol,
                        inLPrice,
                        inHPrice);
            }

            // In the event that user wished to only search by symbol.
            retMsg.append(String.format("Found %s with symbol '%s':\n\n",
                    investments.get(index).getClass().getSimpleName(),
                    inSymbol));

            retMsg.append(investments.get(index)).append("\n");

            return retMsg.toString();
        }

        // Mostly identical to searching by range within search by symbol.
        if (searchWithRange)
        {
            int[] results = new int[investments.size()];

            float lowBound;
            try
            {
                if (inLPrice.isEmpty())
                {
                    lowBound = 0;
                }
                else
                {
                    lowBound = Float.parseFloat(inLPrice);

                    if (lowBound < 0)
                        throw new Exception("Please enter a positive floating-point value (or empty) for low price");
                }
            }
            catch (Exception e)
            {
                throw new Exception("Please enter a positive floating-point value (or empty) for low price");
            }

            float highBound;
            try
            {
                if (inHPrice.isEmpty())
                    highBound = Float.MAX_VALUE;
                else
                {
                    highBound = Float.parseFloat(inHPrice);

                    if (highBound < 0)
                        throw new Exception("Please enter a positive floating-point value (or empty) for high price");
                }
            }
            catch (Exception e)
            {
                throw new Exception("Please enter a positive floating-point value (or empty) for high price");
            }

            if (lowBound > highBound)
                throw new Exception("WARNING: Low price is higher than high price.");

            // Determine if any investments fall into the price range.
            if (GetByPrice(lowBound, highBound, results))
            {
                retMsg.append(String.format("Found investment(s) when searching by price range '%s-%s'.\n",
                        inLPrice,
                        inHPrice));

                // Iterate over indices of mirror array. Only print objects
                // at indices with values equal to 1.
                for (int i = 0; i < results.length; ++i)
                {
                    if (results[i] == 1)
                        retMsg.append(investments.get(i)).append("\n");
                }
            }
            else
            {
                // Display message to user.
                return String.format("No investment was found when searching by price range '%s-%s'.", inLPrice, inHPrice);
            }

            return retMsg.toString();
        }

        // In the case that the user did not enter any search criteria.
        retMsg.append("No search criteria entered. Printing all investments within portfolio:\n");

        // Print out all Investment objects.
        for (var i : investments)
            retMsg.append(i).append("\n");

        return retMsg.toString();
    }

    /**
     * Handles the addition and deletion of investments
     * from portfolio and their relation (by keyword)
     * to the hashmap.
     *
     * Gets a list of keywords by the space-separated
     * words from the 'name' parameter.
     *
     * Adds keyword(s) and (ArrayList of type Integer) value
     * to hashmap in the case of new investments.
     *
     * Replaces keyword(s) and (ArrayList of type Integer) value
     * in hashmap, in the case of new investments with the
     * one or more of the same keyword or the deletion of
     * an investment with a keyword linked to other
     * investments.
     *
     * Deletes keyword(s) and (ArrayList of type Integer) value
     * in hashmap, in the case of no matches being made to
     * any keywords found in investments of portfolio.
     * @param name Name of Investment object.
     */
    private static void OnUpdateHashMap(String name)
    {
        if (name == null)
            return;

        // Get the space-separated keywords of 'name'.
        String[] regex = name.split(" ");

        // Create an ArrayList of ArrayList<Integer> with size regex.length.
        ArrayList<ArrayList<Integer>> p = new ArrayList<>(regex.length);

        // Create ArrayLists for each keyword in regex.
        for (int i = 0; i < regex.length; i++)
            p.add(new ArrayList<>(investments.size()));

        int len = investments.size();

        // Iterate over 'investments' ArrayList.
        for (int i = 0; i < len; ++i)
        {
            // Get the space-separated words of an investment's name.
            String[] keywords = investments.get(i).getName().split(" ");

            // Iterate over keywords of investment's name.
            for (var k : keywords)
            {
                // Iterate over keywords of passed in name.
                for (int j = 0; j < regex.length; ++j)
                {
                    // Check if keyword from passed in name equals keyword from investment
                    if (regex[j].equalsIgnoreCase(k))
                    {
                        // Check that ArrayList does not already contain index.
                        if (!p.get(j).contains(i))
                            p.get(j).add(i);
                    }
                }
            }
        }

        // Iterate over keywords of passed in name.
        for (int i = 0; i < regex.length; ++i)
        {
            // If the keyword was not found in any investments
            // e.g., investment was removed and its name passed as argument.
            if (p.get(i).isEmpty())
            {
                // Remove keyword, and its associated values from hashmap.
                hashMap.remove(regex[i]);
                continue;
            }

            // Replace keyword's ArrayList if keyword is found,
            // add keyword and its ArrayList to hashmap if not.
            if (hashMap.containsKey(regex[i]))
                hashMap.replace(regex[i], p.get(i));
            else
                hashMap.put(regex[i], p.get(i));
        }
    }

    /**
     * Calls OnUpdateHashMap(...) over all investments
     * in portfolio, using each investments' name as
     * the parameter for OnUpdateHashMap(...).
     *
     * @param nameToDelete Calls OnUpdateHashMap(name)
     *                     to attempt to delete the keywords of 'name'.
     */
    private static void UpdateHashMap(String nameToDelete)
    {
        for (var i : investments)
            OnUpdateHashMap(i.getName());

        OnUpdateHashMap(nameToDelete);
    }

    /**
     * Determines whether the portfolio has any
     * existing investments.
     * @return Boolean value indicating whether portfolio has any investments.
     */
    public static boolean InvestmentExists()
    {
        return investments.size() > 0;
    }

    /**
     * Attempts to get an investment by its symbol.
     *
     * Compares investments' symbols against argument 'symbol',
     * ignoring character case.
     * @param symbol The symbol to search for.
     * @return The index of the investment found, or -1 to indicate no match.
     */
    private static int GetBySymbol(String symbol)
    {
        int len = investments.size();

        // Iterate over 'investments' ArrayList.
        for (int i = 0; i < len; ++i)
        {
            // Only one investment can have 'symbol'.
            if (investments.get(i).getSymbol().equalsIgnoreCase(symbol))
                return i;
        }

        return -1;
    }

    /**
     * Attempts to get an investment by its price.
     *
     * Compares investments' prices by lowBound and highBound.
     * Comparisons are inclusive for both lowBound and highBound.
     *
     * Stores indices of found investments within array of integers.
     * @param lowBound The lower bound to compare against,
     *                 greater than or equal to 0 and
     *                 lower than or equal to highBound.
     * @param highBound The higher bound to compare against,
     *                  greater than or equal to lowBound and
     *                  lower than or equal to Integer.MaxValue.
     * @param results The integer array to store the results of
     *                the search. Indices are set (=1) if the
     *                investment at the index matched the bounds
     *                and cleared (=0) if not. Should be of length
     *                equal to ArrayList of Investments.
     * @return Boolean value of whether any investment was within
     * the bounds.
     */
    private static boolean GetByPrice(float lowBound, float highBound, int[] results)
    {
        boolean found = false;

        // Iterate over results array, and 'investments' ArrayList.
        for (int i = 0; i < results.length; i++)
        {
            if (investments.get(i).getPrice() >= lowBound && investments.get(i).getPrice() <= highBound)
            {
                // Set the result at i, and set found to true.
                results[i] = 1;
                found = true;
            }
            else
            {
                // Clear the result at i.
                results[i] = 0;
            }
        }

        return found;
    }

    /**
     * Attempts to load investments from a file, specified by 'filename'
     * into the portfolio.
     *
     * Checks if the file exists, and if it contains characters.
     *
     * Reads single lines from the file, storing each into an array of
     * type String, and incrementing a counter variable.
     *
     * When all lines for an investment have been read and stored,
     * the investment is created as a Stock or MutualFund object
     * by parsing the stored Strings, and is added to the 'investments'
     * ArrayList before continuing to read the file.
     *
     * @param filename The name of the file to load.
     */
    private void Load(String filename)
    {
        // Create the file object from 'filename'.
        File f = new File(filename);

        // Check if file exists.
        if (!f.exists())
        {
            System.err.printf("ERROR: No file was found with filename '%s'.\n", filename);
            System.out.println("Continuing program with empty portfolio.");

            return;
        }
        // Check if file contains any characters.
        else if (f.length() == 0)
        {
            inFilename = filename;

            System.err.printf("ERROR: File '%s' contains no data.\n", filename);
            System.out.println("Continuing program with empty portfolio.");

            return;
        }

        try
        {
            Scanner fScanner = new Scanner(f);
            String[] variables = new String[5];

            int type = 0;
            int counter = 0;

            // Keep a reference to the line number, in case of errors.
            int lineNum = 0;

            while (fScanner.hasNextLine())
            {
                ++lineNum;

                // Get the next line from the file.
                String line = fScanner.nextLine();

                if (line.isBlank())
                    continue;

                // Store the type of investment if line is "type='xxx'".
                if (line.startsWith("type"))
                    type = line.contains("stock") ? 1 : 2;
                    // Store the variable within double-quotes in the String[]
                    // and increment counter.
                else
                    variables[counter++] = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\""));

                // Check for when counter equals 5, as there are currently 5
                // variables within an Investment object.
                if (counter == 5)
                {
                    // Check that file format was correct.
                    if (!(type == 1 || type == 2))
                    {
                        System.err.printf("ERROR: Incorrect file format. [line : %d]\n" +
                                        "Entries must be included with type='xxx' before other variables.\n",
                                lineNum - 5
                        );
                    }

                    investments.add((type == 2)
                            ? new MutualFund(variables[0],
                            variables[1],
                            Integer.parseInt(variables[2]),
                            Float.parseFloat(variables[3]),
                            Float.parseFloat(variables[4]))

                            // Investment will be added to portfolio as Stock by default,
                            // if type != 2 to avoid errors with buying, selling, searching, etc.
                            : new Stock(variables[0],
                            variables[1],
                            Integer.parseInt(variables[2]),
                            Float.parseFloat(variables[3]),
                            Float.parseFloat(variables[4]))
                    );

                    // Reset the counter.
                    counter = 0;

                    // Reset the type.
                    type = 0;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();

            System.err.println("ERROR: Load operation failed.");
            System.out.println("Continuing program with incomplete portfolio.");
            return;
        }

        inFilename = filename;

        System.out.println("Investments loaded from file into portfolio successfully.");
    }

    /**
     * Attempt to save the portfolio to a file, specified by 'inFilename'.
     *
     * Creates a new file if none existed, and clears preexisting file.
     *
     * Writes all Investment objects to file using the
     * Investment.toFileString() method and a StringBuilder for more
     * efficient concatenation of a varying number of Strings.
     * @throws Exception Several exceptions can be encountered, when
     * attempting to create a new file, based on 'filename', when
     * creating the PrintWriter object based on the file 'f',
     * and when formatting the Investment object String
     * representation during the writing.
     */
    public static void Exit() throws Exception
    {
        if (investments.size() <= 0 && inFilename.equals(defaultFilename))
        {
            // If "output.txt" does not exist and there are no investments
            // in portfolio, don't save a blank file.
            if (!(new File(defaultFilename).exists()))
            {
                throw new Exception(("Portfolio is empty, and no filename was entered into command line " +
                        "/ " + "'" + defaultFilename + "'" + " does not currently exist.\n"));
            }
        }

        File f = inFilename == null
                ? new File(defaultFilename)
                : new File(inFilename);

        if (f.createNewFile())
        {
            System.err.printf("ERROR: No file was found with filename '%s'.\n", inFilename);
            System.out.printf("Created new file, with filename '%s'.\n", defaultFilename);
        }

        // Use StringBuilder because of varying number of concatenations.
        StringBuilder allInvestments = new StringBuilder();

        // Append objects in 'students' to 'allStudentInfo' in proper format for file.
        for (var i : investments)
            allInvestments.append(i.toFileString());

        // Create PrintWriter object based on file 'f', with UTF-8 charset.
        PrintWriter pw = new PrintWriter(f, "UTF-8");

        // Clear the file.
        pw.write("");

        if (allInvestments.length() != 0)
            // Write the info in 'allStudentInfo', ignoring the last newline character.
            pw.write(allInvestments.substring(0, allInvestments.lastIndexOf("\n")));

        // Close the PrintWriter object.
        pw.close();
    }

    /**
     * Gets a copy of an Investment object from the "investments"
     * ArrayList by index.
     * @param index The index to get an Investment from.
     * @return A copy of an Investment object.
     */
    public static Investment GetInvestment(int index)
    {
        if (!InvestmentExists())
            return null;

        if (index < 0 || index >= investments.size())
            return null;

        var i = investments.get(index);

        int quantity = 0;
        quantity += i.getQuantity();

        float price = 0;
        price += i.getPrice();

        float bookValue = 0;
        bookValue += i.getBookValue();

        // Check whether investment is a Stock or a MutualFund.
        if (i instanceof Stock)
            return new Stock(i.getSymbol(), i.getName(), quantity, price, bookValue);

        return new MutualFund(i.getSymbol(), i.getName(), quantity, price, bookValue);
    }

    /**
     * Get the size of the "investments" ArrayList.
     * @return Size of the "investments" ArrayList.
     */
    public static int Size()
    {
        return investments.size();
    }
}
