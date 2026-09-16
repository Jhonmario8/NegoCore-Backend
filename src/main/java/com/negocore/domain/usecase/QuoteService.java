package com.negocore.domain.usecase;

import com.negocore.domain.api.IAuthenticationServicePort;
import com.negocore.domain.api.IQuoteServicePort;
import com.negocore.domain.constants.DomainConstants;
import com.negocore.domain.exception.BadRequestException;
import com.negocore.domain.exception.NotFoundException;
import com.negocore.domain.model.*;
import com.negocore.domain.spi.IBusinessPersistencePort;
import com.negocore.domain.spi.IProductPersistencePort;
import com.negocore.domain.spi.IUserPersistencePort;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class QuoteService implements IQuoteServicePort {

    private final IAuthenticationServicePort authenticationServicePort;
    private final IBusinessPersistencePort businessPersistencePort;
    private final IProductPersistencePort productPersistencePort;
    private final IUserPersistencePort userPersistencePort;

    @Override
    public QuoteResponse generateQuote(Long businessId, QuoteRequest quoteRequest) {
        Long userId = authenticationServicePort.getCurrentUserId();

        Business business = businessPersistencePort.findById(businessId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND));

        if (!business.getOwnerId().equals(userId)) {
            throw new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND);
        }

        if (quoteRequest.items() == null || quoteRequest.items().isEmpty()) {
            throw new BadRequestException(DomainConstants.QUOTE_ITEMS_REQUIRED);
        }

        User seller = userPersistencePort.findById(userId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.USER_NOT_FOUND));

        List<Long> productIds = quoteRequest.items().stream()
                .map(QuoteItemRequest::productId)
                .toList();

        Set<Long> uniqueProductIds = new HashSet<>(productIds);

        List<Product> products = productPersistencePort.findAllByIdsAndBusinessId(productIds, businessId);

        if (products.size() != uniqueProductIds.size()) {
            throw new NotFoundException(DomainConstants.PRODUCT_NOT_FOUND);
        }

        Map<Long, Product> productsById = products.stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        BigDecimal total = BigDecimal.ZERO;
        List<QuoteItemResponse> items = quoteRequest.items().stream()
                .map(item -> {
                    Product product = productsById.get(item.productId());
                    BigDecimal unitPrice = item.unitPrice() != null ? item.unitPrice() : product.getSalePrice();
                    BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(item.quantity()));

                    return new QuoteItemResponse(
                            product.getId(),
                            product.getName(),
                            product.getSku(),
                            product.getImageUrl(),
                            item.quantity(),
                            unitPrice,
                            subtotal
                    );
                })
                .toList();

        for (QuoteItemResponse item : items) {
            total = total.add(item.subtotal());
        }

        int validityDays = quoteRequest.validityDays() != null
                ? quoteRequest.validityDays()
                : DomainConstants.DEFAULT_QUOTE_VALIDITY_DAYS;

        LocalDate quoteDate = LocalDate.now();

        return new QuoteResponse(
                business.getName(),
                business.getAddress(),
                business.getPhone(),
                business.getEmail(),
                seller.getName(),
                quoteRequest.clientName(),
                quoteDate,
                quoteDate.plusDays(validityDays),
                items,
                total
        );
    }
}
