package com.negocore.application.handler;

import com.negocore.application.dto.request.ClientRequestDTO;
import com.negocore.application.dto.response.ClientResponseDTO;

import java.util.List;

public interface IClientHandler {

    ClientResponseDTO registerClient(Long businessId, ClientRequestDTO clientRequestDTO);

    List<ClientResponseDTO> getClientsByBusinessId(Long businessId);

}
