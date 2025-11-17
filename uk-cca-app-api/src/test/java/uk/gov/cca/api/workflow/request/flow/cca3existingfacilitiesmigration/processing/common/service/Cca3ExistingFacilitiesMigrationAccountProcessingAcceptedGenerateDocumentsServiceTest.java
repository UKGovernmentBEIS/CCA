package uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.common.domain.Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Cca3ExistingFacilitiesMigrationAccountProcessingAcceptedGenerateDocumentsServiceTest {

    @InjectMocks
    private Cca3ExistingFacilitiesMigrationAccountProcessingAcceptedGenerateDocumentsService service;

    @Mock
    private RequestService requestService;

    @Mock
    private Cca3ExistingFacilitiesMigrationAccountProcessingCreateDocumentService cca3ExistingFacilitiesMigrationAccountProcessingCreateDocumentService;

    @Mock
    private Cca3ExistingFacilitiesMigrationAccountProcessingOfficialNoticeService cca3ExistingFacilitiesMigrationAccountProcessingOfficialNoticeService;

    @Test
    void generateDocuments() {
        final String requestId = "requestId";
        final Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload requestPayload =
                Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload.builder().build();
        final Request request = Request.builder()
                .payload(requestPayload)
                .build();

        final String documentUuid = UUID.randomUUID().toString();
        final FileInfoDTO document = FileInfoDTO.builder().name("una.pdf").uuid(documentUuid).build();
        final FileInfoDTO officialNotice = FileInfoDTO.builder().name("notice.pdf").build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(cca3ExistingFacilitiesMigrationAccountProcessingCreateDocumentService.createUnderlyingAgreementDocument(requestId))
                .thenReturn(CompletableFuture.completedFuture(document));
        when(cca3ExistingFacilitiesMigrationAccountProcessingOfficialNoticeService.generateAcceptedOfficialNotice(requestId))
                .thenReturn(CompletableFuture.completedFuture(officialNotice));

        // Invoke
        service.generateDocuments(requestId);

        // Verify
        assertThat(requestPayload.getUnderlyingAgreementDocument()).isEqualTo(document);
        assertThat(requestPayload.getOfficialNotice()).isEqualTo(officialNotice);
        verify(requestService, times(1)).findRequestById(requestId);
        verify(cca3ExistingFacilitiesMigrationAccountProcessingCreateDocumentService, times(1))
                .createUnderlyingAgreementDocument(requestId);
        verify(cca3ExistingFacilitiesMigrationAccountProcessingOfficialNoticeService, times(1))
                .generateAcceptedOfficialNotice(requestId);
    }

    @Test
    void generateDocuments_request_not_exist() {
        final String requestId = "requestId";

        when(requestService.findRequestById(requestId)).thenThrow(new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class, () ->
                service.generateDocuments(requestId));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
        verify(requestService, times(1)).findRequestById(requestId);
        verifyNoInteractions(cca3ExistingFacilitiesMigrationAccountProcessingCreateDocumentService,
                cca3ExistingFacilitiesMigrationAccountProcessingOfficialNoticeService);
    }

    @Test
    void generateDocuments_throws_internal_server_error_exception() {
        final String requestId = "requestId";
        final Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload requestPayload =
                Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload.builder().build();
        final Request request = Request.builder()
                .payload(requestPayload)
                .build();

        final FileInfoDTO officialNotice = FileInfoDTO.builder().name("notice.pdf").build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(cca3ExistingFacilitiesMigrationAccountProcessingCreateDocumentService.createUnderlyingAgreementDocument(requestId)).thenAnswer(answer -> {
            CompletableFuture<?> future = new CompletableFuture<>();
            future.completeExceptionally(new BusinessException(ErrorCode.DOCUMENT_TEMPLATE_FILE_GENERATION_ERROR, "una.pdf"));
            return future;
        });
        when(cca3ExistingFacilitiesMigrationAccountProcessingOfficialNoticeService.generateAcceptedOfficialNotice(requestId))
                .thenReturn(CompletableFuture.completedFuture(officialNotice));

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class, () ->
                service.generateDocuments(requestId));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.DOCUMENT_TEMPLATE_FILE_GENERATION_ERROR);
        assertThat(requestPayload.getUnderlyingAgreementDocument()).isNull();
        assertThat(requestPayload.getOfficialNotice()).isNull();
        verify(requestService, times(1)).findRequestById(requestId);
        verify(cca3ExistingFacilitiesMigrationAccountProcessingCreateDocumentService, times(1))
                .createUnderlyingAgreementDocument(requestId);
        verify(cca3ExistingFacilitiesMigrationAccountProcessingOfficialNoticeService, times(1))
                .generateAcceptedOfficialNotice(requestId);
    }

    @Test
    void generateDocuments_throws_runtime_exception() {
        final String requestId = "requestId";
        final Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload requestPayload =
                Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload.builder().build();
        final Request request = Request.builder()
                .payload(requestPayload)
                .build();

        final FileInfoDTO officialNotice = FileInfoDTO.builder().name("notice.pdf").build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(cca3ExistingFacilitiesMigrationAccountProcessingCreateDocumentService.createUnderlyingAgreementDocument(requestId)).thenAnswer(answer -> {
            CompletableFuture<?> future = new CompletableFuture<>();
            future.completeExceptionally(new RuntimeException("Runtime exception"));
            return future;
        });
        when(cca3ExistingFacilitiesMigrationAccountProcessingOfficialNoticeService.generateAcceptedOfficialNotice(requestId))
                .thenReturn(CompletableFuture.completedFuture(officialNotice));

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class, () ->
                service.generateDocuments(requestId));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INTERNAL_SERVER);
        assertThat(requestPayload.getUnderlyingAgreementDocument()).isNull();
        assertThat(requestPayload.getOfficialNotice()).isNull();
        verify(requestService, times(1)).findRequestById(requestId);
        verify(cca3ExistingFacilitiesMigrationAccountProcessingCreateDocumentService, times(1))
                .createUnderlyingAgreementDocument(requestId);
        verify(cca3ExistingFacilitiesMigrationAccountProcessingOfficialNoticeService, times(1))
                .generateAcceptedOfficialNotice(requestId);
    }

    @Test
    void generateDocuments_throws_unknown_exception() {
        final String requestId = "requestId";

        when(requestService.findRequestById(requestId)).thenThrow(new RuntimeException("Runtime exception"));

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class, () ->
                service.generateDocuments(requestId));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INTERNAL_SERVER);
        verify(requestService, times(1)).findRequestById(requestId);
        verifyNoInteractions(cca3ExistingFacilitiesMigrationAccountProcessingCreateDocumentService,
                cca3ExistingFacilitiesMigrationAccountProcessingOfficialNoticeService);
    }

    @Test
    void generateDocuments_throws_interrupted_exception() {
        final String requestId = "requestId";
        final Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload requestPayload =
                Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload.builder().build();
        final Request request = Request.builder()
                .payload(requestPayload)
                .build();

        final FileInfoDTO officialNotice = FileInfoDTO.builder().name("notice.pdf").build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(cca3ExistingFacilitiesMigrationAccountProcessingCreateDocumentService.createUnderlyingAgreementDocument(requestId)).thenAnswer(answer -> {
            CompletableFuture<?> future = new CompletableFuture<>();
            Thread.currentThread().interrupt();
            return future;
        });
        when(cca3ExistingFacilitiesMigrationAccountProcessingOfficialNoticeService.generateAcceptedOfficialNotice(requestId))
                .thenReturn(CompletableFuture.completedFuture(officialNotice));

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class, () ->
                service.generateDocuments(requestId));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INTERNAL_SERVER);
        assertThat(requestPayload.getUnderlyingAgreementDocument()).isNull();
        assertThat(requestPayload.getOfficialNotice()).isNull();
        verify(requestService, times(1)).findRequestById(requestId);
        verify(cca3ExistingFacilitiesMigrationAccountProcessingCreateDocumentService, times(1))
                .createUnderlyingAgreementDocument(requestId);
        verify(cca3ExistingFacilitiesMigrationAccountProcessingOfficialNoticeService, times(1))
                .generateAcceptedOfficialNotice(requestId);
    }
}
