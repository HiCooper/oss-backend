package com.berry.oss.service;

import com.berry.oss.aop.log.LogMethodExecutionInfo;
import com.berry.oss.config.AsyncConfiguration;
import com.berry.oss.config.GlobalProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

/**
 * Service for sending emails.
 * <p>
 * We use the @Async annotation to send emails asynchronously.
 *
 * @author xueancao
 */
@ConditionalOnBean(AsyncConfiguration.class)
@Service
public class MailService {

    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    private static final String USER = "user";

    private static final String BASE_URL = "baseUrl";

    private final GlobalProperties globalProperties;

    private final JavaMailSender javaMailSender;

    private final MessageSource messageSource;

    private final TemplateEngine templateEngine;

    @Autowired
    public MailService(GlobalProperties globalProperties, JavaMailSender javaMailSender,
                       MessageSource messageSource, TemplateEngine templateEngine) {
        this.globalProperties = globalProperties;
        this.javaMailSender = javaMailSender;
        this.messageSource = messageSource;
        this.templateEngine = templateEngine;
    }

    @Async(value = "taskExecutor")
    public void sendActivationEmail(MailTestUser user) {
        log.debug("Sending activation email to '{}'", user.getEmail());
        sendEmailFromTemplate(user, "mail/activationEmail", "email.activation.title");
    }

    @Async(value = "taskExecutor")
    public void sendCreationEmail(MailTestUser user) {
        log.debug("Sending creation email to '{}'", user.getEmail());
        sendEmailFromTemplate(user, "mail/creationEmail", "email.activation.title");
    }

    @Async(value = "taskExecutor")
    public void sendPasswordResetMail(MailTestUser user) {
        log.debug("Sending password reset email to '{}'", user.getEmail());
        sendEmailFromTemplate(user, "mail/passwordResetEmail", "email.reset.title");
    }

    private void sendEmailFromTemplate(MailTestUser user, String templateName, String titleKey) {
        Locale locale = Locale.forLanguageTag(user.getLangKey());
        Context context = new Context(locale);
        context.setVariable(USER, user);
        context.setVariable(BASE_URL, globalProperties.getMail().getBaseUrl());
        String content = templateEngine.process(templateName, context);
        String subject = messageSource.getMessage(titleKey, null, locale);
        sendEmail(user.getEmail(), subject, content, false, true);

    }

    @LogMethodExecutionInfo
    @Async(value = "taskExecutor")
    public void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
        System.out.println(Thread.currentThread().getName());
        log.debug("Send email[multipart '{}' and html '{}'] to '{}' with subject '{}'", isMultipart, isHtml, to, subject);

        // Prepare message using a Spring helper
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, StandardCharsets.UTF_8.name());
            message.setTo(to);
            message.setFrom(globalProperties.getMail().getFrom());
            message.setSubject(subject);
            message.setText(content, isHtml);
            javaMailSender.send(mimeMessage);
            log.debug("Sent email to User '{}'", to);
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.warn("Email could not be sent to user '{}'", to, e);
            } else {
                log.warn("Email could not be sent to user '{}': {}", to, e.getMessage());
            }
        }
    }

}
