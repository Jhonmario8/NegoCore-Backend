package com.negocore.infrastructure.output.jpa.adapter;

import com.negocore.domain.model.OrderItem;
import com.negocore.domain.spi.IOrderItemsPersistencePort;
import com.negocore.infrastructure.output.jpa.mapper.IOrderItemEntityMapper;
import com.negocore.infrastructure.output.jpa.repository.IOrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderItemJpaAdapter implements IOrderItemsPersistencePort {

    private final IOrderItemRepository repository;
    private final IOrderItemEntityMapper mapper;

    @Override
    public OrderItem save(OrderItem item) {
        return mapper.toDomain(repository.save(mapper.toEntity(item)));
    }

    @Override
    public List<OrderItem> findAllByOrderId(Long orderId) {
        return repository.findAllByOrderId(orderId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<OrderItem> findByIdAndOrderId(Long itemId, Long orderId) {
        return repository.findByIdAndOrderId(itemId, orderId)
                .map(mapper::toDomain);
    }

    @Override
    public void deleteByIdAndOrderId(Long itemId, Long orderId) {
        repository.deleteByIdAndOrderId(itemId, orderId);
    }
}
