package dataObject;

import java.util.List;

public record Order(String ISBN, String orderId, String uid, String orderDate, String shippingStatus) {
    public static void outputList(List<Order> orders) {
        System.out.println("List of order:");
        for (Order order : orders) {
            System.out.print("\t");
            System.out.println(order);
        }
    }
}
