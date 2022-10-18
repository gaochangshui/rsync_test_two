package com.trechina.planocycle.config;

import cn.hutool.extra.mail.Mail;
import cn.hutool.extra.mail.MailAccount;
import com.google.common.collect.ImmutableMap;
import com.sun.mail.util.MailSSLSocketFactory;
import org.apache.commons.collections4.MapUtils;

import java.security.GeneralSecurityException;
import java.util.Map;

public class MailConfig {
    static class LocalMailConfig{
        public static final String MAIL1 = "10218504chen_ke";
        public static final String MAIL2 = "cn.tre-inc.com";

        public static final String PASS = "chTRE10218504";

        public static final String SMTP = "cn.tre-inc.com";
        public static final boolean IS_SSL = false;
    }

    static class CloudMailConfig{
        public static final String MAIL1 = "planocycle.retailai";
        public static final String MAIL2 = "gmail.com";
        public static final String PASS = "planocycleqdtre";
        public static final String SMTP = "smtp.gmail.com";
        public static final boolean IS_SSL = true;
    }

    public static void main(String[] args) {
        MailSSLSocketFactory sf = null;
        try {
            sf = new MailSSLSocketFactory();
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
        sf.setTrustAllHosts(true);
        MailAccount mailAccount = getMailAccount(true);
        mailAccount.setPort(465);
        mailAccount.setStarttlsEnable(true);
        mailAccount.setCustomProperty("mail.smtp.ssl.socketFactory", sf);
        Mail.create(mailAccount).setTos("10218504chen_ke@cn.tre-inc.com")
                .setTitle("test").setContent("test").send();
    }

    static Map<String, Object> getMailConfig(boolean isCloud){
        if (isCloud) {
            return ImmutableMap.of("MAIL", CloudMailConfig.MAIL1+"@"+CloudMailConfig.MAIL2, "PASS", CloudMailConfig.PASS,
                    "SMTP", CloudMailConfig.SMTP,"IS_SSL", CloudMailConfig.IS_SSL);
        }

        return ImmutableMap.of("MAIL", LocalMailConfig.MAIL1+"@"+LocalMailConfig.MAIL2, "PASS", LocalMailConfig.PASS,
                "SMTP", LocalMailConfig.SMTP,"IS_SSL", LocalMailConfig.IS_SSL);
    }

    public static MailAccount getMailAccount(boolean isCloud){
        MailAccount account = new MailAccount();
        Map<String, Object> mailMap = MailConfig.getMailConfig(isCloud);
        account.setAuth(true);
        account.setDebug(true);
        account.setSslEnable(MapUtils.getBoolean(mailMap, "IS_SSL"));
        account.setFrom(MapUtils.getString(mailMap, "MAIL"));
        account.setPass(MapUtils.getString(mailMap, "PASS"));
        account.setHost(MapUtils.getString(mailMap, "SMTP"));

        return account;
    }

    public static final String MAIL_EXCEPTION_TEMPLATE = "<!DOCTYPE html>" +
            "<html><body><p>方法名：%s</p>" +
            //"<p>参数：%s</p>" +
            //"<p>用户：%s</p>" +
            //"<p>Cookie：%s</p>" +
            //"<p>浏览器：%s</p>" +
            //"<p>请求者IP:%s</p>" +
            "<p>异常信息：%s</p>" +
            "</body>" +
            "</html>";
    public static final String MAIL_SUCCESS_TEMPLATE = "<!DOCTYPE html>" +
            "<html><body><p>方法名：%s</p>" +
            //"<p>参数：%s</p>" +
            //"<p>用户：%s</p>" +
            //"<p>Cookie：%s</p>" +
            //"<p>浏览器：%s</p>" +
            //"<p>请求者IP:%s</p>" +
            "<p>商品轴マスター：%s</p>" +
            "<p>店铺轴マスター：%s</p>" +
            "<p>开始时间：%s</p>" +
            "<p>结束时间：%s</p>" +
            "<p>同期用时：%s毫秒</p>" +
            "</body>" +
            "</html>";

}
