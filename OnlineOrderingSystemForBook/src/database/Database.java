package database;

import java.sql.*;

public class Database {

    public Integer getCountNumOfTable(String target_table) {
        String sql = "SELECT COUNT(*) FROM booksystem." + target_table;
        int countResult = 0;
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

    public boolean checkTablesExist() {
        try {
            String sql = "SELECT COUNT(*) FROM Book";
            executeQuery(sql);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public void initializeTables() throws SQLException {
        try (Connection conn = DataSource.getConnection(); Statement stmt = conn.createStatement()) {
            String[] statements = new String[]{
                    "CREATE DATABASE IF NOT EXISTS booksystem",
                    "CREATE TABLE IF NOT EXISTS Book (ISBN varchar(13) NOT NULL, title varchar(100) NOT NULL DEFAULT '', inventory_quantity int NOT NULL DEFAULT 0, price int NOT NULL DEFAULT 0, PRIMARY KEY (ISBN));",
                    "CREATE TABLE IF NOT EXISTS Author (Aid int NOT NULL AUTO_INCREMENT,Aname varchar(50) NOT NULL DEFAULT '',PRIMARY KEY (Aid));",
                    "CREATE TABLE IF NOT EXISTS Book_Author (   Aid int NOT NULL,   ISBN varchar(13) NOT NULL,   FOREIGN KEY (Aid) REFERENCES Author(Aid),   FOREIGN KEY (ISBN) REFERENCES Book(ISBN) );",
                    "CREATE TABLE IF NOT EXISTS `Customer` (   Uid varchar(10) NOT NULL,   Cname varchar(50) NOT NULL DEFAULT '',   Address varchar(200) NOT NULL DEFAULT '',   PRIMARY KEY (Uid) );",
                    "CREATE TABLE IF NOT EXISTS `Order` (   Oid varchar(8) NOT NULL,   orderISBN varchar(13) NOT NULL DEFAULT '',   UID varchar(10) NOT NULL DEFAULT '',   order_quantity int NOT NULL DEFAULT 0,   PRIMARY KEY (Oid, orderISBN),   FOREIGN KEY (Uid) REFERENCES Customer(Uid) );",
                    "CREATE TABLE IF NOT EXISTS `Order_By` (   Oid varchar(8) NOT NULL,   Uid varchar(10) NOT NULL DEFAULT '',   order_date DATETIME,   shipping_status varchar(10) NOT NULL DEFAULT '',   PRIMARY KEY (Oid),   FOREIGN KEY (Uid) REFERENCES Customer(Uid) );",
                    "CREATE TABLE IF NOT EXISTS `Book_Ordered` (   Oid varchar(8) NOT NULL,   ISBN varchar(13) NOT NULL DEFAULT '',   FOREIGN KEY (Oid) REFERENCES `Order`(Oid),   FOREIGN KEY (ISBN) REFERENCES Book(ISBN) );"
            };

            for (String statement : statements) {
                stmt.executeUpdate(statement);
            }

            System.out.println("Tables created successfully!");

        }
    }

    public void dbLoadLocalRecords() {
        try {
            Connection conn = DataSource.getConnection();

            RecordInsertor recordInsertor = new RecordInsertor();
            recordInsertor.loadBookRecord(conn);
            recordInsertor.loadAuthorRecord(conn);
            recordInsertor.loadBookAuthorRecord(conn);
            recordInsertor.loadCustomerRecord(conn);
            recordInsertor.loadOrderRecord(conn);
            recordInsertor.loadOrderByRecord(conn);
            recordInsertor.loadBookOrderedRecord(conn);

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dbDropRecords() {
        Connection conn;
        String[] tableNames = {"Book_Ordered", "Order_By", "`Order`", "Customer", "Book_Author", "Author", "Book"};

        try {
            conn = DataSource.getConnection();
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

    public static ResultSet executeQuery(String sql) throws SQLException {
        Connection conn;
        PreparedStatement stmt;
        ResultSet rs;

        conn = DataSource.getConnection();
        stmt = conn.prepareStatement(sql);
        rs = stmt.executeQuery();

        return rs;

    }


}
