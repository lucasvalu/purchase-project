package usecase;

import model.PurchaseTransaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import repository.PurchaseTransactionRepository;

import java.math.BigDecimal;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class GetConvertedTransactionUseCaseTest {

    @Mock
    private PurchaseTransactionRepository repository;

    @Mock
    private HttpClient httpClient;

    @InjectMocks
    private GetConvertedTransactionUseCase getConvertedTransactionUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testExecute_Success() throws Exception {
        String transactionId = UUID.randomUUID().toString();

        PurchaseTransaction transaction = new PurchaseTransaction(transactionId,
                "Test Transaction",
                LocalDate.of(2024, 11, 15),
                BigDecimal.valueOf(1000.0));

        when(repository.findById("1")).thenReturn(Optional.of(transaction));

        String apiResponseBody = "{ \"data\": [ { \"country_currency_desc\": \"USD\", \"exchange_rate\": \"1.5\", \"record_date\": \"2024-10-31\" } ] }";
        HttpResponse<String> httpResponse = mock(HttpResponse.class);
        when(httpResponse.body()).thenReturn(apiResponseBody);
        when(httpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(httpResponse);

        GetConvertedTransactionUseCase useCase = new GetConvertedTransactionUseCase(repository);
        useCase.httpClient = httpClient;

        Map<String, Object> result = useCase.execute("1", "USD");

        assertEquals(transactionId, result.get("transactionId"));
        assertEquals("Test Transaction", result.get("transactionDescription"));
        assertEquals(LocalDate.of(2024, 11, 15), result.get("transactionDate"));
        assertEquals("1000.00", result.get("transactionPurchaseAmount - USD").toString());
        assertEquals(1.5, result.get("exchangeRate"));
        assertEquals("1500.00", result.get("convertedAmount"));
        assertEquals("2024-10-31", result.get("recordDate"));

        verify(repository, times(1)).findById("1");
        verify(httpClient, times(1)).send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()));
    }

    @Test
    void testExecute_Failure_TransactionNotFound() {
        when(repository.findById("1")).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            getConvertedTransactionUseCase.execute("1", "USD");
        });

        assertEquals("Transaction with transactionId 1 not found in database", exception.getMessage());
    }

    @Test
    void testExecute_Failure_FailedToFetchExchangeRateData() throws Exception {
        String transactionId = UUID.randomUUID().toString();

        PurchaseTransaction transaction = new PurchaseTransaction(transactionId,
                "Test Transaction",
                LocalDate.of(2024, 11, 15),
                BigDecimal.valueOf(1000.0));

        when(repository.findById("1")).thenReturn(Optional.of(transaction));

        String apiResponseBody = "{ \"data\": [{\"country_currency_desc\": \"ARGENTINA-PESO\",\"exchange_rate\": \"14.593\",\"record_date\": \"2016-03-31\"}] }";
        HttpResponse<String> httpResponse = mock(HttpResponse.class);
        when(httpResponse.body()).thenReturn(apiResponseBody);
        when(httpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(httpResponse);

        GetConvertedTransactionUseCase useCase = new GetConvertedTransactionUseCase(repository);
        useCase.httpClient = httpClient;

        Exception exception = assertThrows(RuntimeException.class, () -> {
            useCase.execute("1", "USD");
        });

        assertEquals("Failed to fetch exchange rate data", exception.getMessage());
    }
}
