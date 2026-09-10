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

    @Override
    public List<Product> findAllByIds(List<Long> productIds) {
        return repository.findAllByIdIn(productIds).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void saveAll(List<Product> products) {
        repository.saveAll(products.stream().map(mapper::toEntity).toList());
    }

    @Override
    public Optional<Product> findByIdAndBusinessId(Long productId, Long businessId) {
        return repository.findByIdAndBusinessId(productId, businessId)
                .map(mapper::toDomain);
    }

    @Override
    public List<Product> findAllByBusinessId(
            Long businessId,
            Long categoryId,
            Boolean lowStock
    ) {
        return repository.findAllByBusinessId(businessId, categoryId, lowStock)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}
