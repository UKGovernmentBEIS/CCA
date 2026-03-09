package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.handler;

import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

import uk.gov.cca.api.common.domain.CcaRoleTypeConstants;
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
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.workflow.request.StartProcessRequestService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestAccountCreateActionHandler;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.domain.RequestCreateActionEmptyPayload;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class UnderlyingAgreementVariationCreateActionHandler implements RequestAccountCreateActionHandler<RequestCreateActionEmptyPayload> {

    private final WorkflowSchemeVersionConfig workflowSchemeVersionConfig;
	private final RequestCreateAccountAndSectorResourcesService requestCreateAccountAndSectorResourcesService;
    private final UnderlyingAgreementQueryService underlyingAgreementQueryService;
	private final StartProcessRequestService startProcessRequestService;
	
	@Override
    public String process(Long accountId, RequestCreateActionEmptyPayload payload, AppUser appUser) {
        final SchemeVersion workflowSchemeVersion = workflowSchemeVersionConfig.getUnaVariation();
        final UnderlyingAgreementVariationRequestPayload requestPayload = createRequestPayload(workflowSchemeVersion, accountId, appUser);

		final RequestParams requestParams = RequestParams.builder()
                .type(CcaRequestType.UNDERLYING_AGREEMENT_VARIATION)
                .requestResources(requestCreateAccountAndSectorResourcesService.createRequestResources(accountId))
                .requestPayload(requestPayload)
                .processVars(Map.of(
                        BpmnProcessConstants.REQUEST_INITIATOR_ROLE_TYPE, appUser.getRoleType()
                ))
                .requestMetadata(UnderlyingAgreementVariationRequestMetadata.builder()
                        .type(CcaRequestMetadataType.UNDERLYING_AGREEMENT_VARIATION)
                        .workflowSchemeVersion(workflowSchemeVersion)
                        .initiatorRoleType(appUser.getRoleType())
                        .build())
                .build();

        Request request = startProcessRequestService.startProcess(requestParams);

        return request.getId();
    }

    @Override
    public String getRequestType() {
        return CcaRequestType.UNDERLYING_AGREEMENT_VARIATION;
    }

    private UnderlyingAgreementVariationRequestPayload createRequestPayload(final SchemeVersion workflowSchemeVersion, final Long accountId,
                                                                            final AppUser appUser) {
        UnderlyingAgreementVariationRequestPayload requestPayload = UnderlyingAgreementVariationRequestPayload.builder()
                .payloadType(CcaRequestPayloadType.UNDERLYING_AGREEMENT_VARIATION_REQUEST_PAYLOAD)
                .workflowSchemeVersion(workflowSchemeVersion)
                .initiatorRoleType(appUser.getRoleType())
                .underlyingAgreementVersionMap(underlyingAgreementQueryService.getConsolidationNumberMap(accountId))
                .originalUnderlyingAgreementContainer(underlyingAgreementQueryService.getUnderlyingAgreementContainerByAccountId(accountId))
                .build();

        switch (appUser.getRoleType()) {
            case CcaRoleTypeConstants.SECTOR_USER -> requestPayload.setSectorUserAssignee(appUser.getUserId());
            case RoleTypeConstants.REGULATOR -> requestPayload.setRegulatorAssignee(appUser.getUserId());
            default -> throw new BusinessException(ErrorCode.REQUEST_CREATE_ACTION_NOT_ALLOWED, appUser.getRoleType());
        }

        return requestPayload;
    }
}
