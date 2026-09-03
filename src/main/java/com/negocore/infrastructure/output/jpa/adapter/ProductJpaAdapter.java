package com.negocore.infrastructure.output.jpa.adapter;

import com.negocore.domain.model.Product;
import com.negocore.domain.spi.IProductPersistencePort;
import com.negocore.infrastructure.output.jpa.mapper.IProductEntityMapper;
import com.negocore.infrastructure.output.jpa.repository.IProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductJpaAdapter implements IProductPersistencePort {

    private final IProductRepository repository;
    private final IProductEntityMapper mapper;


    @Override
    public Product saveProduct(Product product) {
        return mapper.toDomain(repository.save(mapper.toEntity(product)));
    }

    @Override
    public Boolean existByBusinessIdAndSku(Long businessId, String sku) {
        return repository.existsByBusinessIdAndSku(businessId, sku);
    }

    @Override
    public Optional<Product> findById(Long productId) {
        return repository.findById(productId).map(mapper::toDomain);
    }

    @Override
    public List<Product> findAllByIdsAndBusinessId(List<Long> productIds, Long businessId) {
        return repository.findAllByIdInAndBusinessId(productIds, businessId).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
