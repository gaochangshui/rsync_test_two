package com.trechina.planocycle.config;

import cn.hutool.extra.mail.MailAccount;
import com.google.common.collect.ImmutableMap;
import com.sun.mail.util.MailSSLSocketFactory;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.GeneralSecurityException;
import java.util.Map;

public class MailConfig {
    private static final Logger logger = LoggerFactory.getLogger(MailConfig.class);
    //static class LocalMailConfig{
    //    public static final String MAIL1 = "10218504chen_ke";
    //    public static final String MAIL2 = "cn.tre-inc.com";
    //
    //    public static final String PASS = "chTRE10218504";
    //
    //    public static final String SMTP = "cn.tre-inc.com";
    //    public static final boolean IS_SSL = false;
    //}

    static class CloudMailConfig{
        public static final String MAIL1 = "planocycle.retailai";
        public static final String MAIL2 = "gmail.com";
        public static final String PASS = "pqsncazynwwiiypc";
        public static final String SMTP = "smtp.gmail.com";
        public static final boolean IS_SSL = true;
    }

    static Map<String, Object> getMailConfig(boolean isCloud){
        if (true) {
            return ImmutableMap.of("MAIL", CloudMailConfig.MAIL1+"@"+CloudMailConfig.MAIL2, "PASS", CloudMailConfig.PASS,
                    "SMTP", CloudMailConfig.SMTP,"IS_SSL", CloudMailConfig.IS_SSL);
        }

        return ImmutableMap.of("MAIL", CloudMailConfig.MAIL1+"@"+CloudMailConfig.MAIL2, "PASS", CloudMailConfig.PASS,
                "SMTP", CloudMailConfig.SMTP,"IS_SSL", CloudMailConfig.IS_SSL);

        //return ImmutableMap.of("MAIL", LocalMailConfig.MAIL1+"@"+LocalMailConfig.MAIL2, "PASS", LocalMailConfig.PASS,
        //        "SMTP", LocalMailConfig.SMTP,"IS_SSL", LocalMailConfig.IS_SSL);
    }

    public static MailAccount getMailAccount(boolean isCloud) {
        MailAccount account = new MailAccount();
        Map<String, Object> mailMap = MailConfig.getMailConfig(true);
        account.setAuth(true);
        account.setDebug(true);
        account.setSslEnable(MapUtils.getBoolean(mailMap, "IS_SSL"));
        account.setFrom(MapUtils.getString(mailMap, "MAIL"));
        account.setPass(MapUtils.getString(mailMap, "PASS"));
        account.setHost(MapUtils.getString(mailMap, "SMTP"));
        account.setPort(465);
        account.setStarttlsEnable(true);
        MailSSLSocketFactory sf = null;
        try {
            sf = new MailSSLSocketFactory();
            sf.setTrustAllHosts(true);
        } catch (GeneralSecurityException e) {
            logger.info("",e);
        }
        account.setCustomProperty("mail.smtp.ssl.socketFactory", sf);

        return account;
    }

    public static final String MAIL_EXCEPTION_TEMPLATE = "<!DOCTYPE html>" +
            "<html><body><p>????????????%s</p>" +
            //"<p>?????????%s</p>" +
            //"<p>?????????%s</p>" +
            //"<p>Cookie???%s</p>" +
            //"<p>????????????%s</p>" +
            //"<p>?????????IP:%s</p>" +
            "<p>???????????????%s</p>" +
            "</body>" +
            "</html>";
    public static final String MAIL_SUCCESS_TEMPLATE = "<!DOCTYPE html>" +
            "<html><body><p>????????????%s</p>" +
            //"<p>?????????%s</p>" +
            //"<p>?????????%s</p>" +
            //"<p>Cookie???%s</p>" +
            //"<p>????????????%s</p>" +
            //"<p>?????????IP:%s</p>" +
            "<p>????????????????????????%s</p>" +
            "<p>????????????????????????%s</p>" +
            "<p>???????????????%s</p>" +
            "<p>???????????????%s</p>" +
            "<p>???????????????%s??????</p>" +
            "</body>" +
            "</html>";

}
