package com.negocore.infrastructure.output.jpa.adapter;

import com.negocore.domain.model.Category;
import com.negocore.domain.spi.ICategoryPersistencePort;
import com.negocore.infrastructure.output.jpa.mapper.ICategoryEntityMapper;
import com.negocore.infrastructure.output.jpa.repository.ICategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryJpaAdapter implements ICategoryPersistencePort {

    private final ICategoryEntityMapper mapper;
    private final ICategoryRepository repository;

    @Override
    public Category saveCategory(Category category) {
        return mapper.toDomain(repository.save(mapper.toEntity(category)));
    }

    @Override
    public Boolean existsByNameAndBusinessId(String name, Long businessId) {
        return repository.existsByBusinessIdAndName(businessId, name);
    }

    @Override
    public Boolean existsByIdAndBusinessId(Long categoryId, Long businessId) {
        return repository.existsByIdAndBusinessId(categoryId, businessId);
    }

    @Override
    public List<Category> findAllByBusinessId(Long businessId) {
        return repository.findAllByBusinessId(businessId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}
