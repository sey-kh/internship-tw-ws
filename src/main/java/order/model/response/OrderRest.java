package order.model.response;

import order.model.BaseOrderModel;

public class OrderRest extends BaseOrderModel {

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    private String orderId;
}
