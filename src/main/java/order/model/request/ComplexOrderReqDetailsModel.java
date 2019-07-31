package order.model.request;

import order.model.response.OrderRest;

import javax.validation.constraints.NotNull;

public class ComplexOrderReqDetailsModel extends OrderRest {

    @NotNull
    private String activation;

    private String side;

    private Integer minQuantity;

    private String activationDate;

    // Getter and Setter

    public String getActivation() {
        return activation;
    }

    public void setActivation(String activation) {
        this.activation = activation;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public Integer getMinQuantity() {
        return minQuantity;
    }

    public void setMinQuantity(Integer minQuantity) {
        this.minQuantity = minQuantity;
    }

    public String getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(String activationDate) {
        this.activationDate = activationDate;
    }
}

