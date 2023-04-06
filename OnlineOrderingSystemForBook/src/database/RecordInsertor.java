package database;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;

public class RecordInsertor {

    public void loadBookRecord(Connection conn) {
        loadRecords(
                conn,
                "INSERT INTO Book (ISBN, title, inventory_quantity, price) VALUES (?, ?, ?, ?)",
                "sample_book.csv",
                "Book",
                4
        );
    }

    public void loadAuthorRecord(Connection conn) {
        loadRecords(
                conn,
                "INSERT INTO Author (Aid, Aname) VALUES (?, ?)",
                "sample_author.csv",
                "Author",
                2
        );
    }

    public void loadBookAuthorRecord(Connection conn) {
        loadRecords(
                conn,
                "INSERT INTO Book_Author (Aid, ISBN) VALUES (?, ?)",
                "sample_book_author.csv",
                "Book_Author",
                2
        );
    }

    public void loadCustomerRecord(Connection conn) {
        try {
            String insertSql = "INSERT INTO Customer (Uid, Cname, Address) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(insertSql);

            BufferedReader bufferedReader = prepareReader("sample_customer.csv");
            String line;
            while ((line = bufferedReader.readLine()) != null) {
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
            System.out.println("Customer record inserted successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadOrderRecord(Connection conn) {
        loadRecords(
                conn,
                "INSERT INTO `Order` (Oid, orderISBN, Uid, order_quantity) VALUES (?, ?, ?, ?)",
                "sample_order.csv",
                "Order",
                4
        );
    }

    public void loadOrderByRecord(Connection conn) {
        loadRecords(
                conn,
                "INSERT INTO Order_By (Oid, Uid, order_date, shipping_status) VALUES (?, ?, ?, ?)",
                "sample_order_by.csv",
                "Order_By",
                4
        );
    }

    public void loadBookOrderedRecord(Connection conn) {
        loadRecords(
                conn,
                "INSERT INTO Book_Ordered (Oid, ISBN) VALUES (?, ?)",
                "sample_book_ordered.csv",
                "Book_Ordered",
                2
        );
    }

    private BufferedReader prepareReader(String path) throws IOException {
        String csvFolderPath = System.getProperty("user.dir")+"/sample_record/";
        String recordCsvPath = csvFolderPath + path;
        BufferedReader bufferedReader = new BufferedReader(new FileReader(recordCsvPath));
        bufferedReader.readLine();  // skip the first line (column headers)
        return bufferedReader;
    }

    private void updateRow(BufferedReader bufferedReader, PreparedStatement statement, int fieldCount) throws IOException, SQLException {
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            String[] data = line.split(",");

            for (int i = 0; i < fieldCount; i++) {
                statement.setObject(i + 1, data[i]);
            }
            statement.executeUpdate();
        }
        bufferedReader.close();
        statement.close();
    }

    private void loadRecords(Connection conn, String sql, String path, String tableName, int fieldCount) {
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);

            BufferedReader bufferedReader = prepareReader(path);
            updateRow(bufferedReader, preparedStatement, fieldCount);

            System.out.println(tableName + " record inserted successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
