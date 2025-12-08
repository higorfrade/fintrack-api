package com.fintrack.controller;

import com.fintrack.entity.UserEntity;
import com.fintrack.service.*;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/email")
public class EmailController {

    private final ExcelService excelService;
    private final IncomeService incomeService;
    private final ExpenseService expenseService;
    private final EmailService emailService;
    private final UserService userService;

    @GetMapping("/income")
    public ResponseEntity<Void> emailIncomeExcel() throws IOException, MessagingException {
        UserEntity user = userService.getCurrentUser();
        ByteArrayOutputStream os = new ByteArrayOutputStream(); // Arquivo virtual em memória
        excelService.writeIncomesToExcel(os, incomeService.getCurrentMonthIncomes()); // Receitas do mês
        emailService.sendExcelEmail(user.getEmail(),
                "Relatório de Receitas Excel",
                "Aqui está o anexo com o relatório das suas receitas desse mês",
                os.toByteArray(),
                "incomes.xlsx"); // Converte o Excel em bytes e envia como anexo por email

        return ResponseEntity.ok(null);
    }

    @GetMapping("/expense")
    public ResponseEntity<Void> emailExpenseExcel() throws IOException, MessagingException {
        UserEntity user = userService.getCurrentUser();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        excelService.writeExpensesToExcel(os, expenseService.getCurrentMonthExpenses()); // Despesas do mês
        emailService.sendExcelEmail(user.getEmail(),
                "Relatório de Despesas Excel",
                "Aqui está o anexo com o relatório das suas despesas desse mês",
                os.toByteArray(),
                "expenses.xlsx");

        return ResponseEntity.ok(null);
    }
}
