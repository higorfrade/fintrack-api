package com.fintrack.service;

import com.fintrack.dto.ExpenseDTO;
import com.fintrack.dto.IncomeDTO;
import com.fintrack.entity.UserEntity;
import com.fintrack.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("🧩 Testes unitários para NotificationService")
class NotificationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private IncomeService incomeService;

    @Mock
    private ExpenseService expenseService;

    @InjectMocks
    private NotificationService notificationService;

    private UserEntity user;

    @BeforeEach
    void SetUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(notificationService, "frontendUrl", "http://localhost:3000");
        user = new UserEntity();
        user.setId(1L);
        user.setEmail("user@email.com");
    }

    @Test
    @DisplayName("Deve enviar lembrete diário a todos os usuários")
    void sendDailyTransactionsReminderSuccess() {

        when(userRepository.findAll()).thenReturn(List.of(user));

        notificationService.sendDailyTransactionsReminder();

        verify(emailService).sendDailyReminderEmail(user, "http://localhost:3000", 4L);
    }

    @Test
    @DisplayName("Não deve enviar email se lista de usuários estiver vazia")
    void sendDailyTransactionsReminderError() {

        when(userRepository.findAll()).thenReturn(List.of());

        notificationService.sendDailyTransactionsReminder();

        verify(emailService, never()).sendDailyReminderEmail(any(), any(), any());
    }

    @Test
    @DisplayName("Deve enviar resumo diário quando houver transações")
    void sendDailyTransactionsSummarySuccess() {

        IncomeDTO income = new IncomeDTO();
        income.setName("Salario");
        income.setAmount(BigDecimal.valueOf(5000));
        income.setDate(LocalDate.now());

        ExpenseDTO expense = new ExpenseDTO();
        expense.setName("Aluguel");
        expense.setAmount(BigDecimal.valueOf(1200));
        expense.setDate(LocalDate.now());

        when(userRepository.findAll()).thenReturn(List.of(user));
        when(incomeService.getDailyIncomes(1L, LocalDate.now())).thenReturn(List.of(income));
        when(expenseService.getDailyExpenses(1L, LocalDate.now())).thenReturn(List.of(expense));

        notificationService.sendDailyTransactionsSummary();

        verify(emailService, times(1)).sendDailyTransactionsEmail(
                eq(user),
                anyString(),
                contains("Salario"),
                eq(2L)
        );
    }

    @Test
    @DisplayName("Não deve enviar resumo se não houver transações")
    void sendDailyTransactionsSummaryError() {

        when(userRepository.findAll()).thenReturn(List.of(user));
        when(incomeService.getDailyIncomes(1L, LocalDate.now())).thenReturn(List.of());
        when(expenseService.getDailyExpenses(1L, LocalDate.now())).thenReturn(List.of());

        notificationService.sendDailyTransactionsSummary();

        verify(emailService, never()).sendDailyTransactionsEmail(any(), any(), any(), any());
    }
}