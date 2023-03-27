import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;

public class RecordInsertor {

    public void loadBookRecord(Connection conn) {
        try {
            String insertSql = "INSERT INTO Book (ISBN, title, inventory_quantity, price) VALUES (?, ?, ?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(insertSql);

            BufferedReader bufferedReader = prepareReader("sample_book.csv");
            updateRow(bufferedReader, preparedStatement, 4);
            System.out.println("Book record inserted successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadAuthorRecord(Connection conn) {
        try {
            String insertSql = "INSERT INTO Author (Aid, Aname) VALUES (?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(insertSql);

            BufferedReader bufferedReader = prepareReader("sample_author.csv");
            updateRow(bufferedReader, preparedStatement, 2);
            System.out.println("Author record inserted successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadBookAuthorRecord(Connection conn) {
        try {
            String insertSql = "INSERT INTO Book_Author (Aid, ISBN) VALUES (?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(insertSql);

            BufferedReader bufferedReader = prepareReader("sample_book_author.csv");
            updateRow(bufferedReader, preparedStatement, 2);
            System.out.println("Book_Author record inserted successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        try {
            String insertSql = "INSERT INTO `Order` (Oid, orderISBN, Uid, order_quantity) VALUES (?, ?, ?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(insertSql);

            BufferedReader bufferedReader = prepareReader("sample_order.csv");
            updateRow(bufferedReader, preparedStatement, 4);
            System.out.println("Order record inserted successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadOrderByRecord(Connection conn) {
        try {
            String insertSql = "INSERT INTO Order_By (Oid, Uid, order_date, shipping_status) VALUES (?, ?, ?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(insertSql);

            BufferedReader bufferedReader = prepareReader("sample_order_by.csv");
            updateRow(bufferedReader, preparedStatement, 4);
            System.out.println("Order_By record inserted successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadBookOrderedRecord(Connection conn) {
        try {
            String insertSql = "INSERT INTO Book_Ordered (Oid, ISBN) VALUES (?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(insertSql);

            BufferedReader bufferedReader = prepareReader("sample_book_ordered.csv");
            updateRow(bufferedReader, preparedStatement, 2);
            System.out.println("Book_Ordered record inserted successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BufferedReader prepareReader(String path) throws IOException {
        String csvFolderPath = "OnlineOrderingSystemForBook/sample_record/";
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
}
