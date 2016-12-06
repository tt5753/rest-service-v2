package com.rest.service.security.simple;

import com.rest.service.security.AuthKeyGenerator;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

/**
 * Created by liuzh on 16-3-25.
 */
public class DefaultAuthKeyGenerator implements AuthKeyGenerator {
    @Override
    public byte[] generateAuthKey() {
        KeyPairGenerator keyGenerator = null;
        try {
            keyGenerator = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        keyGenerator.initialize(1024);
        KeyPair kp = keyGenerator.genKeyPair();
        PublicKey publicKey = kp.getPublic();

        return "liuzh".getBytes();
    }
}
