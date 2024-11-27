package repository;

import model.PurchaseTransaction;

import java.util.Optional;

public interface PurchaseTransactionRepository {
    void save(PurchaseTransaction transaction);

    Optional<PurchaseTransaction> findById(String id);
}
