package uk.gov.cca.api.workflow.request.flow.targetunitaccount.accountcreation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDetailsDTO;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.core.service.AccountReferenceDetailsService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementRequestPayload;
import uk.gov.netz.api.workflow.request.StartProcessRequestService;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;

/**
 * Service for starting UNDERLYING_AGREEMENT workflow process.
 */
@Service
@RequiredArgsConstructor
public class TargetUnitAccountApplyUnderlyingAgreementService {

    private final AccountReferenceDetailsService accountReferenceDetailsService;
    private final StartProcessRequestService startProcessRequestService;

    public void execute(Long accountId) {
        RequestParams requestParams = RequestParams.builder()
                .type(CcaRequestType.UNDERLYING_AGREEMENT)
                .accountId(accountId)
                .requestPayload(initializeUnderlyingAgreementRequestPayload(accountId))
                .requestMetadata(UnderlyingAgreementRequestMetadata.builder()
                        .type(CcaRequestMetadataType.UNDERLYING_AGREEMENT)
                        .build())
                .build();
        startProcessRequestService.startProcess(requestParams);
    }

    private UnderlyingAgreementRequestPayload initializeUnderlyingAgreementRequestPayload(Long accountId) {
        TargetUnitAccountDetailsDTO accountDetails = accountReferenceDetailsService.getTargetUnitAccountDetails(accountId);

        return UnderlyingAgreementRequestPayload.builder()
                .payloadType(CcaRequestPayloadType.UNDERLYING_AGREEMENT_REQUEST_PAYLOAD)
                .businessId(accountDetails.getBusinessId())
                .sectorUserAssignee(accountDetails.getCreatedBy())
                .build();
    }
}
