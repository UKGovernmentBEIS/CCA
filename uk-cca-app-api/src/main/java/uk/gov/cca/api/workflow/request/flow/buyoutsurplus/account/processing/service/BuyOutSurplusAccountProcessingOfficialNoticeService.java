package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.account.processing.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.notification.template.constants.CcaDocumentTemplateType;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.account.processing.domain.BuyOutSurplusAccountProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaDocumentTemplateGenerationContextActionType;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaFileDocumentGeneratorService;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaFileDocumentUnprocessedGeneratorService;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaOfficialNoticeSendService;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;

@Service
@RequiredArgsConstructor
public class BuyOutSurplusAccountProcessingOfficialNoticeService {

    private final CcaFileDocumentGeneratorService ccaFileDocumentGeneratorService;
    private final CcaOfficialNoticeSendService ccaOfficialNoticeSendService;
    private final CcaFileDocumentUnprocessedGeneratorService ccaFileDocumentUnprocessedGeneratorService;

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
        return ccaFileDocumentUnprocessedGeneratorService.copyTemplateAndSaveFileDocument(
                CcaDocumentTemplateType.REFUND_CLAIM_FORM_BUY_OUT,
                CompetentAuthorityEnum.valueOf(request.getRequestResourcesMap().get(ResourceType.CA)),
                "Buyout Refund Claim Form.docx");
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
