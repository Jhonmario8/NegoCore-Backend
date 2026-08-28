package com.negocore.infrastructure.config;

import com.negocore.domain.api.*;
import com.negocore.domain.spi.IBusinessPersistencePort;
import com.negocore.domain.spi.ICategoryPersistencePort;
import com.negocore.domain.spi.IProductPersistencePort;
import com.negocore.domain.spi.IUserPersistencePort;
import com.negocore.domain.usecase.BusinessService;
import com.negocore.domain.usecase.CategoryService;
import com.negocore.domain.usecase.ProductService;
import com.negocore.domain.usecase.UserService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@AllArgsConstructor
public class BeanConfiguration {

    private final IUserPersistencePort userPersistencePort;
    private final ITokenServicePort tokenServicePort;
    private final IAuthenticationServicePort authenticationServicePort;
    private final IBusinessPersistencePort businessPersistencePort;
    private final ICategoryPersistencePort categoryPersistencePort;
    private final IProductPersistencePort productPersistencePort;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public IUserServicePort userServicePort(IPasswordServicePort passwordServicePort){
        return new UserService(userPersistencePort, passwordServicePort, tokenServicePort );
    }

    @Bean
    public IBusinessServicePort businessServicePort(){
        return new BusinessService(businessPersistencePort, authenticationServicePort);
    }

    @Bean
    public ICategoryServicePort categoryServicePort(){
        return new CategoryService(categoryPersistencePort, businessPersistencePort, authenticationServicePort);
    }
    @Bean
    public IProductServicePort productServicePort(){
        return new ProductService(productPersistencePort, businessPersistencePort, authenticationServicePort, categoryPersistencePort);
    }
}
