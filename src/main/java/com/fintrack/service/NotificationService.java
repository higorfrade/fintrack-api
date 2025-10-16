package com.fintrack.service;

import com.fintrack.dto.ExpenseDTO;
import com.fintrack.dto.IncomeDTO;
import com.fintrack.entity.UserEntity;
import com.fintrack.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final IncomeService incomeService;
    private final ExpenseService expenseService;

    @Value("${fintrack.frontend.url}")
    private String frontendUrl;

    @Scheduled(cron = "0 0 19 * * *", zone = "America/Sao_Paulo")
    public void sendDailyTransactionsReminder() {
        log.info("Serviço iniciado: sendDailyTransactionsReminder()");
        List<UserEntity> users = userRepository.findAll();

        for (UserEntity user : users) {

            emailService.sendDailyReminderEmail(user, frontendUrl, 4L);
        }
        log.info("Serviço concluído: sendDailyTransactionsReminder()");
    }

    @Scheduled(cron = "0 0 22 * * *", zone = "America/Sao_Paulo")
    public void sendDailyTransactionsSummary() {
        log.info("Serviço iniciado: sendDailyTransactionsSummary()");
        List<UserEntity> users = userRepository.findAll();

        for (UserEntity user : users) {
            List<IncomeDTO> todayIncomes = incomeService.getDailyIncomes(user.getId(), LocalDate.now());
            List<ExpenseDTO> todayExpenses= expenseService.getDailyExpenses(user.getId(), LocalDate.now());

            if (!todayIncomes.isEmpty() || !todayExpenses.isEmpty()) {
                StringBuilder table = new StringBuilder();
                table.append("<table class='transaction-table' style='border-collapse:collapse; width:100%;'>");
                table.append("<tr style='background-color:white;'>")
                        .append("<thead><tr>")
                        .append("<th style='border:1px solid black; padding:10px;'>S.No</th>")
                        .append("<th style='border:1px solid black; padding:10px;'>Nome</th>")
                        .append("<th style='border:1px solid black; padding:10px;'>Valor</th>")
                        .append("<th style='border:1px solid black; padding:10px;'>Categoria</th>")
                        .append("<th style='border:1px solid black; padding:10px;'>Tipo</th>")
                        .append("<th style='border:1px solid black; padding:10px;'>Data</th>")
                        .append("</tr></thead><tbody>");

                int i = 1;
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

                for (IncomeDTO income : todayIncomes) {
                    table.append("<tr>")
                            .append("<td style='border:1px solid black; padding:8px;'>").append(i++).append("</td>")
                            .append("<td style='border:1px solid black; padding:8px;'>").append(income.getName()).append("</td>")
                            .append("<td style='border:1px solid black; padding:8px; color:#14b55c;'>").append(currencyFormatter.format(income.getAmount())).append("</td>")
                            .append("<td style='border:1px solid black; padding:8px;'>").append(income.getCategoryId() != null ? income.getCategoryName() : "N/A").append("</td>")
                            .append("<td style='border:1px solid black; padding:8px;'>Receita</td>")
                            .append("<td style='border:1px solid black; padding:8px;'>").append(income.getDate() != null ? income.getDate().format(formatter) : "")
                            .append("</tr>");
                }

                for (ExpenseDTO expense : todayExpenses) {
                    table.append("<tr>")
                            .append("<td style='border:1px solid black; padding:8px;'>").append(i++).append("</td>")
                            .append("<td style='border:1px solid black; padding:8px;'>").append(expense.getName()).append("</td>")
                            .append("<td style='border:1px solid black; padding:8px; color:#bd3131;'>").append(currencyFormatter.format(expense.getAmount())).append("</td>")
                            .append("<td style='border:1px solid black; padding:8px;'>").append(expense.getCategoryId() != null ? expense.getCategoryName() : "N/A").append("</td>")
                            .append("<td style='border:1px solid black; padding:8px;'>Despesa</td>")
                            .append("<td style='border:1px solid black; padding:8px;'>").append(expense.getDate() != null ? expense.getDate().format(formatter) : "")
                            .append("</tr>");
                }

                table.append("</tbody></table>");

                String currentDate = LocalDate.now().format(formatter);
                emailService.sendDailyTransactionsEmail(user, currentDate, table.toString(), 2L);
            }
        }
        log.info("Serviço concluído: sendDailyTransactionsSummary()");
    }
}
