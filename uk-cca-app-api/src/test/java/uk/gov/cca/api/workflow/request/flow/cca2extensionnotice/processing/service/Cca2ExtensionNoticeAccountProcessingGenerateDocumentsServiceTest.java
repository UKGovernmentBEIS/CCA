package uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.processing.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.domain.SchemeVersion;
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

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Cca2ExtensionNoticeAccountProcessingGenerateDocumentsServiceTest {

    @InjectMocks
    private Cca2ExtensionNoticeAccountProcessingGenerateDocumentsService service;

    @Mock
    private UnderlyingAgreementService underlyingAgreementService;

    @Mock
    private UnderlyingAgreementQueryService underlyingAgreementQueryService;

    @Mock
    private Cca2ExtensionNoticeAccountProcessingCreateDocumentService cca2ExtensionNoticeCreateDocumentService;

    @Mock
    private Cca2ExtensionNoticeAccountProcessingOfficialNoticeService cca2ExtensionNoticeAccountProcessingOfficialNoticeService;

    @Test
    void generateDocuments() {
        final Long accountId = 5L;
        final Cca2ExtensionNoticeAccountState accountState = Cca2ExtensionNoticeAccountState.builder()
                .accountId(accountId)
                .build();
        final Cca2ExtensionNoticeAccountProcessingRequestPayload requestPayload =
                Cca2ExtensionNoticeAccountProcessingRequestPayload.builder().build();
        final Request request = Request.builder()
                .payload(requestPayload)
                .build();

        final String documentUuid = UUID.randomUUID().toString();
        final FileInfoDTO document = FileInfoDTO.builder().name("una.pdf").uuid(documentUuid).build();
        final FileInfoDTO officialNotice = FileInfoDTO.builder().name("notice.pdf").build();
        final long documentId = 11;
        final UnderlyingAgreementDTO underlyingAgreementDTO = UnderlyingAgreementDTO.builder()
                .underlyingAgreementDocuments(List.of(
                        UnderlyingAgreementDocumentDTO.builder().id(documentId).schemeVersion(SchemeVersion.CCA_2).consolidationNumber(1).build()
                ))
                .build();

        when(underlyingAgreementQueryService.getUnderlyingAgreementByAccountId(accountId))
                .thenReturn(underlyingAgreementDTO);
        when(cca2ExtensionNoticeCreateDocumentService.createUnderlyingAgreementDocument(request, accountState, 2))
                .thenReturn(CompletableFuture.completedFuture(document));
        when(cca2ExtensionNoticeAccountProcessingOfficialNoticeService.generateExtensionNotice(request, 2))
                .thenReturn(CompletableFuture.completedFuture(officialNotice));

        // Invoke
        service.generateDocuments(request, accountState);

        // Verify
        assertThat(requestPayload.getUnderlyingAgreementDocument()).isEqualTo(document);
        assertThat(requestPayload.getOfficialNotice()).isEqualTo(officialNotice);
        verify(underlyingAgreementQueryService, times(1))
                .getUnderlyingAgreementByAccountId(accountId);
        verify(cca2ExtensionNoticeCreateDocumentService, times(1))
                .createUnderlyingAgreementDocument(request, accountState, 2);
        verify(cca2ExtensionNoticeAccountProcessingOfficialNoticeService, times(1))
                .generateExtensionNotice(request, 2);
        verify(underlyingAgreementService, times(1))
                .updateFileDocumentById(documentId, documentUuid);
    }

    @Test
    void generateDocuments_UNA_doc_not_exist() {
        final Long accountId = 5L;
        final Cca2ExtensionNoticeAccountState accountState = Cca2ExtensionNoticeAccountState.builder()
                .accountId(accountId)
                .build();
        final Cca2ExtensionNoticeAccountProcessingRequestPayload requestPayload =
                Cca2ExtensionNoticeAccountProcessingRequestPayload.builder().build();
        final Request request = Request.builder()
                .payload(requestPayload)
                .build();

        final long documentId = 11;
        final UnderlyingAgreementDTO underlyingAgreementDTO = UnderlyingAgreementDTO.builder()
                .underlyingAgreementDocuments(List.of(
                        UnderlyingAgreementDocumentDTO.builder().id(documentId).schemeVersion(SchemeVersion.CCA_3).consolidationNumber(1).build()
                ))
                .build();

        when(underlyingAgreementQueryService.getUnderlyingAgreementByAccountId(accountId))
                .thenReturn(underlyingAgreementDTO);

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class, () ->
                service.generateDocuments(request, accountState));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
        assertThat(requestPayload.getUnderlyingAgreementDocument()).isNull();
        assertThat(requestPayload.getOfficialNotice()).isNull();
        verify(underlyingAgreementQueryService, times(1))
                .getUnderlyingAgreementByAccountId(accountId);
        verifyNoInteractions(cca2ExtensionNoticeCreateDocumentService, cca2ExtensionNoticeAccountProcessingOfficialNoticeService,
                underlyingAgreementService);
    }

    @Test
    void generateDocuments_throws_internal_server_error_exception() {
        final Long accountId = 5L;
        final Cca2ExtensionNoticeAccountState accountState = Cca2ExtensionNoticeAccountState.builder()
                .accountId(accountId)
                .build();
        final Cca2ExtensionNoticeAccountProcessingRequestPayload requestPayload =
                Cca2ExtensionNoticeAccountProcessingRequestPayload.builder().build();
        final Request request = Request.builder()
                .payload(requestPayload)
                .build();

        final FileInfoDTO officialNotice = FileInfoDTO.builder().name("notice.pdf").build();
        final long documentId = 11;
        final UnderlyingAgreementDTO underlyingAgreementDTO = UnderlyingAgreementDTO.builder()
                .underlyingAgreementDocuments(List.of(
                        UnderlyingAgreementDocumentDTO.builder().id(documentId).schemeVersion(SchemeVersion.CCA_2).consolidationNumber(1).build()
                ))
                .build();

        when(underlyingAgreementQueryService.getUnderlyingAgreementByAccountId(accountId))
                .thenReturn(underlyingAgreementDTO);
        when(cca2ExtensionNoticeCreateDocumentService.createUnderlyingAgreementDocument(request, accountState, 2)).thenAnswer(answer -> {
            CompletableFuture<?> future = new CompletableFuture<>();
            future.completeExceptionally(new BusinessException(ErrorCode.DOCUMENT_TEMPLATE_FILE_GENERATION_ERROR, "una.pdf"));
            return future;
        });
        when(cca2ExtensionNoticeAccountProcessingOfficialNoticeService.generateExtensionNotice(request, 2))
                .thenReturn(CompletableFuture.completedFuture(officialNotice));

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class, () ->
                service.generateDocuments(request, accountState));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.DOCUMENT_TEMPLATE_FILE_GENERATION_ERROR);
        assertThat(requestPayload.getUnderlyingAgreementDocument()).isNull();
        assertThat(requestPayload.getOfficialNotice()).isNull();
        verify(underlyingAgreementQueryService, times(1))
                .getUnderlyingAgreementByAccountId(accountId);
        verify(cca2ExtensionNoticeCreateDocumentService, times(1))
                .createUnderlyingAgreementDocument(request, accountState, 2);
        verify(cca2ExtensionNoticeAccountProcessingOfficialNoticeService, times(1))
                .generateExtensionNotice(request, 2);
        verifyNoInteractions(underlyingAgreementService);
    }

    @Test
    void generateDocuments_throws_runtime_exception() {
        final Long accountId = 5L;
        final Cca2ExtensionNoticeAccountState accountState = Cca2ExtensionNoticeAccountState.builder()
                .accountId(accountId)
                .build();
        final Cca2ExtensionNoticeAccountProcessingRequestPayload requestPayload =
                Cca2ExtensionNoticeAccountProcessingRequestPayload.builder().build();
        final Request request = Request.builder()
                .payload(requestPayload)
                .build();

        final FileInfoDTO officialNotice = FileInfoDTO.builder().name("notice.pdf").build();
        final long documentId = 11;
        final UnderlyingAgreementDTO underlyingAgreementDTO = UnderlyingAgreementDTO.builder()
                .underlyingAgreementDocuments(List.of(
                        UnderlyingAgreementDocumentDTO.builder().id(documentId).schemeVersion(SchemeVersion.CCA_2).consolidationNumber(1).build()
                ))
                .build();

        when(underlyingAgreementQueryService.getUnderlyingAgreementByAccountId(accountId))
                .thenReturn(underlyingAgreementDTO);
        when(cca2ExtensionNoticeCreateDocumentService.createUnderlyingAgreementDocument(request, accountState, 2)).thenAnswer(answer -> {
            CompletableFuture<?> future = new CompletableFuture<>();
            future.completeExceptionally(new RuntimeException("Runtime exception"));
            return future;
        });
        when(cca2ExtensionNoticeAccountProcessingOfficialNoticeService.generateExtensionNotice(request, 2))
                .thenReturn(CompletableFuture.completedFuture(officialNotice));

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class, () ->
                service.generateDocuments(request, accountState));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INTERNAL_SERVER);
        assertThat(requestPayload.getUnderlyingAgreementDocument()).isNull();
        assertThat(requestPayload.getOfficialNotice()).isNull();
        verify(underlyingAgreementQueryService, times(1))
                .getUnderlyingAgreementByAccountId(accountId);
        verify(cca2ExtensionNoticeCreateDocumentService, times(1))
                .createUnderlyingAgreementDocument(request, accountState, 2);
        verify(cca2ExtensionNoticeAccountProcessingOfficialNoticeService, times(1))
                .generateExtensionNotice(request, 2);
        verifyNoInteractions(underlyingAgreementService);
    }

    @Test
    void generateDocuments_throws_unknown_exception() {
        final Long accountId = 5L;
        final Cca2ExtensionNoticeAccountState accountState = Cca2ExtensionNoticeAccountState.builder()
                .accountId(accountId)
                .build();
        final Cca2ExtensionNoticeAccountProcessingRequestPayload requestPayload =
                Cca2ExtensionNoticeAccountProcessingRequestPayload.builder().build();
        final Request request = Request.builder()
                .payload(requestPayload)
                .build();

        when(underlyingAgreementQueryService.getUnderlyingAgreementByAccountId(accountId))
                .thenThrow(new RuntimeException("Runtime exception"));

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class, () ->
                service.generateDocuments(request, accountState));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INTERNAL_SERVER);
        assertThat(requestPayload.getUnderlyingAgreementDocument()).isNull();
        assertThat(requestPayload.getOfficialNotice()).isNull();
        verifyNoInteractions(underlyingAgreementService, cca2ExtensionNoticeCreateDocumentService,
                cca2ExtensionNoticeAccountProcessingOfficialNoticeService);
    }

    @Test
    void generateDocuments_throws_interrupted_exception() {
        final Long accountId = 5L;
        final Cca2ExtensionNoticeAccountState accountState = Cca2ExtensionNoticeAccountState.builder()
                .accountId(accountId)
                .build();
        final Cca2ExtensionNoticeAccountProcessingRequestPayload requestPayload =
                Cca2ExtensionNoticeAccountProcessingRequestPayload.builder().build();
        final Request request = Request.builder()
                .payload(requestPayload)
                .build();

        final FileInfoDTO officialNotice = FileInfoDTO.builder().name("notice.pdf").build();
        final long documentId = 11;
        final UnderlyingAgreementDTO underlyingAgreementDTO = UnderlyingAgreementDTO.builder()
                .underlyingAgreementDocuments(List.of(
                        UnderlyingAgreementDocumentDTO.builder().id(documentId).schemeVersion(SchemeVersion.CCA_2).consolidationNumber(1).build()
                ))
                .build();

        when(underlyingAgreementQueryService.getUnderlyingAgreementByAccountId(accountId))
                .thenReturn(underlyingAgreementDTO);
        when(cca2ExtensionNoticeCreateDocumentService.createUnderlyingAgreementDocument(request, accountState, 2)).thenAnswer(answer -> {
            CompletableFuture<?> future = new CompletableFuture<>();
            Thread.currentThread().interrupt();
            return future;
        });
        when(cca2ExtensionNoticeAccountProcessingOfficialNoticeService.generateExtensionNotice(request, 2))
                .thenReturn(CompletableFuture.completedFuture(officialNotice));

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class, () ->
                service.generateDocuments(request, accountState));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INTERNAL_SERVER);
        assertThat(requestPayload.getUnderlyingAgreementDocument()).isNull();
        assertThat(requestPayload.getOfficialNotice()).isNull();
        verify(underlyingAgreementQueryService, times(1))
                .getUnderlyingAgreementByAccountId(accountId);
        verify(cca2ExtensionNoticeCreateDocumentService, times(1))
                .createUnderlyingAgreementDocument(request, accountState, 2);
        verify(cca2ExtensionNoticeAccountProcessingOfficialNoticeService, times(1))
                .generateExtensionNotice(request, 2);
        verifyNoInteractions(underlyingAgreementService);
    }
}
