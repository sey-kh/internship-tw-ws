package order.service;

import order.io.entity.Order;
import order.model.request.CancelReqModel;
import order.model.request.OrderReqDetailsModel;
import order.model.request.UpdateReqModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OrderService {

    Order addOrder(OrderReqDetailsModel req);

    Order updateOrderQuantity(String orderId, UpdateReqModel req);

    Order cancelOrder(CancelReqModel req);

    Order getOrderById(String orderId);

    List<Order> getOrderByAccount(String account);

    List<Order> getOrderBySymbol(String symbol);

    List<Order> findByAccountAndSymbol(String account, String symbol);
}
