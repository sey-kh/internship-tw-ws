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
    public List<ComplexOrder> getToBeActivated(String symbol, Boolean buy, Integer quantity){

        List<ComplexOrder> complexOrders = new ArrayList<>();
        List<ComplexOrder> listForIterate = new ArrayList<>();

        do {
            if (listForIterate.size() == 0){
                // get all complex orders that are waiting to be triggered
                List<ComplexOrder> orders = complexOrderRepository.findAllByParams(symbol, buy, quantity);

                // expand iteration
                if (orders!=null){
                    listForIterate.addAll(orders);
                    complexOrderRepository.deleteInBatch(orders, Consts.ByOtherOrder);
                }
            }
            else {
                ComplexOrder o = listForIterate.get(0);
                // get all complex orders that are waiting to be triggered
                List<ComplexOrder> orders = complexOrderRepository.findAllByParams(o.getSymbol(), o.getBuy(), o.getQuantity());

                complexOrders.add(o);
                listForIterate.remove(o);

                // expand iteration
                if (orders!=null) {
                    listForIterate.addAll(orders);
                    complexOrderRepository.deleteInBatch(orders, Consts.ByOtherOrder);
                }
            }
        } while (listForIterate.size() > 0);

        return complexOrders;
    }

    @Override
    public void activateComplexOrder(List<ComplexOrder> allOrders) {
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

    // for scheduling task
    @Override
    public void activateByTime(Date time) {
        List<ComplexOrder> list = complexOrderRepository.findAllWithCurrentDateBefore(time);

        if (list!=null){
            List<ComplexOrder> toBeActivatedOrders = new ArrayList<>(list);
            for (ComplexOrder o:list){
                List<ComplexOrder> result = getToBeActivated(o.getSymbol(), o.getBuy(), o.getQuantity());
                if (result.size()!=0)
                    toBeActivatedOrders.addAll(result);
            }
            // activate all complex orders
            activateComplexOrder(toBeActivatedOrders);
            complexOrderRepository.deleteInBatch(list, Consts.ByTime);
        }
        else Consts.LOGGER.info("There is no complex order to be activated");
    }

    @Override
    public void activateByOrder(Order o) {
        List<ComplexOrder> toBeActivatedOrders = getToBeActivated(o.getSymbol(),o.getBuy(), o.getQuantity());
        if (toBeActivatedOrders.size()!=0){
            // activate all complex orders
            activateComplexOrder(toBeActivatedOrders);
        }
    }
}
