package order.service;

import order.io.entity.ComplexOrder;
import order.io.entity.Order;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public interface ActivationService {

    List<ComplexOrder> getToBeActivated(String symbol, Boolean buy, Integer quantity);

    void activateComplexOrder(List<ComplexOrder> allOrders);

    void activateByTime(Date time);

    void activateByOrder(Order o);
}
