package model;

import java.math.BigDecimal;
import java.time.LocalDate;


public class PurchaseTransaction {
    private String id;
    private String description;
    private LocalDate transactionDate;
    private BigDecimal purchaseAmount;

    public PurchaseTransaction(String id, String description, LocalDate transactionDate, BigDecimal purchaseAmount) {
        this.id = id;
        this.description = description;
        this.transactionDate = transactionDate;
        this.purchaseAmount = purchaseAmount.setScale(2);
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public BigDecimal getPurchaseAmount() {
        return purchaseAmount;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    //Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public void setPurchaseAmount(BigDecimal purchaseAmount) {
        this.purchaseAmount = purchaseAmount;
    }
}
