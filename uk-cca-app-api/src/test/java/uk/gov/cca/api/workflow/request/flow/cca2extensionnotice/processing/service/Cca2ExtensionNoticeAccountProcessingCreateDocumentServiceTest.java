package uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.processing.service;

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
import uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.common.domain.Cca2ExtensionNoticeAccountState;
import uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.processing.domain.Cca2ExtensionNoticeAccountProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaDocumentTemplateCommonParamsProvider;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaDocumentTemplateCommonUnderlyingAgreementParamsProvider;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaFileDocumentGeneratorService;
import uk.gov.netz.api.documenttemplate.domain.templateparams.TemplateParams;
import uk.gov.netz.api.workflow.request.core.domain.Request;

import java.time.LocalDate;
import java.util.Map;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Cca2ExtensionNoticeAccountProcessingCreateDocumentServiceTest {

    @InjectMocks
    private Cca2ExtensionNoticeAccountProcessingCreateDocumentService service;

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
        final String signatory = "049445d5-c3f6-4ed3-adf6-ae1608168f99";
        final Cca2ExtensionNoticeAccountState accountState = Cca2ExtensionNoticeAccountState.builder()
                .accountBusinessId("accountBusinessId")
                .build();
        final int version = 2;
        final String fileName = "accountBusinessId CCA2 Underlying Agreement v2.pdf";
        final UnderlyingAgreement una = UnderlyingAgreement.builder().build();
        final TargetUnitAccountDetails accountDetails = TargetUnitAccountDetails.builder()
                .operatorName("operatorName")
                .build();
        final Request request = Request.builder()
                .payload(Cca2ExtensionNoticeAccountProcessingRequestPayload.builder()
                        .defaultSignatory(signatory)
                        .underlyingAgreement(una)
                        .accountReferenceData(AccountReferenceData.builder()
                                .targetUnitAccountDetails(accountDetails)
                                .build())
                        .build())
                .build();

        final String currentDate = LocalDate.now().toString();
        final TemplateParams params = TemplateParams.builder()
                .params(Map.of("targetUnitDetails", "targetUnitDetails", "una", "una"))
                .build();

        when(documentTemplateTransformationMapper.formatCurrentDate()).thenReturn(currentDate);
        when(ccaDocumentTemplateCommonUnderlyingAgreementParamsProvider
                .constructTemplateParams(una, currentDate, SchemeVersion.CCA_2, version))
                .thenReturn(Map.of("una", "una"));
        when(ccaDocumentTemplateCommonParamsProvider
                .constructTargetUnitDetailsParams(accountDetails))
                .thenReturn(Map.of("targetUnitDetails", "targetUnitDetails"));

        // Invoke
        service.createUnderlyingAgreementDocument(request, accountState, version);

        // Verify
        verify(documentTemplateTransformationMapper, times(1)).formatCurrentDate();
        verify(ccaDocumentTemplateCommonUnderlyingAgreementParamsProvider, times(1))
                .constructTemplateParams(una, currentDate, SchemeVersion.CCA_2, version);
        verify(ccaDocumentTemplateCommonParamsProvider, times(1))
                .constructTargetUnitDetailsParams(accountDetails);
        verify(ccaFileDocumentGeneratorService, times(1))
                .generateAsync(request, signatory, CcaDocumentTemplateType.UNDERLYING_AGREEMENT_CCA2, params, SchemeVersion.CCA_2, fileName);
    }
}
