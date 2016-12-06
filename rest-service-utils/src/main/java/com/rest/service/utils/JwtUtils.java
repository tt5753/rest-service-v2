package com.rest.service.utils;

import com.rest.service.security.AuthenticationForbiddenException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.compression.CompressionCodecs;

import java.util.Date;

/**
 * Created by liuzh on 16-3-13.
 */
public class JwtUtils {

    public static String tokenBuilder(byte[] key, String subject) {
        return tokenBuilder(key, subject, 3600);
    }

    public static String tokenBuilder(byte[] key, String subject, int expSeconds) {

        long millis = System.currentTimeMillis() + (expSeconds * 1000);

        long seconds = millis / 1000;
        long secondOnlyPrecisionMillis = seconds * 1000;
        Date expDate = new Date(secondOnlyPrecisionMillis);

        String compact = Jwts.builder().setSubject(subject).setExpiration(expDate).signWith(SignatureAlgorithm.HS256, key).compressWith(CompressionCodecs.GZIP).compact();

        return compact;
    }

    public static Claims tokenParser(byte[] key, String compact) throws AuthenticationForbiddenException {

        try{
            Claims claims = (Claims) Jwts.parser().setSigningKey(key).parseClaimsJws(compact).getBody();

            return claims;
        }catch (ExpiredJwtException e){
            throw new AuthenticationForbiddenException("认证超时.");
        }catch (MalformedJwtException e){
            throw new AuthenticationForbiddenException("权限认证出错.");
        }catch (Exception e){
            throw new AuthenticationForbiddenException(e.getMessage());
        }
    }

    public static String subjectParser(byte[] key, String compact) throws AuthenticationForbiddenException {
        Claims claims = tokenParser(key, compact);

        return claims.getSubject();
    }

}
