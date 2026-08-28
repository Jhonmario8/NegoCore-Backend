package com.negocore.domain.api;

import com.negocore.domain.model.Business;

import java.util.List;

public interface IBusinessServicePort {

    Business createBusiness(Business business);

    List<Business> findAllBusinesses();
}
