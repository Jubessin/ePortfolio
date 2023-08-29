package ePortfolio;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import static javax.swing.BoxLayout.Y_AXIS;

/**
 * JFrame that hosts all JContainers and UI elements
 * of the displayed window.
 */
public class Window extends JFrame
{
    private static final int
            width = 600,
            height = 400,
            minWidth = 450,
            minHeight = 350;

    private static final String windowTitle = "ePortfolio";

    private static final Color white = Color.white;

    private static final Cursor handCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
    private static final Cursor textCursor = Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR);

    private JPanel currentPanel = null;

    public Window()
    {
        super();

        // Make a menu item array with each element being a command.
        JMenuItem[] commands = new JMenuItem[]
        {
            new JMenuItem("Buy"),
            new JMenuItem("Sell"),
            new JMenuItem("Update"),
            new JMenuItem("Get Gain"),
            new JMenuItem("Search"),
            new JMenuItem("Quit")
        };

        // Make an ActionListener array, with each element corresponding to the
        // elements in the commands array.
        ActionListener[] commandListeners = new ActionListener[]
        {
            new BuyListener(),
            new SellListener(),
            new UpdateListener(),
            new GetGainListener(),
            new SearchListener(),
            new QuitListener(),
        };

        // Set size of the window.
        setSize(width, height);

        // Set the minimum size of the window (where all elements can still be
        // displayed properly).
        setMinimumSize(new Dimension(minWidth, minHeight));

        setTitle(windowTitle);

        // Use a BoxLayout for the overall content pane.
        setLayout(new BoxLayout(getContentPane(), Y_AXIS));

        // Have a custom listener for close events.
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        getContentPane().setBackground(white);

        JMenuBar menuBar = new JMenuBar();

        JMenu menu = new JMenu("Commands");

        // Stop the menu name from being overwritten by changing menu items.
        menu.setName("Commands");

        // Add associated action listeners to the menu items, then add to menu.
        for (int i = 0; i < commands.length; ++i)
        {
            commands[i].addActionListener(commandListeners[i]);

            menu.add(commands[i]);
        }

        menuBar.add(menu);

        menuBar.setBackground(white);

        // UI. Show a vertical line below the menu bar.
        menuBar.setBorderPainted(true);

        setJMenuBar(menuBar);

        JPanel CommandPanel = CommandPanel();

        // Add the CommandPanel to the JFrame.
        AddToContentPane(CommandPanel);

        // Center the JFrame on the screen.
        setLocationRelativeTo(null);

        // Check for windowClosing events.
        addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e)
            {
                // Clear the screen.
                AddToContentPane(QuitPanel());

                // Attempt to save the file
                try
                {
                    Portfolio.Exit();
                }
                catch (Exception ignored)
                {
                }
                // End the program.
                System.exit(0);
            }

            @Override
            public void windowClosed(WindowEvent e)
            {

            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });
    }

    /**
     * Displays the welcome message to the user
     * using a JTextArea.
     * @return A panel with a welcome message.
     */
    private JPanel CommandPanel()
    {
        JPanel CommandPanel = new JPanel();
        CommandPanel.setBackground(white);
        CommandPanel.setLayout(new BoxLayout(CommandPanel, Y_AXIS));

        JTextArea jta = new JTextArea();

        jta.setColumns(40);
        jta.setBackground(white);
        jta.setWrapStyleWord(true);
        jta.setLineWrap(true);
        jta.setEditable(false);
        jta.setFocusable(false);
        jta.setText("Welcome to ePortfolio.\n\nChoose a command from the \"Commands\" menu to buy or sell" +
                " an investment, update prices for all investments, get gain for the portfolio, search for " +
                "relevant investments, or quit the program.");

        CommandPanel.setMaximumSize(new Dimension((int) GetScreenWidth(), (int) GetScreenHeight()));

        CommandPanel.add(jta, Component.BOTTOM_ALIGNMENT);

        EmptyBorder(CommandPanel, 20, 5, 0, 0);

        return CommandPanel;
    }

    /**
     * @return The width of the computer screen.
     */
    private static double GetScreenWidth()
    {
        return Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    }

    /**
     * @return The height of the computer screen.
     */
    private static double GetScreenHeight()
    {
        return Toolkit.getDefaultToolkit().getScreenSize().getHeight();
    }

    /**
     * Removes any existing panel, and adds a new one
     * to the content pane.
     * @param panel The panel to add.
     */
    private void AddToContentPane(JPanel panel)
    {
        if (currentPanel != null)
            getContentPane().remove(currentPanel);

        getContentPane().add(panel);

        currentPanel = panel;

        // Refresh the content pane layout.
        getContentPane().validate();
    }

    /**
     * ActionListener for buying.
     */
    private class BuyListener implements ActionListener
    {
        private final JPanel typePanel = TypePanel();
        private final JPanel symbolPanel = Label_TextField_Panel("Symbol", 5, true,10);
        private final JPanel namePanel = Label_TextField_Panel("Name", 15, true, 21);
        private final JPanel quantityPanel = Label_TextField_Panel("Quantity", 4, true,0);
        private final JPanel pricePanel = Label_TextField_Panel("Price", 4, true,25);

        private final JTextField symbolTF = (JTextField)(symbolPanel.getComponent(2));
        private final JTextField nameTF = (JTextField)(namePanel.getComponent(2));
        private final JTextField quantityTF = (JTextField)(quantityPanel.getComponent(2));
        private final JTextField priceTF = (JTextField)(pricePanel.getComponent(2));

        private final JScrollPane messageBox = MessageBox(320);

        private final JTextArea jta = (JTextArea) ((JPanel)((JViewport)messageBox.getComponent(0)).getComponent(0)).getComponent(0);

        /**
         * ButtonActionListener for the reset button.
         */
        private class ResetButtonActionListener extends ButtonActionListener
        {
            ResetButtonActionListener(String name)
            {
                super(name);
            }

            @Override
            public void actionPerformed(ActionEvent e)
            {
                symbolTF.setText(null);
                nameTF.setText(null);
                quantityTF.setText(null);
                priceTF.setText(null);
                jta.setText(jta.getText() + "Reset.\n");
            }
        }

        /**
         * ButtonActionListener for the add button.
         */
        private class AddButtonActionListener extends ButtonActionListener
        {
            AddButtonActionListener(String name)
            {
                super(name);
            }

            @Override
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    int type = ((JComboBox<?>)typePanel.getComponent(2)).getSelectedIndex();

                    String result = Portfolio.Buy
                    (
                        type == 0 ? "Stock" : "Mutual Fund",
                        symbolTF.getText().trim(),
                        nameTF.getText().trim(),
                        quantityTF.getText().trim(),
                        priceTF.getText().trim()
                    );

                    jta.setText(jta.getText() + result);
                }
                catch (Exception ex)
                {
                    jta.setText(jta.getText() + ex.getMessage());
                }
            }
        }

        /**
         * Creates the JPanel for buying.
         * @return The JPanel for buying.
         */
        private JPanel BuyPanel()
        {
            // Create the JPanel.
            JPanel BuyPanel = new JPanel();

            BuyPanel.setBackground(white);
            BuyPanel.setLayout(new BoxLayout(BuyPanel, Y_AXIS));

            // Upper section of the buy panel.
            JPanel mSection1 = new JPanel();

            // Lower section of the buy panel.
            JPanel mSection2 = new JPanel();

            mSection1.setBackground(white);
            mSection1.setLayout(new BorderLayout());
            mSection1.setMaximumSize(new Dimension((int) GetScreenWidth(), 200));

            mSection2.setBackground(white);
            mSection2.setLayout(new GridLayout(1,1));
            mSection2.setMaximumSize(new Dimension((int) GetScreenWidth(), (int)GetScreenHeight() - 210));

            // Subsections of the upper and lower JPanels.
            JPanel mSection1_s1, mSection1_s2, mSection2_s1;

            // Create the left section of the upper panel.
            mSection1_s1 = new JPanel();
            mSection1_s1.setLayout(new BoxLayout(mSection1_s1, Y_AXIS));

            // Create the right section of the upper panel.
            mSection1_s2 = new JPanel();
            mSection1_s2.setLayout(new FlowLayout(FlowLayout.CENTER));

            mSection1_s1.add(LabelPanel("Adding an investment", 0));
            mSection1_s1.add(typePanel);
            mSection1_s1.add(symbolPanel);
            mSection1_s1.add(namePanel);
            mSection1_s1.add(quantityPanel);
            mSection1_s1.add(pricePanel);

            // Add the buttons to the upper right section.
            mSection1_s2.add(ButtonPanel(new ButtonActionListener[]
                {
                    new ResetButtonActionListener("reset"),
                    new AddButtonActionListener("add"),
                },
                40)
            );

            // Create the subsection of the lower JPanel.
            mSection2_s1 = new JPanel();
            mSection2_s1.setLayout(new BoxLayout(mSection2_s1, Y_AXIS));

            mSection2_s1.add(LabelPanel("Messages",20));
            mSection2_s1.add(messageBox);

            // Clear the message box.
            jta.setText(null);

            // Add the subsection panels to the main section panels.
            mSection1.add(mSection1_s1, BorderLayout.WEST);
            mSection1.add(mSection1_s2, BorderLayout.CENTER);
            mSection2.add(mSection2_s1);

            // Add the main section panels to the BuyPanel.
            BuyPanel.add(mSection1);
            BuyPanel.add(mSection2);

            return BuyPanel;
        }

        /**
         * Creates a panel for displaying the field for
         * selecting the type of investment to buy.
         * @return JPanel for selecting type of investment.
         */
        private JPanel TypePanel()
        {
            // Create the main panel.
            JPanel TypePanel = new JPanel();
            TypePanel.setLayout(new FlowLayout(FlowLayout.LEFT));

            // Create the JLabel and set its text.
            JLabel TypePanelLabel = new JLabel("Type");
            TypePanelLabel.setBackground(white);

            // Create a combo box to display and select between
            // mutual fund and stock.
            JComboBox<String> TypePanelCB = new JComboBox<>();
            TypePanelCB.setFocusable(false);
            TypePanelCB.addItem("Stock");
            TypePanelCB.addItem("Mutual Fund");

            // Add the JLabel to the main panel.
            TypePanel.add(TypePanelLabel);

            // Add a space in between the JLabel and ComboBox.
            TypePanel.add(Box.createHorizontalStrut(26));

            // Add the ComboBox to the main panel.
            TypePanel.add(TypePanelCB);

            // UI. Put a margin of 20 from the left.
            EmptyBorder(TypePanel, 0,20,0,0);

            return TypePanel;
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            // Add BuyPanel to the content pane if selected from the MenuBar.
            AddToContentPane(BuyPanel());
        }
    }

    /**
     * ActionListener for selling.
     */
    private class SellListener implements ActionListener
    {
        private final JPanel symbolPanel = Label_TextField_Panel("Symbol", 5, true,10);
        private final JPanel quantityPanel = Label_TextField_Panel("Quantity", 4, true, 0);
        private final JPanel pricePanel = Label_TextField_Panel("Price", 4, true,25);

        private final JTextField symbolTF = (JTextField)(symbolPanel.getComponent(2));
        private final JTextField quantityTF = (JTextField)(quantityPanel.getComponent(2));
        private final JTextField priceTF = (JTextField)(pricePanel.getComponent(2));

        private final JScrollPane messageBox = MessageBox(320);

        private final JTextArea jta = (JTextArea) ((JPanel)((JViewport)messageBox.getComponent(0)).getComponent(0)).getComponent(0);

        /**
         * ButtonActionListener for reset button.
         */
        private class ResetButtonActionListener extends ButtonActionListener
        {
            ResetButtonActionListener(String name)
            {
                super(name);
            }

            @Override
            public void actionPerformed(ActionEvent e)
            {
                // Clear all text fields.
                symbolTF.setText(null);
                quantityTF.setText(null);
                priceTF.setText(null);

                // Display operation to the message box.
                jta.setText(jta.getText() + "Reset.\n");
            }
        }

        /**
         * ButtonActionListener for sell button.
         */
        private class SellButtonActionListener extends ButtonActionListener
        {
            SellButtonActionListener(String name)
            {
                super(name);
            }

            @Override
            public void actionPerformed(ActionEvent e)
            {
                // Try to sell using (trimmed) input from user,
                // and display resulting message in box.
                try
                {
                    String result = Portfolio.Sell
                    (
                        symbolTF.getText().trim(),
                        quantityTF.getText().trim(),
                        priceTF.getText().trim()
                    );

                    jta.setText(jta.getText() + result);
                }
                catch (Exception ex)
                {
                    // Display warning/error message in message box.
                    jta.setText(jta.getText() + ex.getMessage());
                }
            }
        }

        /**
         * Create the JPanel for selling.
         * @return The JPanel for selling.
         */
        private JPanel SellPanel()
        {
            // Create the JPanel.
            JPanel SellPanel = new JPanel();

            SellPanel.setBackground(white);
            SellPanel.setLayout(new BoxLayout(SellPanel, Y_AXIS));

            // The upper section panel of the layout.
            JPanel mSection1 = new JPanel();

            // The lower section panel of the layout.
            JPanel mSection2 = new JPanel();

            mSection1.setBackground(white);
            mSection1.setLayout(new BorderLayout());
            mSection1.setMaximumSize(new Dimension((int) GetScreenWidth(), 200));

            mSection2.setLayout(new GridLayout(1,1));
            mSection2.setBackground(white);
            mSection2.setMaximumSize(new Dimension((int) GetScreenWidth(), (int)GetScreenHeight() - 210));

            // The subsection panels.
            JPanel mSection1_s1, mSection1_s2, mSection2_s1;

            // The left section of the upper panel.
            mSection1_s1 = new JPanel();
            mSection1_s1.setLayout(new BoxLayout(mSection1_s1, Y_AXIS));

            // The right section of the upper panel.
            mSection1_s2 = new JPanel();
            mSection1_s2.setLayout(new FlowLayout(FlowLayout.CENTER));

            // The subsection of the lower panel.
            mSection2_s1 = new JPanel();
            mSection2_s1.setLayout(new BoxLayout(mSection2_s1, Y_AXIS));

            mSection1_s1.add(LabelPanel("Selling an investment", 0));
            mSection1_s1.add(symbolPanel);
            mSection1_s1.add(quantityPanel);
            mSection1_s1.add(pricePanel);

            // Add the buttons to the right subsection panel.
            mSection1_s2.add(ButtonPanel(new ButtonActionListener[]
                {
                    new ResetButtonActionListener("reset"),
                    new SellButtonActionListener("sell"),
                },
                40)
            );

            // Add the label and message box to the lower subsection panel.
            mSection2_s1.add(LabelPanel("Messages", 20));
            mSection2_s1.add(messageBox);

            // Clear the message box.
            jta.setText(null);

            // Add the subsection panels to the main section panels.
            mSection1.add(mSection1_s1, BorderLayout.WEST);
            mSection1.add(mSection1_s2, BorderLayout.CENTER);
            mSection2.add(mSection2_s1);

            // Add the main section panels to the SellPanel.
            SellPanel.add(mSection1);
            SellPanel.add(mSection2);

            return SellPanel;
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            // Add the SellPanel to the ContentPane if selected
            // from the MenuBar.
            AddToContentPane(SellPanel());
        }
    }

    /**
     * ActionListener for updating.
     */
    private class UpdateListener implements ActionListener
    {
        private final JPanel symbolPanel = Label_TextField_Panel("Symbol", 5, false,2);
        private final JPanel namePanel = Label_TextField_Panel("Name", 15,false,13);
        private final JPanel pricePanel = Label_TextField_Panel("Price", 4,true,17);

        private final JTextField symbolTF = (JTextField)(symbolPanel.getComponent(2));
        private final JTextField nameTF = (JTextField)(namePanel.getComponent(2));
        private final JTextField priceTF = (JTextField)(pricePanel.getComponent(2));

        private final JPanel ButtonPanel = ButtonPanel(new ButtonActionListener[]
            {
                new PrevButtonActionListener("prev"),
                new NextButtonActionListener("next"),
                new SaveButtonActionListener("save")
            },
    20
        );

        private final JScrollPane messageBox = MessageBox(320);

        private final JTextArea jta = (JTextArea) ((JPanel)((JViewport)messageBox.getComponent(0)).getComponent(0)).getComponent(0);

        // Store the current index for investment to display.
        private int index = 0;

        /**
         * ButtonActionListener for previous button.
         */
        private class PrevButtonActionListener extends ButtonActionListener
        {
            PrevButtonActionListener(String name)
            {
                super(name);

                // Check whether the button should be enabled, based on index
                // and whether investments exist.
                if (!Portfolio.InvestmentExists() || index == 0)
                {
                    // Check that the ButtonPanel has been created
                    // as the ButtonActionListener gets created within
                    // the ButtonPanel.
                    if (ButtonPanel != null && ButtonPanel.getComponent(0) != null)
                        ButtonPanel.getComponent(0).setEnabled(false);
                }
            }

            @Override
            public void actionPerformed(ActionEvent e)
            {
                // Disable if index after clicking is 0.
                if (--index == 0)
                    ButtonPanel.getComponent(0).setEnabled(false);

                // Always enable next button after clicking previous.
                ButtonPanel.getComponent(2).setEnabled(true);

                var i = Portfolio.GetInvestment(index);

                // This should never happen, but I've added it to avoid
                // NullPointerException warnings in the editor.
                if (i == null)
                {
                    symbolTF.setText(null);
                    nameTF.setText(null);
                    priceTF.setText(null);

                    index = 0;
                    return;
                }

                // Set the text fields with the values from the investment.
                symbolTF.setText(i.getSymbol());
                nameTF.setText(i.getName());
                priceTF.setText(String.valueOf(i.getPrice()));
            }
        }

        /**
         * ButtonActionListener for next button.
         */
        private class NextButtonActionListener extends ButtonActionListener
        {
            NextButtonActionListener(String name)
            {
                super(name);

                // Similar logic to previous button, but check that index is not
                // at the max.
                if (!Portfolio.InvestmentExists() || Portfolio.Size() < 2 || index == Portfolio.Size() - 1)
                {
                    if (ButtonPanel != null && ButtonPanel.getComponent(2) != null)
                    {
                        ButtonPanel.getComponent(2).setEnabled(false);
                        System.out.println("disable");
                    }
                }
            }

            // Similar logic to previous button.
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (++index == Portfolio.Size() - 1)
                    ButtonPanel.getComponent(2).setEnabled(false);

                ButtonPanel.getComponent(0).setEnabled(true);

                var i = Portfolio.GetInvestment(index);

                if (i == null)
                {
                    symbolTF.setText(null);
                    nameTF.setText(null);
                    priceTF.setText(null);

                    index = Portfolio.Size() - 1;
                    return;
                }

                symbolTF.setText(i.getSymbol());
                nameTF.setText(i.getName());
                priceTF.setText(String.valueOf(i.getPrice()));
            }
        }

        /**
         * ButtonActionListener for save button.
         */
        private class SaveButtonActionListener extends ButtonActionListener
        {
            SaveButtonActionListener(String name)
            {
                super(name);

                // Check that there is at least one investment.
                if (!Portfolio.InvestmentExists())
                {
                    // Same logic as with previous and next buttons.
                    if (ButtonPanel != null && ButtonPanel.getComponent(4) != null)
                        ButtonPanel.getComponent(4).setEnabled(false);
                }
            }

            @Override
            public void actionPerformed(ActionEvent e)
            {
                // Try to update investment with (trimmed) input values.
                // Display result in message box.
                try
                {
                    String result = Portfolio.Update
                    (
                        symbolTF.getText().trim(),
                        nameTF.getText().trim(),
                        priceTF.getText().trim()
                    );

                    jta.setText(jta.getText() + result);
                }
                catch (Exception ex)
                {
                    // Display warning/error message in box.
                    jta.setText(jta.getText() + ex.getMessage());
                }
            }
        }

        /**
         * Create the JPanel for updating.
         * @return THe JPanel for updating.
         */
        private JPanel UpdatePanel()
        {
            // Create the JPanel.
            JPanel UpdatePanel = new JPanel();

            UpdatePanel.setBackground(white);
            UpdatePanel.setLayout(new BoxLayout(UpdatePanel, Y_AXIS));

            // Upper section panel.
            JPanel mSection1 = new JPanel();

            // Lower section panel.
            JPanel mSection2 = new JPanel();

            mSection1.setBackground(white);
            mSection1.setLayout(new BorderLayout());
            mSection1.setMaximumSize(new Dimension((int) GetScreenWidth(), 200));

            mSection2.setLayout(new GridLayout(1,1));
            mSection2.setBackground(white);
            mSection2.setMaximumSize(new Dimension((int) GetScreenWidth(), (int)GetScreenHeight() - 210));

            // Subsections for the main section panels.
            JPanel mSection1_s1, mSection1_s2, mSection2_s1;

            // Left subsection panel.
            mSection1_s1 = new JPanel();
            mSection1_s1.setLayout(new BoxLayout(mSection1_s1, Y_AXIS));

            // Right subsection panel.
            mSection1_s2 = new JPanel();
            mSection1_s2.setLayout(new FlowLayout(FlowLayout.CENTER));

            mSection1_s1.add(LabelPanel("Updating investments", 0));
            mSection1_s1.add(symbolPanel);
            mSection1_s1.add(namePanel);
            mSection1_s1.add(pricePanel);

            mSection1_s2.add(ButtonPanel);

            // Clear text fields.
            symbolTF.setText(null);
            nameTF.setText(null);
            priceTF.setText(null);

            if (Portfolio.InvestmentExists())
            {
                var i = Portfolio.GetInvestment(0);

                // Should never be null if an investment exists,
                // just to avoid NullPointerException warnings in editor.
                if (i != null)
                {
                    symbolTF.setText(i.getSymbol());
                    nameTF.setText(i.getName());
                    priceTF.setText(String.valueOf(i.getPrice()));
                    priceTF.setEditable(true);
                    ButtonPanel.getComponent(4).setEnabled(true);
                }

                if (index == 0)
                    ButtonPanel.getComponent(0).setEnabled(false);

                if (index == Portfolio.Size() - 1)
                    ButtonPanel.getComponent(2).setEnabled(false);
                else if (Portfolio.Size() > 1)
                    ButtonPanel.getComponent(2).setEnabled(true);
            }
            else
            {
                // Disable all buttons if no investment exists.
                ButtonPanel.getComponent(0).setEnabled(false);
                ButtonPanel.getComponent(2).setEnabled(false);
                ButtonPanel.getComponent(4).setEnabled(false);
                priceTF.setEditable(false);
            }

            // Disable previous button.
            if (index == 0)
                ButtonPanel.getComponent(0).setEnabled(false);

            // Subsection of lower main panel.
            mSection2_s1 = new JPanel();
            mSection2_s1.setLayout(new BoxLayout(mSection2_s1, Y_AXIS));

            // Add message label and box.
            mSection2_s1.add(LabelPanel("Messages", 20));
            mSection2_s1.add(messageBox);

            // Clear message box.
            jta.setText(null);

            // Add subsection panels to main section panels.
            mSection1.add(mSection1_s1, BorderLayout.WEST);
            mSection1.add(mSection1_s2, BorderLayout.CENTER);
            mSection2.add(mSection2_s1);

            // Add main section panels to UpdatePanel.
            UpdatePanel.add(mSection1);
            UpdatePanel.add(mSection2);

            return UpdatePanel;
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            // Add UpdatePanel to ContentPane when selected
            // from MenuBar.
            AddToContentPane(UpdatePanel());
        }
    }

    /**
     * ActionListener for getting gain.
     */
    private class GetGainListener implements ActionListener
    {
        private final JPanel gainPanel = Label_TextField_Panel("Total gain", 10, false,5);

        private final JTextField gainTF = (JTextField)(gainPanel.getComponent(2));

        private final JScrollPane messageBox = MessageBox(220);

        private final JTextArea jta = (JTextArea) ((JPanel)((JViewport)messageBox.getComponent(0)).getComponent(0)).getComponent(0);

        /**
         * Create the JPanel for getting gain.
         * @return The JPanel for getting gain.
         */
        private JPanel GetGainPanel()
        {
            // Create the JPanel.
            JPanel GetGainPanel = new JPanel();

            GetGainPanel.setBackground(white);
            GetGainPanel.setLayout(new BoxLayout(GetGainPanel, Y_AXIS));

            // Upper section panel.
            JPanel mSection1 = new JPanel();

            // Lower section panel.
            JPanel mSection2 = new JPanel();

            mSection1.setBackground(white);
            mSection1.setLayout(new GridLayout(1,2, 50, 0));
            mSection1.setMaximumSize(new Dimension((int) GetScreenWidth(), 100));

//            mSection2.setLayout(new BoxLayout(mSection2, Y_AXIS));
            mSection2.setLayout(new GridLayout(1,1));
            mSection2.setBackground(white);
            mSection2.setMaximumSize(new Dimension((int) GetScreenWidth(), (int)GetScreenHeight() - 110));

            // Subsection panels.
            JPanel mSection1_s1, mSection2_s1;

            // Create subsection panel for upper main panel.
            mSection1_s1 = new JPanel();
            mSection1_s1.setLayout(new BoxLayout(mSection1_s1, Y_AXIS));

            // Create subsection panel for lower main panel.
            mSection2_s1 = new JPanel();
            mSection2_s1.setLayout(new BoxLayout(mSection2_s1, Y_AXIS));

            // Add label and text field.
            mSection1_s1.add(LabelPanel("Getting total gain", 0));
            mSection1_s1.add(gainPanel);

            // Add message label and box.
            mSection2_s1.add(LabelPanel("Individual gains", 20));
            mSection2_s1.add(messageBox);

            if (!Portfolio.InvestmentExists())
            {
                // Clear text field and message box if no investments.
                gainTF.setText(null);
                jta.setText(null);
            }
            else
            {
                // Wrap call to Portfolio.GetGain in a try-catch.
                // Try to set text field with value, and write
                // individual gains to message box.
                try
                {
                    gainTF.setText(String.format("$%.2f", Portfolio.GetGain()));

                    jta.setText(null);

                    int size = Portfolio.Size();

                    // Write individual gains to message box.
                    for (int i = 0; i < size; ++i)
                    {
                        var investment = Portfolio.GetInvestment(i);

                        jta.setText(jta.getText() + String.format("'%s' [%s] : $%.2f\n", investment.getSymbol(), investment.getName(), Portfolio.GetGain(i)));
                    }
                }
                catch (Exception e)
                {
                    // Write warning/error to message box.
                    jta.setText(e.getMessage());
                }
            }

            // Add subsection panels to main panels.
            mSection1.add(mSection1_s1);
            mSection2.add(mSection2_s1);

            // Add main panels to GetGainPanel.
            GetGainPanel.add(mSection1);
            GetGainPanel.add(mSection2);

            return GetGainPanel;
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            // Add GetGainPanel to ContentPane when selected
            // from MenuBar.
            AddToContentPane(GetGainPanel());
        }
    }

    /**
     * ActionListener for searching.
     */
    private class SearchListener implements ActionListener
    {
        private final JPanel symbolPanel = Label_TextField_Panel("Symbol", 5, true,62);
        private final JPanel keywordsPanel = Label_TextField_Panel("Name keywords", 15, true, 0);
        private final JPanel lPricePanel = Label_TextField_Panel("Low price", 4, true,44);
        private final JPanel hPricePanel = Label_TextField_Panel("High price", 4, true,40);

        private final JTextField symbolTF = (JTextField)(symbolPanel.getComponent(2));
        private final JTextField keywordsTF = (JTextField)(keywordsPanel.getComponent(2));
        private final JTextField lPriceTF = (JTextField)(lPricePanel.getComponent(2));
        private final JTextField hPriceTF = (JTextField)(hPricePanel.getComponent(2));

        private final JScrollPane messageBox = MessageBox(320);

        private final JTextArea jta = (JTextArea) ((JPanel)((JViewport)messageBox.getComponent(0)).getComponent(0)).getComponent(0);

        /**
         * ButtonActionListener for reset button.
         */
        private class ResetButtonActionListener extends ButtonActionListener
        {
            ResetButtonActionListener(String name)
            {
                super(name);
            }

            @Override
            public void actionPerformed(ActionEvent e)
            {
                // Clear all text fields.
                symbolTF.setText(null);
                keywordsTF.setText(null);
                lPriceTF.setText(null);
                hPriceTF.setText(null);

                // Display reset to message box.
                jta.setText(jta.getText() + "Reset.\n");
            }
        }

        /**
         * ButtonActionListener for search button.
         */
        private class SearchButtonActionListener extends ButtonActionListener
        {
            SearchButtonActionListener(String name)
            {
                super(name);
            }

            @Override
            public void actionPerformed(ActionEvent e)
            {
                // Attempt to search with (trimmed) user input
                // and display the result to the message.
                try
                {
                    String result = Portfolio.Search
                    (
                        symbolTF.getText().trim(),
                        keywordsTF.getText().trim(),
                        lPriceTF.getText().trim(),
                        hPriceTF.getText().trim()
                    );

                    jta.setText(result);
                }
                catch (Exception ex)
                {
                    // Display the warning/error to the message box.
                    jta.setText(ex.getMessage());
                }
            }
        }

        /**
         * Create the JPanel for searching.
         * @return The JPanel for searching.
         */
        private JPanel SearchPanel()
        {
            // Create the JPanel.
            JPanel SearchPanel = new JPanel();

            SearchPanel.setBackground(white);
            SearchPanel.setLayout(new BoxLayout(SearchPanel, Y_AXIS));

            // Upper section panel.
            JPanel mSection1 = new JPanel();

            // Lower section panel.
            JPanel mSection2 = new JPanel();

            mSection1.setBackground(white);
            mSection1.setLayout(new BorderLayout());
            mSection1.setMaximumSize(new Dimension((int) GetScreenWidth(), 200));

            mSection2.setLayout(new GridLayout(1,1));
            mSection2.setBackground(white);
            mSection2.setMaximumSize(new Dimension((int) GetScreenWidth(), (int)GetScreenHeight() - 210));

            // Subsection panel.
            JPanel mSection1_s1, mSection1_s2, mSection2_s1;

            // Create the left subsection panel for the upper panel.
            mSection1_s1 = new JPanel();
            mSection1_s1.setLayout(new BoxLayout(mSection1_s1, Y_AXIS));

            // Create the right subsection panel for the upper panel.
            mSection1_s2 = new JPanel();
            mSection1_s2.setLayout(new FlowLayout(FlowLayout.CENTER));

            mSection1_s1.add(LabelPanel("Searching investments", 0));
            mSection1_s1.add(symbolPanel);
            mSection1_s1.add(keywordsPanel);
            mSection1_s1.add(lPricePanel);
            mSection1_s1.add(hPricePanel);

            // Add the buttons to the right subsection panel.
            mSection1_s2.add(ButtonPanel(new ButtonActionListener[]
                {
                    new ResetButtonActionListener("reset"),
                    new SearchButtonActionListener("search"),
                },
        40)
            );

            // Create the subsection for the lower panel.
            mSection2_s1 = new JPanel();
            mSection2_s1.setLayout(new BoxLayout(mSection2_s1, Y_AXIS));

            // Add message label and box.
            mSection2_s1.add(LabelPanel("Search results", 20));
            mSection2_s1.add(messageBox);

            // Clear message box.
            jta.setText(null);

            // Add the subsection panels to main section panels.
            mSection1.add(mSection1_s1, BorderLayout.WEST);
            mSection1.add(mSection1_s2, BorderLayout.CENTER);
            mSection2.add(mSection2_s1);

            // Add main section panels to SearchPanel.
            SearchPanel.add(mSection1);
            SearchPanel.add(mSection2);

            return SearchPanel;
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            // Add the SearchPanel to the ContentPane when
            // selected by the MenuBar.
            AddToContentPane(SearchPanel());
        }
    }

    /**
     * ActionListener for quitting.
     */
    private class QuitListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            // Add the QuitPanel to the ContentPane
            // when selected from the MenuBar.
            AddToContentPane(QuitPanel());

            // Try to save investments using Portfolio.Exit.
            try
            {
                Portfolio.Exit();
            }
            catch(Exception ignored)
            {
            }

            // Exit the program.
            System.exit(0);
        }
    }

    /**
     * ActionListener for buttons.
     */
    private abstract class ButtonActionListener implements ActionListener
    {
        ButtonActionListener(String name)
        {
            super();

            this.name = name;
        }

        // Use this to assign the name of the button the
        // listener is set to.
        private final String name;

        public String GetName()
        {
            return name;
        }

        @Override
        public void actionPerformed(ActionEvent e) {}
    }

    /**
     * Creates a JPanel with a JLabel.
     * @param text The text to set to the JLabel.
     * @param lBorder The distance for the leftmost area.
     * @return A JPanel with a JLabel.
     */
    private static JPanel LabelPanel(String text, int lBorder)
    {
        // Create the JPanel.
        JPanel LabelPanel = new JPanel();

        LabelPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        // Create the JLabel with the text.
        JLabel Label = new JLabel(text);

        // Add the label to the panel.
        LabelPanel.add(Label);

        LabelPanel.setMaximumSize(new Dimension((int) GetScreenWidth(), 40));

        // Create a border for the panel.
        EmptyBorder(LabelPanel, 0,lBorder,0,0);

        return LabelPanel;
    }

    /**
     * Creates a JPanel with a JLabel and JTextField.
     * @param labelText Text for the JLabel.
     * @param fieldCols Number of characters on one line of the JTextField.
     * @param editable Is the JTextField editable.
     * @param strutWidth Distance between the JLabel and JTextField.
     * @return A JPanel with a JLabel and JTextField.
     */
    private static JPanel Label_TextField_Panel(String labelText, int fieldCols, boolean editable, int strutWidth)
    {
        // Create the panel.
        JPanel panel = new JPanel();

        panel.setLayout(new FlowLayout(FlowLayout.LEFT));

        // Create the label.
        JLabel panelLabel = new JLabel(labelText);

        // Create the text field.
        JTextField panelTF = new JTextField(fieldCols);
        panelTF.setEditable(editable);
        panelTF.setCursor(textCursor);

        // Add the label to the panel.
        panel.add(panelLabel);

        // Add space between the label and text field.
        panel.add(Box.createHorizontalStrut(strutWidth));

        // Add the text field to the panel.
        panel.add(panelTF);

        // Create a border for the panel.
        EmptyBorder(panel, 0, 20,0,0);

        return panel;
    }

    /**
     * Create a JPanel for buttons.
     * @param listeners Array of ButtonActionListeners that
     *                  determine the number of JButtons to create
     *                  and the names of each JButton.
     * @param ySpacing Spacing between each JButton.
     * @return A JPanel for buttons.
     */
    private static JPanel ButtonPanel(ButtonActionListener[] listeners, int ySpacing)
    {
        // Create the panel
        JPanel ButtonPanel = new JPanel();

        ButtonPanel.setLayout(new BoxLayout(ButtonPanel, Y_AXIS));

        // Create a JButton for each ButtonActionListener,
        // add the ButtonActionListener to the newly created
        // button, and add the newly created button to the
        // panel.
        for (int i = 0; i < listeners.length; ++i)
        {
            JButton btn = new JButton(listeners[i].GetName());
            btn.setMaximumSize(new Dimension(95, 50));
            btn.setFocusable(false);
            btn.setCursor(handCursor);
            btn.addActionListener(listeners[i]);

            ButtonPanel.add(btn);

            // Only add a space if not at the last button.
            if (i != listeners.length - 1)
                ButtonPanel.add(Box.createVerticalStrut(ySpacing));
        }

        // Border for the ButtonPanel using the same spacing as used between buttons.
        EmptyBorder(ButtonPanel, ySpacing,0,0,0);

        return ButtonPanel;
    }

    /**
     * Creates the JScrollPane for the message box.
     * @param maxSizeYOffset Basically the size of the upper section panel.
     *                       This is used to avoid the scroll pane from
     *                       having a maximum size that goes beyond the window
     *                       size.
     * @return The JScrollPane for the message box.
     */
    private static JScrollPane MessageBox(int maxSizeYOffset)
    {
        // Create the panel.
        JPanel MessagePanel = new JPanel();
        MessagePanel.setLayout(new BoxLayout(MessagePanel, Y_AXIS));

        // Create the message box.
        JTextArea MessageTA = new JTextArea();
        MessageTA.setBackground(Color.white);
        MessageTA.setWrapStyleWord(true);
        MessageTA.setFocusable(true);
        MessageTA.setLineWrap(true);
        MessageTA.setEditable(false);
        MessageTA.setText(null);
        MessageTA.setCursor(textCursor);

        // Add the JTextArea to the panel.
        MessagePanel.add(MessageTA);

        MessagePanel.setMaximumSize(new Dimension((int) GetScreenWidth(), (int)GetScreenHeight() - maxSizeYOffset));

        // UI. Create a line border (for showing color) and an empty border.
        LineBorder(MessageTA);
        EmptyBorder(MessagePanel, 20, 24,20,24);

        // Return a JScrollPane whose view model is the MessagePanel.
        // Show horizontal and vertical scroll bars at all times.
        return new
                JScrollPane(MessagePanel,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    }

    /**
     * Creates the JPanel for quitting.
     * @return The JPanel for quitting.
     */
    public static JPanel QuitPanel()
    {
        // Create the panel and set its background to white.
        JPanel QuitPanel = new JPanel();

        QuitPanel.setBackground(white);

        return QuitPanel;
    }

    /**
     * Convenience method. Calls
     * component.SetBorder(BorderFactor.createEmptyBorder(...)),
     * using parameters provided.
     * @param component JComponent to make border for.
     * @param top Top border.
     * @param left Left border.
     * @param bottom Bottom border.
     * @param right Right border.
     */
    private static void EmptyBorder(JComponent component, int top, int left, int bottom, int right)
    {
        component.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
    }

    /**
     * Convenience method. Creates a
     * grey border for the JComponent
     * argument.
     * @param component The JComponent to create the
     *                  color border for.
     */
    private static void LineBorder(JComponent component)
    {
        component.setBorder(BorderFactory.createLineBorder(Color.gray, 1));
    }
}
