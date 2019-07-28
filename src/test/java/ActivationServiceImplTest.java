import order.WsApplication;
import order.constant.Consts;
import order.io.entity.ComplexOrder;
import order.io.entity.Order;
import order.io.repository.ComplexOrderRepository;
import order.io.repository.OrderRepository;
import order.service.ActivationService;
import order.service.OrderService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import shared.TestUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {WsApplication.class})
@WebAppConfiguration
public class ActivationServiceImplTest {

    @Autowired
    private ActivationService activationService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ComplexOrderRepository complexOrderRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Before
    public void setUp() {
        // clear all orders before each test
        orderRepository.deleteAll();
        complexOrderRepository.deleteAll();

        // add 60 complex orders before each test
        // 40 activate by future order (aapl:30, amzn:10)
        // 20 by time (aapl:10, amzn:10))

        // add 10 buy orders of aapl waiting to be triggered by future buy order
        for (int i=0; i<10; i++){
            ComplexOrder order = TestUtils.makeComplexOrder("acc_1",
                    true, BigDecimal.valueOf(100), "aapl", 100, Consts.ByOtherOrder, null,
                    100, Consts.BUY);
            complexOrderRepository.save(order);
        }

        // add 10 buy orders of aapl waiting to be triggered by any future order
        for (int i=0; i<10; i++){
            ComplexOrder order = TestUtils.makeComplexOrder("acc_1",
                    false, BigDecimal.valueOf(100), "aapl", 100, Consts.ByOtherOrder, null,
                    100, Consts.ANY);
            complexOrderRepository.save(order);
        }

        // add 10 sale orders of aapl waiting to be triggered by future sale order
        for (int i=0; i<10; i++){
            ComplexOrder order = TestUtils.makeComplexOrder("acc_1",
                    false, BigDecimal.valueOf(100), "aapl", 100, Consts.ByOtherOrder, null,
                    100, Consts.SALE);
            complexOrderRepository.save(order);
        }

        // add 10 sale orders of amzn waiting to be triggered by future sale order
        for (int i=0; i<10; i++){
            ComplexOrder order = TestUtils.makeComplexOrder("acc_1",
                    false, BigDecimal.valueOf(100), "amzn", 100, Consts.ByOtherOrder, null,
                    100, Consts.SALE);
            complexOrderRepository.save(order);
        }

        // add 10 buy orders of aapl waiting to be triggered by time
        for (int i=0; i<10; i++){
            String activationDate = TestUtils.getDateNowStr();
            ComplexOrder order = TestUtils.makeComplexOrder("acc_1",
                    true, BigDecimal.valueOf(100), "aapl", 100, Consts.ByTime, activationDate,
                    null, null);
            complexOrderRepository.save(order);
        }

        // add 10 buy orders of amzn waiting to be triggered by time
        for (int i=0; i<10; i++){
            String activationDate = TestUtils.getDateNowStr();
            ComplexOrder order = TestUtils.makeComplexOrder("acc_1",
                    false, BigDecimal.valueOf(100), "amzn", 100, Consts.ByTime, activationDate,
                    null, null);
            complexOrderRepository.save(order);
        }
    }

    // test activateByOrder service
    @Test
    public void activateByOrder(){

        List<ComplexOrder> allOrders = complexOrderRepository.getAllOrders();

        assertEquals(60, allOrders.size());

        String symbol = "aapl";
        Order order = TestUtils.makeSimpleOrder("acc_1",
                true, BigDecimal.valueOf(100), symbol, 100);

        List<ComplexOrder> complexOrders = complexOrderRepository.findBySymbol(symbol);
        List<Order> orders = orderService.getOrderBySymbol(symbol);

        // Measure size of complexOrders and orders of aapl symbol before they are being manipulated
        assertEquals(40, complexOrders.size());
        assertEquals(0, orders.size());

        activationService.activateByOrder(order);

        complexOrders = complexOrderRepository.findBySymbol(symbol);
        orders = orderService.getOrderBySymbol(symbol);

        // out of 40 complex orders waiting to be trigger by future order there are 30 have been activated
        assertEquals(30, orders.size());
        // out of 40 complex orders of aapl there are 30 has been activated remaining 10 waiting to be triggered by time
        assertEquals(10, complexOrders.size());

        symbol = "amzn";
        List<ComplexOrder> amzn_complexOrders = complexOrderRepository.findBySymbol(symbol);
        // numbers of amzn complex orders still remain the same
        assertEquals(20, amzn_complexOrders.size());
    }

    // test activateByTime service
    @Test
    public void activateByTime(){
        Date currentDate = TestUtils.getDateNow();

        List<ComplexOrder> allComplexOrders = complexOrderRepository.getAllOrders();
        List<Order> allOrders = orderService.getOrderByAccount("acc_1");

        // Measure size of allComplexOrders and allOrders before they are being manipulated
        assertEquals(0, allOrders.size());
        assertEquals(60, allComplexOrders.size());

        activationService.activateByTime(currentDate);

        allComplexOrders = complexOrderRepository.getAllOrders();
        // 20 complex orders waiting to be triggered by time have been activated and they also activate all other complex orders
        assertEquals(0, allComplexOrders.size());

        allOrders = orderService.getOrderByAccount("acc_1");
        // 60 complex orders have been activated
        assertEquals(60, allOrders.size());
    }
}
