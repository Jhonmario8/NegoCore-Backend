package com.negocore.infrastructure.output.cloudinary.adapter;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.negocore.domain.exception.BadRequestException;
import com.negocore.domain.spi.IImageStoragePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CloudinaryImageStorageAdapter implements IImageStoragePort {

    private final Cloudinary cloudinary;

    @Override
    public String upload(byte[] content, String publicId) {
        try {
            Map<?, ?> result = cloudinary.uploader().upload(content, ObjectUtils.asMap(
                    "public_id", publicId,
                    "folder", "negocore/products",
                    "overwrite", true,
                    "resource_type", "image"
            ));
            return (String) result.get("secure_url");
        } catch (IOException e) {
            throw new BadRequestException("No se pudo subir la imagen");
        }
    }
}
