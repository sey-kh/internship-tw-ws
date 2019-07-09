package order.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.OK)
public class UserError extends RuntimeException {
    public UserError(String message) {
        super(message);
    }
}
