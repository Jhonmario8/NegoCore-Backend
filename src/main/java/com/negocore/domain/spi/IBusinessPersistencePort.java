package com.negocore.domain.spi;


import com.negocore.domain.model.Business;

public interface IBusinessPersistencePort {

    Business saveBusiness(Business business);

}
