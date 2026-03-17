package br.com.techmarket_order_service.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    @Value("${api.security.token.secret}")
    private String secret;

    public DecodedJWT validarToken(String tokenJWT) {
        try {
            var algoritmo = Algorithm.HMAC256(secret);
            return JWT.require(algoritmo)
                    .withIssuer("API TechMarket")
                    .build()
                    .verify(tokenJWT);

        } catch (JWTVerificationException exception) {
            return null;
        }
    }

    public String getSubjectToken(String tokenJWT) {
        DecodedJWT decoded = validarToken(tokenJWT);
        return decoded != null ? decoded.getSubject() : null;
    }
}
