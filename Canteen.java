import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

abstract class Customer {
    private String status;

    public Customer(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public abstract double calculateDiscount(double totalAmount);
}

class StudentCustomer extends Customer {
    public StudentCustomer() {
        super("Student");
    }

    @Override
    public double calculateDiscount(double totalAmount) {
        if (totalAmount >= 500) {
            return totalAmount * 0.15;
        } else {
            return totalAmount * 0.10;
        }
    }
}

class RegularCustomer extends Customer {
    public RegularCustomer() {
        super("Regular");
    }

    @Override
    public double calculateDiscount(double totalAmount) {
        if (totalAmount >= 500) {
            return totalAmount * 0.05;
        } else {
            return 0.0;
        }
    }
}

class MenuItem {
    private String name;
    private double price;

    public MenuItem(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return name + " - $" + String.format("%.2f", price);
    }
}

class Menu {
    private MenuItem[] items;

    public Menu() {
        items = new MenuItem[]{
            new MenuItem("Adobo Rice Meal", 85.00),
            new MenuItem("Sinigang na Baboy", 95.00),
            new MenuItem("Fried Chicken with Rice", 75.00),
            new MenuItem("Spaghetti", 60.00),
            new MenuItem("Iced Tea", 25.00)
        };
    }

    public void displayMenu() {
        System.out.println("\n===== CANTEEN MENU =====");
        for (int i = 0; i < items.length; i++) {
            System.out.println((i + 1) + ". " + items[i]);
        }
        System.out.println("=========================");
    }

    public boolean isValidItemNumber(int itemNumber) {
        return itemNumber >= 1 && itemNumber <= items.length;
    }

    public MenuItem getItem(int itemNumber) {
        return items[itemNumber - 1];
    }

    public int getItemCount() {
        return items.length;
    }
}

class OrderItem {
    private MenuItem menuItem;
    private int quantity;

    public OrderItem(MenuItem menuItem, int quantity) {
        this.menuItem = menuItem;
        this.quantity = quantity;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getSubtotal() {
        return menuItem.getPrice() * quantity;
    }
}

class ShoppingCart {
    private static final int MAX_TOTAL_QUANTITY = 10;

    private List<OrderItem> orderItems;

    public ShoppingCart() {
        orderItems = new ArrayList<>();
    }

    public void addItem(MenuItem item, int quantity) {
        orderItems.add(new OrderItem(item, quantity));
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public double getTotalAmount() {
        double total = 0.0;
        for (OrderItem oi : orderItems) {
            total += oi.getSubtotal();
        }
        return total;
    }

    public int getTotalQuantity() {
        int total = 0;
        for (OrderItem oi : orderItems) {
            total += oi.getQuantity();
        }
        return total;
    }

    public boolean isEmpty() {
        return orderItems.isEmpty();
    }

    public boolean wouldExceedLimit(int additionalQuantity) {
        return (getTotalQuantity() + additionalQuantity) > MAX_TOTAL_QUANTITY;
    }

    public static int getMaxTotalQuantity() {
        return MAX_TOTAL_QUANTITY;
    }
}

public class Canteen {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Menu menu = new Menu();

        int grandTotalQuantity = 0;
        double grandTotalBeforeDeductions = 0.0;
        double grandTotalDeductions = 0.0;

        String orderAgain = "Y";

        while (orderAgain.equalsIgnoreCase("Y")) {

            menu.displayMenu();

            ShoppingCart cart = new ShoppingCart();
            boolean addingItems = true;

            while (addingItems) {
                System.out.print("\nEnter item number: ");
                int itemNumber = Integer.parseInt(scanner.nextLine().trim());

                System.out.print("Enter quantity: ");
                int quantity = Integer.parseInt(scanner.nextLine().trim());

                boolean validItem = menu.isValidItemNumber(itemNumber);
                boolean validQuantity = (quantity >= 1 && quantity <= 10);

                if (!validItem || !validQuantity) {
                    System.out.println("\nInvalid order entered. This item will not be added.");
                    if (!validItem) {
                        System.out.println("- Item number must be between 1 and " + menu.getItemCount() + ".");
                    }
                    if (!validQuantity) {
                        System.out.println("- Quantity must be between 1 and 10.");
                    }
                    System.out.print("\nWould you like to add another item? (Y/N): ");
                    String again = scanner.nextLine().trim();
                    if (!again.equalsIgnoreCase("Y")) {
                        addingItems = false;
                    }
                    continue;
                }

                if (cart.wouldExceedLimit(quantity)) {
                    System.out.println("\nOrder has exceeded the maximum of "
                            + ShoppingCart.getMaxTotalQuantity() + " items per transaction.");
                    System.out.println("Proceeding to final computation...");
                    addingItems = false; // skip further prompts, go straight to checkout
                    continue;
                }

                MenuItem chosenItem = menu.getItem(itemNumber);
                cart.addItem(chosenItem, quantity);

                System.out.println("Added: " + quantity + " x " + chosenItem.getName()
                        + " ($" + String.format("%.2f", chosenItem.getPrice() * quantity) + ")");
                System.out.println("Current total quantity: " + cart.getTotalQuantity()
                        + " / " + ShoppingCart.getMaxTotalQuantity());

                if (cart.getTotalQuantity() >= ShoppingCart.getMaxTotalQuantity()) {
                    System.out.println("\nMaximum item limit reached. Proceeding to final computation...");
                    addingItems = false;
                    continue;
                }

                System.out.print("\nWould you like to add another item? (Y/N): ");
                String again = scanner.nextLine().trim();
                if (!again.equalsIgnoreCase("Y")) {
                    addingItems = false;
                }
            }

            Customer customer = null;
            if (!cart.isEmpty()) {
                System.out.print("\nAre you a student? (Y/N): ");
                String studentAnswer = scanner.nextLine().trim();

                if (studentAnswer.equalsIgnoreCase("Y")) {
                    customer = new StudentCustomer();
                } else {
                    customer = new RegularCustomer();
                }
            }

            if (cart.isEmpty()) {
                System.out.println("\nNo valid items were ordered in this transaction.");
            } else {
                double totalBeforeDiscount = cart.getTotalAmount();

                double discount = customer.calculateDiscount(totalBeforeDiscount);
                double finalAmount = totalBeforeDiscount - discount;

                System.out.println("\n===== TRANSACTION RECEIPT =====");
                System.out.println("Customer Type: " + customer.getStatus());
                System.out.println("--------------------------------");
                for (OrderItem oi : cart.getOrderItems()) {
                    System.out.println(oi.getQuantity() + " x " + oi.getMenuItem().getName()
                            + " = $" + String.format("%.2f", oi.getSubtotal()));
                }
                System.out.println("--------------------------------");
                System.out.println("Total quantity: " + cart.getTotalQuantity());
                System.out.println("Amount before deduction: $" + String.format("%.2f", totalBeforeDiscount));
                System.out.println("Deduction: $" + String.format("%.2f", discount));
                System.out.println("Amount to pay: $" + String.format("%.2f", finalAmount));
                System.out.println("================================");

                grandTotalQuantity += cart.getTotalQuantity();
                grandTotalBeforeDeductions += totalBeforeDiscount;
                grandTotalDeductions += discount;
            }


            System.out.print("\nDo you want to order again? (Y/N): ");
            orderAgain = scanner.nextLine().trim();
        }

        double grandFinalAmount = grandTotalBeforeDeductions - grandTotalDeductions;

        System.out.println("\n===== FINAL SUMMARY (ALL TRANSACTIONS) =====");
        System.out.println("Total quantity of items purchased: " + grandTotalQuantity);
        System.out.println("Total amount before deductions: $" + String.format("%.2f", grandTotalBeforeDeductions));
        System.out.println("Total deduction: $" + String.format("%.2f", grandTotalDeductions));
        System.out.println("Final amount to pay: $" + String.format("%.2f", grandFinalAmount));
        System.out.println("==============================================");

        scanner.close();
    }
}