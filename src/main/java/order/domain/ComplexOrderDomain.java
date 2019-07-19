package order.domain;

import order.constant.Consts;
import order.entity.ComplexOrder;
import order.entity.Order;
import order.exceptions.ConditionError;
import order.exceptions.NotFoundException;
import order.model.request.CancelReqModel;
import order.model.request.ComplexOrderReqDetailsModel;
import order.service.ComplexOrderRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Component
public class ComplexOrderDomain {

    private static ComplexOrderRepository complexOrderRepository;

    @Autowired
    public ComplexOrderDomain(ComplexOrderRepository complexOrderRepository) {
        ComplexOrderDomain.complexOrderRepository = complexOrderRepository;
    }

    private static Date getDateNow() {
        return new java.util.Date();
    }

    // activate complex order
    private static void activate(ComplexOrder complexOrder) {

        // prepare order object
        Order order = new Order();
        BeanUtils.copyProperties(complexOrder, order);

        deleteOrder(complexOrder.getOrderId());

        // send order to stock exchange
        OrderDomain.addOrder(order);

    }

    public static void activateByTime(Date time) {

        // find all where its activationDate reached
        List<ComplexOrder> orders = complexOrderRepository.findAllWithCurrentDateBefore(time);

        // activate all
        for (ComplexOrder order : orders) {
            activate(order);
        }
    }

    static void activateByOtherOrder(String symbol, String side, Integer quantity) {
        List<ComplexOrder> orders = complexOrderRepository.findAllByParams(symbol, side, quantity);

        if (orders.size() != 0) {
            ComplexOrder order = orders.get(0);
            activate(order);
        }
    }

    // add new entry
    public static ComplexOrder addOrder(ComplexOrderReqDetailsModel req) {
        // entity
        ComplexOrder order = new ComplexOrder();

        // copy properties from request params
        BeanUtils.copyProperties(req, order);

        // update order properties where need be to generated by system
        String orderId = UUID.randomUUID().toString();
        order.setOrderId(orderId);
        order.setOrderDate(getDateNow());
        order.setModifiedDate(getDateNow());
        order.setStatus(Consts.CONFIRMED);

        if (req.getActivationDate() != null) {
            try {
                Date activationDate = new SimpleDateFormat(Consts.TIME_STAMP_FORMAT).parse(req.getActivationDate());
                order.setActivationDate(activationDate);
            } catch (ParseException e) {
                e.printStackTrace();
                throw new ConditionError("activationDate must be formatted as " + Consts.TIME_STAMP_FORMAT);
            }
        }

        // create entry
        complexOrderRepository.save(order);

        return order;
    }

    // cancel entry
    public static ComplexOrder cancelOrder(CancelReqModel req) {

        try {
            ComplexOrder order = complexOrderRepository.findById(req.getOrderId()).get();
            if (order.getStatus().equals(Consts.CANCEL)){
                throw new ConditionError("Order already cancelled!");
            }
            order.setStatus(Consts.CANCEL);
            complexOrderRepository.save(order);
            return order;
        } catch (NoSuchElementException e) {
            throw new NotFoundException("id-" + req.getOrderId());
        }
    }

    // delete entry
    private static void deleteOrder(String orderId) {
        complexOrderRepository.deleteById(orderId);
    }
}
