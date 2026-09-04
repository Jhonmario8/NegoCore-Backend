package com.negocore.infrastructure.output.jpa.adapter;

import com.negocore.domain.model.CashRegister;
import com.negocore.domain.model.CashRegisterStatus;
import com.negocore.domain.spi.ICashRegisterPersistencePort;
import com.negocore.infrastructure.output.jpa.mapper.ICashRegisterEntityMapper;
import com.negocore.infrastructure.output.jpa.repository.ICashRegisterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CashRegisterJpaAdapter implements ICashRegisterPersistencePort {

    private final ICashRegisterRepository repository;
    private final ICashRegisterEntityMapper mapper;


    @Override
    public CashRegister save(CashRegister cashRegister) {
        return mapper.toDomain(repository.save(mapper.toEntity(cashRegister)));
    }

    @Override
    public Boolean existsOpenCashRegisterByBusinessIdAndStatus(Long businessId, CashRegisterStatus status) {
        return repository.existsByBusinessIdAndStatus(businessId, status );
    }

    @Override
    public Optional<CashRegister> findOpenCashRegisterByBusinessIdAndStatus(Long businessId, CashRegisterStatus status) {
        return repository.findByBusinessIdAndStatus(businessId, status).map(mapper::toDomain);
    }

    @Override
    public Optional<CashRegister> findById(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }
}
