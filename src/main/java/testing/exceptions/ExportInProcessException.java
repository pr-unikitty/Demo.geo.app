package testing.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NO_CONTENT)
public class ExportInProcessException extends RuntimeException {

    public ExportInProcessException(String message) {
        super(message);
    }

}