package com.fintrack.controller;

import com.fintrack.dto.ExchangeRateResponse;
import com.fintrack.service.ExchangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/exchange")
@RequiredArgsConstructor
public class ExchangeController {
    private final ExchangeService exchangeService;

    @GetMapping("/rates")
    public ResponseEntity<ExchangeRateResponse> rates(){
        return ResponseEntity.ok(exchangeService.getRates());
    }
}
