package demo.geo.app.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class UnprocessableException extends RuntimeException {

    public UnprocessableException(String message) {
        super(message);
    }
    
    public UnprocessableException(String message, Throwable cause) {
        super(message, cause);
    }

}