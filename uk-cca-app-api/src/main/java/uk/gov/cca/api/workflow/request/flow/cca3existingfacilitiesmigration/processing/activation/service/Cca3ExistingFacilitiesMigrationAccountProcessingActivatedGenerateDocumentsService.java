package uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.activation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.common.utils.ConcurrencyUtils;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementQueryService;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementService;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.common.domain.Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.common.service.Cca3ExistingFacilitiesMigrationAccountProcessingCreateDocumentService;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.common.service.Cca3ExistingFacilitiesMigrationAccountProcessingOfficialNoticeService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class Cca3ExistingFacilitiesMigrationAccountProcessingActivatedGenerateDocumentsService {

    private final RequestService requestService;
    private final UnderlyingAgreementService underlyingAgreementService;
    private final UnderlyingAgreementQueryService underlyingAgreementQueryService;
    private final Cca3ExistingFacilitiesMigrationAccountProcessingCreateDocumentService cca3ExistingFacilitiesMigrationAccountProcessingCreateDocumentService;
    private final Cca3ExistingFacilitiesMigrationAccountProcessingOfficialNoticeService cca3ExistingFacilitiesMigrationAccountProcessingOfficialNoticeService;

    @Transactional
    public void generateDocuments(final String requestId) {
        CompletableFuture<FileInfoDTO> underlyingAgreementDocumentFuture = null;
        CompletableFuture<FileInfoDTO> officialNoticeFuture = null;
        CompletableFuture<Void> allFutures = null;

        try {
            final Request request = requestService.findRequestById(requestId);
            final Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload requestPayload =
                    (Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload) request.getPayload();

            // Generate notice and document
            underlyingAgreementDocumentFuture = cca3ExistingFacilitiesMigrationAccountProcessingCreateDocumentService
                    .createUnderlyingAgreementDocument(requestId);
            officialNoticeFuture = cca3ExistingFacilitiesMigrationAccountProcessingOfficialNoticeService
                    .generateActivatedOfficialNotice(requestId);

            allFutures = CompletableFuture.allOf(underlyingAgreementDocumentFuture, officialNoticeFuture);
            allFutures.get();

            final FileInfoDTO underlyingAgreementDocument = underlyingAgreementDocumentFuture.get();
            final FileInfoDTO officialNotice = officialNoticeFuture.get();

            // Create and save UNA CCA3 document
            Long documentId = underlyingAgreementQueryService
                    .getUnderlyingAgreementByAccountId(request.getAccountId()).getUnderlyingAgreementDocuments().stream()
                    .filter(doc -> SchemeVersion.CCA_3.equals(doc.getSchemeVersion()))
                    .findFirst()
                    .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND))
                    .getId();
            underlyingAgreementService.saveFileDocumentUuid(documentId, underlyingAgreementDocument.getUuid());

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
