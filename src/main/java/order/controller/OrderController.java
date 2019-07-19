package order.controller;

import order.domain.OrderDomain;
import order.entity.Order;
import order.model.request.OrderReqDetailsModel;
import order.model.request.CancelReqModel;
import order.model.request.UpdateReqModel;
import order.model.response.CancelRest;
import order.model.response.CreateRest;
import order.model.response.OrderRest;
import org.springframework.beans.BeanUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    // Url Mapping

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<Order> getOrders(@RequestParam(value = "account") String account,
                                 @RequestParam(value = "symbol", defaultValue = "") String symbol) {

        if (symbol.equals("")){
            return OrderDomain.getOrderByAccount(account);
        }
        else{
            return OrderDomain.findByAccountAndSymbol(account, symbol);
        }
    }

    @GetMapping(path = "/{orderId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public OrderRest getOrder(@PathVariable String orderId) {

        Order order = OrderDomain.getOrderById(orderId);

        // response
        OrderRest returnValue = new OrderRest();
        BeanUtils.copyProperties(order, returnValue);

        return returnValue;
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {
            MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> createOrder(@Valid @RequestBody OrderReqDetailsModel req) {

        Order order = OrderDomain.addOrder(req);

        // response
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(order.getOrderId())
                .toUri();
        CreateRest returnValue = new CreateRest();
        BeanUtils.copyProperties(order, returnValue);

        // response
        return ResponseEntity.created(location).body(returnValue);
    }

    @PatchMapping(path = "/{orderId}", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {
            MediaType.APPLICATION_JSON_VALUE})
    public OrderRest updateOrder(@PathVariable String orderId,
                                 @Valid @RequestBody UpdateReqModel req) {

        Order order = OrderDomain.updateOrderQuantity(orderId, req);

        // response
        OrderRest returnValue = new OrderRest();
        BeanUtils.copyProperties(order, returnValue);
        return returnValue;
    }

    @PostMapping(path = "/cancel")
    public ResponseEntity<Object> cancelOrder(
            @Valid @RequestBody CancelReqModel req) {

        Order order = OrderDomain.cancelOrder(req);

        // response
        CancelRest returnValue = new CancelRest();
        BeanUtils.copyProperties(order, returnValue);

        return ResponseEntity.ok(returnValue);
    }
}
