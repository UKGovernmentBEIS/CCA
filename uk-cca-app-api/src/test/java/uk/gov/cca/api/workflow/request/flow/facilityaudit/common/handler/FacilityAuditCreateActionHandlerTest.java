package uk.gov.cca.api.workflow.request.flow.facilityaudit.common.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaRequestParams;
import uk.gov.cca.api.workflow.request.flow.common.service.RequestCreateFacilityAndAccountAndSectorResourcesService;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.common.domain.FacilityAuditRequestPayload;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.workflow.request.StartProcessRequestService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.flow.common.domain.RequestCreateActionEmptyPayload;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.netz.api.common.constants.RoleTypeConstants.REGULATOR;

@ExtendWith(MockitoExtension.class)
class FacilityAuditCreateActionHandlerTest {

    @InjectMocks
    private FacilityAuditCreateActionHandler handler;

    @Mock
    private RequestCreateFacilityAndAccountAndSectorResourcesService requestCreateFacilityAndAccountAndSectorResourcesService;

    @Mock
    private StartProcessRequestService startProcessRequestService;

    @Test
    void process() {
        final Long accountId = 1L;
        final Long sectorAssociationId = 2L;
        final Long facilityId = 3L;
        final String userId = "userId";
        final RequestCreateActionEmptyPayload payload = RequestCreateActionEmptyPayload.builder().build();
        final AppUser appUser = AppUser.builder().userId(userId).roleType(REGULATOR).build();

        final RequestParams requestParams = CcaRequestParams.builder()
                .type(CcaRequestType.FACILITY_AUDIT)
                .requestResources(Map.of(
                        ResourceType.ACCOUNT, accountId.toString(),
                        CcaResourceType.SECTOR_ASSOCIATION, sectorAssociationId.toString(),
                        CcaResourceType.FACILITY, facilityId.toString()
                ))
                .requestPayload(FacilityAuditRequestPayload.builder()
                        .payloadType(CcaRequestPayloadType.FACILITY_AUDIT_REQUEST_PAYLOAD)
                        .regulatorAssignee(userId)
                        .build())
                .build();

        when(requestCreateFacilityAndAccountAndSectorResourcesService.createRequestResources(facilityId)).thenReturn(Map.of(
                ResourceType.ACCOUNT, accountId.toString(),
                CcaResourceType.SECTOR_ASSOCIATION, sectorAssociationId.toString(),
                CcaResourceType.FACILITY, facilityId.toString()
        ));
        when(startProcessRequestService.startProcess(requestParams))
                .thenReturn(Request.builder().id("1").build());

        // Invoke
        String result = handler.process(facilityId, payload, appUser);

        // Verify
        assertThat(result).isEqualTo("1");
        verify(requestCreateFacilityAndAccountAndSectorResourcesService, times(1)).createRequestResources(facilityId);
        verify(startProcessRequestService, times(1)).startProcess(requestParams);
    }

    @Test
    void getRequestType() {
        assertThat(handler.getRequestType()).isEqualTo(CcaRequestType.FACILITY_AUDIT);
    }

}
