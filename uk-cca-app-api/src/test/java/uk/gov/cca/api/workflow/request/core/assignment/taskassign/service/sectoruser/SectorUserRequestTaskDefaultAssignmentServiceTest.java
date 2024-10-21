package uk.gov.cca.api.workflow.request.core.assignment.taskassign.service.sectoruser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.account.service.TargetUnitAccountSiteContactService;
import uk.gov.cca.api.authorization.ccaauth.core.service.CcaUserRoleTypeService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.flow.targetunitaccount.accountcreation.domain.TargetUnitAccountCreationRequestPayload;
import uk.gov.netz.api.common.exception.BusinessCheckedException;
import uk.gov.netz.api.workflow.request.core.assignment.requestassign.release.RequestReleaseService;
import uk.gov.netz.api.workflow.request.core.assignment.taskassign.service.RequestTaskAssignmentService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskType;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoInteractions;
import static uk.gov.cca.api.common.domain.CcaRoleTypeConstants.SECTOR_USER;

@ExtendWith(MockitoExtension.class)
public class SectorUserRequestTaskDefaultAssignmentServiceTest {

    @InjectMocks
    private SectorUserRequestTaskDefaultAssignmentService sectorUserRequestTaskDefaultAssignmentService;

    @Mock
    private RequestTaskAssignmentService requestTaskAssignmentService;

    @Mock
    private RequestReleaseService requestReleaseService;

    @Mock
    private CcaUserRoleTypeService ccaUserRoleTypeService;

    @Mock
    private TargetUnitAccountSiteContactService targetUnitAccountSiteContactService;

    @Test
    void assignDefaultAssigneeToTask() throws BusinessCheckedException {
        String requestSectorAssignee = "requestSectorAssignee";
        Request request = Request.builder()
                .accountId(1L)
                .payload(TargetUnitAccountCreationRequestPayload.builder()
                        .payloadType(CcaRequestPayloadType.TARGET_UNIT_ACCOUNT_CREATION_REQUEST_PAYLOAD)
                        .sectorUserAssignee(requestSectorAssignee)
                        .build())
                .build();

        RequestTask requestTask = RequestTask.builder().request(request).type(RequestTaskType.builder().code("requestTaskType").build()).build();

        when(ccaUserRoleTypeService.isUserSectorUser(requestSectorAssignee)).thenReturn(true);

        sectorUserRequestTaskDefaultAssignmentService.assignDefaultAssigneeToTask(requestTask);

        verify(ccaUserRoleTypeService, times(1)).isUserSectorUser(requestSectorAssignee);
        verify(requestTaskAssignmentService, times(1)).assignToUser(requestTask, requestSectorAssignee);
        verifyNoInteractions(requestReleaseService);
    }


    @Test
    void assignDefaultAssigneeToTask_non_sector_assignee_exist() {
        String requestSectorAssignee = "requestSectorAssignee";
        Long accountId = 1L;
        String siteContact = "siteContact";
        Request request = Request.builder()
                .accountId(accountId)
                .payload(TargetUnitAccountCreationRequestPayload.builder()
                        .payloadType(CcaRequestPayloadType.TARGET_UNIT_ACCOUNT_CREATION_REQUEST_PAYLOAD)
                        .sectorUserAssignee(requestSectorAssignee)
                        .build())
                .build();

        RequestTask requestTask = RequestTask.builder().request(request).type(RequestTaskType.builder().code("requestTaskType").build()).build();

        when(ccaUserRoleTypeService.isUserSectorUser(requestSectorAssignee)).thenReturn(false);
        when(targetUnitAccountSiteContactService.findTargetUnitAccountSiteContactByAccountId(accountId)).thenReturn(Optional.of(siteContact));

        sectorUserRequestTaskDefaultAssignmentService.assignDefaultAssigneeToTask(requestTask);

        verify(ccaUserRoleTypeService, times(1)).isUserSectorUser(requestSectorAssignee);
        verify(targetUnitAccountSiteContactService, times(1)).findTargetUnitAccountSiteContactByAccountId(accountId);
        verifyNoInteractions(requestReleaseService);
    }

    @Test
    void getRoleType() {
        assertEquals(SECTOR_USER, sectorUserRequestTaskDefaultAssignmentService.getRoleType());
    }
}
