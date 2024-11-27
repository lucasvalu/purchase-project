package repository;

import model.PurchaseTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public class PurchaseTransactionRepositoryImpl implements PurchaseTransactionRepository {
    private static final Logger logger = LoggerFactory.getLogger(PurchaseTransactionRepositoryImpl.class);
    private final String databaseUrl = "jdbc:sqlite:transactions.db";

    public PurchaseTransactionRepositoryImpl() {
        try (Connection connection = DriverManager.getConnection(databaseUrl)) {
            String createTableQuery = """
                        CREATE TABLE IF NOT EXISTS transactions (
                            id TEXT PRIMARY KEY,
                            description TEXT NOT NULL,
                            transaction_date TEXT NOT NULL,
                            purchase_amount REAL NOT NULL
                        )
                    """;
            connection.createStatement().execute(createTableQuery);
        } catch (SQLException e) {
            throw new RuntimeException("Error initializing database", e);
        }
    }

    @Override
    public void save(PurchaseTransaction transaction) {
        String insertQuery = "INSERT INTO transactions (id, description, transaction_date, purchase_amount) VALUES (?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(databaseUrl);
             PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {

            preparedStatement.setString(1, transaction.getId());
            preparedStatement.setString(2, transaction.getDescription());
            preparedStatement.setString(3, transaction.getTransactionDate().toString());
            preparedStatement.setBigDecimal(4, transaction.getPurchaseAmount());
            preparedStatement.executeUpdate();

            logger.info("Transaction {}  inserted on database successfully", transaction.getId());
        } catch (SQLException e) {
            logger.error("Error while trying to insert transaction {} on database", transaction.getId());
            throw new RuntimeException("Error saving transaction", e);
        }
    }

    @Override
    public Optional<PurchaseTransaction> findById(String id) {
        String query = "SELECT id, description, transaction_date, purchase_amount FROM transactions WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(databaseUrl);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                PurchaseTransaction transaction = new PurchaseTransaction(
                        resultSet.getString("id"),
                        resultSet.getString("description"),
                        LocalDate.parse(resultSet.getString("transaction_date")),
                        resultSet.getBigDecimal("purchase_amount")
                );
                return Optional.of(transaction);
            }
        } catch (SQLException e) {
            logger.error("Error while trying to fetch transaction {}", id);
        }
        return Optional.empty();
    }

}
