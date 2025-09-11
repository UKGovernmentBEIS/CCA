package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BuyOutSurplusAccountProcessingOfficialNoticeService {

    private final CcaFileDocumentGeneratorService ccaFileDocumentGeneratorService;
    private final CcaOfficialNoticeSendService ccaOfficialNoticeSendService;

    @Transactional
    public FileInfoDTO generateOfficialNotice(final Request request, final PerformanceDataSubmissionType submissionType,
                                              final BuyOutSurplusPaymentStatus paymentStatus, final String transactionCode) {
        if(submissionType.equals(PerformanceDataSubmissionType.PRIMARY)) {
            return generateOfficialNotice(request, CcaDocumentTemplateType.PRIMARY_BUY_OUT,
                    transactionCode + " Primary buy-out MoA.pdf");
        } else if(submissionType.equals(PerformanceDataSubmissionType.SECONDARY) && paymentStatus.equals(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)) {
            return generateOfficialNotice(request, CcaDocumentTemplateType.SECONDARY_BUY_OUT,
                    transactionCode + " Secondary buy-out MoA.pdf");
        } else if(submissionType.equals(PerformanceDataSubmissionType.SECONDARY) && paymentStatus.equals(BuyOutSurplusPaymentStatus.AWAITING_REFUND)) {
            return generateOfficialNotice(request, CcaDocumentTemplateType.SECONDARY_OVERPAYMENT_BUY_OUT,
                    transactionCode + " Secondary buy-out overpayment letter.pdf");
        }

        return FileInfoDTO.builder().build();
    }

    @Transactional
    public FileInfoDTO generateRefundOfficialNotice(final Request request) {
        return generateOfficialNotice(request, CcaDocumentTemplateType.REFUND_CLAIM_FORM_BUY_OUT, "Buyout Refund Claim Form.docx");
    }

    public void sendOfficialNotice(Request request, FileInfoDTO... officialNotices) {
        List<FileInfoDTO> attachments = Arrays.stream(officialNotices)
                .filter(ObjectUtils::isNotEmpty)
                .toList();

        ccaOfficialNoticeSendService.sendOfficialNotice(attachments, request, new ArrayList<>());
    }

    private FileInfoDTO generateOfficialNotice(final Request request, String templateType, String filename) {
        BuyOutSurplusAccountProcessingRequestPayload payload = (BuyOutSurplusAccountProcessingRequestPayload) request.getPayload();

        return ccaFileDocumentGeneratorService.generate(request,
                payload.getSubmitterId(),
                CcaDocumentTemplateGenerationContextActionType.BUY_OUT_SURPLUS_NOTICE,
                templateType,
                filename);
    }
}
