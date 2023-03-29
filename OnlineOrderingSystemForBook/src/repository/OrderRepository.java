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
    public boolean checkOrderExists(String orderId, String uid) {
        try {
            String sql = "SELECT DISTINCT * FROM booksystem.order WHERE Oid=? AND Uid=?";
            Connection conn = DataSource.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql);

            statement.setString(1, orderId);
            statement.setString(2, uid);

            ResultSet rs = statement.executeQuery();
            int count = 0;
            while (rs.next()) {
                count += 1;
            }

            statement.close();
            conn.close();

            return count > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Order> findOrdersByUid(String uid) {
        try {
            String sql = "SELECT DISTINCT * FROM booksystem.order NATURAL JOIN order_by WHERE uid=?";
            Connection conn = DataSource.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql);

            statement.setString(1, uid);
            List<Order> orders = findOrderBys(statement);

            statement.close();
            conn.close();

            return orders;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void placeOrders(List<BookOrder> bookOrders) throws SQLException {
        if (bookOrders.size() == 0) {
            return;
        }

        String lastOrderId = findLastOrderId();
        String currentOrderId = getNextOrderId(lastOrderId);
        System.out.println(lastOrderId);
        System.out.println(currentOrderId);
        String sql1 = "INSERT INTO booksystem.Order (Oid, orderISBN, Uid, order_quantity) VALUES (?,?,?,?);";
        String sql2 = "INSERT INTO Order_by (Oid, Uid, order_date, shipping_status) VALUES (?,?,?,?);";
        String sql3 = "UPDATE Book SET inventory_quantity=inventory_quantity-? WHERE ISBN=?";
        Connection conn = DataSource.getConnection();
        PreparedStatement statement1 = conn.prepareStatement(sql1);
        PreparedStatement statement2 = conn.prepareStatement(sql2);
        PreparedStatement statement3 = conn.prepareStatement(sql3);

        statement2.setString(1, currentOrderId);
        statement2.setString(2, bookOrders.get(0).uid());
        statement2.setTimestamp(3, java.sql.Timestamp.from(java.time.Instant.now()));
        statement2.setString(4, "ordered");
        statement2.executeUpdate();

        for (BookOrder bookOrder : bookOrders) {
            statement1.setString(1, currentOrderId);
            statement1.setString(2, bookOrder.ISBN());
            statement1.setString(3, bookOrder.uid());
            statement1.setInt(4, bookOrder.quantity());

            statement3.setInt(1, bookOrder.quantity());
            statement3.setString(2, bookOrder.ISBN());

            statement1.executeUpdate();
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

    public List<Order> findOrdersByShippingStatus(String status) {
        try {
            String sql = "SELECT * FROM booksystem.Order NATURAL JOIN Order_by WHERE shipping_status=?";

            Connection conn = DataSource.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql);

            statement.setString(1, status);
            List<Order> orders = findOrderBys(statement);

            statement.close();
            conn.close();

            return orders;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public boolean updateOrder(Order order) {
        try {
            String sql = "UPDATE Order_by SET shipping_status=? WHERE Oid=? AND Uid=? ";

            Connection conn = DataSource.getConnection();
            PreparedStatement statement = conn.prepareStatement(sql);

            statement.setString(1, order.shippingStatus());
            statement.setString(2, order.orderId());
            statement.setString(3, order.uid());
            statement.executeUpdate();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private String findLastOrderId() throws SQLException {
        String sql = "SELECT Oid FROM booksystem.Order";

        Connection conn = DataSource.getConnection();
        PreparedStatement statement = conn.prepareStatement(sql);

        ResultSet rs = statement.executeQuery();

        int orderNumber = 0;
        String orderId = "";
        while (rs.next()) {
            String nextOrderId = rs.getString("Oid");
            int nextOrderNumber = Integer.parseInt(nextOrderId.substring(1));
            if (nextOrderNumber > orderNumber) {
                orderNumber = nextOrderNumber;
                orderId = nextOrderId;
            }
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
