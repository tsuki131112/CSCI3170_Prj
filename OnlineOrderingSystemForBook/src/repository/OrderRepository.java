package repository;

import dataObject.BookOrder;
import dataObject.Order;
import database.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderRepository {
    public List<Order> findOrdersByUid(String uid) throws SQLException {
        String sql = "SELECT DISTINCT * FROM booksystem.order NATURAL JOIN order_by WHERE uid=?";
        Connection conn = DataSource.getConnection();
        PreparedStatement statement = conn.prepareStatement(sql);

        statement.setString(1, uid);
        List<Order> orders = findOrderBys(statement);

        statement.close();
        conn.close();

        return orders;
    }

    public void placeOrders(List<BookOrder> bookOrders) throws SQLException {
        String lastOrderId = findLastOrderId();
        String currentOrderId = getNextOrderId(lastOrderId);

        String sql1 = "INSERT INTO booksystem.Order (Oid, orderISBN, Uid, order_quantity) VALUES (?,?,?,?);";
        String sql2 = "INSERT INTO Order_by (Oid, Uid, order_date, shipping_status) VALUES (?,?,?,?);";
        String sql3 = "UPDATE Book SET inventory_quantity=inventory_quantity-? WHERE ISBN=?";
        Connection conn = DataSource.getConnection();
        PreparedStatement statement1 = conn.prepareStatement(sql1);
        PreparedStatement statement2 = conn.prepareStatement(sql2);
        PreparedStatement statement3 = conn.prepareStatement(sql3);

        for (BookOrder bookOrder : bookOrders) {
            statement1.setString(1, currentOrderId);
            statement1.setString(2, bookOrder.ISBN());
            statement1.setString(3, bookOrder.uid());
            statement1.setInt(4, bookOrder.quantity());

            statement2.setString(1, currentOrderId);
            statement2.setString(2, bookOrder.uid());
            statement2.setTimestamp(3, java.sql.Timestamp.from(java.time.Instant.now()));
            statement2.setString(4, "ordered");

            statement3.setInt(1, bookOrder.quantity());
            statement3.setString(2, bookOrder.ISBN());

            statement1.executeUpdate();
            statement2.executeUpdate();
            statement3.executeUpdate();
        }

        statement1.close();
        statement2.close();
        statement3.close();
        conn.close();
    }

    private List<Order> findOrderBys(PreparedStatement statement) {
        List<Order> orders = new ArrayList<>();
        try {
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                Order order = new Order(
                        rs.getString("orderISBN"),
                        rs.getString("oid"),
                        rs.getString("uid"),
                        rs.getString("order_date"),
                        rs.getString("shipping_status")
                );
                orders.add(order);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return orders;
    }

    private String findLastOrderId() throws SQLException {
        String sql = "SELECT Oid FROM booksystem.Order";

        Connection conn = DataSource.getConnection();
        PreparedStatement statement = conn.prepareStatement(sql);

        ResultSet rs = statement.executeQuery();
        String orderId = "";
        while (rs.next()) {
            orderId = rs.getString("Oid");
        }

        statement.close();
        conn.close();

        return orderId;
    }

    private String getNextOrderId(String orderId) {
        int orderNumber = Integer.parseInt(orderId.substring(1));
        int nextOrderNumber = orderNumber + 1;
        return "O" + String.format("%07d", nextOrderNumber);

    }
}
