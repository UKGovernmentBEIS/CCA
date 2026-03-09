package uk.gov.cca.api.workflow.request.flow.noncompliance.common.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.common.service.RequestCreateAccountAndSectorResourcesService;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceRequestPayload;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.StartProcessRequestService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestAccountCreateActionHandler;
import uk.gov.netz.api.workflow.request.flow.common.domain.RequestCreateActionEmptyPayload;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;

@Component
@RequiredArgsConstructor
public class NonComplianceCreateActionHandler implements RequestAccountCreateActionHandler<RequestCreateActionEmptyPayload> {

    private final RequestCreateAccountAndSectorResourcesService requestCreateAccountAndSectorResourcesService;
    private final StartProcessRequestService startProcessRequestService;

    @Override
    public String process(Long accountId, RequestCreateActionEmptyPayload payload, AppUser appUser) {

        RequestParams requestParams = createRequestParams(accountId, appUser);

        Request request = startProcessRequestService.startProcess(requestParams);

        return request.getId();
    }

    private RequestParams createRequestParams(Long accountId, AppUser appUser) {
        return RequestParams.builder()
                .type(CcaRequestType.NON_COMPLIANCE)
                .requestResources(requestCreateAccountAndSectorResourcesService.createRequestResources(accountId))
                .requestPayload(NonComplianceRequestPayload.builder()
                        .payloadType(CcaRequestPayloadType.NON_COMPLIANCE_REQUEST_PAYLOAD)
                        .regulatorAssignee(appUser.getUserId())
                        .build())
                .build();
    }

    @Override
    public String getRequestType() {
        return CcaRequestType.NON_COMPLIANCE;
    }
}
