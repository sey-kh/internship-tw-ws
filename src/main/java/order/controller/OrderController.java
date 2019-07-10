package order.controller;

import order.entity.Order;
import order.exceptions.OrderNotFoundException;
import order.exceptions.UserError;
import order.model.request.OrderReqDetailsModel;
import order.model.response.OrderRest;
import order.service.OrderRepository;
import order.service.OrderRepositoryCustom;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.SimpleDateFormat;
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

    // return all orders for the given account and symbol
    // returns all orders for the given account
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<Order> getOrders(@RequestParam(value = "account", required = true) String account,
                                 @RequestParam(value = "symbol", defaultValue = "") String symbol) {
        return orderRepositoryCustom.findOrdersByAccountAndSymbol(account, symbol);
    }


    // return a particular order corresponding to request orderId
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

    // create order and return orderId
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {
            MediaType.APPLICATION_JSON_VALUE})
    public HashMap<String, String> createOrder(@Valid @RequestBody OrderReqDetailsModel orderReq) {

        Order order = new Order();
        orderReq.setStatus("confirmed");

        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
        orderReq.setOrderDate(now);
        orderReq.setModifiedDate(now);

        BeanUtils.copyProperties(orderReq, order);

        String orderId = orderRepositoryCustom.createOrder(order);

        HashMap<String, String> returnValue = new HashMap<String, String>();
        returnValue.put("orderId", orderId);
        returnValue.put("status", order.getStatus());

        return returnValue;
    }

    // Update order quantity
    @PatchMapping(path = "/{orderId}", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {
            MediaType.APPLICATION_JSON_VALUE})
    public OrderRest updateOrder(@PathVariable String orderId,
                                 @RequestBody HashMap updateReq) {

        if (updateReq.get("quantity") == null) throw new UserError("You can update only quantity!");
        else{
            Order order = orderRepositoryCustom.updateQuantity((Integer) updateReq.get("quantity"), orderId);
            OrderRest returnValue = new OrderRest();
            BeanUtils.copyProperties(order, returnValue);
            return returnValue;
        }
    }

    // Cancel order
    @PatchMapping(path = "/cancel")
    public HashMap<String, String> cancelOrder(
            @RequestBody HashMap cancelReq) {

        String orderId = (String) cancelReq.get("orderId");
        Order order = orderRepositoryCustom.cancelOrder(orderId);

        HashMap<String, String> returnValue = new HashMap<String, String>();
        returnValue.put("orderId", orderId);
        returnValue.put("status", order.getStatus());

        return returnValue;
    }

}
