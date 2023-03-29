package repository;

import dataObject.Book;
import database.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookRepository {
    public List<Book> findBookByISBN(String ISBN) {
        try {
            return findBooksBySingleField("ISBN", ISBN);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<Book> findBookByTitle(String title) throws SQLException {
        return findBooksBySingleField("title", title);
    }

    public List<Book> findBooksByAuthorName(String authorName) throws SQLException {
        String sql =
                "SELECT DISTINCT * FROM book NATURAL JOIN book_author NATURAL JOIN author WHERE Aname=?";
        Connection conn = DataSource.getConnection();
        PreparedStatement statement = conn.prepareStatement(sql);

        statement.setString(1, authorName);
        List<Book> books = findBooks(statement);

        statement.close();
        conn.close();

        return books;
    }

    private List<Book> findBooksBySingleField(String field, String value) throws SQLException {
        String sql = "SELECT * FROM Book WHERE " + field + "=?";

        Connection conn = DataSource.getConnection();
        PreparedStatement statement = conn.prepareStatement(sql);

        statement.setString(1, value);
        List<Book> books = findBooks(statement);

        statement.close();
        conn.close();

        return books;
    }

    private List<Book> findBooks(PreparedStatement statement) {
        List<Book> books = new ArrayList<>();
        try {
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                Book book = new Book(
                        rs.getString("ISBN"),
                        rs.getString("title"),
                        rs.getInt("inventory_quantity"),
                        rs.getInt("price")
                );
                books.add(book);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return books;
    }
}
