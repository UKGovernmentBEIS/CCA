package uk.gov.cca.api.workflow.request.flow.targetunitaccount.accountcreation.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.TargetUnitAccountOperatorType;
import uk.gov.cca.api.account.domain.TargetUnitAccountStatus;
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

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

@ExtendWith(MockitoExtension.class)
class TargetUnitAccountApplyUnderlyingAgreementServiceTest {

    @InjectMocks
    private TargetUnitAccountApplyUnderlyingAgreementService installationAccountPermitIssuanceService;

    @Mock
    private WorkflowSchemeVersionConfig workflowSchemeVersionConfig;

    @Mock
    private AccountReferenceDetailsService accountReferenceDetailsService;

    @Mock
    private StartProcessRequestService startProcessRequestService;

    @Test
    void execute() {
        final Long accountId = 1L;
        final String businessId = "businessId";
        final SchemeVersion workflowSchemeVersion = SchemeVersion.CCA_3;
        final TargetUnitAccountDetailsDTO targetUnitAccountDTO = TargetUnitAccountDetailsDTO.builder()
                .id(accountId)
                .businessId("businessId")
                .createdBy("createdBy")
                .status(TargetUnitAccountStatus.NEW)
                .name("Name")
                .operatorType(TargetUnitAccountOperatorType.LIMITED_COMPANY)
                .sectorAssociationId(1L)
                .build();

        final RequestParams requestParams = RequestParams.builder()
                .type(CcaRequestType.UNDERLYING_AGREEMENT)
                .requestResources(Map.of(
                		ResourceType.ACCOUNT, accountId.toString(),
                		CcaResourceType.SECTOR_ASSOCIATION, targetUnitAccountDTO.getSectorAssociationId().toString()
                		))
                .requestPayload(UnderlyingAgreementRequestPayload.builder()
                        .payloadType(CcaRequestPayloadType.UNDERLYING_AGREEMENT_REQUEST_PAYLOAD)
                        .workflowSchemeVersion(workflowSchemeVersion)
                        .businessId(businessId)
                        .sectorUserAssignee("createdBy")
                        .build())
                .requestMetadata(UnderlyingAgreementRequestMetadata.builder()
                        .type(CcaRequestMetadataType.UNDERLYING_AGREEMENT)
                        .workflowSchemeVersion(workflowSchemeVersion)
                        .build())
                .build();

        when(workflowSchemeVersionConfig.getUna()).thenReturn(workflowSchemeVersion);
        when(accountReferenceDetailsService.getTargetUnitAccountDetails(accountId)).thenReturn(targetUnitAccountDTO);

        // Invoke
        installationAccountPermitIssuanceService.execute(accountId);

        // Verify
        verify(workflowSchemeVersionConfig, times(1)).getUna();
        verify(accountReferenceDetailsService, times(1)).getTargetUnitAccountDetails(accountId);
        verify(startProcessRequestService, times(1)).startProcess(requestParams);
    }
}
