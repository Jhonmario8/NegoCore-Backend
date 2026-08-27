package com.negocore.infrastructure.output.security.adapter;

import com.negocore.domain.api.IAuthenticationServicePort;
import com.negocore.domain.constants.DomainConstants;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthenticationServiceAdapter implements IAuthenticationServicePort {

    @Override
    public Long getCurrentUserId() {
        Map<String, Object> claims = getClaimsFromAuthentication();
        if (claims == null){
            return null;
        }
        return Long.valueOf(claims.get(DomainConstants.KEY_USER_ID).toString());
    }


    private static Map<String, Object> getClaimsFromAuthentication() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        return (Map<String, Object>) authentication.getDetails();
    }


}
