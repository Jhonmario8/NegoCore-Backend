package com.negocore.domain.spi;

public interface IImageStoragePort {

    String upload(byte[] content, String publicId);
}
