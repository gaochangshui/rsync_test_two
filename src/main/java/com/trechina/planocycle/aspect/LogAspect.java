package com.trechina.planocycle.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

/**
 * <p>title: com.fengmi.aspect</p>
 * description: 切面
 */
@Aspect
@Component
@EnableAspectJAutoProxy
@Slf4j
public class LogAspect {

    //定义切入点
    //通过自定义注解来定义切入点表达式
    //只要加了MonitorLog注解的方法，就是切入点
    @Pointcut("execution(public * com.trechina.planocycle.service..*.*(..))")
    public void point(){

    }


    //定义通知
    //@Around("point()")
    @AfterThrowing(pointcut = "point()",throwing = "ex")
    public void serviceMonitor(JoinPoint joinPoint,Throwable ex) throws ClassNotFoundException {


        //获取目标方法名称
        String methodName = joinPoint.getSignature().getName();

        //获取被代理对象名称
        String targetName = joinPoint.getTarget().getClass().getName();

        //获取传入目标方法的参数对象
        String arguments = joinPoint.getArgs().toString();
        //获取class对象


        String s = ex.toString();
        System.out.println(11);

    }




}