package com.negocore.domain.api;

import com.negocore.domain.model.QuoteRequest;
import com.negocore.domain.model.QuoteResponse;

public interface IQuoteServicePort {

    QuoteResponse generateQuote(Long businessId, QuoteRequest quoteRequest);

}
