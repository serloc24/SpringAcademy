package com.serloc.cashcard;

public interface CashCardRepository {
    CashCard findById(Long theId);
    CashCard createCashCard(CashCard theCashCard);
}
