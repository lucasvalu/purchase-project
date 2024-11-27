package config;

import controller.PurchaseTransactionController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import repository.PurchaseTransactionRepositoryImpl;
import service.PurchaseTransactionService;
import usecase.AddTransactionUseCase;
import usecase.GetConvertedTransactionUseCase;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public PurchaseTransactionController purchaseTransactionController() {
        PurchaseTransactionRepositoryImpl repository = new PurchaseTransactionRepositoryImpl();
        AddTransactionUseCase addTransactionUseCase = new AddTransactionUseCase(repository);
        GetConvertedTransactionUseCase getConvertedTransactionUseCase = new GetConvertedTransactionUseCase(repository);
        PurchaseTransactionService service = new PurchaseTransactionService(addTransactionUseCase, getConvertedTransactionUseCase);
        return new PurchaseTransactionController(service);
    }
}
