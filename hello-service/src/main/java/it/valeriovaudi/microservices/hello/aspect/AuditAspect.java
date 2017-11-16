package it.valeriovaudi.microservices.hello.aspect;


import com.netflix.spectator.api.Registry;
import com.netflix.spectator.api.Timer;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Aspect
@Component
public class AuditAspect {

    @Autowired
    private Registry registry;

    @Around(value = "@annotation(audit)")
    public Object audit(ProceedingJoinPoint joinPoint, Audit audit) throws Throwable {
        String auditId = audit.id();
        String methodName = "".equals(auditId) ? joinPoint.getTarget().getClass().getName() : auditId;
        Timer timer = registry.timer(methodName);
        Long start = System.nanoTime();
        Object proceed = joinPoint.proceed();
        Long end = System.nanoTime();
        timer.record(end - start, TimeUnit.NANOSECONDS);
        timer.count();
        return proceed;
    }

}
