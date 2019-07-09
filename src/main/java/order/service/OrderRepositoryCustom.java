package order.service;

import order.entity.Order;

import java.util.List;

public interface OrderRepositoryCustom {
    List<Order> findOrdersByAccountAndSymbol(String account, String symbol);

    Order updateQuantity(int quantity, String orderId);

    String createOrder(Order order);

    Order cancelOrder(String orderId);
}
