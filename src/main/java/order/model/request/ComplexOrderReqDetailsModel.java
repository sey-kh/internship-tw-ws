package order.model.request;

import order.model.response.OrderRest;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import java.util.Date;

public class ComplexOrderReqDetailsModel extends OrderRest {

    @NotNull
    private String activation;

    private String side;

    private Integer minQuantity;

    @Temporal(TemporalType.TIMESTAMP)
    private Date activationDate;

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

    public Date getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(Date activationDate) {
        this.activationDate = activationDate;
    }
}

