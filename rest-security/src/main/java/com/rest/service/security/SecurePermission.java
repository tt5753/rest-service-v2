package com.rest.service.security;

/**
 * Created by liuzh on 16-3-10.
 */
public interface SecurePermission {

    boolean validation(String uri, String authorization) throws AuthenticationException;
}
