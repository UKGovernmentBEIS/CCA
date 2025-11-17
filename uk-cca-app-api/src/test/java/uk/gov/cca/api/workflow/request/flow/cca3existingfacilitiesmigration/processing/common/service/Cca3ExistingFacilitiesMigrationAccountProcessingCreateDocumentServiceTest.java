package uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.notification.template.constants.CcaDocumentTemplateType;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.workflow.request.core.domain.AccountReferenceData;
import uk.gov.cca.api.workflow.request.core.domain.TargetUnitAccountDetails;
import uk.gov.cca.api.workflow.request.core.transform.DocumentTemplateTransformationMapper;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.activation.domain.Cca3ExistingFacilitiesMigrationAccountProcessingActivationDetails;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.common.domain.Cca3ExistingFacilitiesMigrationAccountProcessingRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.common.domain.Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaDocumentTemplateCommonParamsProvider;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaDocumentTemplateCommonUnderlyingAgreementParamsProvider;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaFileDocumentGeneratorService;
import uk.gov.netz.api.documenttemplate.domain.templateparams.TemplateParams;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.domain.DecisionNotification;

import java.util.Map;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Cca3ExistingFacilitiesMigrationAccountProcessingCreateDocumentServiceTest {

    @InjectMocks
    private Cca3ExistingFacilitiesMigrationAccountProcessingCreateDocumentService service;

    @Mock
    private RequestService requestService;

    @Mock
    private CcaDocumentTemplateCommonUnderlyingAgreementParamsProvider ccaDocumentTemplateCommonUnderlyingAgreementParamsProvider;

    @Mock
    private CcaDocumentTemplateCommonParamsProvider ccaDocumentTemplateCommonParamsProvider;

    @Mock
    private DocumentTemplateTransformationMapper documentTemplateTransformationMapper;

    @Mock
    private CcaFileDocumentGeneratorService ccaFileDocumentGeneratorService;

    @Test
    void createUnderlyingAgreementDocument() {
        final String requestId = "requestId";
        final String signatory = "signatory";
        final TargetUnitAccountDetails accountDetails = TargetUnitAccountDetails.builder().operatorName("name").build();
        final UnderlyingAgreement underlyingAgreement = UnderlyingAgreement.builder().build();
        final String currentDate = "currentDate";

        final Request request = Request.builder()
                .metadata(Cca3ExistingFacilitiesMigrationAccountProcessingRequestMetadata.builder()
                        .accountBusinessId("accountBusinessId")
                        .build())
                .payload(Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload.builder()
                        .accountReferenceData(AccountReferenceData.builder()
                                .targetUnitAccountDetails(accountDetails)
                                .build())
                        .underlyingAgreement(underlyingAgreement)
                        .activationDetails(Cca3ExistingFacilitiesMigrationAccountProcessingActivationDetails.builder().build())
                        .decisionNotification(CcaDecisionNotification.builder()
                                .decisionNotification(DecisionNotification.builder().signatory(signatory).build())
                                .build())
                        .build())
                .build();
        final TemplateParams params = TemplateParams.builder()
                .params(Map.of("una", "una", "accountDetails", "accountDetails"))
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(documentTemplateTransformationMapper.formatCurrentDate()).thenReturn(currentDate);
        when(ccaDocumentTemplateCommonUnderlyingAgreementParamsProvider
                .constructTemplateParams(underlyingAgreement, currentDate, SchemeVersion.CCA_3, 1))
                .thenReturn(Map.of("una", "una"));
        when(ccaDocumentTemplateCommonParamsProvider.constructTargetUnitDetailsParams(accountDetails))
                .thenReturn(Map.of("accountDetails", "accountDetails"));

        // Invoke
        service.createUnderlyingAgreementDocument(requestId);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(documentTemplateTransformationMapper, times(1)).formatCurrentDate();
        verify(ccaDocumentTemplateCommonUnderlyingAgreementParamsProvider, times(1))
                .constructTemplateParams(underlyingAgreement, currentDate, SchemeVersion.CCA_3, 1);
        verify(ccaDocumentTemplateCommonParamsProvider, times(1))
                .constructTargetUnitDetailsParams(accountDetails);
        verify(ccaFileDocumentGeneratorService, times(1)).generateAsync(request, signatory,
                CcaDocumentTemplateType.UNDERLYING_AGREEMENT_CCA3, params,
                "accountBusinessId CCA3 Underlying Agreement v1.pdf");
    }

    @Test
    void createUnderlyingAgreementDocument_proposed() {
        final String requestId = "requestId";
        final String signatory = "signatory";
        final TargetUnitAccountDetails accountDetails = TargetUnitAccountDetails.builder().operatorName("name").build();
        final UnderlyingAgreement underlyingAgreement = UnderlyingAgreement.builder().build();
        final String currentDate = "currentDate";

        final Request request = Request.builder()
                .metadata(Cca3ExistingFacilitiesMigrationAccountProcessingRequestMetadata.builder()
                        .accountBusinessId("accountBusinessId")
                        .build())
                .payload(Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload.builder()
                        .defaultSignatory(signatory)
                        .accountReferenceData(AccountReferenceData.builder()
                                .targetUnitAccountDetails(accountDetails)
                                .build())
                        .underlyingAgreement(underlyingAgreement)
                        .build())
                .build();
        final TemplateParams params = TemplateParams.builder()
                .params(Map.of("una", "una", "accountDetails", "accountDetails"))
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(documentTemplateTransformationMapper.formatCurrentDate()).thenReturn(currentDate);
        when(ccaDocumentTemplateCommonUnderlyingAgreementParamsProvider
                .constructTemplateParams(underlyingAgreement, currentDate, SchemeVersion.CCA_3, 1))
                .thenReturn(Map.of("una", "una"));
        when(ccaDocumentTemplateCommonParamsProvider.constructTargetUnitDetailsParams(accountDetails))
                .thenReturn(Map.of("accountDetails", "accountDetails"));

        // Invoke
        service.createUnderlyingAgreementDocument(requestId);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(documentTemplateTransformationMapper, times(1)).formatCurrentDate();
        verify(ccaDocumentTemplateCommonUnderlyingAgreementParamsProvider, times(1))
                .constructTemplateParams(underlyingAgreement, currentDate, SchemeVersion.CCA_3, 1);
        verify(ccaDocumentTemplateCommonParamsProvider, times(1))
                .constructTargetUnitDetailsParams(accountDetails);
        verify(ccaFileDocumentGeneratorService, times(1)).generateAsync(request, signatory,
                CcaDocumentTemplateType.UNDERLYING_AGREEMENT_CCA3, params,
                "accountBusinessId CCA3 Underlying Agreement v1 [proposed].pdf");
    }
}
