package com.trechina.planocycle.aspect;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.trechina.planocycle.mapper.LogMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

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
    private ThreadPoolTaskExecutor executor;

    //定义切入点
    //通过自定义注解来定义切入点表达式
    //只要加了MonitorLog注解的方法，就是切入点
    @Pointcut("execution(public * com.trechina.planocycle.service..*.*(..))")
    public void point(){

    }


    //定义通知
    @AfterThrowing(pointcut = "point()",throwing = "ex")
    public  void serviceMonitor(JoinPoint joinPoint,Throwable ex) {
        executor.execute(()->{
            //获取目标方法名称
            String methodName = joinPoint.getSignature().getName();
            //获取被代理对象名称
            String targetName = joinPoint.getTarget().getClass().getName();

            //获取传入目标方法的参数对象
            Object[] arguments = joinPoint.getArgs();

            JSONArray jsonArray = new JSONArray();
            jsonArray.addAll(Arrays.asList(arguments));
            //获取class对象
            String s = JSON.toJSONString(ex);
            String params = jsonArray.toString();
            logMapper.saveErrorLog(targetName+"_"+methodName, params,s);
        });
    }





}