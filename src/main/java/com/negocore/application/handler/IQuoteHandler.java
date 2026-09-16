package com.negocore.application.handler;

import com.negocore.application.dto.request.QuoteRequestDTO;
import com.negocore.application.dto.response.QuoteResponseDTO;

public interface IQuoteHandler {

    QuoteResponseDTO generateQuote(Long businessId, QuoteRequestDTO quoteRequestDTO);

}
