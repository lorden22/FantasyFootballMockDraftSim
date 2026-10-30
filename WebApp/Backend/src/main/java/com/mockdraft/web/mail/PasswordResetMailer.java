package com.mockdraft.web.mail;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

import com.mockdraft.core.service.AuthService.PasswordResetRequest;
import com.mockdraft.web.config.AppProperties;

import jakarta.mail.internet.MimeMessage;

/**
 * Sends password reset links. Runs asynchronously so the API responds in the same time whether or not the
 * email belongs to an account. Links are built from the configured base URL, never from the request's Host.
 */
@Component
public class PasswordResetMailer {

    private static final Logger log = LoggerFactory.getLogger(PasswordResetMailer.class);

    private final ObjectProvider<JavaMailSender> mailSender;
    private final AppProperties props;

    public PasswordResetMailer(ObjectProvider<JavaMailSender> mailSender, AppProperties props) {
        this.mailSender = mailSender;
        this.props = props;
    }

    @Async
    public void send(PasswordResetRequest request) {
        String link = props.baseUrl().replaceAll("/+$", "") + "/reset-password?token="
                + URLEncoder.encode(request.token(), StandardCharsets.UTF_8);
        JavaMailSender sender = mailSender.getIfAvailable();
        if (sender == null) {
            if (props.mail().logResetLinks()) {
                log.info("[dev] Password reset link for {}: {}", request.username(), link);
            } else {
                log.warn("Password reset requested for {} but SMTP is not configured (set MAIL_HOST)", request.username());
            }
            return;
        }
        try {
            MimeMessage message = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(props.mail().from());
            helper.setTo(request.email());
            helper.setSubject("Reset your MockDraft Pro password");
            helper.setText(plainText(request.username(), link), html(request.username(), link));
            sender.send(message);
            log.info("Sent password reset email for {}", request.username());
        } catch (Exception e) {
            log.error("Failed to send password reset email for {}: {}", request.username(), e.getMessage());
        }
    }

    private static String plainText(String username, String link) {
        return "Hi " + username + ",\n\n"
                + "Someone asked to reset the password for your MockDraft Pro account. Use this link within 30 minutes:\n\n"
                + link + "\n\nIf this wasn't you, ignore this email; your password won't change.\n";
    }

    private static String html(String username, String link) {
        String safeName = HtmlUtils.htmlEscape(username);
        String safeLink = HtmlUtils.htmlEscape(link);
        return "<!doctype html><html><body style=\"margin:0;background:#0b1020;font-family:Arial,Helvetica,sans-serif\">"
                + "<table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"><tr><td align=\"center\" style=\"padding:32px 16px\">"
                + "<table role=\"presentation\" width=\"100%\" style=\"max-width:520px;background:#111a2e;border-radius:14px;color:#e2e8f0\">"
                + "<tr><td style=\"padding:32px\">"
                + "<h1 style=\"margin:0 0 16px;font-size:22px;color:#ffffff\">Reset your password</h1>"
                + "<p style=\"margin:0 0 16px;line-height:1.6\">Hi " + safeName + ", someone asked to reset the password for your MockDraft Pro account.</p>"
                + "<p style=\"margin:24px 0\"><a href=\"" + safeLink + "\" style=\"background:#34d399;color:#04140d;text-decoration:none;"
                + "padding:12px 22px;border-radius:10px;font-weight:bold;display:inline-block\">Choose a new password</a></p>"
                + "<p style=\"margin:0 0 8px;line-height:1.6;color:#94a3b8;font-size:13px\">This link expires in 30 minutes and can be used once.</p>"
                + "<p style=\"margin:0;line-height:1.6;color:#94a3b8;font-size:13px\">If this wasn't you, ignore this email; your password won't change.</p>"
                + "</td></tr></table></td></tr></table></body></html>";
    }
}
