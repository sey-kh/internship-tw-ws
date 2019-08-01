package order.io.repository;

import order.io.entity.ComplexOrder;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ComplexOrderRepository {

    List<ComplexOrder> findAllWithCurrentDateBefore(Date currentDate);

    List<ComplexOrder> findAllByParams(String symbol, Boolean buy, Integer quantity);

    List<ComplexOrder> findBySymbol(String symbol);

    List<ComplexOrder> getAllOrders();

    ComplexOrder findById(String orderId);

    ComplexOrder save(ComplexOrder order);

    Boolean deleteInBatch(List<ComplexOrder> orders, String activation);

    void deleteAll();
}
