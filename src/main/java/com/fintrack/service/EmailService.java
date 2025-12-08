package com.fintrack.service;

import brevo.ApiClient;
import brevo.ApiException;
import brevo.Configuration;
import brevoApi.TransactionalEmailsApi;
import brevoModel.SendSmtpEmail;
import brevoModel.SendSmtpEmailSender;
import brevoModel.SendSmtpEmailTo;
import com.fintrack.entity.UserEntity;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("${brevo.apiKey}")
    private String brevoApiKey;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.properties.mail.smtp.from}")
    private String fromEmail;

    @Value("${brevo.defaultSenderName}")
    private String fromName;

    private Long templateId;

    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao enviar o e-mail: " + e.getMessage());
        }
    }

    public void sendActivationEmail(String toEmail, String subject, String name, String activationLink, Long templateId) {
        // Configuracao da API da Brevo
        ApiClient brevoClient = Configuration.getDefaultApiClient();
        brevoClient.setApiKey(brevoApiKey);

        TransactionalEmailsApi apiInstance = new TransactionalEmailsApi(brevoClient);

        // Cria o objeto de envio do email
        SendSmtpEmail email = new SendSmtpEmail();
        SendSmtpEmailSender sender = new SendSmtpEmailSender();
        sender.setEmail(fromEmail);
        sender.setName(fromName);
        email.setSender(sender);

        // Destinatario
        SendSmtpEmailTo to = new SendSmtpEmailTo();
        to.setEmail(toEmail);
        email.setTo(Collections.singletonList(to));

        // Assunto
        email.setSubject(subject);

        // Escolhe o template criado no Brevo
        email.setTemplateId(templateId);

        // Substituindo os parametros do template
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("activationLink", activationLink);
        email.setParams(params);

        try {
            apiInstance.sendTransacEmail(email);
        } catch (ApiException e) {
            throw new RuntimeException("Erro ao enviar o e-mail: " + e.getMessage());
        }
    }

    public void sendDailyReminderEmail(UserEntity user, String siteUrl, Long templateId) {
        // Configuração da API Brevo
        ApiClient brevoClient = Configuration.getDefaultApiClient();
        brevoClient.setApiKey(brevoApiKey);

        TransactionalEmailsApi apiInstance = new TransactionalEmailsApi(brevoClient);

        // Criar o e-mail
        SendSmtpEmail email = new SendSmtpEmail();

        // Remetente
        SendSmtpEmailSender sender = new SendSmtpEmailSender();
        sender.setEmail(fromEmail);
        sender.setName(fromName);
        email.setSender(sender);

        // Destinatário
        SendSmtpEmailTo to = new SendSmtpEmailTo();
        to.setEmail(user.getEmail());
        email.setTo(Collections.singletonList(to));

        // Assunto (opcional se definido no template)
        email.setSubject("Lembrete diário: Adicione suas transações de hoje na Fintrack");

        // ID do template Brevo
        email.setTemplateId(templateId);

        // Parâmetros dinâmicos do template
        Map<String, Object> params = new HashMap<>();
        params.put("name", user.getName());
        params.put("url", siteUrl);

        email.setParams(params);

        try {
            apiInstance.sendTransacEmail(email);
        } catch (ApiException e) {
            throw new RuntimeException("Erro ao enviar e-mail diário: " + e.getMessage(), e);
        }
    }

    public void sendDailyTransactionsEmail(UserEntity user, String currentDate, String tableHtml, Long templateId) {
        // Configuração da API Brevo
        ApiClient brevoClient = Configuration.getDefaultApiClient();
        brevoClient.setApiKey(brevoApiKey);

        TransactionalEmailsApi apiInstance = new TransactionalEmailsApi(brevoClient);

        // Criar o e-mail
        SendSmtpEmail email = new SendSmtpEmail();

        // Remetente
        SendSmtpEmailSender sender = new SendSmtpEmailSender();
        sender.setEmail(fromEmail);
        sender.setName(fromName);
        email.setSender(sender);

        // Destinatário
        SendSmtpEmailTo to = new SendSmtpEmailTo();
        to.setEmail(user.getEmail());
        email.setTo(Collections.singletonList(to));

        // Assunto (opcional se definido no template)
        email.setSubject("Resumo das suas transações de hoje");

        // ID do template Brevo
        email.setTemplateId(templateId);

        // Parâmetros dinâmicos do template
        Map<String, Object> params = new HashMap<>();
        params.put("name", user.getName());
        params.put("currentDate", currentDate);
        params.put("table", tableHtml); // Tabela gerada como HTML via StringBuilder

        email.setParams(params);

        try {
            apiInstance.sendTransacEmail(email);
        } catch (ApiException e) {
            throw new RuntimeException("Erro ao enviar e-mail diário: " + e.getMessage(), e);
        }
    }

    // Destinatário, Assunto, Corpo, Arquivo em forma de Array de Bytes (PDF, Excel), Nome do arquivo no anexo
    public void sendExcelEmail(String to, String subject, String body, byte[] attachment, String filename) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true); // Pode ter anexos ou partes adicionais
        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body);
        helper.addAttachment(filename, new ByteArrayResource(attachment));
        mailSender.send(message);
    }
}
