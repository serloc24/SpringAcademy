package com.serloc.cashcard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/cashcards")
public class CashCardController {
    private final CashCardRepository cashCardRepository;

    @Autowired
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

    @PostMapping
    private ResponseEntity<Void> addCashCard(@RequestBody CashCard newCashCardRequest, UriComponentsBuilder ucb){
        CashCard theCashCard = cashCardRepository.createCashCard(newCashCardRequest);
        URI locationOfNewCashCard = ucb.path("cashcards/{id}").buildAndExpand(theCashCard.getId()).toUri();
        return ResponseEntity.created(locationOfNewCashCard).build();
    }
}
