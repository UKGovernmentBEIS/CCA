package uk.gov.cca.api.workflow.request.flow.facilityaudit.common.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.common.actionhandler.RequestFacilityCreateActionHandler;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaRequestParams;
import uk.gov.cca.api.workflow.request.flow.common.service.RequestCreateFacilityAndAccountAndSectorResourcesService;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.common.domain.FacilityAuditRequestPayload;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.StartProcessRequestService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.flow.common.domain.RequestCreateActionEmptyPayload;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;

@Component
@RequiredArgsConstructor
public class FacilityAuditCreateActionHandler implements RequestFacilityCreateActionHandler<RequestCreateActionEmptyPayload> {

    private final RequestCreateFacilityAndAccountAndSectorResourcesService requestCreateFacilityAndAccountAndSectorResourcesService;
    private final StartProcessRequestService startProcessRequestService;

    @Override
    public String process(Long facilityId, RequestCreateActionEmptyPayload payload, AppUser appUser) {

        RequestParams requestParams = createRequestParams(facilityId, appUser);

        Request request = startProcessRequestService.startProcess(requestParams);

        return request.getId();
    }

    private RequestParams createRequestParams(Long facilityId, AppUser appUser) {
        return CcaRequestParams.builder()
                .type(CcaRequestType.FACILITY_AUDIT)
                .requestResources(requestCreateFacilityAndAccountAndSectorResourcesService.createRequestResources(facilityId))
                .requestPayload(FacilityAuditRequestPayload.builder()
                        .payloadType(CcaRequestPayloadType.FACILITY_AUDIT_REQUEST_PAYLOAD)
                        .regulatorAssignee(appUser.getUserId())
                        .build())
                .build();
    }

    @Override
    public String getRequestType() {
        return CcaRequestType.FACILITY_AUDIT;
    }
}
