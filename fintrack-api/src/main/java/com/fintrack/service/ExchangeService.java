package com.fintrack.service;

import com.fintrack.dto.ExchangeRateResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class ExchangeService {
    //URL da API das cotações
    private static final String URL = "https://economia.awesomeapi.com.br/json/last/USD-BRL,EUR-BRL,BTC-BRL";

    //RestTemplate: cliente HTTP síncrono do Spring
    private final RestTemplate restTemplate = new RestTemplate();

    @SuppressWarnings("unchecked")
    public ExchangeRateResponse getRates() {
        // Faz GET e deserializa o JSON em Map<String, Object>
        Map<String, Object> body = restTemplate.getForObject(URL, Map.class);
        if (body == null) {
            throw new RuntimeException("Failed to fetch exchange rates");
        }

        return new ExchangeRateResponse(
                rate(body, "USDBRL"),
                rate(body, "EURBRL"),
                rate(body, "BTCBRL")
        );
    }

    @SuppressWarnings("unchecked")
    private double rate(Map<String, Object> body, String key) {
        return Double.parseDouble((String) ((Map<String, Object>) body.get(key)).get("bid"));
    }
}
