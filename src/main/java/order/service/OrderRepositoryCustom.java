package order.service;

import order.entity.Order;

import java.util.List;

public interface OrderRepositoryCustom {
    List<Order> findOrdersByAccountAndSymbol(String account, String symbol);
    void updateQuantity(int quantity, String orderId);
    void createOrder(Order order);
}
