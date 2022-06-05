package a.gleb.oauthservice.exception;

public class InvalidRegisterClientRequest extends RuntimeException {
    public InvalidRegisterClientRequest(String message){
        super(message);
    }
}
