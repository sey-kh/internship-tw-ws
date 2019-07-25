package order.service.impl;

import order.constant.Consts;
import order.io.entity.ComplexOrder;
import order.io.entity.Order;
import order.io.repository.ComplexOrderRepository;
import order.io.repository.OrderRepository;
import order.service.ActivationService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ActivationServiceImpl implements ActivationService {

    private static ComplexOrderRepository complexOrderRepository;
    private static OrderRepository orderRepository;

    @Autowired
    public ActivationServiceImpl(ComplexOrderRepository complexOrderRepository, OrderRepository orderRepository) {
        ActivationServiceImpl.complexOrderRepository = complexOrderRepository;
        ActivationServiceImpl.orderRepository = orderRepository;
    }


    @Override
    public List<ComplexOrder> get_to_be_activated_orders(List<ComplexOrder> list) {

        List<ComplexOrder> to_be_activated_orders = new ArrayList<>(list);
        List<ComplexOrder> allOrders = new ArrayList<>();

        while (to_be_activated_orders.size() > 0) {
            ComplexOrder o = to_be_activated_orders.get(0);

            // activation params
            String side = ((o.getBuy()) ? Consts.SALE : Consts.BUY);
            String symbol = o.getSymbol();
            Integer quantity = o.getQuantity();

            List<ComplexOrder> orders = complexOrderRepository.findAllByParams(symbol, side, quantity);

            if (orders != null) {
                to_be_activated_orders.addAll(orders);
                complexOrderRepository.delete_orders_by_other_order_in_batch(orders);
            }
            allOrders.add(o);
            to_be_activated_orders.remove(0);
        }
        return allOrders;
    }

    @Override
    public void activateOrder(List<ComplexOrder> allOrders) {
        // prepare list of order objects
        List<Order> orders = new ArrayList<>();

        for (ComplexOrder o : allOrders) {
            Order order = new Order();
            BeanUtils.copyProperties(o, order);
            orders.add(order);
        }
        // create orders entries
        orderRepository.saveAll(orders);
    }

    @Override
    public void activateByOrder(Order order) {
        ComplexOrder complexOrder = new ComplexOrder();
        BeanUtils.copyProperties(order, complexOrder);

        List<ComplexOrder> list = new ArrayList<>();
        list.add(complexOrder);

        List<ComplexOrder> _list = new ArrayList<>();
        List<ComplexOrder> allOrders = get_to_be_activated_orders(list);

        // activate all
        activateOrder(allOrders);
    }

    // for scheduling task
    @Override
    public void activateByTime(Date time) {
        List<ComplexOrder> list = complexOrderRepository.findAllWithCurrentDateBefore(time);

        if (list != null) {
            complexOrderRepository.delete_orders_by_time_in_batch(list);

            // get all others complex orders where can be activated by these
            List<ComplexOrder> allOrders = get_to_be_activated_orders(list);

            // activate all
            activateOrder(allOrders);
        } else {
            Consts.LOGGER.info("There is no complex order to be activated");
        }
    }

}
