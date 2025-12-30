package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.notification.template.constants.CcaDocumentTemplateType;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.Determination;
import uk.gov.cca.api.workflow.request.flow.common.service.CcaDecisionNotificationUsersService;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaDocumentTemplateGenerationContextActionType;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaFileDocumentGeneratorService;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaOfficialNoticeSendService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.VariationDetermination;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementVariationOfficialNoticeServiceTest {

    @InjectMocks
    private UnderlyingAgreementVariationOfficialNoticeService service;

    @Mock
    private RequestService requestService;
    @Mock
    private CcaFileDocumentGeneratorService ccaOfficialNoticeGeneratorService;
    @Mock
    private CcaDecisionNotificationUsersService ccaDecisionNotificationUsersService;
    @Mock
    private CcaOfficialNoticeSendService ccaOfficialNoticeSendService;


    @Test
    void generateAcceptedOfficialNotice() {
        final String requestId = "1";
        final CcaDecisionNotification decisionNotification = CcaDecisionNotification.builder()
                .sectorUsers(Set.of("sector1", "sector2"))
                .build();
        final UnderlyingAgreementVariationRequestPayload requestPayload = UnderlyingAgreementVariationRequestPayload.builder()
                .determination(VariationDetermination.builder()
                        .determination(Determination.builder()
                                .reason("Reason")
                                .additionalInformation("EXPLANATION")
                                .build())
                        .build())
                .decisionNotification(decisionNotification)
                .build();
        final Request request = Request.builder()
                .id(requestId)
                .payload(requestPayload)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(ccaOfficialNoticeGeneratorService.generateAsync(request,
                decisionNotification,
                CcaDocumentTemplateGenerationContextActionType.UNDERLYING_AGREEMENT_VARIATION_ACCEPTED,
                CcaDocumentTemplateType.UNDERLYING_AGREEMENT_VARIATION_ACCEPTED,
                "Proposed underlying agreement variation cover letter.pdf"))
                .thenReturn(CompletableFuture.completedFuture(FileInfoDTO.builder().build()));

        requestPayload.setOfficialNotices(List.of(FileInfoDTO.builder().build()));

        // Invoke
        service.generateAcceptedOfficialNotice(request.getId());

        // Verify
        verify(ccaOfficialNoticeGeneratorService, times(1))
                .generateAsync(
                        request,
                        decisionNotification,
                        CcaDocumentTemplateGenerationContextActionType.UNDERLYING_AGREEMENT_VARIATION_ACCEPTED,
                        CcaDocumentTemplateType.UNDERLYING_AGREEMENT_VARIATION_ACCEPTED,
                        "Proposed underlying agreement variation cover letter.pdf");
    }

    @Test
    void generateAndSaveRejectedOfficialNotice() {
        final String requestId = "1";
        final CcaDecisionNotification decisionNotification = CcaDecisionNotification.builder()
                .sectorUsers(Set.of("sector1", "sector2"))
                .build();
        final UnderlyingAgreementVariationRequestPayload requestPayload = UnderlyingAgreementVariationRequestPayload.builder()
                .determination(VariationDetermination.builder()
                        .determination(Determination.builder()
                                .reason("Reason")
                                .additionalInformation("EXPLANATION")
                                .build())
                        .build())
                .decisionNotification(decisionNotification)
                .build();
        final Request request = Request.builder()
                .id(requestId)
                .payload(requestPayload)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        when(ccaOfficialNoticeGeneratorService.generate(request,
                decisionNotification,
                CcaDocumentTemplateGenerationContextActionType.UNDERLYING_AGREEMENT_VARIATION_REJECTED,
                CcaDocumentTemplateType.UNDERLYING_AGREEMENT_VARIATION_REJECTED,
                "Underlying agreement variation rejection notice.pdf"))
                .thenReturn(FileInfoDTO.builder().build());

        requestPayload.setOfficialNotices(List.of(FileInfoDTO.builder().build()));

        // Invoke
        service.generateAndSaveRejectedOfficialNotice(request.getId());

        // Verify
        verify(ccaOfficialNoticeGeneratorService, times(1))
                .generate(
                        request,
                        decisionNotification,
                        CcaDocumentTemplateGenerationContextActionType.UNDERLYING_AGREEMENT_VARIATION_REJECTED,
                        CcaDocumentTemplateType.UNDERLYING_AGREEMENT_VARIATION_REJECTED,
                        "Underlying agreement variation rejection notice.pdf"
                );
    }

    @Test
    void generateAndSaveCompletedOfficialNotice() {
        final String requestId = "1";
        final CcaDecisionNotification decisionNotification = CcaDecisionNotification.builder()
                .sectorUsers(Set.of("sector1", "sector2"))
                .build();
        final UnderlyingAgreementVariationRequestPayload requestPayload = UnderlyingAgreementVariationRequestPayload.builder()
                .determination(VariationDetermination.builder()
                        .determination(Determination.builder()
                                .reason("Reason")
                                .additionalInformation("EXPLANATION")
                                .build())
                        .build())
                .decisionNotification(decisionNotification)
                .build();
        final Request request = Request.builder()
                .id(requestId)
                .payload(requestPayload)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        when(ccaOfficialNoticeGeneratorService.generate(request,
                decisionNotification,
                CcaDocumentTemplateGenerationContextActionType.UNDERLYING_AGREEMENT_VARIATION_COMPLETED,
                CcaDocumentTemplateType.UNDERLYING_AGREEMENT_VARIATION_COMPLETED,
                "Variation acknowledgement letter.pdf"))
                .thenReturn(FileInfoDTO.builder().build());

        requestPayload.setOfficialNotices(List.of(FileInfoDTO.builder().build()));

        // Invoke
        service.generateAndSaveCompletedOfficialNotice(request.getId());

        // Verify
        verify(ccaOfficialNoticeGeneratorService, times(1))
                .generate(
                        request,
                        decisionNotification,
                        CcaDocumentTemplateGenerationContextActionType.UNDERLYING_AGREEMENT_VARIATION_COMPLETED,
                        CcaDocumentTemplateType.UNDERLYING_AGREEMENT_VARIATION_COMPLETED,
                        "Variation acknowledgement letter.pdf"
                );
    }

    @Test
    void generateActivatedOfficialNotice() {
        final String requestId = "1";
        final CcaDecisionNotification decisionNotification = CcaDecisionNotification.builder()
                .sectorUsers(Set.of("sector1", "sector2"))
                .build();
        final UnderlyingAgreementVariationRequestPayload requestPayload = UnderlyingAgreementVariationRequestPayload.builder()
                .determination(VariationDetermination.builder()
                        .determination(Determination.builder()
                                .reason("Reason")
                                .additionalInformation("EXPLANATION")
                                .build())
                        .build())
                .decisionNotification(decisionNotification)
                .build();
        final Request request = Request.builder()
                .id(requestId)
                .payload(requestPayload)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(ccaOfficialNoticeGeneratorService.generateAsync(request,
                decisionNotification,
                CcaDocumentTemplateGenerationContextActionType.UNDERLYING_AGREEMENT_VARIATION_ACTIVATED,
                CcaDocumentTemplateType.UNDERLYING_AGREEMENT_ACTIVATED,
                "Activated underlying agreement cover letter.pdf"))
                .thenReturn(CompletableFuture.completedFuture(FileInfoDTO.builder().build()));

        requestPayload.setOfficialNotices(List.of(FileInfoDTO.builder().build()));

        // Invoke
        service.generateActivatedOfficialNotice(request.getId());

        // Verify
        verify(ccaOfficialNoticeGeneratorService, times(1))
                .generateAsync(
                        request,
                        decisionNotification,
                        CcaDocumentTemplateGenerationContextActionType.UNDERLYING_AGREEMENT_VARIATION_ACTIVATED,
                        CcaDocumentTemplateType.UNDERLYING_AGREEMENT_ACTIVATED,
                        "Activated underlying agreement cover letter.pdf");
    }

    @Test
    void generateTerminationOfficialNotice_CCA2() {
        final SchemeVersion schemeVersion = SchemeVersion.CCA_2;
        final String requestId = "1";
        final CcaDecisionNotification decisionNotification = CcaDecisionNotification.builder()
                .sectorUsers(Set.of("sector1", "sector2"))
                .build();
        final Request request = Request.builder()
                .id(requestId)
                .payload(UnderlyingAgreementVariationRequestPayload.builder()
                        .determination(VariationDetermination.builder()
                                .determination(Determination.builder()
                                        .reason("Reason")
                                        .additionalInformation("EXPLANATION")
                                        .build())
                                .build())
                        .decisionNotification(decisionNotification)
                        .build())
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        // Invoke
        service.generateTerminationOfficialNotice(request.getId(), schemeVersion);

        // Verify
        verify(ccaOfficialNoticeGeneratorService, times(1))
                .generateAsync(
                        request,
                        decisionNotification,
                        CcaDocumentTemplateGenerationContextActionType.UNDERLYING_AGREEMENT_VARIATION_ACTIVATED_SCHEME_TERMINATION_CCA2,
                        CcaDocumentTemplateType.UNDERLYING_AGREEMENT_VARIATION_ACTIVATED_SCHEME_TERMINATION,
                        "CCA2 Underlying agreement variation termination notice.pdf");
    }

    @Test
    void generateTerminationOfficialNotice_CCA3() {
        final SchemeVersion schemeVersion = SchemeVersion.CCA_3;
        final String requestId = "1";
        final CcaDecisionNotification decisionNotification = CcaDecisionNotification.builder()
                .sectorUsers(Set.of("sector1", "sector2"))
                .build();
        final Request request = Request.builder()
                .id(requestId)
                .payload(UnderlyingAgreementVariationRequestPayload.builder()
                        .determination(VariationDetermination.builder()
                                .determination(Determination.builder()
                                        .reason("Reason")
                                        .additionalInformation("EXPLANATION")
                                        .build())
                                .build())
                        .decisionNotification(decisionNotification)
                        .build())
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        // Invoke
        service.generateTerminationOfficialNotice(request.getId(), schemeVersion);

        // Verify
        verify(ccaOfficialNoticeGeneratorService, times(1))
                .generateAsync(
                        request,
                        decisionNotification,
                        CcaDocumentTemplateGenerationContextActionType.UNDERLYING_AGREEMENT_VARIATION_ACTIVATED_SCHEME_TERMINATION_CCA3,
                        CcaDocumentTemplateType.UNDERLYING_AGREEMENT_VARIATION_ACTIVATED_SCHEME_TERMINATION,
                        "CCA3 Underlying agreement variation termination notice.pdf");
    }

    @Test
    void sendOfficialNotice() {

        final String requestId = "1";
        final CcaDecisionNotification decisionNotification = CcaDecisionNotification.builder()
                .sectorUsers(Set.of("sector1", "sector2"))
                .build();
        final FileInfoDTO officialNotice = FileInfoDTO.builder()
                .name("officialNotice")
                .uuid("uuid")
                .build();
        final UnderlyingAgreementVariationRequestPayload requestPayload = UnderlyingAgreementVariationRequestPayload.builder()
                .determination(VariationDetermination.builder()
                        .determination(Determination.builder()
                                .reason("Reason")
                                .additionalInformation("EXPLANATION")
                                .build())
                        .build())
                .decisionNotification(decisionNotification)
                .officialNotices(List.of(officialNotice))
                .build();
        final Request request = Request.builder()
                .id(requestId)
                .payload(requestPayload)
                .build();


        when(requestService.findRequestById(requestId)).thenReturn(request);

        when(ccaDecisionNotificationUsersService.findCCUserEmails(decisionNotification))
                .thenReturn(Collections.singletonList("Email@email.com"));


        requestPayload.setOfficialNotices(List.of(officialNotice));

        // Invoke
        service.sendOfficialNotice(request.getId());

        verify(ccaOfficialNoticeSendService, times(1))
                .sendOfficialNotice(List.of(officialNotice), request,
                        ccaDecisionNotificationUsersService.findCCUserEmails(decisionNotification));
    }
}
