package uk.gov.cca.api.workflow.request.flow.admintermination.submit.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.workflow.request.core.transform.DocumentTemplateTransformationMapper;
import uk.gov.cca.api.workflow.request.flow.admintermination.common.domain.AdminTerminationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.admintermination.common.service.CalculateAdminTerminationWithdrawExpirationRemindersService;
import uk.gov.cca.api.workflow.request.flow.admintermination.submit.domain.AdminTerminationReason;
import uk.gov.cca.api.workflow.request.flow.admintermination.submit.domain.AdminTerminationReasonDetails;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaDocumentTemplateGenerationContextActionType;

import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminTerminationSubmittedDocumentTemplateWorkflowParamsProviderTest {

    @InjectMocks
    private AdminTerminationSubmittedDocumentTemplateWorkflowParamsProvider adminTerminationSubmittedDocumentTemplateWorkflowParamsProvider;

    @Mock
    private CalculateAdminTerminationWithdrawExpirationRemindersService calculateAdminTerminationWithdrawExpirationRemindersService;
    
    @Mock
    private DocumentTemplateTransformationMapper documentTemplateTransformationMapper;

    @Test
    void getContextActionType() {
        assertThat(adminTerminationSubmittedDocumentTemplateWorkflowParamsProvider.getContextActionType())
                .isEqualTo(CcaDocumentTemplateGenerationContextActionType.ADMIN_TERMINATION_FINALISED);
    }

    @Test
    void constructParams() {
        final AdminTerminationReasonDetails reasonDetails = AdminTerminationReasonDetails.builder()
                .reason(AdminTerminationReason.DATA_NOT_PROVIDED)
                .build();
        Map<SchemeVersion, Integer> versionMap = Map.of(SchemeVersion.CCA_2, 1);
        final AdminTerminationRequestPayload requestPayload = AdminTerminationRequestPayload.builder()
                .underlyingAgreementVersionMap(versionMap)
                .adminTerminationReasonDetails(reasonDetails)
                .build();

        final LocalDate expirationDate = LocalDate.of(2020, 1, 3);

        final Map<String, Object> expected = Map.of(
                "reasonDetails", reasonDetails,
                "versionMap", getVersionMap(versionMap),
                "terminationDate", expirationDate.minusDays(1)
        );

        when(calculateAdminTerminationWithdrawExpirationRemindersService.getExpirationDate())
                .thenReturn(expirationDate);
        when(documentTemplateTransformationMapper.constructVersionMap(versionMap)).thenReturn(getVersionMap(versionMap));

        // Invoke
        Map<String, Object> actual = adminTerminationSubmittedDocumentTemplateWorkflowParamsProvider
                .constructParams(requestPayload);

        // Verify
        assertThat(actual).isEqualTo(expected);
        verify(calculateAdminTerminationWithdrawExpirationRemindersService, times(1))
                .getExpirationDate();
    }

    private Map<String, String> getVersionMap(Map<SchemeVersion, Integer> underlyingAgreementVersionMap) {
        return underlyingAgreementVersionMap.entrySet().stream()
                .collect(Collectors.toMap(entry -> entry.getKey().getDescription(), entry -> "v" + entry.getValue()));
    }
}
