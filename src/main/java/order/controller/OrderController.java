package order.controller;

import order.entity.Order;
import order.exceptions.OrderNotFoundException;
import order.model.request.OrderReqDetailsModel;
import order.model.request.OrderReqPartialModel;
import order.model.response.OrderRest;
import order.service.OrderRepository;
import order.service.OrderRepositoryCustom;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderRepositoryCustom orderRepositoryCustom;


    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<Order> getOrders(@RequestParam(value = "account", required = true) String account,
                                 @RequestParam(value = "symbol", defaultValue = "") String symbol) {
        List<Order> orders = orderRepositoryCustom.findOrdersByAccountAndSymbol(account, symbol);
        return orders;
    }


    @GetMapping(path = "/{orderId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public OrderRest getOrder(@PathVariable String orderId) {
        OrderRest returnValue = new OrderRest();
        Optional<Order> optional = orderRepository.findById(orderId);
        if (!optional.isPresent())
            throw new OrderNotFoundException("id-" + orderId);
        optional.ifPresent(order -> {
            BeanUtils.copyProperties(order, returnValue);
        });
        return returnValue;
    }

    @GetMapping(path = "/{orderId}/cancel")
    public HashMap<String, String> cancelOrder(@PathVariable String orderId) {
        Order order = orderRepositoryCustom.cancelOrder(orderId);
        HashMap<String, String> returnValue = new HashMap<String, String>();
        returnValue.put("orderId", orderId);
        returnValue.put("status", order.getStatus());
        return returnValue;
    }


    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {
            MediaType.APPLICATION_JSON_VALUE})
    public HashMap<String, String> createOrder(@RequestBody OrderReqDetailsModel orderReq) {
        Order order = new Order();
        orderReq.setStatus("confirmed");
        BeanUtils.copyProperties(orderReq, order);
        String orderId = orderRepositoryCustom.createOrder(order);
        HashMap<String, String> returnValue = new HashMap<String, String>();
        returnValue.put("orderId", orderId);
        returnValue.put("status", order.getStatus());
        return returnValue;
    }

    @PatchMapping(path = "/{orderId}", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {
            MediaType.APPLICATION_JSON_VALUE})
    public OrderRest updateOrder(@PathVariable String orderId,
                                 @RequestBody OrderReqPartialModel updateQuantity) {
        Order order = orderRepositoryCustom.updateQuantity(updateQuantity.getQuantity(), orderId);
        OrderRest returnValue = new OrderRest();
        BeanUtils.copyProperties(order, returnValue);
        return returnValue;
    }

}
