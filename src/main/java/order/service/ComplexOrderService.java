package order.service;

import order.io.entity.ComplexOrder;
import order.model.request.CancelReqModel;
import order.model.request.ComplexOrderReqDetailsModel;
import org.springframework.stereotype.Service;

import java.util.SortedSet;

@Service
public interface ComplexOrderService {

    ComplexOrder addOrder(ComplexOrderReqDetailsModel req);

    ComplexOrder cancelOrder(CancelReqModel req);

    SortedSet<ComplexOrder> getAllOrders();

    void displayOrders();

}
