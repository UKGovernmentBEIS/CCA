package uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.common.utils.ConcurrencyUtils;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.common.domain.Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class Cca3ExistingFacilitiesMigrationAccountProcessingAcceptedGenerateDocumentsService {

    private final RequestService requestService;
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
                    .generateAcceptedOfficialNotice(requestId);

            allFutures = CompletableFuture.allOf(underlyingAgreementDocumentFuture, officialNoticeFuture);
            allFutures.get();

            final FileInfoDTO underlyingAgreementDocument = underlyingAgreementDocumentFuture.get();
            final FileInfoDTO officialNotice = officialNoticeFuture.get();

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
        }catch (InterruptedException e) {
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
