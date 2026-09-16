package com.negocore.application.mapper;

import com.negocore.application.dto.request.QuoteRequestDTO;
import com.negocore.application.dto.response.QuoteItemResponseDTO;
import com.negocore.application.dto.response.QuoteResponseDTO;
import com.negocore.domain.model.QuoteItemResponse;
import com.negocore.domain.model.QuoteRequest;
import com.negocore.domain.model.QuoteResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IQuoteMapper {

    QuoteRequest toDomain(QuoteRequestDTO quoteRequestDTO);
    QuoteResponseDTO toResponseDto(QuoteResponse quoteResponse);
    QuoteItemResponseDTO toItemResponseDto(QuoteItemResponse quoteItemResponse);
}
