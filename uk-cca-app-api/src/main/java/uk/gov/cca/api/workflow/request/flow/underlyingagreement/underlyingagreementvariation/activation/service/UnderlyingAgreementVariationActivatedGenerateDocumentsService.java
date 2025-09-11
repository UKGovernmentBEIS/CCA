package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.activation.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.common.utils.ConcurrencyUtils;
import uk.gov.cca.api.underlyingagreement.domain.dto.UnderlyingAgreementDTO;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementQueryService;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.service.UnderlyingAgreementVariationCreateDocumentService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.service.UnderlyingAgreementVariationOfficialNoticeService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Log4j2
@Service
@RequiredArgsConstructor
public class UnderlyingAgreementVariationActivatedGenerateDocumentsService {

    private final RequestService requestService;
    private final UnderlyingAgreementService underlyingAgreementService;
    private final UnderlyingAgreementQueryService underlyingAgreementQueryService;
    private final UnderlyingAgreementVariationCreateDocumentService underlyingAgreementVariationCreateDocumentService;
    private final UnderlyingAgreementVariationOfficialNoticeService underlyingAgreementVariationOfficialNoticeService;

    @Transactional
    public void generateDocuments(String requestId) {
        CompletableFuture<FileInfoDTO> underlyingAgreementDocumentFuture = null;
        CompletableFuture<FileInfoDTO> officialNoticeFuture = null;
        CompletableFuture<Void> allFutures = null;

        try {
            underlyingAgreementDocumentFuture = underlyingAgreementVariationCreateDocumentService.create(requestId);
            officialNoticeFuture = underlyingAgreementVariationOfficialNoticeService.generateActivatedOfficialNotice(requestId);

            allFutures = CompletableFuture.allOf(underlyingAgreementDocumentFuture, officialNoticeFuture);
            allFutures.get();

            final FileInfoDTO underlyingAgreementDocument = underlyingAgreementDocumentFuture.get();
            final FileInfoDTO officialNotice = officialNoticeFuture.get();

            final Request request = requestService.findRequestById(requestId);
            final UnderlyingAgreementVariationRequestPayload requestPayload = (UnderlyingAgreementVariationRequestPayload) request.getPayload();
            final UnderlyingAgreementDTO underlyingAgreementDTO =
                    underlyingAgreementQueryService.getUnderlyingAgreementByAccountId(request.getAccountId());

            requestPayload.setUnderlyingAgreementDocument(underlyingAgreementDocument);
            requestPayload.setOfficialNotice(officialNotice);
            underlyingAgreementService.saveFileDocumentUuid(underlyingAgreementDTO.getId(), underlyingAgreementDocument.getUuid());
        } catch (ExecutionException e) {
            Throwable caused = e.getCause();
            if (caused.getClass() == BusinessException.class) {
                throw (BusinessException) caused;
            } else {
                log.error(caused.getMessage());
                throw new BusinessException(ErrorCode.INTERNAL_SERVER, caused);
            }
        } catch (InterruptedException e) {
            Throwable caused = e.getCause();
            log.error(e.getMessage());
            Thread.currentThread().interrupt();
            throw new BusinessException(ErrorCode.INTERNAL_SERVER, caused);
        } catch (Exception e) {
            Throwable caused = e.getCause();
            log.error(e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER, caused);
        } finally {
            ConcurrencyUtils.completeCompletableFutures(underlyingAgreementDocumentFuture, officialNoticeFuture, allFutures);
        }
    }
}
