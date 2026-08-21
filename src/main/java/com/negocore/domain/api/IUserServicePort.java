package com.negocore.domain.api;

import com.negocore.domain.model.User;

public interface IUserServicePort {
    User createUser(User user);
}
