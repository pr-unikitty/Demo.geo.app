package testing.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.OK)
public class OkException extends RuntimeException {

    public OkException(String message) {
        super(message);
    }

}