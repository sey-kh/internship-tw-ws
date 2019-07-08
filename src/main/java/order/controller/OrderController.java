package order.controller;

import order.model.request.OrderDetailsRequestModel;
import order.model.response.OrderRest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public List<OrderRest> getOrders(@RequestParam(value = "account", required = true) String account,
                                     @RequestParam(value = "symbol", defaultValue = "") String symbol) {
        List<OrderRest> returnValue = new ArrayList<>();

        return returnValue;
    }


    @GetMapping(path = "/{orderId}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public OrderRest getOrder(@PathVariable String orderId) {
        OrderRest returnValue = new OrderRest();
        return returnValue;
    }


    @PostMapping
    public String createOrder(){
        return "Order created";
    }

    @PutMapping(path = "/{orderId}")
    public String updateOrder(@PathVariable String orderId) {
        return "Order updated";
    }
}
