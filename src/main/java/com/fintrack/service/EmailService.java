package com.fintrack.service;

import brevo.ApiClient;
import brevo.ApiException;
import brevo.Configuration;
import brevoApi.TransactionalEmailsApi;
import brevoModel.SendSmtpEmail;
import brevoModel.SendSmtpEmailSender;
import brevoModel.SendSmtpEmailTo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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

    public void sendCustomEmail(String toEmail, String subject, String name, String activationLink) {
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
        Long templateId = 1L;
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
}
