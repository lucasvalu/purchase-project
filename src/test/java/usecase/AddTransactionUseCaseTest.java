package usecase;

import model.PurchaseTransaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import repository.PurchaseTransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AddTransactionUseCaseTest {

    @Mock
    private PurchaseTransactionRepository repository;

    @InjectMocks
    private AddTransactionUseCase addTransactionUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void execute_ShouldAddTransaction_WhenInputIsValid() {
        String description = "Test Transaction";
        String transactionDate = "2023-11-01";
        String purchaseAmount = "100.50";

        String transactionId = addTransactionUseCase.execute(description, transactionDate, purchaseAmount);

        assertNotNull(transactionId);

        ArgumentCaptor<PurchaseTransaction> captor = ArgumentCaptor.forClass(PurchaseTransaction.class);
        verify(repository, times(1)).save(captor.capture());
        PurchaseTransaction capturedTransaction = captor.getValue();

        assertEquals(description, capturedTransaction.getDescription());
        assertEquals(LocalDate.parse(transactionDate), capturedTransaction.getTransactionDate());
        assertEquals(new BigDecimal(purchaseAmount), capturedTransaction.getPurchaseAmount());
    }

    @Test
    void execute_ShouldThrowException_WhenDescriptionIsInvalid() {
        String invalidDescription = "A".repeat(51);
        String transactionDate = "2023-11-01";
        String purchaseAmount = "100.50";

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> addTransactionUseCase.execute(invalidDescription, transactionDate, purchaseAmount)
        );
        assertEquals("Description must not exceed 50 characters", exception.getMessage());
        verifyNoInteractions(repository);
    }

    @Test
    void execute_ShouldThrowException_WhenTransactionDateIsInvalid() {
        String description = "Valid Description";
        String invalidTransactionDate = "invalid-date";
        String purchaseAmount = "100.50";

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> addTransactionUseCase.execute(description, invalidTransactionDate, purchaseAmount)
        );
        assertEquals("Transaction date must be valid, following the pattern YYYY-MM-DD", exception.getMessage());
        verifyNoInteractions(repository);
    }

    @Test
    void execute_ShouldThrowException_WhenPurchaseAmountIsInvalid() {
        String description = "Valid Description";
        String transactionDate = "2023-11-01";
        String invalidPurchaseAmount = "-10.00";

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> addTransactionUseCase.execute(description, transactionDate, invalidPurchaseAmount)
        );
        assertEquals("Purchase amount must be positive", exception.getMessage());
        verifyNoInteractions(repository);
    }
}
