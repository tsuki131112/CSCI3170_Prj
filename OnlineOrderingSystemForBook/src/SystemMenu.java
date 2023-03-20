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
    public static void main(String[] args) {
        System.out.println("None,You haven't input the right choose number");	
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
    private static String getNumberOfBooks() {
        return "123";
        // Get from db
    }
    private static String getNumberOfCustomers() {
        return "456";
        // Get from db
    }
    private static String getNumberOfOrders() {
        return "789";
        // Get from db
    }

    private static int chooseFunction() {
        Scanner scan = new Scanner(System.in);
        return scan.nextInt();
    
    }

    private static void dbInit() {
    	Database db = new Database();
        try {
        	db.testGetBooks();
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
