package com.serloc.cashcard;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class CashCardRepositoryImpl implements CashCardRepository{
    private EntityManager entityManager;

    @Autowired
    public CashCardRepositoryImpl(EntityManager theEntityManager){
        entityManager = theEntityManager;
    }

    @Override
    public Optional<CashCard> findById(Long theId) {
        return Optional.ofNullable(entityManager.find(CashCard.class, theId));
    }

    @Override
    @Transactional
    public CashCard createCashCard(CashCard theCashCard) {
        entityManager.persist(theCashCard);
        return theCashCard;
    }

    @Override
    public List<CashCard> findAll(int pageNumber, int pageSize, String sortBy) {
        TypedQuery<CashCard> query = entityManager.createQuery("SELECT c from CashCard c ORDER BY c.amount", CashCard.class);
        return query.setFirstResult(pageNumber * pageSize)
                .setMaxResults(pageSize).getResultList();
    }


}
