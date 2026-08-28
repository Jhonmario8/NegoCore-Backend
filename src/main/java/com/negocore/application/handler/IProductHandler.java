package com.negocore.application.handler;

import com.negocore.application.dto.request.ProductRequestDTO;
import com.negocore.application.dto.response.ProductResponseDTO;

public interface IProductHandler {

    ProductResponseDTO createProduct(Long businessId ,ProductRequestDTO productRequestDTO);

}
