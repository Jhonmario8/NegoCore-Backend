package com.negocore.infrastructure.output.security.helper;

import com.negocore.domain.constants.DomainConstants;
import com.negocore.infrastructure.constants.InfrastructureConstants;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Map;

@AllArgsConstructor
public class JwtAuth implements Authentication {

    private final Map<String, Object> claims;
    private final String token;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getDetails() {
        return claims;
    }

    @Override
    public Object getPrincipal() {
        return claims.get(DomainConstants.KEY_SUBJECT);
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        throw new UnsupportedOperationException(InfrastructureConstants.MSG_AUTHENTICATION_STATE_CHANGE_NOT_SUPPORTED);
    }

    @Override
    public String getName() {
        return (String) claims.get(DomainConstants.KEY_SUBJECT);
    }
}
