package br.com.techmarket_order_service.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper mapper;

    public CustomAuthenticationEntryPoint(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException {

        String mensagem;

        if (authException instanceof DisabledException) {
            mensagem = "Usuário inativo";
        } else if (authException instanceof BadCredentialsException) {
            mensagem = "Email ou senha inválidos";
        } else {
            mensagem = "Usuário não autenticado ou token inválido";
        }

        ErrorResponse error = new ErrorResponse(
                401,
                mensagem,
                null
        );

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        mapper.writeValue(response.getOutputStream(), error);
    }
}