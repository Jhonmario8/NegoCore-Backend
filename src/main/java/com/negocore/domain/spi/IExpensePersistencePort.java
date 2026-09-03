package com.negocore.domain.spi;


import com.negocore.domain.model.Expense;

public interface IExpensePersistencePort {

    Expense save(Expense expense);

}
