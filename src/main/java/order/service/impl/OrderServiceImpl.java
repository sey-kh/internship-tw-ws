package order.service.impl;

import order.constant.Consts;
import order.io.entity.Order;
import order.exceptions.ConditionError;
import order.exceptions.NotFoundException;
import order.model.request.CancelReqModel;
import order.model.request.OrderReqDetailsModel;
import order.model.request.UpdateReqModel;
import order.io.repository.OrderRepository;
import order.service.ActivationService;
import order.service.OrderService;
import order.shared.Utils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {

    private static OrderRepository orderRepository;
    private static ActivationService activationService;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, ActivationService activationService) {
        OrderServiceImpl.orderRepository = orderRepository;
        OrderServiceImpl.activationService = activationService;
    }

    @Override
    public Order updateOrderQuantity(String orderId, UpdateReqModel req) {
        try {
            Order order = orderRepository.findById(orderId).get();
            if (order.getStatus().equals(Consts.CANCEL)) {
                throw new ConditionError("Can not update cancelled order!");
            }
            order.setQuantity(req.getQuantity());
            order.setModifiedDate(Utils.getDateNow());
            orderRepository.save(order);
            return order;
        } catch (NoSuchElementException e) {
            throw new NotFoundException("id-" + orderId);
        }
    }

    @Override
    public Order cancelOrder(CancelReqModel req) {
        try {
            Order order = orderRepository.findById(req.getOrderId()).get();
            if (order.getStatus().equals(Consts.CANCEL)) {
                throw new ConditionError("Order already cancelled!");
            }
            order.setStatus(Consts.CANCEL);
            order.setModifiedDate(Utils.getDateNow());
            orderRepository.save(order);
            return order;
        } catch (NoSuchElementException e) {
            throw new NotFoundException("id-" + req.getOrderId());
        }
    }

    @Override
    public Order getOrderById(String orderId) {
        try {
            return orderRepository.findById(orderId).get();
        } catch (NoSuchElementException e) {
            throw new NotFoundException("id-" + orderId);
        }
    }

    @Override
    public List<Order> getOrderByAccount(String account) {
        return orderRepository.findByAccount(account);
    }

    @Override
    public List<Order> getOrderBySymbol(String symbol) {
        return orderRepository.findBySymbol(symbol);
    }

    @Override
    public List<Order> findByAccountAndSymbol(String account, String symbol) {
        return orderRepository.findByAccountAndSymbol(account, symbol);
    }

    @Override
    public Order addOrder(OrderReqDetailsModel req) {
        // order entity
        Order order = new Order();

        // copy properties from request params
        BeanUtils.copyProperties(req, order);

        // update order properties where need be to generated by system
        String orderId = Utils.generateRandomString();
        order.setOrderId(orderId);
        order.setOrderDate(Utils.getDateNow());
        order.setModifiedDate(Utils.getDateNow());
        order.setStatus(Consts.CONFIRMED);

        orderRepository.save(order);

        // activate complex orders by this order
        activationService.activateByOrder(order);

        return order;
    }
}
