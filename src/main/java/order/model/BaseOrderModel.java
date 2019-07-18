package order.model;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class BaseOrderModel {

    @NotNull
    private String account;

    @NotNull
    private String symbol;

    @NotNull
    private Integer quantity;

    @NotNull
    private Boolean buy;

    private BigDecimal limitPrice; // limitPrice can be null (such an order is called "market order")

    // Getter and Setter

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Boolean getBuy() {
        return buy;
    }

    public void setBuy(Boolean buy) {
        this.buy = buy;
    }

    public BigDecimal getLimitPrice() {
        return limitPrice;
    }

    public void setLimitPrice(BigDecimal limitPrice) {
        this.limitPrice = limitPrice;
    }
}
