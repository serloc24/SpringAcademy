package com.serloc.cashcard;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
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
        TypedQuery<CashCard> query = entityManager.createQuery("SELECT c from CashCard c ORDER BY :data", CashCard.class);
        query.setParameter("data", sortBy);
        return query.setFirstResult(pageNumber * pageSize)
                .setMaxResults(pageSize).getResultList();
    }

    @Override
    public Optional<CashCard> findByIdAndOwner(Long theId, String theOwner) {
        TypedQuery<CashCard> query = entityManager.createQuery("SELECT c FROM CashCard c WHERE id = :theId AND owner = :theOwner", CashCard.class);
        query.setParameter("theId",theId);
        query.setParameter("theOwner",theOwner);
        try {
            return Optional.of(query.getSingleResult());
        }catch (NoResultException e){
            return Optional.empty();
        }

    }

    @Override
    public List<CashCard> findByOwner(int pageNumber, int pageSize, String sortBy,String theOwner) {
        TypedQuery<CashCard> query = entityManager.createQuery("SELECT c FROM CashCard c WHERE c.owner = :theOwner ORDER BY c." + sortBy , CashCard.class);
        query.setParameter("theOwner",theOwner);

        return query.setFirstResult(pageNumber * pageSize)
                .setMaxResults(pageSize).getResultList();
    }

    @Override
    @Transactional
    public void update(CashCard theCashCard) {
        entityManager.merge(theCashCard);
    }


}
