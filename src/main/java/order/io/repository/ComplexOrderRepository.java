package order.io.repository;

import order.io.entity.ComplexOrder;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.SortedSet;

@Repository
public interface ComplexOrderRepository {

    List<ComplexOrder> findAllWithCurrentDateBefore(Date currentDate);

    List<ComplexOrder> findAllByParams(String symbol, Boolean buy, Integer quantity);

    List<ComplexOrder> findBySymbol(String symbol);

    ComplexOrder findById(String orderId);

    SortedSet<ComplexOrder> getAllOrders();

    ComplexOrder save(ComplexOrder order);

    void deleteInBatch(List<ComplexOrder> orders, String activation);
    void deleteAll();

}
