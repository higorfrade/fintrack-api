package com.fintrack.repository;

import com.fintrack.entity.CategoryEntity;
import com.fintrack.entity.ExpenseEntity;
import com.fintrack.entity.UserEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;


@DataJpaTest
class ExpenseRepositoryTest {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Deve retornar a soma total das despesas de um usuário com sucesso")
    void findTotalExpensesByUserIdSuccess() {

        UserEntity user = new UserEntity();
        user.setName("Teste");
        user.setEmail("teste@email.com");
        user.setPassword("123");
        user.setIsActive(true);
        user = userRepository.saveAndFlush(user);

        CategoryEntity category = new CategoryEntity();
        category.setName("Alimentação");
        category.setType("Despesa");
        category.setUser(user);
        category = categoryRepository.saveAndFlush(category);

        ExpenseEntity e1 = new ExpenseEntity();
        e1.setName("Supermercado");
        e1.setCategory(category);
        e1.setAmount(BigDecimal.valueOf(200));
        e1.setDate(LocalDate.now());
        e1.setUser(user);

        ExpenseEntity e2 = new ExpenseEntity();
        e2.setName("Conta de luz");
        e2.setCategory(category);
        e2.setAmount(BigDecimal.valueOf(75));
        e2.setDate(LocalDate.now());
        e2.setUser(user);

        expenseRepository.save(e1);
        expenseRepository.save(e2);

        BigDecimal total = expenseRepository.findTotalByUserId(user.getId());

        assertThat(total)
                .isNotNull()
                .isEqualByComparingTo(BigDecimal.valueOf(275));

    }

    @Test
    @DisplayName("Deve retornar null quando o usuário não tiver despesas")
    void findTotalExpensesByUserIdNull() {

        UserEntity user = new UserEntity();
        user.setName("Teste Silva");
        user.setEmail("testesilva@email.com");
        user.setPassword("321");
        user.setIsActive(true);
        user = userRepository.saveAndFlush(user);

        BigDecimal total = expenseRepository.findTotalByUserId(user.getId());

        assertThat(total).isNull();

    }
}