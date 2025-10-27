package com.serloc.cashcard;

import java.util.List;
import java.util.Optional;

public interface CashCardRepository{
    Optional<CashCard> findById(Long theId);
    CashCard createCashCard(CashCard theCashCard);
    //List<CashCard> findAll();
    List<CashCard> findAll(int pageNumber, int pageSize, String sortBy);
    Optional<CashCard> findByIdAndOwner(Long theId, String theOwner);
    List<CashCard> findByOwner(int pageNumber, int pageSize, String sortBy, String theOwner);
    void update(CashCard theCashCard);
}
