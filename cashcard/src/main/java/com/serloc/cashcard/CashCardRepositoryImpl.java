package com.serloc.cashcard;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.parser.Entity;

@Repository
public class CashCardRepositoryImpl implements CashCardRepository {
    private EntityManager entityManager;

    @Autowired
    public CashCardRepositoryImpl(EntityManager theEntityManager){
        entityManager = theEntityManager;
    }

    @Override
    public CashCard findById(Long theId) {
        return entityManager.find(CashCard.class, theId);
    }

    @Override
    @Transactional
    public CashCard createCashCard(CashCard theCashCard) {
        entityManager.persist(theCashCard);
        return theCashCard;
    }
}
