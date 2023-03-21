import java.util.InputMismatchException;
import java.util.Scanner;
import java.sql.SQLException;
import java.text.DateFormat;  
import java.text.SimpleDateFormat;  
import java.util.Date;  
import java.util.Calendar;
public class SystemMenu {
    private static Date cur_date = Calendar.getInstance().getTime();  
    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");  
    private static String date_in_str = dateFormat.format(cur_date); 
    private static Database db = new Database();
    public static void main(String[] args) {
        try {
            while(true) {
                mainMenu();
                int choose = chooseFunction();
                switch(choose) {
                    case 1: dbInit();
                    break;
                    
                    case 2: customerOperation();
                    break;
                    
                    case 3: BookStoreOperation();
                    break;
                    
                    case 4: return;
                    
                    default: System.out.println("None,You haven't input the right choose number");
                    break;
                }//end switch	
            } //end while
        } catch (InputMismatchException e) {
            System.out.println("Please input the right format");
        }
    }//end main	


    private static void mainMenu() {
        System.out.println();
        System.out.println("============Welcome to the Book Ordering Management System============"+ '\n' );
        
        System.out.println("+ System Date: "+ date_in_str + '\n' ); //TODO change the existing time
        System.out.println("+ Database Records: Books ("+ getNumberOfBooks() + "), Customers ("+ getNumberOfCustomers() +"), Orders (" +getNumberOfOrders() +")"+ '\n' ); 
        System.out.println("-------------------------------------"+ '\n' );
        System.out.printf("> 1: Database Initialization%n%n> 2: Customer Operation%n%n> 3: Bookstore Operation%n%n> 4: Quit%n%n" );
        System.out.println(">>> Please Enter Your Query: ");
        
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

    private static int chooseFunction() {
        Scanner scan = new Scanner(System.in);
        return scan.nextInt();
    
    }

    private static void dbInit() {
    	Scanner scan = new Scanner(System.in);
    	System.out.println("Choose the function for database initialization");
    	System.out.println("1: Create Tables   2: Load Init Records 3. Drop existing Records  ");
    	int dbOption = scan.nextInt();
    	
    	Database db = new Database();
        try {
        	if (dbOption == 1)  db.dbCreateTables();
        	else if (dbOption == 2) db.dbLoadLocalRecords();
        	else if (dbOption == 3) db.dbDropRecords();
        	else {
        		System.out.println("Please input the right inventory shipping status number");
        		dbInit();
        		}
        	//db.testGetBooks();
        }catch(SQLException se){
            // handle JDBC error
            se.printStackTrace();
        }catch(Exception e){
            // handle Class.forName error
            e.printStackTrace();
        }
    	
    }

    private static void customerOperation() {

    }

    private static void BookStoreOperation() {

    }

} // end class
