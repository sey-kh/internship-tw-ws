package order.io.repository;

import order.io.entity.ComplexOrder;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.SortedSet;

@Repository
public interface ComplexOrderRepository {

    List<ComplexOrder> findAllWithCurrentDateBefore(Date currentDate);

    List<ComplexOrder> findAllByParams(String symbol, String side, Integer quantity);

    SortedSet<ComplexOrder> getAllOrders();

    ComplexOrder getById(String orderId);

    ComplexOrder save(ComplexOrder order);

    void delete_orders_by_time_in_batch(List<ComplexOrder> orders);

    void delete_orders_by_other_order_in_batch(List<ComplexOrder> orders);

    void displayOrders();

}
