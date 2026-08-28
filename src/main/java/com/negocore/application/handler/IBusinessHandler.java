package com.negocore.application.handler;

import com.negocore.application.dto.request.BusinessCreateDTO;
import com.negocore.application.dto.response.BusinessListResponseDTO;
import com.negocore.application.dto.response.BusinessResponseDTO;

import java.util.List;

public interface IBusinessHandler {

    BusinessResponseDTO createBusiness(BusinessCreateDTO businessDTO);

    List<BusinessListResponseDTO> findAllBusiness();

}
