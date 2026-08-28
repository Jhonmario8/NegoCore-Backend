package com.negocore.domain.spi;


import com.negocore.domain.model.Business;

import java.util.List;

public interface IBusinessPersistencePort {

    Business saveBusiness(Business business);
    List<Business> findAllByOwnerId(Long ownerId);

}
