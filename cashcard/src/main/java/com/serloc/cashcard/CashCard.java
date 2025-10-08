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

    public CashCard(){}

    public CashCard(Long id, Double amount) {
        this.id = id;
        this.amount = amount;
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
        if (!(o instanceof CashCard)) return false;
        CashCard cashCard = (CashCard) o;
        return Objects.equals(id, cashCard.id) &&
                Objects.equals(amount, cashCard.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, amount);
    }

    @Override
    public String toString() {
        return "CashCard{id=" + id + ", amount=" + amount + '}';
    }
}
