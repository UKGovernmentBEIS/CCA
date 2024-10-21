package uk.gov.cca.api.workflow.request.application.item.service;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.application.item.service.RequestTaskVisitService;

@Component
@Aspect
@RequiredArgsConstructor
public class CcaRequestTaskVisitedAspect {

    private final RequestTaskVisitService requestTaskVisitService;

    @AfterReturning("execution(* uk.gov.cca.api.workflow.request.application.task.CcaRequestTaskViewService.getTaskItemInfo(..))")
    void createRequestTaskVisitAfterGetTaskItemInfo(JoinPoint joinPoint) {
        Long taskId = (Long)joinPoint.getArgs()[0];
        AppUser appUser = (AppUser)joinPoint.getArgs()[1];

        this.requestTaskVisitService.create(taskId, appUser.getUserId());
    }
}
