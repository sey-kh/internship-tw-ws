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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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


    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {
            MediaType.APPLICATION_JSON_VALUE})
    public Order createOrder(@RequestBody OrderReqDetailsModel orderReq) {
        Order order = new Order();
        BeanUtils.copyProperties(orderReq, order);
        orderRepositoryCustom.createOrder(order);
        return null;
    }

    @PatchMapping(path = "/{orderId}", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {
            MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity updateOrder(@PathVariable String orderId,
                                      @RequestBody OrderReqPartialModel updateQuantity) {
        orderRepositoryCustom.updateQuantity(updateQuantity.getQuantity(), orderId);
        return null;
    }

}
