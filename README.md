# Mini Bank

A banking REST API built with **Java 25** and **Spring Boot 4**, with an Angular front end planned.
Users can open accounts, deposit and withdraw money, transfer between accounts and see their transaction history.

## Tech stack

| Layer | Technology |
|---|---|
| Language | Java 25 |
| Framework | Spring Boot 4.1 (Spring MVC) |
| Validation | Jakarta Bean Validation (Hibernate Validator) |
| Build | Maven |
| Tests | JUnit 5, AssertJ |
| Errors | RFC 9457 Problem Details (`ProblemDetail`) |

## Features

- Open accounts and look them up by id or by owner
- Deposit and withdraw money, with balance rules enforced by the domain
- Transfer money between accounts
- Full transaction history per account (type, amount, balance after, timestamp)
- Request validation with field-level error messages
- Consistent JSON error responses (400, 404, 500) handled in one place

## Getting started

**Requirements:** JDK 25. Maven is not required: the project includes the Maven Wrapper.

```bash
# Run the application (http://localhost:8080)
./mvnw spring-boot:run

# Run the tests
./mvnw test
```

On Windows, use `mvnw.cmd` instead of `bank/mvnw`.

## API

| Method | Endpoint | Body | Success |
|---|---|---|---|
| `POST` | `/api/accounts` | `{"owner": "Alice"}` | `201` + account |
| `GET` | `/api/accounts/{id}` | | `200` + account |
| `GET` | `/api/accounts?owner=Alice` | | `200` + list of accounts |
| `POST` | `/api/accounts/{id}/deposit` | `{"amount": 100.00}` | `200` + account |
| `POST` | `/api/accounts/{id}/withdraw` | `{"amount": 30.00}` | `200` + account |
| `GET` | `/api/accounts/{id}/transactions` | | `200` + list of transactions |
| `POST` | `/api/transfers` | `{"fromId": "...", "toId": "...", "amount": 40.00}` | `204` |

Example account response:

```json
{
  "id": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
  "owner": "Alice",
  "balance": 100.00
}
```

### Errors

Every error uses the standard Problem Details format:

```json
{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Insufficient funds",
  "instance": "/api/transfers"
}
```

| Situation | Status |
|---|---|
| Invalid request body (missing field, negative amount, more than 2 decimals) | `400` + an `errors` object per field |
| Insufficient funds, transfer to the same account | `400` |
| Account not found | `404` |
| Unexpected error | `500` with a generic message (details are only logged) |

Validation error example:

```json
{
  "status": 400,
  "detail": "Validation failed",
  "errors": {
    "fromId": "must not be blank",
    "amount": "must be greater than 0"
  }
}
```

## Design decisions

- **Money is `BigDecimal`, never `double`.** Floating-point numbers can't represent most decimal values exactly; amounts are also limited to 2 decimals.
- **The account protects its own balance.** There is no `setBalance`: the balance only changes through `deposit` and `withdraw`, which enforce the rules. The transaction history is returned as an unmodifiable copy.
- **Validate first, then mutate.** A transfer looks up both accounts and checks the funds before moving any money, so a failed transfer changes nothing.
- **Defense in depth.** Request DTOs are validated at the HTTP boundary (`@Valid`), and the domain still checks its own rules, whoever calls it.
- **Layered architecture.** Thin controllers handle HTTP, the service holds the business logic, and DTOs decouple the API from the domain model.
- **Centralized error handling.** One `@RestControllerAdvice` maps domain exceptions to HTTP statuses, so new endpoints get correct errors without extra code.
- **Thread safety.** The service is a shared singleton bean, so accounts are stored in a `ConcurrentHashMap`.

## Roadmap

- [x] REST API for accounts and transfers
- [x] Error handling and validation
- [ ] API tests with MockMvc
- [ ] PostgreSQL persistence with JPA, Flyway and `@Transactional` transfers
- [ ] Angular dashboard
- [ ] Authentication with Spring Security and JWT, user and admin roles
- [ ] Docker, CI/CD with GitHub Actions, live demo
- [ ] AI features: spending categorization and questions about your transactions

Data is currently stored in memory and is reset on every restart; the database comes next.