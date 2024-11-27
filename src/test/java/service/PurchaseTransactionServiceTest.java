package service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import usecase.AddTransactionUseCase;
import usecase.GetConvertedTransactionUseCase;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PurchaseTransactionServiceTest {

    @Mock
    private AddTransactionUseCase addTransactionUseCase;

    @Mock
    private GetConvertedTransactionUseCase getConvertedTransactionUseCase;

    @InjectMocks
    private PurchaseTransactionService purchaseTransactionService;

    @Test
    void testAddTransaction() {
        String description = "Test Transaction";
        String transactionDate = "2024-11-27";
        String purchaseAmount = "1000";

        String expectedTransactionId = "12345";

        when(addTransactionUseCase.execute(description, transactionDate, purchaseAmount)).thenReturn(expectedTransactionId);

        String actualTransactionId = purchaseTransactionService.addTransaction(description, transactionDate, purchaseAmount);

        assertEquals(expectedTransactionId, actualTransactionId);
        Mockito.verify(addTransactionUseCase).execute(description, transactionDate, purchaseAmount);
    }

    @Test
    void testGetConvertedTransaction() {
        String transactionId = "12345";
        String currency = "EUR";

        Map<String, Object> expectedConvertedTransaction = Map.of(
                "transactionId", transactionId,
                "transactionDescription", "Test Transaction",
                "transactionDate", "2024-11-27",
                "transactionPurchaseAmount - USD", "1000",
                "exchangeRate", 1.2,
                "convertedAmount", "1200.00",
                "recordDate", "2024-11-01"
        );
        when(getConvertedTransactionUseCase.execute(transactionId, currency)).thenReturn(expectedConvertedTransaction);

        Map<String, Object> actualConvertedTransaction = purchaseTransactionService.getConvertedTransaction(transactionId, currency);

        assertNotNull(actualConvertedTransaction);
        assertEquals(expectedConvertedTransaction, actualConvertedTransaction);
        Mockito.verify(getConvertedTransactionUseCase).execute(transactionId, currency);
    }
}
