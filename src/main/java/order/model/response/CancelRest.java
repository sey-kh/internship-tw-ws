package order.model.response;

import java.util.Date;

public class CancelRest extends CreateRest{
    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    private Date modifiedDate;
}
