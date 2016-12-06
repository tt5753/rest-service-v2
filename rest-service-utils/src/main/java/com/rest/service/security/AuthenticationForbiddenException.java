package com.rest.service.security;

/**
 * Created by liuzh on 16-7-19.
 */
public class AuthenticationForbiddenException extends AuthenticationException{
    public AuthenticationForbiddenException() {
    }

    public AuthenticationForbiddenException(String message) {
        super(message);
    }

    public AuthenticationForbiddenException(Throwable cause) {
        super(cause);
    }

    public AuthenticationForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }
}
