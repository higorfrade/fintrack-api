package com.fintrack.service;

import com.fintrack.dto.ExpenseDTO;
import com.fintrack.dto.IncomeDTO;
import com.fintrack.dto.RecentTransactionDTO;
import com.fintrack.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Stream.concat;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserService userService;
    private final IncomeService incomeService;
    private final ExpenseService expenseService;

    public Map<String, Object> getDashboardData() {
        UserEntity user = userService.getCurrentUser();
        Map<String, Object> returnValue = new LinkedHashMap<>();
        List<IncomeDTO> latestIncomes = incomeService.getLatest5Incomes();
        List<ExpenseDTO> latestExpenses = expenseService.getLatest5Expenses();

        List<RecentTransactionDTO> recentTransactions = concat(latestIncomes.stream().map(income ->
                        RecentTransactionDTO.builder()
                                .id(income.getId())
                                .userId(user.getId())
                                .icon(income.getIcon())
                                .name(income.getName())
                                .type("receita")
                                .amount(income.getAmount())
                                .date(income.getDate())
                                .createdAt(income.getCreatedAt())
                                .updatedAt(income.getUpdatedAt())
                                .build()),
                latestExpenses.stream().map(expense ->
                        RecentTransactionDTO.builder()
                                .id(expense.getId())
                                .userId(user.getId())
                                .icon(expense.getIcon())
                                .name(expense.getName())
                                .type("despesa")
                                .amount(expense.getAmount())
                                .date(expense.getDate())
                                .createdAt(expense.getCreatedAt())
                                .updatedAt(expense.getUpdatedAt())
                                .build()))
                .sorted((a, b) -> {
                    int comparator = b.getDate().compareTo(a.getDate());
                    if (comparator == 0 && a.getCreatedAt() != null && b.getCreatedAt() != null) {
                        return b.getCreatedAt().compareTo(a.getCreatedAt());
                    }
                    return comparator;
                }).collect(Collectors.toList());

        returnValue.put("totalBalance", incomeService.getTotalIncomes().subtract(expenseService.getTotalExpenses()));
        returnValue.put("totalIncome", incomeService.getTotalIncomes());
        returnValue.put("totalExpense", expenseService.getTotalExpenses());
        returnValue.put("recent5Incomes", latestIncomes);
        returnValue.put("recent5Expenses", latestExpenses);
        returnValue.put("recentTransactions", recentTransactions);

        return returnValue;
    }
}
