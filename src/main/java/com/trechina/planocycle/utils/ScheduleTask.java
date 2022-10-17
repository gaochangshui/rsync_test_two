package com.trechina.planocycle.utils;

import cn.hutool.extra.mail.MailAccount;
import com.trechina.planocycle.aspect.LogAspect;
import com.trechina.planocycle.config.MailConfig;
import com.trechina.planocycle.constant.MagicString;
import com.trechina.planocycle.enums.ResultEnum;
import com.trechina.planocycle.mapper.*;
import com.trechina.planocycle.service.MstBranchService;
import com.trechina.planocycle.service.MstJanService;
import com.trechina.planocycle.service.TableTransferService;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class ScheduleTask {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private TableTransferService tableTransferService;
    @Autowired
    private PriorityOrderPtsBackupJanMapper ptsBackupJanMapper;
    @Autowired
    private PriorityOrderPtsPatternNameMapper patternNameMapper;
    @Autowired
    private LogMapper logMapper;
    @Autowired
    private MstJanService mstJanService;
    @Autowired
    private MstBranchService mstBranchService;
    @Autowired
    private ProductPowerDataMapper productPowerDataMapper;
    @Autowired
    private LogAspect logAspect;
    @Value("${projectIds}")
    private String projectIds;
    @Autowired
    private SysConfigMapper sysConfigMapper;

    @Scheduled(cron = "0 0 7 * * ?")
    public void MasterInfoSync(){
//        logger.info("定時調度任務--attr表同期開始");
//        tableTransferService.getAttrTransfer();
//        logger.info("定時調度任務--area表同期開始");
//        tableTransferService.getAreasTransfer();
//        logger.info("定時調度任務--branch表同期開始");
//        tableTransferService.getBranchsTransfer();
//        logger.info("定時調度任務--jan表同期開始");
//        tableTransferService.getJansTransfer();
        //logger.info("定時調度任務--janInfo表同期開始");
        //tableTransferService.getJanInfoTransfer();
        LocalDateTime start = LocalDateTime.now();
        String env = sysConfigMapper.selectSycConfig("env");
        logger.info("start sync...");
        Map<String, Object> janInfoResult = mstJanService.syncJanData(env);
        Map<String, Object> tenInfoResult = mstBranchService.syncTenData(env);
        LocalDateTime end = LocalDateTime.now();

        MailAccount account = MailConfig.getMailAccount(!projectIds.equals("nothing"));
        String title = MessageFormat.format("「{0}」マスター データ同期完了", env);

        String janResult = "";
        String tenResult = "";

        if(Objects.equals(MapUtils.getInteger(janInfoResult, "code"), ResultEnum.SUCCESS.getCode())){
            List<Map<String, Object>> data = (List<Map<String, Object>>) MapUtils.getObject(janInfoResult, "data");
            for (Map<String, Object> datum : data) {
                String msg = "<p style=\"margin-left: 30px\">{0}-{1}:{2},{3}条</p>";
                String errorMsg = "";
                if(datum.containsKey("error")){
                    msg+="<p style=\"margin-left: 30px\">异常信息: {4}</p>";
                    errorMsg = ((Exception) datum.get("error")).getMessage();
                }

                janResult += MessageFormat.format(msg, MapUtils.getString(datum, MagicString.COMPANY_CD),
                        MapUtils.getString(datum, "classCd"), MapUtils.getString(datum, "result"),
                        MapUtils.getInteger(datum, "count"), errorMsg);
            }
        }

        if(Objects.equals(MapUtils.getInteger(tenInfoResult, "code"), ResultEnum.SUCCESS.getCode())){
            List<Map<String, Object>> data = (List<Map<String, Object>>) MapUtils.getObject(tenInfoResult, "data");
            for (Map<String, Object> datum : data) {
                String msg = "<p style=\"margin-left: 30px\">{0}-{1}:{2},{3}条</p>";
                String errorMsg = "";
                if(datum.containsKey("error")){
                    msg+="<p style=\"margin-left: 30px\">异常信息: {4}</p>";
                    errorMsg = ((Exception) datum.get("error")).getMessage();
                }

                tenResult += MessageFormat.format(msg, MapUtils.getString(datum, MagicString.COMPANY_CD),
                        MapUtils.getString(datum, "classCd"), MapUtils.getString(datum, "result"),
                        MapUtils.getInteger(datum, "count"),errorMsg);
            }
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-mm HH:mm:ss");
        String content = String.format(MailConfig.MAIL_SUCCESS_TEMPLATE, "MasterInfoSync", janResult, tenResult,
                formatter.format(start), formatter.format(end), Duration.between(start, end).toMillis());
        MailUtils.sendEmail(account, MagicString.TO_MAIL, title, content);
        tableTransferService.syncZokuseiMst();
        logger.info("end sync...");
    }

    @Scheduled(cron = "0 5 0 * * ?")
    public void doDelLog(){
        ptsBackupJanMapper.deleteBackupJan();
        patternNameMapper.deletePtsPatternName();
        logMapper.deleteLog();
        List<String> workTableName = productPowerDataMapper.getWorkTableName();
        for (String s : workTableName) {
            productPowerDataMapper.delWork(s);
        }

    }
}
