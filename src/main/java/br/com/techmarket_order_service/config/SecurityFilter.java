package br.com.techmarket_order_service.config;

import br.com.techmarket_order_service.service.JwtService;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public SecurityFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String tokenJWT = recuperarToken(request);

        if (tokenJWT != null) {
            // Valida o token e extrai os claims
            DecodedJWT decodedJWT = jwtService.validarToken(tokenJWT);

            if (decodedJWT != null) {
                // Extrai informações do token SEM consultar banco de dados!
                String email = decodedJWT.getSubject();
                String perfil = decodedJWT.getClaim("perfil").asString();
                Long usuarioId = decodedJWT.getClaim("id").asLong();

                // Cria as authorities baseado no perfil do token
                List<SimpleGrantedAuthority> authorities =
                        List.of(new SimpleGrantedAuthority("ROLE_" + perfil));

                // Cria autenticação sem buscar no banco!
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(email, null, authorities);

                // Adiciona informações no request para uso nos controllers
                request.setAttribute("usuarioId", usuarioId);
                request.setAttribute("usuarioEmail", email);
                request.setAttribute("usuarioPerfil", perfil);

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String recuperarToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7); // Remove "Bearer "
        }
        return null;
    }
}
