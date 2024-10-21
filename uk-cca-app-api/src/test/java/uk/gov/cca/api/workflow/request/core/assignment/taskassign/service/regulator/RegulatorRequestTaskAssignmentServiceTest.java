package uk.gov.cca.api.workflow.request.core.assignment.taskassign.service.regulator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.account.domain.CcaEmissionTradingScheme;
import uk.gov.cca.api.account.domain.TargetUnitAccount;
import uk.gov.cca.api.account.service.TargetUnitAccountQueryService;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationQueryService;
import uk.gov.netz.api.common.exception.BusinessCheckedException;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.workflow.request.core.assignment.taskassign.service.RequestTaskAssignmentService;
import uk.gov.netz.api.workflow.request.core.assignment.taskassign.service.RequestTaskReleaseService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.domain.constants.RequestStatuses;
import uk.gov.netz.api.workflow.request.core.repository.RequestTaskRepository;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RegulatorRequestTaskAssignmentServiceTest {

    @InjectMocks
    private CcaRegulatorRequestTaskAssignmentService ccaRegulatorRequestTaskAssignmentService;

    @Mock
    private RequestTaskRepository requestTaskRepository;

    @Mock
    private TargetUnitAccountQueryService targetUnitAccountQueryService;

    @Mock
    private SectorAssociationQueryService sectorAssociationQueryService;

    @Mock
    private RequestTaskAssignmentService requestTaskAssignmentService;

    @Mock
    private RequestTaskReleaseService requestTaskReleaseService;

    @Test
    void assignTasksOfToSiteContactOrRelease() throws BusinessCheckedException {
        String userId = "userId";
        String facilitatorUserId = "facilitatorUserId";
        Long accountId = 1L;
        Long sectorAssociationId = 1L;

        TargetUnitAccount targetUnitAccount = TargetUnitAccount.builder()
                .sectorAssociationId(sectorAssociationId)
                .emissionTradingScheme(CcaEmissionTradingScheme.DUMMY_EMISSION_TRADING_SCHEME)
                .name("accountName")
                .businessId("businessId")
                .build();

        Request request = Request.builder().accountId(accountId).status(RequestStatuses.IN_PROGRESS).build();
        RequestTask requestTask = RequestTask.builder().request(request).assignee(userId).build();

        when(requestTaskRepository.findByAssigneeAndRequestStatus(userId, RequestStatuses.IN_PROGRESS))
                .thenReturn(List.of(requestTask));
        when(targetUnitAccountQueryService.getAccountById(accountId)).thenReturn(targetUnitAccount);
        when(sectorAssociationQueryService.getSectorAssociationFacilitatorUserId(sectorAssociationId)).thenReturn(facilitatorUserId);

        ccaRegulatorRequestTaskAssignmentService.assignTasksToSiteContactOrRelease(userId);

        verify(requestTaskRepository, times(1)).findByAssigneeAndRequestStatus(userId, RequestStatuses.IN_PROGRESS);
        verify(requestTaskAssignmentService, times(1)).assignToUser(requestTask, facilitatorUserId);
        verify(targetUnitAccountQueryService, times(1)).getAccountById(accountId);
        verify(sectorAssociationQueryService, times(1)).getSectorAssociationFacilitatorUserId(sectorAssociationId);
    }

    @Test
    void assignTasksToSiteContactOrRelease_no_tasks_found_for_user() {
        String userId = "userId";

        when(requestTaskRepository.findByAssigneeAndRequestStatus(userId, RequestStatuses.IN_PROGRESS))
                .thenReturn(Collections.emptyList());

        ccaRegulatorRequestTaskAssignmentService.assignTasksToSiteContactOrRelease(userId);

        verifyNoInteractions(targetUnitAccountQueryService, sectorAssociationQueryService, requestTaskAssignmentService, requestTaskReleaseService);
    }

    @Test
    void assignTasksToSiteContactOrRelease_no_site_contact() {
        String userId = "userId";
        Long accountId = 1L;
        Long sectorAssociationId = 1L;

        TargetUnitAccount targetUnitAccount = TargetUnitAccount.builder()
                .sectorAssociationId(sectorAssociationId)
                .emissionTradingScheme(CcaEmissionTradingScheme.DUMMY_EMISSION_TRADING_SCHEME)
                .name("accountName")
                .businessId("businessId")
                .build();

        Request request = Request.builder().accountId(accountId).status(RequestStatuses.IN_PROGRESS).build();
        RequestTask requestTask = RequestTask.builder().request(request).assignee(userId).build();

        when(requestTaskRepository
                .findByAssigneeAndRequestStatus(userId, RequestStatuses.IN_PROGRESS))
                .thenReturn(List.of(requestTask));
        when(targetUnitAccountQueryService.getAccountById(accountId)).thenReturn(targetUnitAccount);
        when(sectorAssociationQueryService.getSectorAssociationFacilitatorUserId(sectorAssociationId)).thenReturn(null);

        ccaRegulatorRequestTaskAssignmentService.assignTasksToSiteContactOrRelease(userId);

        verify(requestTaskReleaseService, times(1)).releaseTaskForced(requestTask);
        verifyNoInteractions(requestTaskAssignmentService);
    }

    @Test
    void assignTasksToSiteContactOrRelease_task_can_not_be_assigned_to_site_contact()
            throws BusinessCheckedException {
        String userId = "userId";
        String facilitatorUserId = "facilitatorUserId";
        Long accountId = 1L;
        Long sectorAssociationId = 1L;

        TargetUnitAccount targetUnitAccount = TargetUnitAccount.builder()
                .sectorAssociationId(sectorAssociationId)
                .emissionTradingScheme(CcaEmissionTradingScheme.DUMMY_EMISSION_TRADING_SCHEME)
                .name("accountName")
                .businessId("businessId")
                .build();

        Request request = Request.builder().accountId(accountId).status(RequestStatuses.IN_PROGRESS).build();
        RequestTask requestTask = RequestTask.builder().request(request).assignee(userId).build();

        when(requestTaskRepository.findByAssigneeAndRequestStatus(userId, RequestStatuses.IN_PROGRESS))
                .thenReturn(List.of(requestTask));
        when(targetUnitAccountQueryService.getAccountById(accountId)).thenReturn(targetUnitAccount);
        when(sectorAssociationQueryService.getSectorAssociationFacilitatorUserId(sectorAssociationId)).thenReturn(facilitatorUserId);

        doThrow(BusinessCheckedException.class).when(requestTaskAssignmentService).assignToUser(requestTask, facilitatorUserId);

        ccaRegulatorRequestTaskAssignmentService.assignTasksToSiteContactOrRelease(userId);

        verify(requestTaskAssignmentService, times(1)).assignToUser(requestTask, facilitatorUserId);
        verify(requestTaskReleaseService, times(1)).releaseTaskForced(requestTask);
        verify(targetUnitAccountQueryService, times(1)).getAccountById(accountId);
        verify(sectorAssociationQueryService, times(1)).getSectorAssociationFacilitatorUserId(sectorAssociationId);
    }

    @Test
    void assignTasksToSiteContactOrRelease_task_can_not_be_assigned_to_site_contact_nor_released()
            throws BusinessCheckedException {
        String userId = "userId";
        String facilitatorUserId = "facilitatorUserId";
        Long accountId = 1L;
        Long sectorAssociationId = 1L;

        TargetUnitAccount targetUnitAccount = TargetUnitAccount.builder()
                .sectorAssociationId(sectorAssociationId)
                .emissionTradingScheme(CcaEmissionTradingScheme.DUMMY_EMISSION_TRADING_SCHEME)
                .name("accountName")
                .businessId("businessId")
                .build();

        Request request = Request.builder().accountId(accountId).status(RequestStatuses.IN_PROGRESS).build();
        RequestTask requestTask = RequestTask.builder().request(request).assignee(userId).build();

        when(requestTaskRepository
                .findByAssigneeAndRequestStatus(userId, RequestStatuses.IN_PROGRESS))
                .thenReturn(List.of(requestTask));
        when(targetUnitAccountQueryService.getAccountById(accountId)).thenReturn(targetUnitAccount);
        when(sectorAssociationQueryService.getSectorAssociationFacilitatorUserId(sectorAssociationId)).thenReturn(facilitatorUserId);

        doThrow(BusinessCheckedException.class).when(requestTaskAssignmentService)
                .assignToUser(requestTask, facilitatorUserId);
        doThrow(BusinessException.class).when(requestTaskReleaseService).releaseTaskForced(requestTask);

        ccaRegulatorRequestTaskAssignmentService.assignTasksToSiteContactOrRelease(userId);

        verify(requestTaskAssignmentService, times(1)).assignToUser(requestTask, facilitatorUserId);
        verify(requestTaskReleaseService, times(1)).releaseTaskForced(requestTask);
        verify(targetUnitAccountQueryService, times(1)).getAccountById(accountId);
        verify(sectorAssociationQueryService, times(1)).getSectorAssociationFacilitatorUserId(sectorAssociationId);
    }
}
