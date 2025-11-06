package org.java;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.java.model.Condominium;
import org.java.model.Resident;
import org.java.model.Transaction;
import org.java.repository.CondominiumRepository;
import org.java.repository.ResidentRepository;
import org.java.repository.TransactionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

  private final CondominiumRepository condominiumRepository;
  private final ResidentRepository residentRepository;
  private final TransactionRepository transactionRepository;

  @Override
  public void run(String... args) {
    log.info("Initializing sample data...");

    // Create condominiums
    Condominium condo1 = Condominium.builder()
        .name("Residencial Jardim das Flores")
        .address("Rua das Flores, 123")
        .city("São Paulo")
        .state("SP")
        .active(true)
        .adminEmails(Arrays.asList("admin@jardimflores.com", "gerente@jardimflores.com"))
        .build();

    Condominium condo2 = Condominium.builder()
        .name("Edifício Vista Mar")
        .address("Av. Atlântica, 456")
        .city("Rio de Janeiro")
        .state("RJ")
        .active(true)
        .adminEmails(List.of("admin@vistamar.com"))
        .build();

    condominiumRepository.saveAll(Arrays.asList(condo1, condo2));
    log.info("Created {} condominiums", 2);

    // Create residents for condo1
    Resident resident1 = Resident.builder()
        .condoId(condo1.getId())
        .name("João Silva")
        .email("joao.silva@email.com")
        .phone("(11) 98765-4321")
        .unitNumber("101")
        .status(Resident.ResidentStatus.ACTIVE)
        .build();

    Resident resident2 = Resident.builder()
        .condoId(condo1.getId())
        .name("Maria Santos")
        .email("maria.santos@email.com")
        .phone("(11) 98765-1234")
        .unitNumber("102")
        .status(Resident.ResidentStatus.ACTIVE)
        .build();

    Resident resident3 = Resident.builder()
        .condoId(condo1.getId())
        .name("Carlos Oliveira")
        .email("carlos.oliveira@email.com")
        .phone("(11) 98765-5678")
        .unitNumber("201")
        .status(Resident.ResidentStatus.ACTIVE)
        .build();

    residentRepository.saveAll(Arrays.asList(resident1, resident2, resident3));
    log.info("Created {} residents", 3);

    // Create transactions for condo1
    LocalDate today = LocalDate.now();

    List<Transaction> transactions = Arrays.asList(
        // Revenue transactions
        Transaction.builder()
            .condoId(condo1.getId())
            .description("Condominium fee - Unit 101")
            .amount(new BigDecimal("500.00"))
            .type(Transaction.TransactionType.REVENUE)
            .date(today.minusDays(30))
            .category("Monthly Fee")
            .build(),
        Transaction.builder()
            .condoId(condo1.getId())
            .description("Condominium fee - Unit 102")
            .amount(new BigDecimal("500.00"))
            .type(Transaction.TransactionType.REVENUE)
            .date(today.minusDays(30))
            .category("Monthly Fee")
            .build(),
        Transaction.builder()
            .condoId(condo1.getId())
            .description("Condominium fee - Unit 201")
            .amount(new BigDecimal("600.00"))
            .type(Transaction.TransactionType.REVENUE)
            .date(today.minusDays(30))
            .category("Monthly Fee")
            .build(),
        Transaction.builder()
            .condoId(condo1.getId())
            .description("Parking fee - Unit 101")
            .amount(new BigDecimal("150.00"))
            .type(Transaction.TransactionType.REVENUE)
            .date(today.minusDays(25))
            .category("Parking")
            .build(),
        Transaction.builder()
            .condoId(condo1.getId())
            .description("Pool maintenance special fee")
            .amount(new BigDecimal("300.00"))
            .type(Transaction.TransactionType.REVENUE)
            .date(today.minusDays(20))
            .category("Special Assessment")
            .build(),

        // Expense transactions
        Transaction.builder()
            .condoId(condo1.getId())
            .description("Cleaning service - Common areas")
            .amount(new BigDecimal("800.00"))
            .type(Transaction.TransactionType.EXPENSE)
            .date(today.minusDays(28))
            .category("Maintenance")
            .build(),
        Transaction.builder()
            .condoId(condo1.getId())
            .description("Electricity - Common areas")
            .amount(new BigDecimal("450.00"))
            .type(Transaction.TransactionType.EXPENSE)
            .date(today.minusDays(27))
            .category("Utilities")
            .build(),
        Transaction.builder()
            .condoId(condo1.getId())
            .description("Water bill")
            .amount(new BigDecimal("350.00"))
            .type(Transaction.TransactionType.EXPENSE)
            .date(today.minusDays(27))
            .category("Utilities")
            .build(),
        Transaction.builder()
            .condoId(condo1.getId())
            .description("Security service")
            .amount(new BigDecimal("1200.00"))
            .type(Transaction.TransactionType.EXPENSE)
            .date(today.minusDays(26))
            .category("Security")
            .build(),
        Transaction.builder()
            .condoId(condo1.getId())
            .description("Garden maintenance")
            .amount(new BigDecimal("400.00"))
            .type(Transaction.TransactionType.EXPENSE)
            .date(today.minusDays(22))
            .category("Maintenance")
            .build(),
        Transaction.builder()
            .condoId(condo1.getId())
            .description("Elevator maintenance")
            .amount(new BigDecimal("600.00"))
            .type(Transaction.TransactionType.EXPENSE)
            .date(today.minusDays(15))
            .category("Maintenance")
            .build(),
        Transaction.builder()
            .condoId(condo1.getId())
            .description("Pool chemicals")
            .amount(new BigDecimal("250.00"))
            .type(Transaction.TransactionType.EXPENSE)
            .date(today.minusDays(10))
            .category("Supplies")
            .build()
    );

    transactionRepository.saveAll(transactions);
    log.info("Created {} transactions", transactions.size());

    log.info("Sample data initialization completed successfully!");
    log.info("You can now test the API with Condo ID: {}", condo1.getId());
  }
}