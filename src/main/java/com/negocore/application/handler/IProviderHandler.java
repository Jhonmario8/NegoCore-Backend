package com.negocore.application.handler;

import com.negocore.application.dto.request.ProviderCreateRequestDTO;
import com.negocore.application.dto.response.ProviderResponseDTO;

import java.util.List;

public interface IProviderHandler {

    ProviderResponseDTO createProvider(Long businessId, ProviderCreateRequestDTO providerCreateRequestDTO);
    List<ProviderResponseDTO> findAllByBusinessId(Long businessId);
}
