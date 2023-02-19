package pl.gozderapatryk.gatewayservice.exception;

public class AppAuthenticationException extends RuntimeException {
    public AppAuthenticationException(String message) {
        super(message);
    }
}
