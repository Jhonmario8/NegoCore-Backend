package com.negocore.application.handler;

import com.negocore.application.dto.request.ProviderCreateRequestDTO;
import com.negocore.application.dto.response.ProviderResponseDTO;
import com.negocore.application.mapper.IProviderMapper;
import com.negocore.domain.api.IProviderServicePort;
import com.negocore.domain.model.Provider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProviderHandler implements IProviderHandler {

    private final IProviderMapper providerMapper;
    private final IProviderServicePort providerService;

    @Override
    public ProviderResponseDTO createProvider(
            Long businessId,
            ProviderCreateRequestDTO providerCreateRequestDTO
    ) {

        Provider provider = providerMapper.toDomain(
                providerCreateRequestDTO
        );

        Provider savedProvider = providerService.createProvider(
                businessId,
                provider
        );

        return providerMapper.toResponseDTO(savedProvider);
    }

    @Override
    public List<ProviderResponseDTO> findAllByBusinessId(Long businessId) {

        return providerService.findAllByBusinessId(businessId)
                .stream()
                .map(providerMapper::toResponseDTO)
                .toList();
    }

}
