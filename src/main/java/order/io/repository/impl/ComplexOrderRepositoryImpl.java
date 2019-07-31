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

    private static NavigableSet<ComplexOrder> orderByTime;

    private static Map<String, Map<String, NavigableSet<ComplexOrder>>> orderByOtherOrder;

    public ComplexOrderRepositoryImpl() {
        orderByTime = new TreeSet<>(Comparator.comparing(ComplexOrder::getActivationDate)
                .thenComparing(ComplexOrder::getOrderId)
        );
        orderByOtherOrder = new HashMap<>();
    }

    @Override
    public List<ComplexOrder> getAllOrders() {
        List<ComplexOrder> allOrders = new ArrayList<>();

        for (Map.Entry<String, Map<String, NavigableSet<ComplexOrder>>> entry : orderByOtherOrder.entrySet()) {
            Map<String, NavigableSet<ComplexOrder>> symbolMap = entry.getValue();
            for (Map.Entry<String, NavigableSet<ComplexOrder>> sideMap : symbolMap.entrySet()) {
                SortedSet<ComplexOrder> orders = sideMap.getValue();
                allOrders.addAll(orders);
            }
        }
        allOrders.addAll(orderByTime);
        return allOrders;
    }

    @Override
    public ComplexOrder save(ComplexOrder order) {
        if (order.getActivation().toLowerCase().trim().equals(Consts.ByTime)) {
            orderByTime.add(order);
            return order;
        } else {
            String side = order.getSide();
            String symbol = order.getSymbol();

            // check if symbol existed
            if (orderByOtherOrder.containsKey(symbol)) {
                Map<String, NavigableSet<ComplexOrder>> symbolEntry = orderByOtherOrder.get(symbol);

                for (Map.Entry<String, NavigableSet<ComplexOrder>> sideEntry : symbolEntry.entrySet()) {
                    if (sideEntry.getKey().equals(side)) {
                        sideEntry.getValue().add(order);
                        return order;
                    }
                }
                return order;
            } else {
                // preparing map for storing orders by different side
                List<String> allSides = new ArrayList<>();
                allSides.add(Consts.BUY);
                allSides.add(Consts.SELL);
                allSides.add(Consts.ANY);

                Map<String, NavigableSet<ComplexOrder>> listMap = new HashMap<>();
                for (String s : allSides) {
                    if (!s.equals(side))
                        listMap.put(s, new TreeSet<>(
                                Comparator.comparing(ComplexOrder::getMinQuantity)
                                        .thenComparing(ComplexOrder::getOrderId)
                        ));
                    else {
                        NavigableSet<ComplexOrder> newOrder = new TreeSet<>(
                                Comparator.comparing(ComplexOrder::getMinQuantity)
                                        .thenComparing(ComplexOrder::getOrderId)
                        );
                        newOrder.add(order);
                        listMap.put(side, newOrder);
                    }
                }
                orderByOtherOrder.put(symbol, listMap);
                return order;
            }
        }
    }

    @Override
    public void deleteInBatch(List<ComplexOrder> orders, String activation) {
        if (activation.toLowerCase().trim().equals(Consts.ByTime))
            orderByTime.removeAll(orders);
        else {
            for (Map.Entry<String, Map<String, NavigableSet<ComplexOrder>>> symbolEntry : orderByOtherOrder.entrySet()) {
                Map<String, NavigableSet<ComplexOrder>> symbolMap = symbolEntry.getValue();
                for (Map.Entry<String, NavigableSet<ComplexOrder>> sideEntry : symbolMap.entrySet()) {
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
        // result list
        List<ComplexOrder> result = new ArrayList<>();

        // make a headSet of overall set
        ComplexOrder o = new ComplexOrder();
        o.setActivationDate(currentDate);
        o.setOrderId(String.valueOf(Character.MAX_VALUE));
        NavigableSet<ComplexOrder> headSet = orderByTime.headSet(o, true);

        // remove cancelled orders
        Predicate<ComplexOrder> byStatus = order -> order.getStatus().equals(Consts.CONFIRMED);

        if (headSet.size() != 0)
            result = headSet.stream().filter(byStatus).collect(Collectors.toList());

        return result;
    }

    @Override
    public List<ComplexOrder> findAllByParams(String symbol, Boolean buy, Integer quantity) {
        // result list
        List<ComplexOrder> result = new ArrayList<>();

        // side param
        String side;
        if (buy) side = Consts.SELL;
        else side = Consts.BUY;

        Map<String, NavigableSet<ComplexOrder>> symbolEntry = orderByOtherOrder.get(symbol);

        if (symbolEntry != null) {
            for (Map.Entry<String, NavigableSet<ComplexOrder>> sideEntry : symbolEntry.entrySet()) {
                if (!sideEntry.getKey().equals(side)) {
                    NavigableSet<ComplexOrder> orders = sideEntry.getValue();

                    if (orders.size() != 0) {

                        // make a headSet of overall set
                        ComplexOrder o = new ComplexOrder();
                        o.setMinQuantity(quantity);
                        o.setOrderId(String.valueOf(Character.MAX_VALUE));
                        NavigableSet<ComplexOrder> headSet = orders.headSet(o, true);

                        // remove cancelled orders
                        Predicate<ComplexOrder> byStatus = order -> order.getStatus().equals(Consts.CONFIRMED);

                        if (headSet.size() != 0)
                            result.addAll(headSet.stream().filter(byStatus).collect(Collectors.toList()));
                    }
                }
            }
        }
        return result;
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
