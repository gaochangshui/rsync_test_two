package com.trechina.planocycle.utils;

import cn.hutool.extra.mail.MailAccount;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.trechina.planocycle.aspect.LogAspect;
import com.trechina.planocycle.config.MailConfig;
import com.trechina.planocycle.constant.MagicString;
import com.trechina.planocycle.entity.po.ErrorMsg;
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
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.text.MessageFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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
    @Value("${projectIds}")
    private String projectIds;
    @Autowired
    private SysConfigMapper sysConfigMapper;
    @Autowired
    private MstJanMapper mstJanMapper;
    @Autowired
    private RestTemplate restTemplate;

    @Scheduled(cron = "0 0 7 * * ?")
    public void MasterInfoSync(){
        LocalDateTime start = LocalDateTime.now();
        String env = MagicString.ENV;
        if(Strings.isNullOrEmpty(MagicString.ENV)){
            env = sysConfigMapper.selectSycConfig("env");
        }
        logger.info("start sync...");
        Map<String, Object> janInfoResult = mstJanService.syncJanData(env);
        Map<String, Object> tenInfoResult = mstBranchService.syncTenData(env);
        LocalDateTime end = LocalDateTime.now();

        MailAccount account = MailConfig.getMailAccount(!projectIds.equals("nothing"));
        String title = MessageFormat.format("「{0}」マスター データ同期完了", env);

        StringBuilder janResult = new StringBuilder();
        StringBuilder tenResult = new StringBuilder();
        StringBuilder slackJanResult = new StringBuilder();
        StringBuilder slackTenResult = new StringBuilder();

        if(Objects.equals(MapUtils.getInteger(janInfoResult, "code"), ResultEnum.SUCCESS.getCode())){
            List<Map<String, Object>> data = (List<Map<String, Object>>) MapUtils.getObject(janInfoResult, "data");
            for (Map<String, Object> datum : data) {
                String msg = "<p style=\"margin-left: 30px\">{0}-{1}:{2},{3}条</p>";
                String errorMsg = "";
                if(datum.containsKey("error")){
                    msg+="<p style=\"margin-left: 30px\">异常信息: {4}</p>";
                    errorMsg = ((Exception) datum.get("error")).getMessage();
                }

                String companyCd = MapUtils.getString(datum, MagicString.COMPANY_CD);
                String companyName = mstJanMapper.selectCompanyName(companyCd);
                String classCd = MapUtils.getString(datum, "classCd");
                String result = MapUtils.getString(datum, "result");

                janResult.append(MessageFormat.format(msg,companyCd+" "+ Optional.ofNullable(companyName).orElse("") ,
                        classCd, result,
                        MapUtils.getInteger(datum, "count"), errorMsg));

                String resultMsg = MessageFormat.format("【{0}】Plano-Cycle_商品マスタ更新: {1} {2}-{3}: {4}\n", env, companyCd, companyName, classCd,
                        (result.equals("true")?"正常終了、問題無し":"異常発生、更新無し"));
                slackJanResult.append(resultMsg);
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

                String companyCd = MapUtils.getString(datum, MagicString.COMPANY_CD);
                String companyName = mstJanMapper.selectCompanyName(companyCd);
                String classCd = MapUtils.getString(datum, "classCd");
                String result = MapUtils.getString(datum, "result");
                tenResult.append(MessageFormat.format(msg, companyCd+" "+Optional.ofNullable(companyName).orElse(""),
                        classCd, result,
                        MapUtils.getInteger(datum, "count"),errorMsg));

                String resultMsg = MessageFormat.format("【{0}】Plano-Cycle_店舗マスタ更新: {1} {2}-{3}: {4}\n", env, companyCd, companyName, classCd,
                        (result.equals("true")?"正常終了、問題無し":"異常発生、更新無し"));
                slackTenResult.append(resultMsg);
            }
        }

        this.syncSlackNotify(slackJanResult.toString(), slackTenResult.toString());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-mm HH:mm:ss");
        String content = String.format(MailConfig.MAIL_SUCCESS_TEMPLATE, "MasterInfoSync", janResult, tenResult,
                formatter.format(start), formatter.format(end), Duration.between(start, end).toMillis());
        MailUtils.sendEmail(account, MagicString.TO_MAIL, title, content);
        tableTransferService.syncZokuseiMst();
        logger.info("end sync...");
    }

    @Async
    void syncSlackNotify(String slackJanResult, String slackTenResult){
        if(Strings.isNullOrEmpty(MagicString.SLACK_URL)){
            MagicString.SLACK_URL = sysConfigMapper.selectSycConfig("slack_url");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        JSONObject map = new JSONObject();
        map.put("text", slackJanResult+slackTenResult);
        HttpEntity<JSONObject> request = new HttpEntity<>(map, headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(MagicString.SLACK_URL, request, String.class);

        if(responseEntity.getStatusCode().is2xxSuccessful()){
            logger.info("task slack notify send successful");
        }else{
            logger.info("task slack notify send failed");
        }
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

    @Scheduled(cron = "0 0/10 * * * ?")
    public void slackNotify(){
        if(Strings.isNullOrEmpty(MagicString.SLACK_URL)){
            MagicString.SLACK_URL = sysConfigMapper.selectSycConfig("slack_url");
        }

        if(Strings.isNullOrEmpty(MagicString.SLACK_URL)){
            logger.warn("no slack url!!!");
            return;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        if(Strings.isNullOrEmpty(MagicString.ENV)){
            MagicString.ENV = sysConfigMapper.selectSycConfig("env");
        }

        List<ErrorMsg> msgList = logMapper.selectErrorLog();
        String finalEnv = MagicString.ENV;
        msgList.forEach(msg->{
            JSONObject jsonMsg = JSON.parseObject(msg.getErrorMsg());
            JSONObject cause = jsonMsg.getJSONObject("cause");
            if(cause==null){
                String message = jsonMsg.getString("message");
                msg.setErrorMsg(message);
            }else{
                msg.setErrorMsg(cause.getString("message"));
            }

            String message = MessageFormat.format("【{0}】{1} @ERROR「{2}」時間:「{3}」、ユーザー「{4}」、ブラウザ「{5}」",
                    finalEnv, "Plano-Cycle", msg.getErrorMsg(), msg.getCreateTime(), msg.getAuthorCd(), msg.getBrowser());
            JSONObject requestParam = new JSONObject();
            requestParam.put("text", message);

            HttpEntity<JSONObject> request = new HttpEntity<>(requestParam, headers);
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(MagicString.SLACK_URL, request, String.class);

            if(responseEntity.getStatusCode().is2xxSuccessful()){
                logger.info("send successful");
                logMapper.updateErrorLogFlag(msg.getId());
            }else{
                logger.info("send failed");
            }
        });
    }
}
