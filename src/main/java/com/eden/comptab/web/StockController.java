package com.eden.comptab.web;

import com.eden.comptab.domain.Stock;
import com.eden.comptab.dto.MouvementStockRequest;
import com.eden.comptab.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

    private final com.eden.comptab.repository.StockRepository stockRepository; // Pour le GET existant
    private final StockService stockService; // Pour le POST ajustement

    // Endpoint existant (on le garde ou on le déplace ici si ce n'était pas déjà fait)
    @GetMapping
    public ResponseEntity<?> getStocks(@RequestParam Long magasinId) {
        return ResponseEntity.ok(stockRepository.findByMagasinId(magasinId));
    }

    @PostMapping("/ajustement")
    public ResponseEntity<Stock> ajusterStock(@RequestBody MouvementStockRequest request) {
        Stock updatedStock = stockService.ajusterStock(request);
        return ResponseEntity.ok(updatedStock);
    }
}
