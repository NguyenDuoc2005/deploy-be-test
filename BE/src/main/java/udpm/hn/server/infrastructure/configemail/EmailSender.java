package udpm.hn.server.infrastructure.configemail;


import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import udpm.hn.server.infrastructure.constant.MailConstant;


import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class EmailSender {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;

    @Async
    public void sendEmail(String[] toEmails, String subject, String titleEmail, String bodyEmail) {
        String htmlBody = MailConstant.BODY_STARTS +
                titleEmail +
                MailConstant.BODY_BODY +
                bodyEmail +
                MailConstant.BODY_END;
        sendSimpleMail(toEmails, htmlBody, subject);
    }

    private void sendSimpleMail(String[] recipients, String msgBody, String subject) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.toString());
            ClassPathResource resource = new ClassPathResource(MailConstant.LOGO_PATH);
            mimeMessageHelper.setFrom(sender);
            mimeMessageHelper.setBcc(recipients);
            mimeMessageHelper.setText(msgBody, true);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.addInline("logoImage", resource);
            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            log.error("ERROR WHILE SENDING MAIL: {}", e.getMessage());
        }
    }
}
