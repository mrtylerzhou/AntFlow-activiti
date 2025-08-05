package org.openoa.base.util;


import jodd.mail.Email;
import jodd.mail.EmailAttachment;
import jodd.mail.MailServer;
import jodd.mail.SendMailSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.openoa.base.vo.MailInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class MailUtils {
    @Value("${message.email.password:none}")
    private String password;
    @Value("${message.email.account:none}")
    private  String account;
    @Value("${message.email.host:none}")
    private  String host;

    /**
     * 通过对象发送邮件
     *
     * @param mail
     */

    public void doSendMail(MailInfo mail) {
        sendMailMain(mail);
    }

    /**
     * 通过对象发送邮件
     *
     * @param mail
     */
    public  void sendMail(MailInfo mail) {
        sendMailMain(mail);
    }

    /**
     * 发送邮件实现
     *
     * @param mail
     */
    private  void sendMailMain(MailInfo mail) {
        SendMailSession session = createSendMailSession();
        Email email = createEmail(mail);
        session.open();
        session.sendMail(email);
        session.close();
    }

    /**
     * 批量发送邮件
     *
     * @param mailInfos
     */

    public void doSendMailBatch(List<MailInfo> mailInfos) {
        sendMailBatchMain(mailInfos);
    }

    /**
     * 批量发送邮件
     *
     * @param mailInfos
     */
    public  void sendMailBatch(List<MailInfo> mailInfos) {
        sendMailBatchMain(mailInfos);
    }

    /**
     * 批量发送邮件实现
     *
     * @param mailInfos
     */
    public  void sendMailBatchMain(List<MailInfo> mailInfos) {
        SendMailSession session = createSendMailSession();
        session.open();
        mailInfos.forEach(o -> {
            Email email = createEmail(o);
            session.sendMail(email);
        });
        session.close();
    }

    /**
     * 创建邮件服务
     *
     * @return
     */
    private  SendMailSession createSendMailSession() {
        return MailServer.create()
                .host(host)
                .auth(account, password)
                .buildSmtpMailServer()
                .createSession();
    }

    /**
     * 创建邮件对象
     *
     * @param mail
     * @return
     */
    private  Email createEmail(MailInfo mail) {

        Integer titleLimit = 130;

        String title = mail.getTitle();

        if (title.length() >= titleLimit) {
            title = StringUtils.join(title.substring(0, titleLimit), "...");
        }

        Email email = Email.create()
                .from(account)
                .subject(title);

        if (!StringUtils.isEmpty(mail.getReceiver())) {
            email.to(mail.getReceiver());
        }

        if (!ObjectUtils.isEmpty(mail.getReceivers())) {
            email.to(mail.getReceivers().toArray(new String[mail.getReceivers().size()]));
        }

        String content = Optional.ofNullable(mail.getContent()).orElse(StringUtils.EMPTY);

        email.textMessage(StringUtils.EMPTY);
        email.htmlMessage(content);

        if (!ObjectUtils.isEmpty(mail.getCc())) {
            email.cc(mail.getCc());
        }

        if (mail.getFile()!=null) {
            File file = mail.getFile();
            email.attachment(EmailAttachment
                    .with()
                    .name(file.getName())
                    .content(file));
        }

        if (mail.getFileInputStream()!=null) {
            try {
                email.attachment(EmailAttachment.with()
                        .name(mail.getFileName())
                        .content(mail.getFileInputStream(), null));
            } catch (IOException e) {
                log.error("邮件发送-附件流处理失败", e);
                return email;
            }
        }

        return email;
    }

}
