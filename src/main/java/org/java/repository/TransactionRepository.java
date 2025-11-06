package org.java.repository;

import java.time.LocalDate;
import java.util.List;
import org.java.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

  @Query("SELECT t FROM Transaction t WHERE t.condoId = :condoId " +
      "AND t.date BETWEEN :startDate AND :endDate " +
      "ORDER BY t.date DESC")
  List<Transaction> findByCondoIdAndDateRange(
      @Param("condoId") Long condoId,
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate
  );

  List<org.java.model.Transaction> findByCondoId(Long condoId);
}
