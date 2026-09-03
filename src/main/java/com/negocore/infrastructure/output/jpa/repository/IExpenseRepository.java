package com.negocore.infrastructure.output.jpa.repository;


import com.negocore.infrastructure.output.jpa.entity.ExpenseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IExpenseRepository extends JpaRepository<ExpenseEntity, Long> {
}
