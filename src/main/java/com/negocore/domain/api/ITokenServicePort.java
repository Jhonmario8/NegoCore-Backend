package com.negocore.domain.api;

import com.negocore.domain.model.User;

import java.util.Map;

public interface ITokenServicePort {

    String generateToken(User user);

    Map<String , Object> validateToken(String token);
}
