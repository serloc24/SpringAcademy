package com.serloc.cashcard;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.Objects;

@Entity
class CashCard{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double amount;

    private String owner;

    public CashCard(){}

    public CashCard(Long id, Double amount, String owner) {
        this.id = id;
        this.amount = amount;
        this.owner = owner;
    }

    public Long getId() {
        return id;
    }

    public Double getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CashCard cashCard)) return false;
        return Objects.equals(id, cashCard.id) &&
                Objects.equals(amount, cashCard.amount) &&
                Objects.equals(owner, cashCard.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, amount,owner);
    }

    @Override
    public String toString() {
        return "CashCard{id=" + id + ", amount=" + amount + ", owner= " + owner + '}';
    }
}
