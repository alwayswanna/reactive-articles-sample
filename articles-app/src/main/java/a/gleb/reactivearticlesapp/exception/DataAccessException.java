package a.gleb.reactivearticlesapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class DataAccessException extends RuntimeException{
    private String message;
    public DataAccessException(String message) {
        super(message);
    }
}
