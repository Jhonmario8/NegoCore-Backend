package com.negocore.infrastructure.output.jpa.adapter;

import com.negocore.domain.model.CashMovement;
import com.negocore.domain.model.CashMovementType;
import com.negocore.domain.spi.ICashMovementPersistencePort;
import com.negocore.infrastructure.output.jpa.mapper.ICashMovementEntityMapper;
import com.negocore.infrastructure.output.jpa.repository.ICashMovementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CashMovementJpaAdapter implements ICashMovementPersistencePort {

    private final ICashMovementRepository repository;
    private final ICashMovementEntityMapper mapper;

    @Override
    public CashMovement save(CashMovement cashMovement) {
        return mapper.toDomain(repository.save(mapper.toEntity(cashMovement)));
    }

    @Override
    public List<CashMovement> findByCashRegisterIdAndTypeIn(Long cashRegisterId, List<CashMovementType> types) {
        return repository.findByCashRegisterIdAndTypeIn(cashRegisterId, types)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}
