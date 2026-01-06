package com.example.Mock.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import com.example.common.Logger;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.base-url:https://localhost:5500}")
    private String appBaseUrl;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    public void sendPasswordResetEmail(String toEmail, String username, String token) throws MessagingException {
        String resetLink = appBaseUrl + "/resetpassword.html?token=" + token;

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(toEmail);
        helper.setSubject("Password Reset Request - Fantasy Football Mock Draft");

        String htmlContent = buildPasswordResetEmail(username, resetLink);
        helper.setText(htmlContent, true);

        mailSender.send(message);
        Logger.logInfo("Password reset email sent to: " + toEmail);
    }

    private String buildPasswordResetEmail(String username, String resetLink) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                ".container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                ".header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; }" +
                ".content { padding: 20px; background-color: #f9f9f9; }" +
                ".button { display: inline-block; padding: 12px 24px; background-color: #4CAF50; color: white; " +
                "text-decoration: none; border-radius: 4px; margin: 20px 0; }" +
                ".footer { padding: 20px; text-align: center; font-size: 12px; color: #666; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='header'>" +
                "<h1>Password Reset</h1>" +
                "</div>" +
                "<div class='content'>" +
                "<p>Hello " + escapeHtml(username) + ",</p>" +
                "<p>We received a request to reset your password for your Fantasy Football Mock Draft account.</p>" +
                "<p>Click the button below to reset your password:</p>" +
                "<p style='text-align: center;'>" +
                "<a href='" + escapeHtml(resetLink) + "' class='button'>Reset Password</a>" +
                "</p>" +
                "<p>This link will expire in 1 hour.</p>" +
                "<p>If you didn't request a password reset, you can safely ignore this email.</p>" +
                "</div>" +
                "<div class='footer'>" +
                "<p>Fantasy Football Mock Draft Sim</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }

    private String escapeHtml(String input) {
        if (input == null) return "";
        return input
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }
}
