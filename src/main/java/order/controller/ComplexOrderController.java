package order.controller;

import order.domain.ComplexOrderDomain;
import order.entity.ComplexOrder;
import order.exceptions.ActivationParamsException;
import order.model.request.CancelReqModel;
import order.model.request.ComplexOrderReqDetailsModel;
import order.model.response.CancelRest;
import order.model.response.CreateRest;
import org.springframework.beans.BeanUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;

import java.net.URI;

@RestController
@RequestMapping("/complex-orders")
public class ComplexOrderController {

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {
            MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> createOrder(@Valid @RequestBody ComplexOrderReqDetailsModel req) {

        // validate activation params
        ActivationParamsException valObj = new ActivationParamsException(req);

        if (!valObj.isValid) {
            return ResponseEntity.badRequest().body(valObj.body);
        }

        ComplexOrder order = ComplexOrderDomain.addOrder(req);

        // response object
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(order.getOrderId())
                .toUri();
        CreateRest returnValue = new CreateRest();
        BeanUtils.copyProperties(order, returnValue);

        // response
        return ResponseEntity.created(location).body(returnValue);

    }

    // Cancel order
    @PostMapping(path = "/cancel")
    public ResponseEntity<Object> cancelOrder(
            @Valid @RequestBody CancelReqModel req) {

        ComplexOrder order = ComplexOrderDomain.cancelOrder(req);

        // response
        CancelRest returnValue = new CancelRest();
        BeanUtils.copyProperties(order, returnValue);
        return ResponseEntity.ok(returnValue);
    }
}


