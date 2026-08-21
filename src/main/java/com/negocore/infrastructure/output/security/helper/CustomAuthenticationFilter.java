package com.negocore.infrastructure.output.security.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.negocore.domain.constants.DomainConstants;
import com.negocore.infrastructure.constants.InfrastructureConstants;
import com.negocore.infrastructure.output.security.adapter.TokenServiceAdapter;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@Component
@AllArgsConstructor
public class CustomAuthenticationFilter extends OncePerRequestFilter {

    private final TokenServiceAdapter tokenServiceAdapter;
    private final ObjectMapper objectMapper;


    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        String token = extractToken(request);
        if (token == null) {
            filterChain.doFilter(request,response);
            return;
        }
        try{
            Claims claims = tokenServiceAdapter.validateToken(token);

            if (claims.getExpiration().before(new Date())){
                setResponseProperties(response, InfrastructureConstants.MSG_TOKEN_EXPIRED, request);
                return;
            }

            SecurityContextHolder.getContext().setAuthentication(new JwtAuth(claims, token));
        }catch (Exception e){
            setResponseProperties(response, InfrastructureConstants.MSG_TOKEN_INVALID, request);
            return;
        }
        filterChain.doFilter(request,response);
    }
    private void setResponseProperties(HttpServletResponse response, String message, HttpServletRequest request) throws IOException {
        Map<String, Object> payload = new HashMap<>();
        payload.put(InfrastructureConstants.KEY_STATUS, HttpServletResponse.SC_UNAUTHORIZED);
        payload.put(InfrastructureConstants.KEY_MESSAGE, message);
        payload.put(InfrastructureConstants.KEY_PATH, request != null ? request.getRequestURI() : InfrastructureConstants.EMPTY_STRING);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(InfrastructureConstants.UTF_8);

        String json = objectMapper.writeValueAsString(payload);
        response.getWriter().write(json);
        response.getWriter().flush();
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith(DomainConstants.TOKEN_PREFIX)) {
            return header.substring(7);
        }
        return null;
    }
}