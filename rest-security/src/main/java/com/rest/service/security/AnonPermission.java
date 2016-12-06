package com.rest.service.security;

/**
 * Created by liuzh on 16-3-13.
 */
public interface AnonPermission {

    boolean couldAnonAccess(String uri) throws AuthenticationException;
}
