package com.fintrack.service;

import com.fintrack.dto.IncomeDTO;
import com.fintrack.entity.CategoryEntity;
import com.fintrack.entity.IncomeEntity;
import com.fintrack.entity.UserEntity;
import com.fintrack.repository.CategoryRepository;
import com.fintrack.repository.IncomeRepository;
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

@DisplayName("🧩 Testes unitários para IncomeService")
class IncomeServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private IncomeRepository incomeRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private IncomeService incomeService;

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
    @DisplayName("Deve adicionar receita com sucesso")
    void addIncomeSuccess() {

        IncomeDTO dto = new IncomeDTO();
        dto.setName("Salario");
        dto.setAmount(BigDecimal.valueOf(100));
        dto.setCategoryId(2L);

        when(userService.getCurrentUser()).thenReturn(user);
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(category));
        when(incomeRepository.saveAndFlush(any(IncomeEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0, IncomeEntity.class));

        var result = incomeService.addIncome(dto);

        assertThat(result.getName()).isEqualTo("Salario");
        verify(incomeRepository).saveAndFlush(any());
    }

    @Test
    @DisplayName("Deve lançar exceção se categoria não existir")
    void addIncomeError() {

        IncomeDTO dto = new IncomeDTO();
        dto.setName("Venda");
        dto.setCategoryId(100L);

        when(userService.getCurrentUser()).thenReturn(user);
        when(categoryRepository.findById(100L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> incomeService.addIncome(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Categoria não encontrada");
    }

    @Test
    @DisplayName("Deve retornar total de receitas do usuário")
    void getTotalIncomesSuccess() {

        when(userService.getCurrentUser()).thenReturn(user);
        when(incomeRepository.findTotalByUserId(1L))
                .thenReturn(BigDecimal.valueOf(500));

        BigDecimal result = incomeService.getTotalIncomes();

        assertThat(result).isEqualTo(BigDecimal.valueOf(500));
    }

    @Test
    @DisplayName("Deve retornar ZERO quando não houver receitas")
    void getTotalIncomesNull() {

        when(userService.getCurrentUser()).thenReturn(user);
        when(incomeRepository.findTotalByUserId(1L)).thenReturn(null);

        BigDecimal result = incomeService.getTotalIncomes();

        assertThat(result).isEqualTo(BigDecimal.ZERO);
    }
}