package com.trechina.planocycle.utils;

import cn.hutool.extra.mail.Mail;
import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MailUtils {
    private static Logger logger = LoggerFactory.getLogger(MailUtils.class);

    public static void sendEmail(MailAccount account, String to, String title, String content){
        String send = Mail.create(account).setTos(to)
                .setTitle(title)
                .setContent(content)
                .setHtml(true).send();
        logger.info("to:{}, title:{}, send result:{}", to, title, send);
    }
}
