package order.model.request;

import order.constant.Consts;

import javax.validation.constraints.NotNull;

public class CancelReqModel {

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    @NotNull
    private String orderId;
}
