package com.trechina.planocycle.config;

import cn.hutool.extra.mail.MailAccount;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.collections4.MapUtils;

import java.util.Map;

public class MailConfig {
    class LocalMailConfig{
        public static final String MAIL = "10218504chen_ke@cn.tre-inc.com";
        public static final String PASS = "chTRE10218504";
        public static final String SMTP = "cn.tre-inc.com";
        public static final boolean IS_SSL = false;
    }

    class CloudMailConfig{
        public final static String MAIL = "planocycle.retailai@gmail.com";
        public static final String PASS = "planocycleqdtre";
        public static final String SMTP = "smtp.gmail.com";
        public static final boolean IS_SSL = true;
    }

    static Map<String, Object> getMailConfig(boolean isCloud){
        if (isCloud) {
            return ImmutableMap.of("MAIL", CloudMailConfig.MAIL, "PASS", CloudMailConfig.PASS,
                    "SMTP", CloudMailConfig.SMTP,"IS_SSL", CloudMailConfig.IS_SSL);
        }

        return ImmutableMap.of("MAIL", LocalMailConfig.MAIL, "PASS", LocalMailConfig.PASS,
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
}
