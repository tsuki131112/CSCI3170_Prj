import dataObject.Book;
import dataObject.BookOrder;
import dataObject.Order;
import database.Database;
import repository.BookRepository;
import repository.OrderRepository;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
            int bookCount = getNumberOfBooks();
            int customerCount = getNumberOfCustomers();
            int orderCount = getNumberOfOrders();
            System.out.println("+ Database records: Books (" + bookCount + "), Customers (" + customerCount + "), Orders (" + orderCount + ")");
        } else {
            System.out.println("Note: database does not exist!");
            System.out.println("+ Database records: Books (0), Customers (0), Orders (0)");
        }

        System.out.println("-------------------------------------");
        System.out.printf("[1] Database initialization%n[2] Customer operations%n[3] Bookstore operations%n[4] Quit%n%n");
        System.out.println("Please enter your option: ");

    }

    //TODO change it with get data from db
    private static Integer getNumberOfBooks() {
        return db.getCountNumOfTable("Book");
    }

    private static Integer getNumberOfCustomers() {
        return db.getCountNumOfTable("Customer");
    }

    private static Integer getNumberOfOrders() {
        return db.getCountNumOfTable("`Order`");
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

        Database db = new Database();

        int option = fetchPromptOption();
        try {
            switch (option) {
                case 1 -> db.initializeTables();
                case 2 -> db.dbLoadLocalRecords();
                case 3 -> db.dbDropRecords();
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

        int option = fetchPromptOption();
        switch (option) {
            case 1 -> searchBooks();
            case 2 -> placeBookOrders();
            case 3 -> checkOrderHistory();
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

    }

}
