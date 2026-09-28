import java.util.*;
import java.io.*;

// Represents a stock available in the market
class Stock {
    private String symbol;
    private String companyName;
    private double price;

    public Stock(String symbol, String companyName, double price) {
        this.symbol = symbol;
        this.companyName = companyName;
        this.price = price;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getCompanyName() {
        return companyName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}

// Represents a stock owned by the user
class Holding {
    private String symbol;
    private int quantity;
    private double averagePrice;

    public Holding(String symbol, int quantity, double averagePrice) {
        this.symbol = symbol;
        this.quantity = quantity;
        this.averagePrice = averagePrice;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getAveragePrice() {
        return averagePrice;
    }

    public void buyMore(int quantity, double price) {
        double totalOldValue = this.quantity * this.averagePrice;
        double totalNewValue = quantity * price;

        this.quantity += quantity;
        this.averagePrice =
                (totalOldValue + totalNewValue) / this.quantity;
    }

    public void sell(int quantity) {
        this.quantity -= quantity;
    }
}

// Main class
public class StockTradingPlatform {

    static Scanner scanner = new Scanner(System.in);

    static ArrayList<Stock> market = new ArrayList<>();
    static ArrayList<Holding> portfolio = new ArrayList<>();

    static double balance = 100000.00;

    public static void main(String[] args) {

        loadMarketData();

        System.out.println("==============================================");
        System.out.println("        WELCOME TO STOCK TRADING PLATFORM");
        System.out.println("==============================================");

        int choice;

        do {

            displayMenu();

            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();

            switch (choice) {

                case 1:
                    displayMarket();
                    break;

                case 2:
                    buyStock();
                    break;

                case 3:
                    sellStock();
                    break;

                case 4:
                    viewPortfolio();
                    break;

                case 5:
                    viewAccountSummary();
                    break;

                case 6:
                    viewTransactions();
                    break;

                case 7:
                    System.out.println(
                            "\nThank you for using the Stock Trading Platform!");
                    break;

                default:
                    System.out.println("\nInvalid choice. Please try again.");
            }

        } while (choice != 7);

        scanner.close();
    }

    // ---------------- MENU ----------------

    static void displayMenu() {

        System.out.println("\n----------------------------------------------");
        System.out.println("                 MAIN MENU");
        System.out.println("----------------------------------------------");

        System.out.println("1. View Market Data");
        System.out.println("2. Buy Stock");
        System.out.println("3. Sell Stock");
        System.out.println("4. View Portfolio");
        System.out.println("5. Account Summary");
        System.out.println("6. Transaction History");
        System.out.println("7. Exit");

        System.out.println("----------------------------------------------");
    }

    // ---------------- MARKET DATA ----------------

    static void loadMarketData() {

        market.add(
                new Stock("TCS", "Tata Consultancy Services", 3500));

        market.add(
                new Stock("INFY", "Infosys Limited", 1800));

        market.add(
                new Stock("RELIANCE", "Reliance Industries", 2900));

        market.add(
                new Stock("WIPRO", "Wipro Limited", 550));

        market.add(
                new Stock("HDFC", "HDFC Bank", 1700));

        market.add(
                new Stock("ITC", "ITC Limited", 500));
    }

    static void displayMarket() {

        System.out.println("\n================ MARKET DATA ================");

        System.out.printf(
                "%-12s %-30s %-15s%n",
                "Symbol",
                "Company",
                "Price");

        System.out.println(
                "----------------------------------------------------------");

        for (Stock stock : market) {

            System.out.printf(
                    "%-12s %-30s Rs. %-11.2f%n",
                    stock.getSymbol(),
                    stock.getCompanyName(),
                    stock.getPrice());
        }
    }

    // ---------------- BUY STOCK ----------------

    static void buyStock() {

        displayMarket();

        System.out.print("\nEnter stock symbol: ");
        String symbol = scanner.next();

        Stock stock = findStock(symbol);

        if (stock == null) {

            System.out.println("Stock not found.");
            return;
        }

        System.out.print("Enter quantity: ");
        int quantity = scanner.nextInt();

        if (quantity <= 0) {

            System.out.println("Quantity must be greater than zero.");
            return;
        }

        double totalCost = stock.getPrice() * quantity;

        System.out.println("\n----------- ORDER DETAILS -----------");
        System.out.println("Stock      : " + stock.getSymbol());
        System.out.println("Quantity   : " + quantity);
        System.out.println("Price      : Rs. " + stock.getPrice());
        System.out.println("Total Cost : Rs. " + totalCost);

        if (totalCost > balance) {

            System.out.println("\nInsufficient account balance.");
            return;
        }

        System.out.print("\nConfirm purchase? (Y/N): ");
        String confirmation = scanner.next();

        if (!confirmation.equalsIgnoreCase("Y")) {

            System.out.println("Purchase cancelled.");
            return;
        }

        balance -= totalCost;

        Holding holding = findHolding(symbol);

        if (holding == null) {

            portfolio.add(
                    new Holding(
                            stock.getSymbol(),
                            quantity,
                            stock.getPrice()));

        } else {

            holding.buyMore(quantity, stock.getPrice());
        }

        String transaction =
                "BUY | " +
                stock.getSymbol() +
                " | Quantity: " +
                quantity +
                " | Price: Rs. " +
                stock.getPrice() +
                " | Amount: Rs. " +
                totalCost;

        saveTransaction(transaction);

        System.out.println("\n✓ Purchase successful!");
        System.out.println("Remaining Balance: Rs. " + balance);
    }

    // ---------------- SELL STOCK ----------------

    static void sellStock() {

        if (portfolio.isEmpty()) {

            System.out.println("\nYour portfolio is empty.");
            return;
        }

        viewPortfolio();

        System.out.print("\nEnter stock symbol: ");
        String symbol = scanner.next();

        Holding holding = findHolding(symbol);

        if (holding == null) {

            System.out.println("You do not own this stock.");
            return;
        }

        Stock stock = findStock(symbol);

        System.out.println(
                "Current Market Price: Rs. " + stock.getPrice());

        System.out.println(
                "Available Quantity: " + holding.getQuantity());

        System.out.print("Enter quantity to sell: ");
        int quantity = scanner.nextInt();

        if (quantity <= 0 ||
                quantity > holding.getQuantity()) {

            System.out.println("Invalid quantity.");
            return;
        }

        double totalAmount =
                stock.getPrice() * quantity;

        System.out.print("Confirm sale? (Y/N): ");
        String confirmation = scanner.next();

        if (!confirmation.equalsIgnoreCase("Y")) {

            System.out.println("Sale cancelled.");
            return;
        }

        holding.sell(quantity);

        balance += totalAmount;

        if (holding.getQuantity() == 0) {

            portfolio.remove(holding);
        }

        String transaction =
                "SELL | " +
                stock.getSymbol() +
                " | Quantity: " +
                quantity +
                " | Price: Rs. " +
                stock.getPrice() +
                " | Amount: Rs. " +
                totalAmount;

        saveTransaction(transaction);

        System.out.println("\n✓ Sale successful!");
        System.out.println("Amount Received: Rs. " + totalAmount);
        System.out.println("Current Balance: Rs. " + balance);
    }

    // ---------------- PORTFOLIO ----------------

    static void viewPortfolio() {

        System.out.println("\n================ PORTFOLIO =================");

        if (portfolio.isEmpty()) {

            System.out.println("No stocks in your portfolio.");
            return;
        }

        System.out.printf(
                "%-12s %-12s %-15s %-15s%n",
                "Symbol",
                "Quantity",
                "Avg Price",
                "Current Value");

        System.out.println(
                "----------------------------------------------------------");

        double totalPortfolioValue = 0;

        for (Holding holding : portfolio) {

            Stock stock = findStock(holding.getSymbol());

            double currentValue =
                    stock.getPrice() * holding.getQuantity();

            totalPortfolioValue += currentValue;

            System.out.printf(
                    "%-12s %-12d Rs. %-10.2f Rs. %-10.2f%n",
                    holding.getSymbol(),
                    holding.getQuantity(),
                    holding.getAveragePrice(),
                    currentValue);
        }

        System.out.println(
                "----------------------------------------------------------");

        System.out.println(
                "Total Portfolio Value: Rs. " +
                totalPortfolioValue);
    }

    // ---------------- ACCOUNT SUMMARY ----------------

    static void viewAccountSummary() {

        double portfolioValue = calculatePortfolioValue();

        double totalAccountValue =
                balance + portfolioValue;

        System.out.println("\n============== ACCOUNT SUMMARY ==============");

        System.out.printf(
                "Cash Balance          : Rs. %.2f%n",
                balance);

        System.out.printf(
                "Portfolio Value       : Rs. %.2f%n",
                portfolioValue);

        System.out.printf(
                "Total Account Value   : Rs. %.2f%n",
                totalAccountValue);

        System.out.println(
                "==============================================");
    }

    static double calculatePortfolioValue() {

        double total = 0;

        for (Holding holding : portfolio) {

            Stock stock = findStock(holding.getSymbol());

            if (stock != null) {

                total +=
                        stock.getPrice() *
                        holding.getQuantity();
            }
        }

        return total;
    }

    // ---------------- FIND STOCK ----------------

    static Stock findStock(String symbol) {

        for (Stock stock : market) {

            if (stock.getSymbol()
                    .equalsIgnoreCase(symbol)) {

                return stock;
            }
        }

        return null;
    }

    // ---------------- FIND HOLDING ----------------

    static Holding findHolding(String symbol) {

        for (Holding holding : portfolio) {

            if (holding.getSymbol()
                    .equalsIgnoreCase(symbol)) {

                return holding;
            }
        }

        return null;
    }

    // ---------------- TRANSACTION FILE ----------------

    static void saveTransaction(String transaction) {

        try {

            FileWriter writer =
                    new FileWriter(
                            "transactions.txt",
                            true);

            writer.write(transaction + "\n");

            writer.close();

        } catch (IOException e) {

            System.out.println(
                    "Unable to save transaction.");
        }
    }

    // ---------------- VIEW TRANSACTIONS ----------------

    static void viewTransactions() {

        System.out.println("\n============ TRANSACTION HISTORY ============");

        File file = new File("transactions.txt");

        if (!file.exists()) {

            System.out.println("No transactions available.");
            return;
        }

        try {

            Scanner fileReader =
                    new Scanner(file);

            boolean found = false;

            while (fileReader.hasNextLine()) {

                System.out.println(
                        fileReader.nextLine());

                found = true;
            }

            if (!found) {

                System.out.println(
                        "No transactions available.");
            }

            fileReader.close();

        } catch (IOException e) {

            System.out.println(
                    "Unable to read transaction history.");
        }
    }
}