package service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import usecase.AddTransactionUseCase;
import usecase.GetConvertedTransactionUseCase;

import java.util.Map;

@Service
public class PurchaseTransactionService {
    private static final Logger logger = LoggerFactory.getLogger(PurchaseTransactionService.class);
    private final AddTransactionUseCase addTransactionUseCase;
    private final GetConvertedTransactionUseCase getConvertedTransactionUseCase;

    @Autowired
    public PurchaseTransactionService(AddTransactionUseCase addTransactionUseCase, GetConvertedTransactionUseCase getConvertedTransactionUseCase) {
        this.addTransactionUseCase = addTransactionUseCase;
        this.getConvertedTransactionUseCase = getConvertedTransactionUseCase;
    }

    public String addTransaction(String description, String transactionDate, String purchaseAmount) {
        logger.info("Adding transaction process has started successfully");
        return addTransactionUseCase.execute(description, transactionDate, purchaseAmount);
    }

    public Map<String, Object> getConvertedTransaction(String transactionId, String currency) {
        return getConvertedTransactionUseCase.execute(transactionId, currency);
    }
}
