package order.model.request;

import javax.validation.constraints.NotNull;

public class qtyUpdateReq {

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @NotNull
    private Integer quantity;

}


