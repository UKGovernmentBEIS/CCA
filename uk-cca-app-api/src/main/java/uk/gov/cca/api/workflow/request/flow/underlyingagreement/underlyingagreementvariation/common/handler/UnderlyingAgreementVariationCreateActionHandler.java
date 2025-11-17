package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.handler;

import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementQueryService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.common.configuration.WorkflowSchemeVersionConfig;
import uk.gov.cca.api.workflow.request.flow.common.service.RequestCreateAccountAndSectorResourcesService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.StartProcessRequestService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestAccountCreateActionHandler;
import uk.gov.netz.api.workflow.request.flow.common.domain.RequestCreateActionEmptyPayload;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;

@Component
@RequiredArgsConstructor
public class UnderlyingAgreementVariationCreateActionHandler
		implements RequestAccountCreateActionHandler<RequestCreateActionEmptyPayload> {

    private final WorkflowSchemeVersionConfig workflowSchemeVersionConfig;
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
        SchemeVersion workflowSchemeVersion = workflowSchemeVersionConfig.getUnaVariation();

		return RequestParams.builder()
                .type(CcaRequestType.UNDERLYING_AGREEMENT_VARIATION)
                .requestResources(requestCreateAccountAndSectorResourcesService.createRequestResources(accountId))
                .requestPayload(UnderlyingAgreementVariationRequestPayload.builder()
                        .payloadType(CcaRequestPayloadType.UNDERLYING_AGREEMENT_VARIATION_REQUEST_PAYLOAD)
                        .workflowSchemeVersion(workflowSchemeVersion)
                        .underlyingAgreementVersionMap(underlyingAgreementQueryService.getConsolidationNumberMap(accountId))
                        .originalUnderlyingAgreementContainer(underlyingAgreementQueryService.getUnderlyingAgreementContainerByAccountId(accountId))
                        .sectorUserAssignee(appUser.getUserId())
                        .build())
                .requestMetadata(UnderlyingAgreementVariationRequestMetadata.builder()
                        .type(CcaRequestMetadataType.UNDERLYING_AGREEMENT_VARIATION)
                        .workflowSchemeVersion(workflowSchemeVersion)
                        .build())
                .build();
	}

    @Override
    public String getRequestType() {
        return CcaRequestType.UNDERLYING_AGREEMENT_VARIATION;
    }
}
