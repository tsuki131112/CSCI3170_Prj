import java.sql.*;

public class Database {
    private static final String JDBC_Driver ="com.mysql.cj.jdbc.Driver";
    //static final String strConn ="jdbc:mysql://localhost:3306/booksystem?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String strConn ="jdbc:mysql://localhost:3306/booksystem";
    private static final String DBusername="csci3170";
    private static final String DBpassword="testfor3170";

    Connection conn=null;
    ResultSet rs=null;
    
    public static Connection getConnection() throws SQLException {
    	return DriverManager.getConnection(strConn,DBusername,DBpassword);
    }
    
    
    private void dbInit() {
    	
    }
    
    private void dbCreateTables() {
    	
    }
    
    private void dbLocalRecords() {
    	
    }
    
    private void dbDropRecords() {
    	
    }
    
    private void dbResetRecords() {
    	dbDropRecords();
    	dbLocalRecords();
    }
    
    public void testGetBooks() throws SQLException{
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

}
