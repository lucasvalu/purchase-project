package usecase;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.PurchaseTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import repository.PurchaseTransactionRepository;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Component
public class GetConvertedTransactionUseCase {
    private static final Logger logger = LoggerFactory.getLogger(GetConvertedTransactionUseCase.class);
    private final PurchaseTransactionRepository repository;
    public HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public GetConvertedTransactionUseCase(PurchaseTransactionRepository repository) {
        this.repository = repository;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public Map<String, Object> execute(String transactionId, String currency) {
        logger.info("Retrieving transaction with transactionId {} from database", transactionId);

        PurchaseTransaction transaction = repository.findById(transactionId).orElseThrow(() ->
                new IllegalArgumentException("Transaction with transactionId " + transactionId + " not found in database"));

        String apiUrl = "https://api.fiscaldata.treasury.gov/services/api/fiscal_service/v1/accounting/od/rates_of_exchange" +
                "?fields=country_currency_desc,exchange_rate,record_date" +
                "&filter=country_currency_desc:in:(" + currency + ")";

        logger.info("Calling Fiscal Data to get exchange information");

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(apiUrl))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            Map<String, Object> apiResponse = objectMapper.readValue(response.body(), Map.class);

            Map<String, Object> closestRecord = findClosestRecord(apiResponse, transaction.getTransactionDate());

            double exchangeRate = Double.parseDouble(closestRecord.get("exchange_rate").toString());
            double convertedAmount = exchangeRate * transaction.getPurchaseAmount().doubleValue();

            logger.info("Calculation finished. Returning data and finishing process");

            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("transactionId", transaction.getId());
            responseMap.put("transactionDescription", transaction.getDescription());
            responseMap.put("transactionDate", transaction.getTransactionDate());
            responseMap.put("transactionPurchaseAmount - USD", transaction.getPurchaseAmount());
            responseMap.put("exchangeRate", exchangeRate);
            responseMap.put("convertedAmount", String.format(Locale.US, "%.2f", Math.round(convertedAmount * 100.0) / 100.0));
            responseMap.put("recordDate", closestRecord.get("record_date"));

            return responseMap;
        } catch (Exception e) {
            logger.error("Error while calling exchange rate API", e);
            throw new RuntimeException("Failed to fetch exchange rate data", e);
        }
    }

    private Map<String, Object> findClosestRecord(Map<String, Object> apiResponse, LocalDate transactionDate) {
        LocalDate closestDate = null;
        Map<String, Object> closestRecord = null;

        LocalDate sixMonthsBefore = transactionDate.minusMonths(6);

        for (Map<String, Object> record : (Iterable<Map<String, Object>>) apiResponse.get("data")) {
            LocalDate recordDate = LocalDate.parse(record.get("record_date").toString(), DateTimeFormatter.ISO_DATE);

            if (recordDate.isAfter(sixMonthsBefore)) {
                if (closestDate == null || Math.abs(ChronoUnit.DAYS.between(transactionDate, recordDate)) <
                        Math.abs(ChronoUnit.DAYS.between(transactionDate, closestDate))) {
                    closestDate = recordDate;
                    closestRecord = record;
                }
            }
        }

        if (closestRecord == null) {
            throw new IllegalArgumentException("No exchange rate found within 6 months of the transaction date.");
        }

        return closestRecord;
    }
}
