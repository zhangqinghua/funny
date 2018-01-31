package com.funny.aop;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LogAspect {


    @Pointcut("execution(public * com.funny.controller..*.*(..))")
    public void webLog() {
    }


    @Around("webLog()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        long begin = System.nanoTime();
        Object obj = pjp.proceed();
        long end = System.nanoTime();

        System.err.println(String.format("调用Mapper方法：%s，\n参数：%s，\r\n耗时：%d毫秒", pjp.getSignature().toString(), Arrays.toString(pjp.getArgs()), (end - begin) / 1000000));
        return obj;
    }
}
