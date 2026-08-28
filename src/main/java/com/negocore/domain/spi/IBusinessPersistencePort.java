package com.negocore.domain.spi;


import com.negocore.domain.model.Business;

import java.util.List;
import java.util.Optional;

public interface IBusinessPersistencePort {

    Business saveBusiness(Business business);
    List<Business> findAllByOwnerId(Long ownerId);
    Optional<Business> findById(Long businessId);

}
