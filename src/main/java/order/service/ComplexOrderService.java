package order.service;

import order.io.entity.ComplexOrder;
import order.model.request.CancelReqModel;
import order.model.request.ComplexOrderReqDetailsModel;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public interface ComplexOrderService {

    Date getDateNow();

    ComplexOrder addOrder(ComplexOrderReqDetailsModel req);

    ComplexOrder cancelOrder(CancelReqModel req);

    void displayOrders();

    void addBatchOrders();

}
