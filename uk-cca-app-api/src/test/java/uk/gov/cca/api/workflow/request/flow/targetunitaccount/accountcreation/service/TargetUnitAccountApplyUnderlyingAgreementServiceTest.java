package uk.gov.cca.api.workflow.request.flow.targetunitaccount.accountcreation.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.TargetUnitAccountOperatorType;
import uk.gov.cca.api.account.domain.TargetUnitAccountStatus;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDetailsDTO;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.core.service.AccountReferenceDetailsService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementRequestPayload;
import uk.gov.netz.api.workflow.request.StartProcessRequestService;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TargetUnitAccountApplyUnderlyingAgreementServiceTest {

    @InjectMocks
    private TargetUnitAccountApplyUnderlyingAgreementService installationAccountPermitIssuanceService;

    @Mock
    private AccountReferenceDetailsService accountReferenceDetailsService;

    @Mock
    private StartProcessRequestService startProcessRequestService;

    @Test
    void executeTest() {
        Long accountId = 1L;
        String businessId = "businessId";
        TargetUnitAccountDetailsDTO targetUnitAccountDTO = TargetUnitAccountDetailsDTO.builder()
                .id(accountId)
                .businessId("businessId")
                .createdBy("createdBy")
                .status(TargetUnitAccountStatus.NEW)
                .name("Name")
                .operatorType(TargetUnitAccountOperatorType.LIMITED_COMPANY)
                .build();

        RequestParams requestParams = RequestParams.builder()
                .type(CcaRequestType.UNDERLYING_AGREEMENT)
                .accountId(accountId)
                .requestPayload(UnderlyingAgreementRequestPayload.builder()
                        .payloadType(CcaRequestPayloadType.UNDERLYING_AGREEMENT_REQUEST_PAYLOAD)
                        .businessId(businessId)
                        .sectorUserAssignee("createdBy")
                        .build())
                .requestMetadata(UnderlyingAgreementRequestMetadata.builder()
                        .type(CcaRequestMetadataType.UNDERLYING_AGREEMENT)
                        .build())
                .build();


        when(accountReferenceDetailsService.getTargetUnitAccountDetails(accountId)).thenReturn(targetUnitAccountDTO);

        // Invoke
        installationAccountPermitIssuanceService.execute(accountId);

        // Verify
        verify(accountReferenceDetailsService, times(1)).getTargetUnitAccountDetails(accountId);
        verify(startProcessRequestService, times(1)).startProcess(requestParams);
    }
}
