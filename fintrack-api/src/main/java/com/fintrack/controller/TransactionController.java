package com.fintrack.controller;

import com.fintrack.dto.MonthSummaryResponse;
import com.fintrack.dto.TransactionRequest;
import com.fintrack.dto.TransactionResponse;
import com.fintrack.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;


    // @AuthenticationPrincipal injeta o usuário do token JWT automaticamente
    // ud.getUsername() retorna o email definido no UserDetailsServiceImpl
    @GetMapping
    public ResponseEntity<List<TransactionResponse>> list(
            @AuthenticationPrincipal UserDetails ud,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        return ResponseEntity.ok(transactionService.getTransactions(ud.getUsername(),
                month, year));

    }

    @GetMapping("/summary")
    public ResponseEntity<MonthSummaryResponse> summary (
            @AuthenticationPrincipal UserDetails ud,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        LocalDate now = LocalDate.now();
        int m = month != null ? month : now.getMonthValue();
        int y = year != null ? year : now.getYear();
        return ResponseEntity.ok(transactionService.getSummary(ud.getUsername(), m, y));
    }

    @GetMapping("/history")
    public ResponseEntity<List<MonthSummaryResponse>> history(
            @AuthenticationPrincipal UserDetails ud) {
        return ResponseEntity.ok(transactionService.getLast6MonthsSummary(ud.getUsername()));
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> create(
            @AuthenticationPrincipal UserDetails ud,
            @Valid @RequestBody TransactionRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.create(ud.getUsername(), req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal UserDetails ud,
            @PathVariable Long id) { // {id} da URL
        transactionService.delete(ud.getUsername(), id);
        return ResponseEntity.noContent().build();
    }




}
