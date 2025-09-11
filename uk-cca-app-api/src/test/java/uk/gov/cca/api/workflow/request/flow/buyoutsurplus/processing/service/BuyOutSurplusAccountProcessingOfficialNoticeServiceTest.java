package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;
import uk.gov.cca.api.notification.template.constants.CcaDocumentTemplateType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.domain.BuyOutSurplusAccountProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaDocumentTemplateGenerationContextActionType;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaFileDocumentGeneratorService;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaOfficialNoticeSendService;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BuyOutSurplusAccountProcessingOfficialNoticeServiceTest {

    @InjectMocks
    private BuyOutSurplusAccountProcessingOfficialNoticeService buyOutSurplusAccountProcessingOfficialNoticeService;

    @Mock
    private CcaFileDocumentGeneratorService ccaFileDocumentGeneratorService;

    @Mock
    private CcaOfficialNoticeSendService ccaOfficialNoticeSendService;

    @Test
    void generateOfficialNotice_PRIMARY_AWAITING_PAYMENT() {
        final Request request = Request.builder()
                .payload(BuyOutSurplusAccountProcessingRequestPayload.builder()
                        .submitterId("regulator")
                        .build())
                .build();
        final PerformanceDataSubmissionType submissionType = PerformanceDataSubmissionType.PRIMARY;
        final BuyOutSurplusPaymentStatus paymentStatus = BuyOutSurplusPaymentStatus.AWAITING_PAYMENT;
        final String transactionCode = "transaction";

        // Invoke
        buyOutSurplusAccountProcessingOfficialNoticeService
                .generateOfficialNotice(request, submissionType, paymentStatus, transactionCode);

        // Verify
        verify(ccaFileDocumentGeneratorService, times(1)).generate(request, "regulator",
                CcaDocumentTemplateGenerationContextActionType.BUY_OUT_SURPLUS_NOTICE,
                CcaDocumentTemplateType.PRIMARY_BUY_OUT,
                "transaction Primary buy-out MoA.pdf");
    }

    @Test
    void generateOfficialNotice_SECONDARY_AWAITING_PAYMENT() {
        final Request request = Request.builder()
                .payload(BuyOutSurplusAccountProcessingRequestPayload.builder()
                        .submitterId("regulator")
                        .build())
                .build();
        final PerformanceDataSubmissionType submissionType = PerformanceDataSubmissionType.SECONDARY;
        final BuyOutSurplusPaymentStatus paymentStatus = BuyOutSurplusPaymentStatus.AWAITING_PAYMENT;
        final String transactionCode = "transaction";

        // Invoke
        buyOutSurplusAccountProcessingOfficialNoticeService
                .generateOfficialNotice(request, submissionType, paymentStatus, transactionCode);

        // Verify
        verify(ccaFileDocumentGeneratorService, times(1)).generate(request, "regulator",
                CcaDocumentTemplateGenerationContextActionType.BUY_OUT_SURPLUS_NOTICE,
                CcaDocumentTemplateType.SECONDARY_BUY_OUT,
                "transaction Secondary buy-out MoA.pdf");
    }

    @Test
    void generateOfficialNotice_SECONDARY_AWAITING_REFUND() {
        final Request request = Request.builder()
                .payload(BuyOutSurplusAccountProcessingRequestPayload.builder()
                        .submitterId("regulator")
                        .build())
                .build();
        final PerformanceDataSubmissionType submissionType = PerformanceDataSubmissionType.SECONDARY;
        final BuyOutSurplusPaymentStatus paymentStatus = BuyOutSurplusPaymentStatus.AWAITING_REFUND;
        final String transactionCode = "transaction";

        // Invoke
        buyOutSurplusAccountProcessingOfficialNoticeService
                .generateOfficialNotice(request, submissionType, paymentStatus, transactionCode);

        // Verify
        verify(ccaFileDocumentGeneratorService, times(1)).generate(request, "regulator",
                CcaDocumentTemplateGenerationContextActionType.BUY_OUT_SURPLUS_NOTICE,
                CcaDocumentTemplateType.SECONDARY_OVERPAYMENT_BUY_OUT,
                "transaction Secondary buy-out overpayment letter.pdf");
    }

    @Test
    void generateRefundOfficialNotice() {
        final Request request = Request.builder()
                .payload(BuyOutSurplusAccountProcessingRequestPayload.builder()
                        .submitterId("regulator")
                        .build())
                .build();

        // Invoke
        buyOutSurplusAccountProcessingOfficialNoticeService
                .generateRefundOfficialNotice(request);

        // Verify
        verify(ccaFileDocumentGeneratorService, times(1)).generate(request, "regulator",
                CcaDocumentTemplateGenerationContextActionType.BUY_OUT_SURPLUS_NOTICE,
                CcaDocumentTemplateType.REFUND_CLAIM_FORM_BUY_OUT,
                "Buyout Refund Claim Form.docx");
    }

    @Test
    void sendOfficialNotice() {
        final Request request = Request.builder().build();
        final FileInfoDTO officialNotice = FileInfoDTO.builder().build();

        // Invoke
        buyOutSurplusAccountProcessingOfficialNoticeService.sendOfficialNotice(request, officialNotice, null);

        // Verify
        verify(ccaOfficialNoticeSendService, times(1))
                .sendOfficialNotice(List.of(officialNotice), request, new ArrayList<>());
    }
}
