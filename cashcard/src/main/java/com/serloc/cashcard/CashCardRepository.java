package com.serloc.cashcard;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CashCardRepository{
    Optional<CashCard> findById(Long theId);
    CashCard createCashCard(CashCard theCashCard);
    //List<CashCard> findAll();
    List<CashCard> findAll(int pageNumber, int pageSize, String sortBy);
}
