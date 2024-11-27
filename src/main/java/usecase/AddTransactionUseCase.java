package usecase;

import model.PurchaseTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import repository.PurchaseTransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Component
public class AddTransactionUseCase {
    private static final Logger logger = LoggerFactory.getLogger(AddTransactionUseCase.class);
    private final PurchaseTransactionRepository repository;

    public AddTransactionUseCase(PurchaseTransactionRepository repository) {
        this.repository = repository;
    }

    public String execute(String description, String transactionDate, String purchaseAmount) {
        if (description == null || description.length() > 50) {
            throw new IllegalArgumentException("Description must not exceed 50 characters");
        }
        if (transactionDate == null || transactionDate.isBlank() || !transactionDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new IllegalArgumentException("Transaction date must be valid, following the pattern YYYY-MM-DD");
        }
        if (purchaseAmount == null || new BigDecimal(purchaseAmount).compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Purchase amount must be positive");
        }

        String id = UUID.randomUUID().toString();
        LocalDate date = LocalDate.parse(transactionDate);
        BigDecimal amount = new BigDecimal(purchaseAmount);

        PurchaseTransaction transaction = new PurchaseTransaction(id, description, date, amount);

        logger.info("Adding a new transaction {} with description: {}", transaction.getId(), transaction.getDescription());

        repository.save(transaction);

        return id;
    }
}
