package com.negocore.domain.api;

import com.negocore.domain.model.LoginResponse;
import com.negocore.domain.model.User;

public interface IUserServicePort {
    User createUser(User user);
    LoginResponse login(String email, String password);
}
