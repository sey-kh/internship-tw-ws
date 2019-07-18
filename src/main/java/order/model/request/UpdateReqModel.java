package order.model.request;

import javax.validation.constraints.NotNull;

public class UpdateReqModel {

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @NotNull
    private Integer quantity;
}