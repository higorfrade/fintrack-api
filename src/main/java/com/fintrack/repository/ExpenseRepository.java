package com.fintrack.repository;

import com.fintrack.entity.ExpenseEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Long> {

    // SELECT * FROM expenses WHERE user_id = ? ORDER BY date DESC
    List<ExpenseEntity> findByUserIdOrderByDateDesc(Long userId);

    // SELECT * FROM expenses WHERE user_id = ? ORDER BY date DESC LIMIT 5
    List<ExpenseEntity> findTop5ByUserIdOrderByDateDesc(Long userId);

    @Query("SELECT SUM(e.amount) FROM ExpenseEntity e WHERE e.user.id = :userId")
    BigDecimal findTotalByUserId(@Param("userId") Long userId);

    // SELECT * FROM expenses WHERE user_id = ? AND date BETWEEN ? AND ? AND name LIKE ?
    List<ExpenseEntity> findByUserIdAndDateBetweenAndNameContainingIgnoreCase(
            Long userId,
            LocalDate startDate,
            LocalDate endDate,
            String keyword,
            Sort sort
    );

    // SELECT * FROM expenses WHERE user_id = ? AND date BETWEEN ? AND ?
    List<ExpenseEntity> findByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

    // SELECT * FROM expenses WHERE user_id = ? AND date = ?
    List<ExpenseEntity> findByUserIdAndDate(Long userId, LocalDate date);
}
