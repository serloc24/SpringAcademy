package com.serloc.cashcard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cashcards")
public class CashCardController {
    private final CashCardRepository cashCardRepository;

    @Autowired
    public CashCardController(CashCardRepository cashCardRepository) {
        this.cashCardRepository = cashCardRepository;
    }

    @GetMapping
    private ResponseEntity<List<CashCard>> findAll(@RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size,
                                                   @RequestParam(defaultValue = "amount") String sortBy){
        List<CashCard> cashCardList =cashCardRepository.findAll(page,size,sortBy);
        return ResponseEntity.ok(cashCardList);
    }

    @GetMapping("/{theId}")
    private ResponseEntity<CashCard> findById(@PathVariable Long theId) {
        Optional<CashCard> cashCard = cashCardRepository.findById(theId);
        return cashCard.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    private ResponseEntity<Void> addCashCard(@RequestBody CashCard newCashCardRequest, UriComponentsBuilder ucb){
        CashCard theCashCard = cashCardRepository.createCashCard(newCashCardRequest);
        URI locationOfNewCashCard = ucb.path("cashcards/{id}").buildAndExpand(theCashCard.getId()).toUri();
        return ResponseEntity.created(locationOfNewCashCard).build();
    }
}
