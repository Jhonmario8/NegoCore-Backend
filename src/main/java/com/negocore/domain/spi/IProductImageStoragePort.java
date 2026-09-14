package com.negocore.domain.spi;

public interface IProductImageStoragePort {

    String store(Long businessId, Long productId, String extension, byte[] content);
}
