package com.negocore.infrastructure.output.storage;

import com.negocore.domain.spi.IProductImageStoragePort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class FileSystemProductImageStorageAdapter implements IProductImageStoragePort {

    private static final String STORAGE_ROOT = "./uploads/products";
    private static final String PUBLIC_BASE_PATH = "/files/products";

    @Override
    public String store(Long businessId, Long productId, String extension, byte[] content) {
        try {
            Path businessDir = Path.of(STORAGE_ROOT, String.valueOf(businessId));
            Files.createDirectories(businessDir);

            deleteExistingImages(businessDir, productId);

            Path targetFile = businessDir.resolve(productId + "." + extension);
            Files.write(targetFile, content);

            return PUBLIC_BASE_PATH + "/" + businessId + "/" + targetFile.getFileName();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void deleteExistingImages(Path businessDir, Long productId) throws IOException {
        String prefix = productId + ".";
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(businessDir, prefix + "*")) {
            for (Path existing : stream) {
                Files.deleteIfExists(existing);
            }
        }
    }
}
