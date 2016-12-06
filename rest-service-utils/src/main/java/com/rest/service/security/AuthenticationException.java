package com.rest.service.security;

/**
 * Created by liuzh on 16-3-11.
 */
public class AuthenticationException extends RuntimeException{

    public AuthenticationException() {
    }

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(Throwable cause) {
        super(cause);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
