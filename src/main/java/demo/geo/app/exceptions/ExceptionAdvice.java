package demo.geo.app.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ExceptionAdvice extends ResponseEntityExceptionHandler {
    
    @ExceptionHandler(NotFoundException.class) 
    public ResponseEntity<Response> reponseNotFoundException(Exception e) {
        return new ResponseEntity<>(new Response(HttpStatus.NOT_FOUND, e.getMessage()), 
                HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(UnprocessableException.class) 
    public ResponseEntity<Response> reponseUnprocessableException(Exception e) {
        return new ResponseEntity<>(new Response(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage()), 
                HttpStatus.UNPROCESSABLE_ENTITY);
    }
    
}
