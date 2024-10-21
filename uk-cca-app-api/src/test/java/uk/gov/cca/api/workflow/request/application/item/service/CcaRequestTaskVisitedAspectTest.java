package uk.gov.cca.api.workflow.request.application.item.service;

import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.application.item.service.RequestTaskVisitService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CcaRequestTaskVisitedAspectTest {

    @InjectMocks
    private CcaRequestTaskVisitedAspect ccaRequestTaskVisitedAspect;

    @Mock
    private RequestTaskVisitService requestTaskVisitService;

    @Test
    void createRequestTaskVisitAfterGetTaskItemInfo() {
        JoinPoint joinPoint = Mockito.mock(JoinPoint.class);

        final long taskId = 1L;
        final AppUser appUser = AppUser.builder().userId("user").build();

        when(joinPoint.getArgs()).thenReturn(new Object[]{ taskId, appUser });

        // Invoke
        ccaRequestTaskVisitedAspect.createRequestTaskVisitAfterGetTaskItemInfo(joinPoint);

        // Verify
        verify(joinPoint, times(2)).getArgs();
        verify(requestTaskVisitService, times(1)).create(taskId, "user");
    }
}
