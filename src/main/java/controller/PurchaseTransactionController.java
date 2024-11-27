package controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.PurchaseTransactionService;

import java.util.Map;

@RestController
@RequestMapping("/transactions")
public class PurchaseTransactionController {
    private final PurchaseTransactionService service;

    public PurchaseTransactionController(PurchaseTransactionService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<String> addTransaction(
            @RequestParam String description,
            @RequestParam String transactionDate,
            @RequestParam String purchaseAmount) {
        try {
            String transactionId = service.addTransaction(description, transactionDate, purchaseAmount);
            return ResponseEntity.ok(transactionId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}/converted")
    public ResponseEntity<Map<String, Object>> getConvertedTransaction(@PathVariable String id, @RequestHeader String currency) {
        try {
            Map<String, Object> response = service.getConvertedTransaction(id, currency);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
