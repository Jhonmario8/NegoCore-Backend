package com.negocore.application.handler;

import com.negocore.application.dto.request.QuoteRequestDTO;
import com.negocore.application.dto.response.QuoteResponseDTO;
import com.negocore.application.mapper.IQuoteMapper;
import com.negocore.domain.api.IQuoteServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuoteHandler implements IQuoteHandler {

    private final IQuoteServicePort quoteServicePort;
    private final IQuoteMapper quoteMapper;

    @Override
    public QuoteResponseDTO generateQuote(Long businessId, QuoteRequestDTO quoteRequestDTO) {
        return quoteMapper.toResponseDto(
                quoteServicePort.generateQuote(businessId, quoteMapper.toDomain(quoteRequestDTO))
        );
    }
}
