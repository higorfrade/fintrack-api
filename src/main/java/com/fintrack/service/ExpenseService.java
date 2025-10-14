package com.fintrack.service;

import com.fintrack.dto.ExpenseDTO;
import com.fintrack.entity.CategoryEntity;
import com.fintrack.entity.ExpenseEntity;
import com.fintrack.entity.UserEntity;
import com.fintrack.repository.CategoryRepository;
import com.fintrack.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;
    private final UserService userService;

    public ExpenseDTO addExpense(ExpenseDTO expenseDTO) {
        UserEntity user = userService.getCurrentUser();
        CategoryEntity category = categoryRepository.findById(expenseDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        ExpenseEntity newExpense = toEntity(expenseDTO, user, category);
        newExpense = expenseRepository.saveAndFlush(newExpense);

        return toDTO(newExpense);
    }

    public List<ExpenseDTO> getCurrentMonthExpenses() {
        UserEntity user = userService.getCurrentUser();

        LocalDate currentDate = LocalDate.now();
        LocalDate startDate = currentDate.withDayOfMonth(1);
        LocalDate endDate = currentDate.withDayOfMonth(currentDate.lengthOfMonth());

        List<ExpenseEntity> list  = expenseRepository.findByUserIdAndDateBetween(user.getId(), startDate, endDate);

        return list.stream().map(this::toDTO).toList();
    }

    public List<ExpenseDTO> getLatest5Expenses() {
        UserEntity user = userService.getCurrentUser();
        List<ExpenseEntity> list = expenseRepository.findTop5ByUserIdOrderByDateDesc(user.getId());

        return list.stream().map(this::toDTO).toList();
    }

    public BigDecimal getTotalExpenses() {
        UserEntity user = userService.getCurrentUser();
        BigDecimal total = expenseRepository.findTotalByUserId(user.getId());

        return total != null ? total : BigDecimal.ZERO;
    }

    public void deleteExpense(Long expenseId) {
        UserEntity user = userService.getCurrentUser();

        ExpenseEntity expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Despesa não encontrada"));

        if (!expense.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Exclusão de despesa não permitida");
        }

        expenseRepository.delete(expense);
    }

    private ExpenseEntity toEntity(ExpenseDTO expenseDTO, UserEntity user, CategoryEntity category) {
        return ExpenseEntity.builder()
                .name(expenseDTO.getName())
                .amount(expenseDTO.getAmount())
                .icon(expenseDTO.getIcon())
                .date(expenseDTO.getDate())
                .user(user)
                .category(category)
                .build();
    }

    private ExpenseDTO toDTO(ExpenseEntity expenseEntity) {
        return ExpenseDTO.builder()
                .id(expenseEntity.getId())
                .name(expenseEntity.getName())
                .amount(expenseEntity.getAmount())
                .icon(expenseEntity.getIcon())
                .date(expenseEntity.getDate())
                .categoryId(expenseEntity.getCategory() != null ? expenseEntity.getCategory().getId() : null)
                .categoryName(expenseEntity.getCategory() != null ? expenseEntity.getCategory().getName() : "N/A")
                .createdAt(expenseEntity.getCreatedAt())
                .updatedAt(expenseEntity.getUpdatedAt())
                .build();
    }
}
