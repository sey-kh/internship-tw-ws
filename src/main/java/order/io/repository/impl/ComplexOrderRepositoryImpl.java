package order.io.repository.impl;

import order.constant.Consts;
import order.io.entity.ComplexOrder;
import order.io.repository.ComplexOrderRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class ComplexOrderRepositoryImpl implements ComplexOrderRepository {

    private static SortedSet<ComplexOrder> orders_by_other_orders;
    private static SortedSet<ComplexOrder> orders_by_time;

    public ComplexOrderRepositoryImpl() {
        orders_by_other_orders = new TreeSet<>(Comparator.comparing(ComplexOrder::getOrderDate)
                .reversed()
                .thenComparing(ComplexOrder::getOrderId));
        orders_by_time = new TreeSet<>(Comparator.comparing(ComplexOrder::getActivationDate)
                .thenComparing(ComplexOrder::getOrderId));
    }

    @Override
    public SortedSet<ComplexOrder> getAllOrders() {
        SortedSet<ComplexOrder> allOrders = new TreeSet<>(orders_by_other_orders);
        allOrders.addAll(orders_by_time);
        return allOrders;
    }

    @Override
    public ComplexOrder save(ComplexOrder order) {
        if (order.getActivation().toLowerCase().trim().equals(Consts.ByTime)) {
            orders_by_time.add(order);
            return order;
        } else {
            orders_by_other_orders.add(order);
            return order;
        }
    }

    @Override
    public void deleteInBatch(List<ComplexOrder> orders, String activation) {
        orders_by_other_orders.removeAll(orders);
        if (activation.toLowerCase().trim().equals(Consts.ByTime))
            orders_by_time.removeAll(orders);
        else
            orders_by_other_orders.removeAll(orders);
    }

    @Override
    public void displayOrders() {
        System.out.println("----> All complex orders:");
        SortedSet<ComplexOrder> allOrders = this.getAllOrders();
        System.out.println("-------------------------");
    }

    @Override
    public List<ComplexOrder> findAllWithCurrentDateBefore(Date currentDate) {

        Predicate<ComplexOrder> byBeforeCurrentDate = order -> order.getActivationDate().before(currentDate);
        Predicate<ComplexOrder> byStatus = order -> order.getStatus().equals(Consts.CONFIRMED);

        if (orders_by_time.size() != 0) {
            return orders_by_time.stream()
                    .filter(byBeforeCurrentDate)
                    .filter(byStatus).collect(Collectors.toList());
        } else {
            return null;
        }
    }

    @Override
    public List<ComplexOrder> findAllByParams(String symbol, Boolean buy, Integer quantity) {
        String side;
        if (buy) side = Consts.SALE;
        else side = Consts.BUY;
        String finalSide = side;

        Predicate<ComplexOrder> byStatus = order -> order.getStatus().equals(Consts.CONFIRMED);
        Predicate<ComplexOrder> bySide = order -> !order.getSide().equals(finalSide);
        Predicate<ComplexOrder> bySymbol = order -> order.getSymbol().equals(symbol);
        Predicate<ComplexOrder> byMinQuantity = order -> order.getMinQuantity() <= quantity;

        if (orders_by_other_orders.size() != 0) {
            return orders_by_other_orders.stream()
                    .filter(byStatus)
                    .filter(bySide)
                    .filter(bySymbol)
                    .filter(byMinQuantity)
                    .collect(Collectors.toList());
        } else return null;
    }

    @Override
    public ComplexOrder getById(String orderId) {

        Predicate<ComplexOrder> byId = o -> o.getOrderId().equals(orderId);

        SortedSet<ComplexOrder> allOrders = getAllOrders();

        SortedSet<ComplexOrder> orders = allOrders.stream().filter(byId).collect(Collectors.toCollection(()
                -> new TreeSet<>(Comparator.comparing(ComplexOrder::getOrderId))));
        if (orders.size() != 0) {
            return orders.first();
        } else return null;
    }
}
