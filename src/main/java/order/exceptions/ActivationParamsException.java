package order.exceptions;

import order.model.request.ComplexOrderReqDetailsModel;

import java.util.*;

public class ActivationParamsException {

    public Map<String, Object> body;
    public Boolean isValid;

    public ActivationParamsException(ComplexOrderReqDetailsModel req) {
        switch (req.getActivation().toLowerCase().trim()) {
            case "bytime":
                if (req.getActivationDate() == null) {
                    Map<String, Object> body = new LinkedHashMap<>();
                    body.put("timestamp", new Date());
                    List<String> errors = new ArrayList<>();
                    errors.add("activationDate must be not null");
                    body.put("error", errors);
                    this.body = body;
                    this.isValid = false;
                } else {
                    this.isValid = true;
                }
                break;
            case "byotherorder":
                Map<String, Object> body = new LinkedHashMap<>();
                body.put("timestamp", new Date());
                List<String> errors = new ArrayList<>();

                if (req.getMinQuantity() == null)
                    errors.add("minQuantity must be not null");
                if (req.getSide() == null)
                    errors.add("side must be not null");
                body.put("error", errors);
                this.body = body;

                this.isValid = errors.size() == 0;
                break;
            default:
                throw new order.exceptions.ConditionError("Activation key error -> " + req.getActivation());
        }
    }
}

