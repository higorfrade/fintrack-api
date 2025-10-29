package com.fintrack.service;

import com.fintrack.dto.ExpenseDTO;
import com.fintrack.entity.CategoryEntity;
import com.fintrack.entity.ExpenseEntity;
import com.fintrack.entity.UserEntity;
import com.fintrack.repository.CategoryRepository;
import com.fintrack.repository.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("🧩 Testes unitários para ExpenseService")
class ExpenseServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ExpenseService expenseService;

    private UserEntity user;
    private CategoryEntity category;

    @BeforeEach
    void SetUp() {
        MockitoAnnotations.openMocks(this);
        user = new UserEntity();
        user.setId(1L);

        category = new CategoryEntity();
        category.setId(2L);
    }

    @Test
    @DisplayName("Deve adicionar despesa com sucesso")
    void addExpenseSuccess() {

        ExpenseDTO dto = new ExpenseDTO();
        dto.setName("Supermercado");
        dto.setAmount(BigDecimal.valueOf(100));
        dto.setCategoryId(2L);

        when(userService.getCurrentUser()).thenReturn(user);
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(category));
        when(expenseRepository.saveAndFlush(any(ExpenseEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0, ExpenseEntity.class));

        var result = expenseService.addExpense(dto);

        assertThat(result.getName()).isEqualTo("Supermercado");
        verify(expenseRepository).saveAndFlush(any());
    }

    @Test
    @DisplayName("Deve lançar exceção se categoria não existir")
    void addExpenseError() {

        ExpenseDTO dto = new ExpenseDTO();
        dto.setName("Cinema");
        dto.setCategoryId(100L);

        when(userService.getCurrentUser()).thenReturn(user);
        when(categoryRepository.findById(100L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> expenseService.addExpense(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Categoria não encontrada");
    }

    @Test
    @DisplayName("Deve retornar total de despesas do usuário")
    void getTotalExpensesSuccess() {

        when(userService.getCurrentUser()).thenReturn(user);
        when(expenseRepository.findTotalByUserId(1L))
                .thenReturn(BigDecimal.valueOf(500));

        BigDecimal result = expenseService.getTotalExpenses();

        assertThat(result).isEqualTo(BigDecimal.valueOf(500));
    }

    @Test
    @DisplayName("Deve retornar ZERO quando não houver despesas")
    void getTotalExpensesNull() {

        when(userService.getCurrentUser()).thenReturn(user);
        when(expenseRepository.findTotalByUserId(1L)).thenReturn(null);

        BigDecimal result = expenseService.getTotalExpenses();

        assertThat(result).isEqualTo(BigDecimal.ZERO);
    }
}