package uk.gov.cca.api.workflow.request.flow.targetunitaccount.accountcreation.service;

import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.stereotype.Service;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDetailsDTO;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.core.service.AccountReferenceDetailsService;
import uk.gov.cca.api.workflow.request.flow.common.configuration.WorkflowSchemeVersionConfig;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementRequestPayload;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.workflow.request.StartProcessRequestService;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;

/**
 * Service for starting UNDERLYING_AGREEMENT workflow process.
 */
@Service
@RequiredArgsConstructor
public class TargetUnitAccountApplyUnderlyingAgreementService {

    private final WorkflowSchemeVersionConfig workflowSchemeVersionConfig;
    private final AccountReferenceDetailsService accountReferenceDetailsService;
    private final StartProcessRequestService startProcessRequestService;

    public void execute(Long accountId) {
        SchemeVersion workflowSchemeVersion = workflowSchemeVersionConfig.getUna();
    	TargetUnitAccountDetailsDTO accountDetails = accountReferenceDetailsService.getTargetUnitAccountDetails(accountId);
    	
        RequestParams requestParams = RequestParams.builder()
                .type(CcaRequestType.UNDERLYING_AGREEMENT)
                .requestResources(Map.of(
                		ResourceType.ACCOUNT, accountId.toString(),
                		CcaResourceType.SECTOR_ASSOCIATION, accountDetails.getSectorAssociationId().toString()
                ))
                .requestPayload(UnderlyingAgreementRequestPayload.builder()
                        .payloadType(CcaRequestPayloadType.UNDERLYING_AGREEMENT_REQUEST_PAYLOAD)
                        .workflowSchemeVersion(workflowSchemeVersion)
                        .businessId(accountDetails.getBusinessId())
                        .sectorUserAssignee(accountDetails.getCreatedBy())
                        .build())
                .requestMetadata(UnderlyingAgreementRequestMetadata.builder()
                        .type(CcaRequestMetadataType.UNDERLYING_AGREEMENT)
                        .workflowSchemeVersion(workflowSchemeVersion)
                        .build())
                .build();
        startProcessRequestService.startProcess(requestParams);
    }
}
