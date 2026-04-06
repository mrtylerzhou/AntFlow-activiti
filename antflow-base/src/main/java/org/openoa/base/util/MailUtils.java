package org.openoa.base.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.vo.MailInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class MailUtils {

    @Value("${message.email.account:none}")
    private String account;

    @Autowired
    private JavaMailSender mailSender;

    /**
     * 通过对象发送邮件
     */
    public void doSendMail(MailInfo mail) {
        sendMailMain(mail);
    }

    /**
     * 通过对象发送邮件
     */
    public void sendMail(MailInfo mail) {
        sendMailMain(mail);
    }

    /**
     * 批量发送邮件
     */
    public void doSendMailBatch(List<MailInfo> mailInfos) {
        sendMailBatchMain(mailInfos);
    }

    /**
     * 批量发送邮件
     */
    public void sendMailBatch(List<MailInfo> mailInfos) {
        sendMailBatchMain(mailInfos);
    }

    /**
     * 批量发送邮件实现
     */
    public void sendMailBatchMain(List<MailInfo> mailInfos) {
        mailInfos.forEach(this::sendMailMain);
    }

    /**
     * 发送邮件实现
     */
    private void sendMailMain(MailInfo mail) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            // multipart=true 以支持附件
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(account);

            // 标题截断
            String title = mail.getTitle();
            int titleLimit = 130;
            if (title != null && title.length() >= titleLimit) {
                title = title.substring(0, titleLimit) + "...";
            }
            helper.setSubject(StringUtils.defaultString(title));

            // 收件人
            if (StringUtils.isNotEmpty(mail.getReceiver())) {
                helper.addTo(mail.getReceiver());
            }
            if (!ObjectUtils.isEmpty(mail.getReceivers())) {
                for (String receiver : mail.getReceivers()) {
                    helper.addTo(receiver);
                }
            }

            // 正文（HTML）
            String content = Optional.ofNullable(mail.getContent()).orElse(StringUtils.EMPTY);
            helper.setText(StringUtils.EMPTY, content);

            // 抄送
            if (!ObjectUtils.isEmpty(mail.getCc())) {
                helper.setCc(mail.getCc());
            }

            // 附件（File）
            File file = mail.getFile();
            if (file != null) {
                helper.addAttachment(file.getName(), new FileSystemResource(file));
            }

            // 附件（InputStream）
            if (mail.getFileInputStream() != null) {
                helper.addAttachment(
                        StringUtils.defaultString(mail.getFileName(), "attachment"),
                        new InputStreamResource(mail.getFileInputStream())
                );
            }

            mailSender.send(message);
        } catch (MessagingException e) {
            log.error("邮件发送失败", e);
        }
    }
}
