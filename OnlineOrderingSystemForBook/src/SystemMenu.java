import dataObject.Book;
import dataObject.BookOrder;
import dataObject.Order;
import database.Database;
import repository.BookRepository;
import repository.OrderRepository;

import java.sql.SQLException;
import java.util.*;

public class SystemMenu {
    private static Database db = new Database();

    public static void main(String[] args) {
        try {
            while (true) {
                mainMenu();
                int option = fetchPromptOption();

                if (option == 4) {
                    return;
                }
                goFunctionBranch(option);
                fetchPromptValue(); // placeholder prompt before returning to main menu
            }
        } catch (InputMismatchException e) {
            System.out.println("Please input the right format");
        }
    }

    private static void mainMenu() {
        System.out.println();
        System.out.println("============Welcome to the Book Ordering Management System============");
        System.out.println("+ System Date: " + java.sql.Timestamp.from(java.time.Instant.now()));

        if (db.checkTablesExist()) {
            List<String> tableNames = new ArrayList<>();
            tableNames.add("Author");
            tableNames.add("Book");
            tableNames.add("Book_author");
            tableNames.add("Book_ordered");
            tableNames.add("Customer");
            tableNames.add("Order");
            tableNames.add("Order_by");

            int[] counts = getDatabaseRecordsNumber(tableNames);
            System.out.print("+ Database records: ");
            for (int i = 0; i < counts.length; i++) {
                System.out.print(tableNames.get(i) + "(" + counts[i] + "), ");
            }
            System.out.println();
        } else {
            System.out.println("Note: database does not exist!");
        }

        System.out.println("-------------------------------------");
        System.out.printf("[1] Database initialization%n[2] Customer operations%n[3] Bookstore operations%n[4] Quit%n%n");
        System.out.println("Please enter your option: ");

    }

    private static int[] getDatabaseRecordsNumber(List<String> tableNames) {
        int[] counts = new int[tableNames.size()];
        for (int i = 0; i < tableNames.size(); i++) {
            counts[i] = db.getCountNumOfTable(tableNames.get(i));
        }
        return counts;
    }

    private static int fetchPromptOption() {
        Scanner scan = new Scanner(System.in);
        return scan.nextInt();
    }

    private static String fetchPromptValue() {
        Scanner scan = new Scanner(System.in);
        return scan.nextLine();
    }

    private static void goFunctionBranch(int option) {
        switch (option) {
            case 1 -> databaseOperation();
            case 2 -> customerOperation();
            case 3 -> bookstoreOperation();
            default -> System.out.println("Invalid input");
        }
    }

    private static void databaseOperation() {
        System.out.println("Choose a database function:");
        System.out.println("[1] create tables");
        System.out.println("[2] initialize with records");
        System.out.println("[3] drop existing records");
        System.out.println("[4] return to main menu");

        Database db = new Database();

        int option = fetchPromptOption();
        try {
            switch (option) {
                case 1 -> db.initializeTables();
                case 2 -> db.dbLoadLocalRecords();
                case 3 -> db.dbDropRecords();
                case 4 -> {
                }
                default -> {
                    System.out.println("Please input the right inventory shipping status number");
                    databaseOperation();
                }
            }

            //db.testGetBooks();
        } catch (SQLException se) {
            // handle JDBC error
            se.printStackTrace();
        } catch (Exception e) {
            // handle Class.forName error
            e.printStackTrace();
        }

    }

    private static void customerOperation() {
        System.out.println("Choose a function for customer operation:");
        System.out.println("[1] Search books");
        System.out.println("[2] Place an order");
        System.out.println("[3] Check orders history");
        System.out.println("[4] return to main menu");

        int option = fetchPromptOption();
        switch (option) {
            case 1 -> searchBooks();
            case 2 -> placeBookOrders();
            case 3 -> checkOrderHistory();
            case 4 -> {
            }
            default -> {
                System.out.println("Please input the right inventory shipping status number");
                databaseOperation();
            }
        }
    }

    private static void searchBooks() {
        System.out.println("Search for book(s):");
        System.out.println("[1] search by ISBN");
        System.out.println("[2] search by title");
        System.out.println("[3] search by author name");
        int searchOption = fetchPromptOption();

        System.out.println("Input the search value:");
        String searchValue = fetchPromptValue();

        try {
            BookRepository bookRepository = new BookRepository();
            List<Book> books = new ArrayList<>();
            switch (searchOption) {
                case 1 -> books = bookRepository.findBookByISBN(searchValue);
                case 2 -> books = bookRepository.findBookByTitle(searchValue);
                case 3 -> books = bookRepository.findBooksByAuthorName(searchValue);
                default -> System.out.println("Invalid input. Try the operation again.");
            }

            if (books.size() > 0) {
                Book.outputList(books);
            } else {
                System.out.println("No such book(s) can be found!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void placeBookOrders() {
        List<BookOrder> bookOrders = fetchPromptBookOrders();

        try {
            OrderRepository orderRepository = new OrderRepository();
            orderRepository.placeOrders(bookOrders);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<BookOrder> fetchPromptBookOrders() {
        System.out.println("Input an uid:");
        String uid = fetchPromptValue();

        List<BookOrder> bookOrders = new ArrayList<>();
        System.out.println("Input the number of orders you want to place:");
        int orderCount = fetchPromptOption();

        Set<String> ISBNs = new HashSet<>();
        for (int i = 0; i < orderCount; i++) {
            System.out.println("Input the book's ISBN:");
            String ISBN = fetchPromptValue();
            System.out.println("Input the order quantity:");
            int quantity = fetchPromptOption();

            BookOrder bookOrder = new BookOrder(uid, ISBN, quantity);
            List<String> invalidOrderReasons = bookOrder.validate();
            if (invalidOrderReasons.size() == 0 && !ISBNs.contains(ISBN)) {
                bookOrders.add(bookOrder);
                ISBNs.add(ISBN);
            } else {
                System.out.println("The order for this book has invalid parameter(s). This order is skipped.");
                System.out.println("Reasons:");
                for (String reason : invalidOrderReasons) {
                    System.out.println("\t" + reason);
                }
                if (ISBNs.contains(ISBN)) {
                    System.out.println("\t" + "the ISBN was already contained in previous input");
                }
            }
        }

        return bookOrders;
    }

    private static void checkOrderHistory() {
        System.out.println("Input an uid:");
        String uid = fetchPromptValue();

        try {
            OrderRepository orderRepository = new OrderRepository();
            List<Order> orders = orderRepository.findOrdersByUid(uid);

            if (orders.size() > 0) {
                Order.outputList(orders);
            } else {
                System.out.println("No such order(s) can be found!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void bookstoreOperation() {
        System.out.println("Choose a function for bookstore operation:");
        System.out.println("[1] update order");
        System.out.println("[2] query order");
        System.out.println("[3] find N most popular books");
        System.out.println("[4] return to main menu");
        int searchOption = fetchPromptOption();

        try {
            switch (searchOption) {
                case 1 -> updateOrder();
                case 2 -> queryOrder();
                case 3 -> findMostPopularBooks();
                case 4 -> {
                }
                default -> System.out.println("Invalid input. Try the operation again.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void updateOrder() {
        System.out.println("Input order id:");
        String orderId = fetchPromptValue();

        System.out.println("Input user id:");
        String uid = fetchPromptValue();

        System.out.println("Input shipping status (shipped/ordered/received):");
        String shippingStatus = fetchPromptValue();

        Order order = new Order(null, orderId, uid, null, shippingStatus);

        List<String> invalidOrderReasons = Order.validateUpdate(order);
        if (invalidOrderReasons.size() > 0) {
            System.out.println("Order is invalid. Reasons:");
            for (String reason : invalidOrderReasons) {
                System.out.println("\t" + reason);
            }
        } else {
            OrderRepository orderRepository = new OrderRepository();
            boolean updated = orderRepository.updateOrder(order);
            if (updated) {
                System.out.println("Updated order successfully!");
            } else {
                System.out.println("Updated order fail!");
            }
        }
    }

    private static void queryOrder() {
        System.out.println("Input the shipping status (shipped/ordered/received) to query orders:");
        String shippingStatus = fetchPromptValue();

        OrderRepository orderRepository = new OrderRepository();
        List<Order> orders = orderRepository.findOrdersByShippingStatus(shippingStatus);
        Order.outputList(orders);
    }

    private static void findMostPopularBooks() {
        System.out.println("Input the value of N:");
        int topN = fetchPromptOption();

        BookRepository bookRepository = new BookRepository();
        List<Book> books = bookRepository.findMostPopularBooks(topN);
        Book.outputList(books);
    }
}
