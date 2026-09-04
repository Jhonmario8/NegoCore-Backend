package com.negocore.application.handler;

import com.negocore.application.dto.request.ClientRequestDTO;
import com.negocore.application.dto.response.ClientResponseDTO;
import com.negocore.application.mapper.IClientMapper;
import com.negocore.domain.api.IClientServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientHandler implements IClientHandler {

    private final IClientServicePort clientServicePort;
    private final IClientMapper mapper;

    @Override
    public ClientResponseDTO registerClient(Long businessId, ClientRequestDTO clientRequestDTO) {
        return mapper.toResponse(clientServicePort.registerClient(businessId, mapper.toDomain(clientRequestDTO)));
    }
}
