import java.sql.*;
import java.util.Arrays;
import java.io.BufferedReader;
import java.io.FileReader;



public class Database {
    private static final String JDBC_Driver ="com.mysql.cj.jdbc.Driver";
    //static final String strConn ="jdbc:mysql://localhost:3306/booksystem?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String strConn ="jdbc:mysql://localhost:3306/booksystem";
    private static final String DBusername="csci3170";
    private static final String DBpassword="testfor3170";
    private static String csvFolderPath = "sample_record/";

    Connection conn=null;
    ResultSet rs=null;
    
    public static Connection getConnection() throws SQLException {
    	return DriverManager.getConnection(strConn,DBusername,DBpassword);
    }
    
    public Integer getCountNumOfTable(String target_table) {
        String sql = "SELECT COUNT(*) FROM " + target_table;
        Integer countResult = 0;
        try {
            ResultSet rs = executeQuery(sql);
            if (rs.next()) {
                countResult = rs.getInt(1);
             }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
    	return countResult;
    }
    
    public void dbCreateTables() throws SQLException {
    	Connection conn = null;
    	Statement stmt = null;
    	
    	try {
            conn = getConnection();
            stmt = conn.createStatement();

            // Create the Book table
            String sql = "CREATE TABLE Book (ISBN varchar(13) NOT NULL, title varchar(100) NOT NULL DEFAULT '', inventory_quantity int NOT NULL DEFAULT 0, price int NOT NULL DEFAULT 0, PRIMARY KEY (ISBN));";
            stmt.executeUpdate(sql);

            // Create the Author table
            sql = "CREATE TABLE Author (Aid int NOT NULL AUTO_INCREMENT,Aname varchar(50) NOT NULL DEFAULT '',PRIMARY KEY (Aid));";
            stmt.executeUpdate(sql);
            
            // Create the Book_Author table
            sql = "CREATE TABLE Book_Author (   Aid int NOT NULL,   ISBN varchar(13) NOT NULL,   FOREIGN KEY (Aid) REFERENCES Author(Aid),   FOREIGN KEY (ISBN) REFERENCES Book(ISBN) );";
            stmt.executeUpdate(sql);
            
            // Create the Customer table
            sql = "CREATE TABLE `Customer` (   Uid varchar(10) NOT NULL,   Cname varchar(50) NOT NULL DEFAULT '',   Address varchar(200) NOT NULL DEFAULT '',   PRIMARY KEY (Uid) );";
            stmt.executeUpdate(sql);
            
            // Create the Order table
            sql = "CREATE TABLE `Order` (   Oid varchar(8) NOT NULL,   orderISBN varchar(13) NOT NULL DEFAULT '',   UID varchar(10) NOT NULL DEFAULT '',   order_quantity int NOT NULL DEFAULT 0,   PRIMARY KEY (Oid, orderISBN),   FOREIGN KEY (Uid) REFERENCES Customer(Uid) );";
            stmt.executeUpdate(sql);

            // Create the Order_By table
            sql = "CREATE TABLE `Order_By` (   Oid varchar(8) NOT NULL,   Uid varchar(10) NOT NULL DEFAULT '',   order_date DATETIME,   shipping_status varchar(10) NOT NULL DEFAULT '',   PRIMARY KEY (Oid),   FOREIGN KEY (Uid) REFERENCES Customer(Uid) );";
            stmt.executeUpdate(sql);

            // Create the Book_Ordered table
            sql = "CREATE TABLE `Book_Ordered` (   Oid varchar(8) NOT NULL,   ISBN varchar(13) NOT NULL DEFAULT '',   FOREIGN KEY (Oid) REFERENCES `Order`(Oid),   FOREIGN KEY (ISBN) REFERENCES Book(ISBN) );";
            stmt.executeUpdate(sql);

            System.out.println("Tables created successfully!");

        } finally {
            // Close the resources in a finally block
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }
    
    public void dbLoadLocalRecords() {
    	loadBookRecord();
        loadAuthorRecord();
        loadBookAuthorRecord();
        loadCustomerRecord();
        loadOrderRecord();
        loadOrderByRecord();
        loadBookOrderedRecord(); 
    }
    
    public void dbDropRecords() {
        Connection conn = null;
    	String[] tableNames = {"Book_Ordered","Order_By","`Order`","Customer","Book_Author","Author","Book"};
        
        try {
            conn = getConnection();
            Statement statement = conn.createStatement();
            for (String tableName : tableNames) {
                String sql = "DROP TABLE " + tableName;
                statement.executeUpdate(sql);
                System.out.println("Table " + tableName + " dropped successfully!");
            }
        } catch (SQLException e) {
            System.out.println("Error occurs when dropping table " + e.getMessage());
        }
    }
    
    private void dbResetRecords() { //TODO?
    	dbDropRecords();
    	dbLoadLocalRecords();
    }
    
    public void testGetBooks() throws SQLException{ //For testing connect only
    	String sql = null;
        ResultSet rs = null;
        
        try {
        	sql = "SELECT ISBN, title, inventory_quantity, price FROM Book";
            rs = executeQuery(sql);

            // Process the results here
            while(rs.next()){
                String ISBN  = rs.getString("ISBN");
                String title = rs.getString("title");
                int inventory_quantity = rs.getInt("inventory_quantity");
                int price = rs.getInt("price");
    
                System.out.print("ISBN: " + ISBN);
                System.out.print(", title: " + title);
                System.out.print(", inventory_quantity: " + inventory_quantity);
                System.out.print(", price: " + price);
                System.out.print("\n");
            }

        } finally {
            // Close the resources in a finally block
            if (rs != null) {
                rs.close();
            }
        }
    }
    
    
    public static ResultSet executeQuery(String sql) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            return rs;
            
        } finally {
            
        }
    }

    private void loadBookRecord() {
        try {
            Connection conn = null;
            conn = getConnection();
            String insertSql = "INSERT INTO Book (ISBN, title, inventory_quantity, price) VALUES (?, ?, ?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(insertSql);
            
            String bookRecordCsvPath = csvFolderPath + "sample_book.csv";
            BufferedReader bufferedReader = new BufferedReader(new FileReader(bookRecordCsvPath));
            String headers = bufferedReader.readLine();  // skip the first line (column headers)
            String line;
            while((line = bufferedReader.readLine()) != null) {
                String[] data = line.split(",");
                String ISBN = data[0];
                String title = data[1];
                int inventory_quantity = Integer.parseInt(data[2]);
                int price = Integer.parseInt(data[3]);
                
                preparedStatement.setString(1, ISBN);
                preparedStatement.setString(2, title);
                preparedStatement.setInt(3, inventory_quantity);
                preparedStatement.setInt(4, price);
                
                preparedStatement.executeUpdate();
            }
            
            bufferedReader.close();
            preparedStatement.close();
            conn.close();
            System.out.println("Book record inserted successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    
    }

    private void loadAuthorRecord() {
        try {
            Connection conn = null;
            conn = getConnection();
            String insertSql = "INSERT INTO Author (Aid, Aname) VALUES (?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(insertSql);
            
            String recordCsvPath = csvFolderPath + "sample_author.csv";
            BufferedReader bufferedReader = new BufferedReader(new FileReader(recordCsvPath));
            String headers = bufferedReader.readLine();  // skip the first line (column headers)
            String line;
            while((line = bufferedReader.readLine()) != null) {
                String[] data = line.split(",");
                int Aid = Integer.parseInt(data[0]);
                String Aname = data[1];
                
                preparedStatement.setInt(1, Aid);
                preparedStatement.setString(2, Aname);
                
                preparedStatement.executeUpdate();
            }
            
            bufferedReader.close();
            preparedStatement.close();
            conn.close();
            System.out.println("Author record inserted successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    
    }

    private void loadBookAuthorRecord() {
        try {
            Connection conn = null;
            conn = getConnection();
            String insertSql = "INSERT INTO Book_Author (Aid, ISBN) VALUES (?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(insertSql);
            
            String recordCsvPath = csvFolderPath + "sample_book_author.csv";
            BufferedReader bufferedReader = new BufferedReader(new FileReader(recordCsvPath));
            String headers = bufferedReader.readLine();  // skip the first line (column headers)
            String line;
            while((line = bufferedReader.readLine()) != null) {
                String[] data = line.split(",");
                int Aid = Integer.parseInt(data[0]);
                String ISBN = data[1];
                
                preparedStatement.setInt(1, Aid);
                preparedStatement.setString(2, ISBN);
                
                preparedStatement.executeUpdate();
            }
            
            bufferedReader.close();
            preparedStatement.close();
            conn.close();
            System.out.println("Book_Author record inserted successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    
    }

    private void loadCustomerRecord() {
        try {
            Connection conn = null;
            conn = getConnection();
            String insertSql = "INSERT INTO Customer (Uid, Cname, Address) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(insertSql);
            
            String recordCsvPath = csvFolderPath + "sample_customer.csv";
            BufferedReader bufferedReader = new BufferedReader(new FileReader(recordCsvPath));
            String headers = bufferedReader.readLine();  // skip the first line (column headers)
            String line;
            while((line = bufferedReader.readLine()) != null) {
                String[] data = line.split(",");
                String Uid = data[0];
                String Cname = data[1];
                String[] addressParts = Arrays.copyOfRange(data, 2, data.length);  // Get address parts
                String Address = String.join(", ", addressParts).replaceAll("\"", ""); // Join address parts and remove quotes
                preparedStatement.setString(1, Uid);
                preparedStatement.setString(2, Cname);
                preparedStatement.setString(3, Address);
                
                preparedStatement.executeUpdate();
            }
            
            bufferedReader.close();
            preparedStatement.close();
            conn.close();
            System.out.println("Customer record inserted successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    
    }

    private void loadOrderRecord() {
        try {
            Connection conn = null;
            conn = getConnection();
            String insertSql = "INSERT INTO `Order` (Oid, orderISBN, Uid, order_quantity) VALUES (?, ?, ?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(insertSql);
            
            String recordCsvPath = csvFolderPath + "sample_order.csv";
            BufferedReader bufferedReader = new BufferedReader(new FileReader(recordCsvPath));
            String headers = bufferedReader.readLine();  // skip the first line (column headers)
            String line;
            while((line = bufferedReader.readLine()) != null) {
                String[] data = line.split(",");
                String Oid = data[0];
                String orderISBN = data[1];
                String Uid = data[2];
                int order_quantity = Integer.parseInt(data[3]);
                preparedStatement.setString(1, Oid);
                preparedStatement.setString(2, orderISBN);
                preparedStatement.setString(3, Uid);
                preparedStatement.setInt(4, order_quantity);
                
                preparedStatement.executeUpdate();
            }
            
            bufferedReader.close();
            preparedStatement.close();
            conn.close();
            System.out.println("Order record inserted successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    
    }

    private void loadOrderByRecord() {
        try {
            Connection conn = null;
            conn = getConnection();
            String insertSql = "INSERT INTO Order_By (Oid, Uid, order_date, shipping_status) VALUES (?, ?, ?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(insertSql);
            
            String recordCsvPath = csvFolderPath + "sample_order_by.csv";
            BufferedReader bufferedReader = new BufferedReader(new FileReader(recordCsvPath));
            String headers = bufferedReader.readLine();  // skip the first line (column headers)
            String line;
            while((line = bufferedReader.readLine()) != null) {
                String[] data = line.split(",");
                String Oid = data[0];
                String Uid = data[1];
                Date orderDate = Date.valueOf(data[2]);
                String shippingStatus = data[3];

                preparedStatement.setString(1, Oid);
                preparedStatement.setString(2, Uid);
                preparedStatement.setDate(3, orderDate);
                preparedStatement.setString(4, shippingStatus);
                
                preparedStatement.executeUpdate();
            }
            
            bufferedReader.close();
            preparedStatement.close();
            conn.close();
            System.out.println("Order_By record inserted successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    
    }

    private void loadBookOrderedRecord() {
        try {
            Connection conn = null;
            conn = getConnection();
            String insertSql = "INSERT INTO Book_Ordered (Oid, ISBN) VALUES (?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(insertSql);
            
            String recordCsvPath = csvFolderPath + "sample_book_ordered.csv";
            BufferedReader bufferedReader = new BufferedReader(new FileReader(recordCsvPath));
            String headers = bufferedReader.readLine();  // skip the first line (column headers)
            String line;
            while((line = bufferedReader.readLine()) != null) {
                String[] data = line.split(",");
                String Oid = data[0];
                String ISBN = data[1];
                

                preparedStatement.setString(1, Oid);
                preparedStatement.setString(2, ISBN);
                
                preparedStatement.executeUpdate();
            }
            
            bufferedReader.close();
            preparedStatement.close();
            conn.close();
            System.out.println("Book_Ordered record inserted successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    
    }

}
