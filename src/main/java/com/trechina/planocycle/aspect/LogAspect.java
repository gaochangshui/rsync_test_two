package com.trechina.planocycle.aspect;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.common.base.Strings;
import com.trechina.planocycle.mapper.LogMapper;
import com.trechina.planocycle.mapper.SysConfigMapper;
import com.trechina.planocycle.utils.VehicleNumCache;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;

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

    //定義切入点
    //通オーバー自定義注解来定義切入点表达式
    //只要加了MonitorLog注解的方法，就是切入点
    @Pointcut("execution(public * com.trechina.planocycle.service..*.*(..))")
    public void point(){

    }

    //定義通知
    @AfterThrowing(pointcut = "point()",throwing = "ex")
    public  void serviceMonitor(JoinPoint joinPoint,Throwable ex) {
        executor.execute(()->{
            //取得＃シュトク＃ターゲット方法名称
            String methodName = joinPoint.getSignature().getName();
            //取得＃シュトク＃被代理オブジェクト象名称
            String targetName = joinPoint.getTarget().getClass().getName();

            //取得＃シュトク＃传入ターゲット方法的参数オブジェクト象
            Object[] arguments = joinPoint.getArgs();

            JSONArray jsonArray = new JSONArray();
            jsonArray.addAll(Arrays.asList(arguments));
            //取得＃シュトク＃classオブジェクト象
            String s = JSON.toJSONString(ex);
            String params = jsonArray.toString();
            logMapper.saveErrorLog(targetName+"_"+methodName, params,s);
        });
    }

    public  void setTryErrorLog(Exception ex, Object[] o) {
        executor.execute(()->{
            String s = JSON.toJSONString(ex);
            JSONArray jsonArray = new JSONArray();
            jsonArray.addAll(Arrays.asList(o));
            String params = jsonArray.toString();
            String path = ex.getStackTrace()[0].getFileName()+"_"+ex.getStackTrace()[0].getMethodName();
            logMapper.saveErrorLog(path, params,s);
        });
    }
}