package shared;

import order.constant.Consts;
import order.io.entity.ComplexOrder;
import order.io.entity.Order;
import order.model.request.CancelReqModel;
import order.model.request.ComplexOrderReqDetailsModel;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import static org.assertj.core.util.DateUtil.parse;

public class TestUtils {

    public static String getDateNowStr() {

        Date date = new Date();
        Timestamp ts = new Timestamp(date.getTime());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return formatter.format(ts);
    }

    public static Date getDateNow() {
        return new java.util.Date();
    }

    public static Order makeSimpleOrder(String acc,
                                        Boolean buy, BigDecimal limitPrice, String symbol, Integer quantity) {
        Order order = new Order();

        order.setAccount(acc);
        order.setBuy(buy);
        order.setLimitPrice(limitPrice);
        order.setSymbol(symbol);
        order.setQuantity(quantity);

        // update order properties where need be to generated by system
        String orderId = UUID.randomUUID().toString();
        order.setOrderId(orderId);
        order.setOrderDate(getDateNow());
        order.setModifiedDate(getDateNow());
        order.setStatus(Consts.CONFIRMED);

        return order;
    }

    public static CancelReqModel makeCancelReq(String orderId){
        CancelReqModel req = new CancelReqModel();
        req.setOrderId(orderId);
        return req;
    }

    public static ComplexOrderReqDetailsModel makeComplexOrderReqDetails(String acc,
                                                                         Boolean buy, BigDecimal limitPrice,
                                                                         String symbol, Integer quantity,
                                                                         String activation, String activationDate,
                                                                         Integer minQuantity, String side) {
        ComplexOrderReqDetailsModel req = new ComplexOrderReqDetailsModel();

        Order o = makeSimpleOrder(acc, buy, limitPrice, symbol, quantity);
        BeanUtils.copyProperties(o, req);

        if (activation.equals(Consts.ByOtherOrder)) {
            req.setMinQuantity(minQuantity);
            req.setSide(side);
            req.setActivation(Consts.ByOtherOrder);
        } else {
            req.setActivation(Consts.ByTime);
            req.setActivationDate(activationDate);
        }

        return req;
    }

    public static ComplexOrder makeComplexOrder(String acc,
                                                Boolean buy, BigDecimal limitPrice,
                                                String symbol, Integer quantity,
                                                String activation, String activationDate,
                                                Integer minQuantity, String side) {

        ComplexOrderReqDetailsModel req = makeComplexOrderReqDetails(acc, buy, limitPrice, symbol, quantity, activation,
                activationDate, minQuantity, side);
        ComplexOrder order = new ComplexOrder();
        BeanUtils.copyProperties(req, order);
        order.setActivationDate(parse(req.getActivationDate()));
        return order;
    }
}