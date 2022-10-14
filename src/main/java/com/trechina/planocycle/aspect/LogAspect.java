package com.trechina.planocycle.aspect;

import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.servlet.ServletUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.trechina.planocycle.config.MailConfig;
import com.trechina.planocycle.mapper.LogMapper;
import com.trechina.planocycle.mapper.SysConfigMapper;
import com.trechina.planocycle.utils.MailUtils;
import com.trechina.planocycle.utils.VehicleNumCache;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>title: com.fengmi.aspect</p>
 * description: 切面
 */
@Aspect
@Component
@EnableAspectJAutoProxy
@Slf4j
public class LogAspect {

    @Autowired
    private LogMapper logMapper;
    @Autowired
    private SysConfigMapper sysConfigMapper;
    @Autowired
    private VehicleNumCache cache;
    @Autowired
    private ThreadPoolTaskExecutor executor;
    @Autowired
    private HttpSession session;
    @Autowired
    private HttpServletRequest httpServletRequest;
    @Value("${projectIds}")
    private String projectIds;
    //定義切入点
    //只要加了MonitorLog注解的方法，就是切入点
    @Pointcut("execution(public * com.trechina.planocycle.service..*.*(..))")
    public void point(){ // default implementation ignored
         }

    //定義通知
    @AfterThrowing(pointcut = "point()",throwing = "ex")
    public  void serviceMonitor(JoinPoint joinPoint,Throwable ex) {
        //取得＃シュトク＃ターゲット方法名称
        String methodName = joinPoint.getSignature().getName();
        //取得＃シュトク＃被代理オブジェクト象名称
        String targetName = joinPoint.getTarget().getClass().getName();

        Object[] arguments = joinPoint.getArgs();

        JSONArray jsonArray = new JSONArray();
        jsonArray.addAll(Arrays.asList(arguments));
        //取得＃シュトク＃classオブジェクト象
        String s = JSON.toJSONString(ex);
        String params = jsonArray.toString();
        executor.execute(()->{
            logMapper.saveErrorLog(targetName+"_"+methodName, params,s);
        });

        this.errInfoForEmail(ex.getMessage(),methodName,params);
    }

    public  void setTryErrorLog(Exception ex, Object[] o) {
        String s = JSON.toJSONString(ex);
        JSONArray jsonArray = new JSONArray();
        jsonArray.addAll(Arrays.asList(o));
        String params = jsonArray.toString();
        String path = ex.getStackTrace()[0].getFileName()+"_"+ex.getStackTrace()[0].getMethodName();
        StackTraceElement stackTraceElement = ex.getStackTrace()[0];
        executor.execute(()-> logMapper.saveErrorLog(path, params,s));
        this.errInfoForEmail(ex.getMessage(),stackTraceElement.getMethodName(),params);
    }

    public  void errInfoForEmail(String errMsg,String method,String params){
        Cookie[] cookies = httpServletRequest.getCookies();
        Map<String,Object> cookieList = new HashMap<>();
        String env = sysConfigMapper.selectSycConfig("env");
        //3，ブラウザ情報の取得
        String browser = httpServletRequest.getHeader("User-Agent");
        String url = httpServletRequest.getRequestURI();
        String ip = ServletUtil.getClientIP(httpServletRequest);
        String authorCd = session.getAttribute("aud").toString();
        for (Cookie cookie : cookies) {
            cookieList.put(cookie.getName(),cookie.getValue());
        }
        String msg = MessageFormat.format(
                "エラーコード：500\nパス：{0}\nメソッド名：{1}\n パラメータ:{2}\n ユーザー：{3}\n Cookie:{4}\n ブラウザ：{5}\n" +
                        "ユーザーIP:{6}\n 異常メッセージ：{7}",
                url//url
                ,method//method
                ,params//params
                ,authorCd, //user
                cookieList,//cookie
                browser,//browser
                ip//userIP
                ,errMsg);//errInfo
        String title = MessageFormat.format("「${0}」发生异常",env);
        MailAccount account = MailConfig.getMailAccount(!projectIds.equals("nothing"));
        MailUtils.sendEmail(account, "planocyclesystem@cn.tre-inc.com","版本："+env ,msg);
        MailUtils.sendEmail(account, "10215814liu_xinyu@cn.tre-inc.com",title ,msg);
    }
}