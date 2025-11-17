package uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.processing.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.common.utils.ConcurrencyUtils;
import uk.gov.cca.api.underlyingagreement.domain.dto.UnderlyingAgreementDTO;
import uk.gov.cca.api.underlyingagreement.domain.dto.UnderlyingAgreementDocumentDTO;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementQueryService;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementService;
import uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.common.domain.Cca2ExtensionNoticeAccountState;
import uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.processing.domain.Cca2ExtensionNoticeAccountProcessingRequestPayload;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class Cca2ExtensionNoticeAccountProcessingGenerateDocumentsService {

    private final UnderlyingAgreementService underlyingAgreementService;
    private final UnderlyingAgreementQueryService underlyingAgreementQueryService;
    private final Cca2ExtensionNoticeAccountProcessingCreateDocumentService cca2ExtensionNoticeCreateDocumentService;
    private final Cca2ExtensionNoticeAccountProcessingOfficialNoticeService cca2ExtensionNoticeAccountProcessingOfficialNoticeService;

    @Transactional
    public void generateDocuments(Request request, final Cca2ExtensionNoticeAccountState accountState) {
        CompletableFuture<FileInfoDTO> underlyingAgreementDocumentFuture = null;
        CompletableFuture<FileInfoDTO> officialNoticeFuture = null;
        CompletableFuture<Void> allFutures = null;

        try {
            // Get CCA2 UNA document
            final UnderlyingAgreementDTO underlyingAgreementDTO = underlyingAgreementQueryService
                    .getUnderlyingAgreementByAccountId(accountState.getAccountId());
            final UnderlyingAgreementDocumentDTO document = underlyingAgreementDTO.getUnderlyingAgreementDocuments().stream()
                    .filter(doc -> SchemeVersion.CCA_2.equals(doc.getSchemeVersion()))
                    .findFirst()
                    .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
            int version = document.getConsolidationNumber() + 1;

            // Generate notice and document
            underlyingAgreementDocumentFuture = cca2ExtensionNoticeCreateDocumentService
                    .createUnderlyingAgreementDocument(request, accountState, version);
            officialNoticeFuture = cca2ExtensionNoticeAccountProcessingOfficialNoticeService
                    .generateExtensionNotice(request, version);

            allFutures = CompletableFuture.allOf(underlyingAgreementDocumentFuture, officialNoticeFuture);
            allFutures.get();

            final FileInfoDTO underlyingAgreementDocument = underlyingAgreementDocumentFuture.get();
            final FileInfoDTO officialNotice = officialNoticeFuture.get();

            final Cca2ExtensionNoticeAccountProcessingRequestPayload requestPayload =
                    (Cca2ExtensionNoticeAccountProcessingRequestPayload) request.getPayload();

            // Update UNA CCA2 document
            underlyingAgreementService.updateFileDocumentById(document.getId(), underlyingAgreementDocument.getUuid());

            // Update request payload
            requestPayload.setUnderlyingAgreementDocument(underlyingAgreementDocument);
            requestPayload.setOfficialNotice(officialNotice);

        } catch (ExecutionException e) {
            Throwable caused = e.getCause();
            if(caused.getClass() == BusinessException.class) {
                throw (BusinessException) caused;
            } else {
                throw new BusinessException(ErrorCode.INTERNAL_SERVER, caused);
            }
        } catch (BusinessException e) {
            throw e;
        } catch (InterruptedException e) {
            Throwable caused = e.getCause();
            Thread.currentThread().interrupt();
            throw new BusinessException(ErrorCode.INTERNAL_SERVER, caused);
        } catch (Exception e) {
            Throwable caused = e.getCause();
            throw new BusinessException(ErrorCode.INTERNAL_SERVER, caused);
        } finally {
            ConcurrencyUtils.completeCompletableFutures(underlyingAgreementDocumentFuture, officialNoticeFuture, allFutures);
        }
    }
}
