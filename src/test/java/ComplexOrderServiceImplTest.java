import order.WsApplication;
import order.constant.Consts;
import order.io.entity.ComplexOrder;
import order.io.repository.ComplexOrderRepository;
import order.model.request.CancelReqModel;
import order.model.request.ComplexOrderReqDetailsModel;
import order.service.ComplexOrderService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import shared.TestUtils;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.assertEquals;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {WsApplication.class})
@WebAppConfiguration
public class ComplexOrderServiceImplTest {

    @Autowired
    private ComplexOrderService complexOrderService;

    @Autowired
    private ComplexOrderRepository complexOrderRepository;


    @Before
    public void setUp() {
        // clear all orders before each test
        complexOrderRepository.deleteAll();

        // add 10 complex orders before each test
        for (int i=0; i<10; i++){
            ComplexOrder order = TestUtils.makeComplexOrder("acc_1",
                    true, BigDecimal.valueOf(100), "aapl", 100, Consts.ByOtherOrder, null,
                    100, Consts.BUY);

            complexOrderRepository.save(order);
        }

        for (int i = 0; i < 10; i++) {
            ComplexOrderReqDetailsModel req = TestUtils.makeComplexOrderReqDetails("acc_1",
                    true, BigDecimal.valueOf(100), "aapl", 100, Consts.ByTime, TestUtils.getDateNowStr(),
                    null, null);
            complexOrderService.addOrder(req);
        }
    }
    // trying to add 10000 complex orders
    @Test
    public void addComplexOrder() {
        List<ComplexOrder> allOrders = complexOrderService.getAllOrders();
        assertEquals(20, allOrders.size());

        for (int i = 0; i < 5000; i++) {
            ComplexOrderReqDetailsModel req = TestUtils.makeComplexOrderReqDetails("acc_1",
                    true, BigDecimal.valueOf(100), "aapl", 100, Consts.ByTime, TestUtils.getDateNowStr(),
                    null, null);
            complexOrderService.addOrder(req);
        }

        for (int i = 0; i < 5000; i++) {
            ComplexOrderReqDetailsModel req = TestUtils.makeComplexOrderReqDetails("acc_1",
                    true, BigDecimal.valueOf(100), "aapl", 100, Consts.ByOtherOrder, null,
                    100, Consts.BUY);
            complexOrderService.addOrder(req);
        }
        // retrieving all complex orders
        // check whether all orders have been added
        allOrders = complexOrderService.getAllOrders();
        assertEquals(10020, allOrders.size());
    }

    // cancel complex orders
    @Test
    public void cancelComplexOrder() {
        // retrieving all complex orders have been added during setup
        List<ComplexOrder> allOrders = complexOrderService.getAllOrders();

        for (ComplexOrder o:allOrders){
            assertEquals(o.getStatus(), Consts.CONFIRMED);
        }

        for (ComplexOrder o:allOrders){

            CancelReqModel req = TestUtils.makeCancelReq(o.getOrderId());
            // cancel order
            complexOrderService.cancelOrder(req);
        }

        for (ComplexOrder o:allOrders){
            // check whether above 10 complex orders have been cancelled
            assertEquals(o.getStatus(), Consts.CANCELLED);
        }
    }
}


