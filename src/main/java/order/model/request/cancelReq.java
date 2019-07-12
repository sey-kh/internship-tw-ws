package order.model.request;

import javax.validation.constraints.NotNull;

public class cancelReq {

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    @NotNull
    private String orderId;
}
