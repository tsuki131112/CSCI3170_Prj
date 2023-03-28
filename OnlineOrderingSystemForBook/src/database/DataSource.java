package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataSource {
    private static final String JDBC_Driver = "com.mysql.cj.jdbc.Driver";
    //static final String strConn ="jdbc:mysql://localhost:3306/booksystem?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String strConn = "jdbc:mysql://localhost:3306/booksystem";
    private static final String username = "csci3170";
    private static final String password = "testfor3170";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(strConn, username, password);
    }
}
