package uk.gov.cca.api.workflow.request.flow.admintermination.common.handler;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementQueryService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.admintermination.common.domain.AdminTerminationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.common.service.RequestCreateAccountAndSectorResourcesService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.StartProcessRequestService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestAccountCreateActionHandler;
import uk.gov.netz.api.workflow.request.flow.common.domain.RequestCreateActionEmptyPayload;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;

@Component
@RequiredArgsConstructor
public class AdminTerminationCreateActionHandler
        implements RequestAccountCreateActionHandler<RequestCreateActionEmptyPayload> {

    private final RequestCreateAccountAndSectorResourcesService requestCreateAccountAndSectorResourcesService;
    private final UnderlyingAgreementQueryService underlyingAgreementQueryService;
    private final StartProcessRequestService startProcessRequestService;


    @Override
    public String process(Long accountId, RequestCreateActionEmptyPayload payload, AppUser appUser) {

        RequestParams requestParams = createRequestParams(accountId, appUser);

        Request request = startProcessRequestService.startProcess(requestParams);

        return request.getId();
    }

    private RequestParams createRequestParams(Long accountId, AppUser appUser) {
        return RequestParams.builder()
                .type(CcaRequestType.ADMIN_TERMINATION)
                .requestResources(requestCreateAccountAndSectorResourcesService.createRequestResources(accountId))
                .requestPayload(AdminTerminationRequestPayload.builder()
                        .payloadType(CcaRequestPayloadType.ADMIN_TERMINATION_REQUEST_PAYLOAD)
                        .underlyingAgreementVersionMap(underlyingAgreementQueryService.getConsolidationNumberMapOfActiveSchemes(accountId))
                        .regulatorAssignee(appUser.getUserId())
                        .build())
                .build();
    }

    @Override
    public String getRequestType() {
        return CcaRequestType.ADMIN_TERMINATION;
    }

}
