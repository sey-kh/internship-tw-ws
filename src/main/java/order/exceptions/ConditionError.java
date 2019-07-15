package order.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.PRECONDITION_FAILED)
public class ConditionError extends RuntimeException {
    public ConditionError(String message) {
        super(message);
    }
}
