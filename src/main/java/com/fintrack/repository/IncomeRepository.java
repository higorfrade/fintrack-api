package com.fintrack.repository;

import com.fintrack.entity.IncomeEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface IncomeRepository extends JpaRepository<IncomeEntity, Long> {

    // SELECT * FROM incomes WHERE user_id = ? ORDER BY date DESC
    List<IncomeEntity> findByUserIdOrderByDateDesc(Long userId);

    // SELECT * FROM incomes WHERE user_id = ? ORDER BY date DESC LIMIT 5
    List<IncomeEntity> findTop5ByUserIdOrderByDateDesc(Long userId);

    @Query("SELECT SUM(i.amount) FROM IncomeEntity i WHERE i.user.id = :userId")
    BigDecimal findTotalByUserId(@Param("userId") Long userId);

    // SELECT * FROM incomes WHERE user_id = ? AND date BETWEEN ? AND ? AND name LIKE ?
    List<IncomeEntity> findByUserIdAndDateBetweenAndNameContainingIgnoreCase(
            Long userId,
            LocalDate startDate,
            LocalDate endDate,
            String keyword,
            Sort sort
    );

    // SELECT * FROM incomes WHERE user_id = ? AND date BETWEEN ? AND ?
    List<IncomeEntity> findByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
}
