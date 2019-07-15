package order.controller;

import order.entity.Order;
import order.exceptions.OrderNotFoundException;
import order.model.request.orderReqDetails;
import order.model.request.cancelReq;
import order.model.request.qtyUpdateReq;
import order.model.response.OrderRest;
import order.service.OrderRepository;
import order.service.OrderRepositoryCustom;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderRepository orderRepository;

    private final OrderRepositoryCustom orderRepositoryCustom;

    @Autowired
    public OrderController(OrderRepository orderRepository, OrderRepositoryCustom orderRepositoryCustom) {
        this.orderRepository = orderRepository;
        this.orderRepositoryCustom = orderRepositoryCustom;
    }

    // return all orders for the given account and symbol
    // returns all orders for the given account
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<Order> getOrders(@RequestParam(value = "account") String account,
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
        optional.ifPresent(order -> BeanUtils.copyProperties(order, returnValue));

        return returnValue;
    }

    // create order and return orderId
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {
            MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> createOrder(@Valid @RequestBody orderReqDetails orderReq) {

        Order order = new Order();
        orderReq.setStatus("confirmed");

        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
        orderReq.setOrderDate(now);
        orderReq.setModifiedDate(now);

        BeanUtils.copyProperties(orderReq, order);

        String orderId = orderRepositoryCustom.createOrder(order);

        HashMap<String, String> returnValue = new HashMap<>();
        returnValue.put("orderId", orderId);
        returnValue.put("status", order.getStatus());

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(orderId)
                .toUri();

        return ResponseEntity.created(location).body(returnValue);
    }

    // Update order quantity
    @PatchMapping(path = "/{orderId}", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {
            MediaType.APPLICATION_JSON_VALUE})
    public OrderRest updateOrder(@PathVariable String orderId,
                                 @Valid @RequestBody qtyUpdateReq req) {

        Order order = orderRepositoryCustom.updateQuantity(req.getQuantity(), orderId);
        OrderRest returnValue = new OrderRest();
        BeanUtils.copyProperties(order, returnValue);
        return returnValue;
    }

    // Cancel order
    @PostMapping(path = "/cancel/{orderId}")
    public ResponseEntity<Object> cancelOrder(
            @PathVariable String orderId,
            @Valid @RequestBody cancelReq req) {

        // validate cancel privilege
        // In this case I assumed this validation

        if (req.getToken().equals("OhKo6WJUpd")){
            Order order = orderRepositoryCustom.cancelOrder(orderId);

            HashMap<String, String> returnValue = new HashMap<>();
            returnValue.put("orderId", orderId);
            returnValue.put("modifiedDate", order.getModifiedDate());
            returnValue.put("status", order.getStatus());

            return ResponseEntity.ok(returnValue);
        }
        else{
            HashMap<String, String> returnValue = new HashMap<>();
            returnValue.put("faultstring", "Authentication Failed");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(returnValue);
        }
    }

}
