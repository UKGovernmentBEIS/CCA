package uk.gov.cca.api.workflow.request.flow.subsistencefees.targetunitmoa.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.common.utils.ConcurrencyUtils;
import uk.gov.cca.api.subsistencefees.domain.dto.EligibleFacilityDTO;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.targetunitmoa.domain.TargetUnitMoaRequestPayload;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Log4j2
@Service
@RequiredArgsConstructor
public class TargetUnitMoaGenerateDocumentsService {

    private final TargetUnitMoaCreateDocumentService targetUnitMoaCreateDocumentService;

    @Transactional
    public void generateDocuments(Request request, List<EligibleFacilityDTO> facilities) {

        CompletableFuture<FileInfoDTO> targetUnitMoaDocumentFuture = null;

        try {
            targetUnitMoaDocumentFuture = targetUnitMoaCreateDocumentService.create(request, facilities);

            final FileInfoDTO sectorMoaDocument = targetUnitMoaDocumentFuture.get();

            final TargetUnitMoaRequestPayload requestPayload = (TargetUnitMoaRequestPayload) request.getPayload();
            requestPayload.setTargetUnitMoaDocument(sectorMoaDocument);
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
            ConcurrencyUtils.completeCompletableFutures(targetUnitMoaDocumentFuture);
        }
    }
}
