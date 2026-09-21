package com.negocore.infrastructure.output.jpa.adapter;

import com.negocore.domain.model.Order;
import com.negocore.domain.model.OrderStatus;
import com.negocore.domain.spi.IOrderPersistencePort;
import com.negocore.infrastructure.output.jpa.mapper.IOrderEntityMapper;
import com.negocore.infrastructure.output.jpa.repository.IOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderJpaAdapter implements IOrderPersistencePort {

    private final IOrderRepository repository;
    private final IOrderEntityMapper mapper;

    @Override
    public Order save(Order order) {
        return mapper.toDomain(repository.save(mapper.toEntity(order)));
    }

    @Override
    public Optional<Order> findByIdAndBusinessId(Long orderId, Long businessId) {
        return repository.findByIdAndBusinessId(orderId, businessId)
                .map(mapper::toDomain);
    }

    @Override
    public List<Order> findAllByBusinessIdAndStatus(Long businessId, OrderStatus status) {
        return repository.findAllByBusinessIdAndOptionalStatus(businessId, status)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Integer findMaxOrderNumberByBusinessId(Long businessId) {
        return repository.findMaxOrderNumberByBusinessId(businessId);
    }
}
