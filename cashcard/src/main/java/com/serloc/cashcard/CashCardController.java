package com.serloc.cashcard;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/cashcards")
public class CashCardController {
    private final CashCardRepository cashCardRepository;

    public CashCardController(CashCardRepository cashCardRepository) {
        this.cashCardRepository = cashCardRepository;
    }

    @GetMapping("/{theId}")
    private ResponseEntity<CashCard> findById(@PathVariable Long theId) {
        CashCard cashCard = cashCardRepository.findById(theId);

        if (cashCard != null){
            return ResponseEntity.ok(cashCard);
        }
        return ResponseEntity.notFound().build();
    }
}
