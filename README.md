# Purchase Transaction Project

## Overview

This is a project to register purchase transactions and convert values based on exchange rates.

Before running the project, make sure you have Java (JDK 11 or later), Maven (version 3.8.1 or later) and a SQLite JDBC Driver on your machine.

This project was developed by Lucas Matheus Valu Batista in November 2024.

## Endpoints

### 1. Add Purchase Transaction

**POST** `/transactions`

Registers a new purchase transaction.

#### Parameters:
- `description` (string): Description of the transaction (max. 50 characters)
- `transactionDate` (string): Date of the transaction in `YYYY-MM-DD` format
- `purchaseAmount` (decimal): Purchase amount

#### Example Request:
```bash
curl --location 'http://localhost:8080/transactions' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode 'description=Iphone 8' \
--data-urlencode 'transactionDate=2016-06-30' \
--data-urlencode 'purchaseAmount=3500.00'
```

#### Example Response:
```JSON
"f3c76345-8ac9-4f0d-b0ed-22ad7e3b0e8b"
```

### 2. Get Converted Transaction

**GET** `/transactions/{id}/converted`

Converts the transaction amount to the specified currency using the exchange rate closest to the transaction date.

#### Parameters:

- `id` (string): The transaction ID (obtained when creating the transaction)
- `currency` (string): The currency code for conversion (e.g., "Canada-Dollar", "Mexico-Peso")

```bash
curl --location 'http://localhost:8080/transactions/6e64c6ea-3a10-459b-86f7-dae32609a959/converted' \
--header 'currency: ARGENTINA-PESO'
```

#### Example Response:
```JSON
{
  "convertedAmount": "52328.50",
  "exchangeRate": 14.951,
  "transactionPurchaseAmount - USD": 3500.00,
  "recordDate": "2016-06-30",
  "transactionDescription": "Iphone 8",
  "transactionDate": "2016-06-30",
  "transactionId": "6e64c6ea-3a10-459b-86f7-dae32609a959"
}
```

## Running Locally

### 1. Clone the repository:

git clone https://github.com/lucasvalu/purchase-project

### 2. Compile and run the application:

mvn clean install

mvn spring-boot:run

### 3. Calling API
The APIs will be available at http://localhost:8080

You can use postman to call the APIs using the examples above

### 4. Access database
The database file will be available in the project root directory

You can use "DB Browser for SQLite" to access the transactions.db file
