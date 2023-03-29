package dataObject;

import repository.CustomerRepository;
import repository.OrderRepository;

import java.util.ArrayList;
import java.util.List;

public record Order(String ISBN, String orderId, String uid, String orderDate, String shippingStatus) {
    public static void outputList(List<Order> orders) {
        System.out.println("List of order (" + orders.size() + " orders):");
        for (Order order : orders) {
            System.out.print("\t");
            System.out.println(order);
        }
    }

    public static List<String> validateUpdate(Order order) {
        List<String> invalidReasons = new ArrayList<>();

        OrderRepository orderRepository = new OrderRepository();
        boolean orderExists = orderRepository.checkOrderExists(order.orderId(), order.uid());

        CustomerRepository customerRepository = new CustomerRepository();
        boolean userExists = customerRepository.checkUserExists(order.uid());

        if (!orderExists) {
            invalidReasons.add("Order does not exist");
        } else {
            List<Order> orders = orderRepository.findOrdersByUid(order.uid());
            for (Order o : orders) {
                if (o.orderId().equals(order.orderId())) {
                    if (o.shippingStatus.equals("shipped") || o.shippingStatus.equals("received")) {
                        invalidReasons.add("Order is already shipped or received");
                    }
                }
            }
        }

        if (!userExists) {
            invalidReasons.add("Customer does not exist");
        }

        if (order.orderId().length() == 0) {
            invalidReasons.add("Invalid orderId");
        }
        if (order.uid().length() == 0) {
            invalidReasons.add("Invalid uid");
        }

        if (order.shippingStatus().equals("ordered")) {
            invalidReasons.add("Cannot change shipping status to ordered");
        }

        if (!order.shippingStatus().equals("shipped")
                && !order.shippingStatus().equals("ordered")
                && !order.shippingStatus().equals("received")
        ) {
            invalidReasons.add("Invalid shipping status");
        }

        return invalidReasons;
    }
}
