import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Scanner;

public class SystemMenu {
    private static Date cur_date = Calendar.getInstance().getTime();
    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static String date_in_str = dateFormat.format(cur_date);
    private static Database db = new Database();

    public static void main(String[] args) {
        try {
            while (true) {
                mainMenu();
                int option = fetchFunctionOption();

                if (option == 4) {
                    return;
                }
                goFunctionBranch(option);
            }
        } catch (InputMismatchException e) {
            System.out.println("Please input the right format");
        }
    }

    private static void mainMenu() {
        System.out.println();
        System.out.println("============Welcome to the Book Ordering Management System============" + '\n');
        System.out.println("+ System Date: " + date_in_str + '\n'); //TODO change the existing time

        if (db.checkTablesExist()) {
            int bookCount = getNumberOfBooks();
            int customerCount = getNumberOfCustomers();
            int orderCount = getNumberOfOrders();
            System.out.println("+ Database records: Books (" + bookCount + "), Customers (" + customerCount + "), Orders (" + orderCount + ")" + '\n');
        } else {
            System.out.println("Note: database does not exist!");
            System.out.println("+ Database records: Books (0), Customers (0), Orders (0)" + '\n');
        }

        System.out.println("-------------------------------------" + '\n');
        System.out.printf("> 1: Database initialization%n%n> 2: Customer operations%n%n> 3: Bookstore operations%n%n> 4: Quit%n%n");
        System.out.println(">>> Please enter your option: ");

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

    private static int fetchFunctionOption() {
        Scanner scan = new Scanner(System.in);
        return scan.nextInt();
    }

    private static void goFunctionBranch(int option) {
        switch (option) {
            case 1 -> dbInit();
            case 2 -> customerOperation();
            case 3 -> BookStoreOperation();
            default -> System.out.println("Please give valid input.");
        }
    }

    private static void dbInit() {
        System.out.println("Choose the function for database initialization");
        System.out.println("1: Create Tables 2: Load Init Records 3. Drop existing Records  ");

        Scanner scan = new Scanner(System.in);
        int dbOption = scan.nextInt();

        Database db = new Database();
        try {
            switch (dbOption) {
                case 1 -> db.initializeTables();
                case 2 -> db.dbLoadLocalRecords();
                case 3 -> db.dbDropRecords();
                default -> {
                    System.out.println("Please input the right inventory shipping status number");
                    dbInit();
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

    }

    private static void BookStoreOperation() {

    }

}
