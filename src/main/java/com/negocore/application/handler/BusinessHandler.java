package com.negocore.application.handler;

import com.negocore.application.dto.request.BusinessCreateDTO;
import com.negocore.application.dto.response.BusinessResponseDTO;
import com.negocore.application.mapper.IBusinessMapper;
import com.negocore.domain.api.IBusinessServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BusinessHandler implements IBusinessHandler{

    private final IBusinessServicePort businessServicePort;
    private final IBusinessMapper mapper;

    @Override
    public BusinessResponseDTO createBusiness(BusinessCreateDTO businessDTO) {
        return mapper.toResponse(businessServicePort.createBusiness(mapper.toDomain(businessDTO)));
    }
}
