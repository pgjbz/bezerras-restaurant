package com.pgbezerra.bezerras.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;

@Component
public class JWTUtil {

    @Value("${jwt.expiration}")
    private Long expiration;
    @Value("${jwt.secret}")
    private String secret;

    public String generateToken(String username){
        Algorithm algorithm = Algorithm.HMAC512(secret);
        return JWT.create()
                .withSubject(username)
                .withExpiresAt(new Date(System.currentTimeMillis() + expiration))
                .withIssuer("com.pgbezerra")
                .sign(algorithm);
    }

    public boolean isValidToken(String token){
        DecodedJWT decodedJWT = getDecodedJWT(token);

        if(Objects.nonNull(decodedJWT)){
            String username = decodedJWT.getSubject();
            Date expirationDate = decodedJWT.getExpiresAt();
            Date now = new Date(System.currentTimeMillis());
            return Objects.nonNull(username) && Objects.nonNull(expirationDate) && now.before(expirationDate);
        }
        return false;
    }

    public String getUsername(String token){
        DecodedJWT decodedJWT = getDecodedJWT(token);
        if(Objects.nonNull(decodedJWT))
            return decodedJWT.getSubject();
        return null;
    }

    private DecodedJWT getDecodedJWT(String token){
        try {
            Algorithm algorithm = Algorithm.HMAC512(secret);
            JWTVerifier verifier = JWT
                    .require(algorithm)
                    .withIssuer("com.pgbezerra")
                    .build();
            return verifier.verify(token);
        } catch (Exception e){
            return null;
        }
    }

}
