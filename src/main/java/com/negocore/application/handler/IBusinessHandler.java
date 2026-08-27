package com.negocore.application.handler;

import com.negocore.application.dto.request.BusinessCreateDTO;
import com.negocore.application.dto.response.BusinessResponseDTO;

public interface IBusinessHandler {

    BusinessResponseDTO createBusiness(BusinessCreateDTO businessDTO);


}
