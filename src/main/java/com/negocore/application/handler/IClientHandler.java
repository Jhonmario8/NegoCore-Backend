package com.negocore.application.handler;

import com.negocore.application.dto.request.ClientRequestDTO;
import com.negocore.application.dto.response.ClientResponseDTO;

public interface IClientHandler {

    ClientResponseDTO registerClient(Long businessId, ClientRequestDTO clientRequestDTO);


}
