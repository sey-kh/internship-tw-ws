package order.io.repository.impl;

import order.constant.Consts;
import order.io.entity.ComplexOrder;
import order.io.repository.ComplexOrderRepository;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class ComplexOrderRepositoryImpl implements ComplexOrderRepository {

    private static Map<String, SortedSet<ComplexOrder>> orderByTime;
    private static Map<String, Map<String, SortedSet<ComplexOrder>>> orderByOtherOrder;

    public ComplexOrderRepositoryImpl() {
        orderByTime = new LinkedHashMap<>();
        orderByOtherOrder = new LinkedHashMap<>();
    }

    @Override
    public List<ComplexOrder> getAllOrders() {

        List<ComplexOrder> allOrders = new ArrayList<>();

        for (Map.Entry<String, SortedSet<ComplexOrder>> entry : orderByTime.entrySet()) {
            SortedSet<ComplexOrder> orders = entry.getValue();
            allOrders.addAll(orders);
        }

        for (Map.Entry<String, Map<String, SortedSet<ComplexOrder>>> entry : orderByOtherOrder.entrySet()) {
            Map<String, SortedSet<ComplexOrder>> symbolMap = entry.getValue();
            for (Map.Entry<String, SortedSet<ComplexOrder>> sideMap : symbolMap.entrySet()) {
                SortedSet<ComplexOrder> orders = sideMap.getValue();
                allOrders.addAll(orders);
            }
        }

        return allOrders;
    }

    @Override
    public ComplexOrder save(ComplexOrder order) {
        if (order.getActivation().toLowerCase().trim().equals(Consts.ByTime)) {

            SortedSet<ComplexOrder> newOrders = new TreeSet<>(Comparator.comparing(ComplexOrder::getActivationDate)
                    .thenComparing(ComplexOrder::getOrderId));
            newOrders.add(order);

            Date activationDate = order.getActivationDate();
            DateFormat outputFormatter = new SimpleDateFormat("MM/dd/yyyy");
            String key = outputFormatter.format(activationDate);

            if (orderByTime.containsKey(key)) {
                SortedSet<ComplexOrder> orderList = orderByTime.get(key);
                orderList.add(order);
                return order;
            } else {
                orderByTime.put(key, newOrders);
                return order;
            }
        } else {
            SortedSet<ComplexOrder> newOrders = new TreeSet<>(Comparator.comparing(ComplexOrder::getMinQuantity)
                    .thenComparing(ComplexOrder::getOrderId));
            newOrders.add(order);

            String side = order.getSide();
            String symbol = order.getSymbol();

            if (orderByOtherOrder.containsKey(symbol)) {
                Map<String, SortedSet<ComplexOrder>> symbolEntry = orderByOtherOrder.get(symbol);

                for (Map.Entry<String, SortedSet<ComplexOrder>> sideEntry : symbolEntry.entrySet()) {
                    if (sideEntry.getKey().equals(side)) {
                        sideEntry.getValue().add(order);
                        return order;
                    }
                }
                symbolEntry.put(side, newOrders);
                return order;
            } else {
                Map<String, SortedSet<ComplexOrder>> listMap = new LinkedHashMap<>();
                listMap.put(side, newOrders);
                orderByOtherOrder.put(symbol, listMap);
                return order;
            }
        }
    }

    @Override
    public void deleteInBatch(List<ComplexOrder> orders, String activation) {
        if (activation.toLowerCase().trim().equals(Consts.ByTime))
            for (Map.Entry<String, SortedSet<ComplexOrder>> entry : orderByTime.entrySet()) {
                entry.getValue().removeAll(orders);
            }
        else {
            for (Map.Entry<String, Map<String, SortedSet<ComplexOrder>>> symbolEntry : orderByOtherOrder.entrySet()) {
                Map<String, SortedSet<ComplexOrder>> symbolMap = symbolEntry.getValue();
                for (Map.Entry<String, SortedSet<ComplexOrder>> sideEntry : symbolMap.entrySet()) {
                    sideEntry.getValue().removeAll(orders);
                }
            }
        }
    }

    @Override
    public void deleteAll() {
        orderByTime.clear();
        orderByOtherOrder.clear();
    }

    @Override
    public List<ComplexOrder> findAllWithCurrentDateBefore(Date currentDate) {

        DateFormat outputFormatter = new SimpleDateFormat("MM/dd/yyyy");
        String key = outputFormatter.format(currentDate);

        Predicate<ComplexOrder> byBeforeCurrentDate = order -> order.getActivationDate().before(currentDate);
        Predicate<ComplexOrder> byStatus = order -> order.getStatus().equals(Consts.CONFIRMED);

        if (orderByTime.size() != 0) {
            List<ComplexOrder> result = new ArrayList<>();

            for (Map.Entry<String, SortedSet<ComplexOrder>> entry : orderByTime.entrySet()) {
                if (entry.getKey().equals(key)) {
                    List<ComplexOrder> orders = entry.getValue().stream()
                            .filter(byBeforeCurrentDate)
                            .filter(byStatus).collect(Collectors.toList());
                    result.addAll(orders);
                }
            }
            return result;
        } else {
            return null;
        }
    }

    @Override
    public List<ComplexOrder> findAllByParams(String symbol, Boolean buy, Integer quantity) {
        String side;
        if (buy) side = Consts.SELL;
        else side = Consts.BUY;

        Predicate<ComplexOrder> byStatus = order -> order.getStatus().equals(Consts.CONFIRMED);
        Predicate<ComplexOrder> byMinQuantity = order -> order.getMinQuantity() <= quantity;

        if (orderByOtherOrder.size() != 0) {
            List<ComplexOrder> result = new ArrayList<>();

            Map<String, SortedSet<ComplexOrder>> symbolEntry = orderByOtherOrder.get(symbol);

            for (Map.Entry<String, SortedSet<ComplexOrder>> sideEntry : symbolEntry.entrySet()) {
                if (!sideEntry.getKey().equals(side)) {
                    List<ComplexOrder> orders = sideEntry.getValue().stream()
                            .filter(byStatus)
                            .filter(byMinQuantity)
                            .collect(Collectors.toList());
                    result.addAll(orders);
                }
            }

            return result;
        } else return null;
    }

    @Override
    public ComplexOrder findById(String orderId) {

        Predicate<ComplexOrder> byId = o -> o.getOrderId().equals(orderId);

        List<ComplexOrder> allOrders = getAllOrders();

        SortedSet<ComplexOrder> orders = allOrders.stream().filter(byId).collect(Collectors.toCollection(()
                -> new TreeSet<>(Comparator.comparing(ComplexOrder::getOrderId))));
        if (orders.size() != 0) {
            return orders.first();
        } else return null;
    }

    @Override
    public List<ComplexOrder> findBySymbol(String symbol) {

        Predicate<ComplexOrder> bySymbol = o -> o.getSymbol().equals(symbol);

        List<ComplexOrder> allOrders = getAllOrders();

        return allOrders.stream().filter(bySymbol).collect(Collectors.toList());
    }
}
