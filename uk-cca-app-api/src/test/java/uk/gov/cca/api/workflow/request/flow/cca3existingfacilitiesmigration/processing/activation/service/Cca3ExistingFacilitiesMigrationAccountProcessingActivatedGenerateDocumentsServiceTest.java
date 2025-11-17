package uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.activation.service;

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
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.common.domain.Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.common.service.Cca3ExistingFacilitiesMigrationAccountProcessingCreateDocumentService;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.common.service.Cca3ExistingFacilitiesMigrationAccountProcessingOfficialNoticeService;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestResource;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

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
class Cca3ExistingFacilitiesMigrationAccountProcessingActivatedGenerateDocumentsServiceTest {

    @InjectMocks
    private Cca3ExistingFacilitiesMigrationAccountProcessingActivatedGenerateDocumentsService service;

    @Mock
    private RequestService requestService;

    @Mock
    private UnderlyingAgreementService underlyingAgreementService;

    @Mock
    private UnderlyingAgreementQueryService underlyingAgreementQueryService;

    @Mock
    private Cca3ExistingFacilitiesMigrationAccountProcessingCreateDocumentService cca3ExistingFacilitiesMigrationAccountProcessingCreateDocumentService;

    @Mock
    private Cca3ExistingFacilitiesMigrationAccountProcessingOfficialNoticeService cca3ExistingFacilitiesMigrationAccountProcessingOfficialNoticeService;

    @Test
    void generateDocuments() {
        final String requestId = "requestId";
        final Long accountId = 5L;
        final Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload requestPayload =
                Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload.builder().build();
        final Request request = Request.builder()
                .requestResources(List.of(RequestResource.builder()
                        .resourceType(ResourceType.ACCOUNT)
                        .resourceId(accountId.toString())
                        .build()))
                .payload(requestPayload)
                .build();

        final String documentUuid = UUID.randomUUID().toString();
        final FileInfoDTO document = FileInfoDTO.builder().name("una.pdf").uuid(documentUuid).build();
        final FileInfoDTO officialNotice = FileInfoDTO.builder().name("notice.pdf").build();
        final long documentId = 11;
        final UnderlyingAgreementDTO underlyingAgreementDTO = UnderlyingAgreementDTO.builder()
                .underlyingAgreementDocuments(List.of(
                        UnderlyingAgreementDocumentDTO.builder().id(documentId).schemeVersion(SchemeVersion.CCA_3).build()
                ))
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(underlyingAgreementQueryService.getUnderlyingAgreementByAccountId(accountId))
                .thenReturn(underlyingAgreementDTO);
        when(cca3ExistingFacilitiesMigrationAccountProcessingCreateDocumentService.createUnderlyingAgreementDocument(requestId))
                .thenReturn(CompletableFuture.completedFuture(document));
        when(cca3ExistingFacilitiesMigrationAccountProcessingOfficialNoticeService.generateActivatedOfficialNotice(requestId))
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
                .generateActivatedOfficialNotice(requestId);
        verify(underlyingAgreementQueryService, times(1))
                .getUnderlyingAgreementByAccountId(accountId);
        verify(underlyingAgreementService, times(1))
                .saveFileDocumentUuid(documentId, documentUuid);
    }

    @Test
    void generateDocuments_UNA_doc_not_exist() {
        final String requestId = "requestId";

        when(requestService.findRequestById(requestId)).thenThrow(new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class, () ->
                service.generateDocuments(requestId));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
        verify(requestService, times(1)).findRequestById(requestId);
        verifyNoInteractions(underlyingAgreementQueryService, cca3ExistingFacilitiesMigrationAccountProcessingCreateDocumentService,
                cca3ExistingFacilitiesMigrationAccountProcessingOfficialNoticeService, underlyingAgreementService);
    }

    @Test
    void generateDocuments_throws_internal_server_error_exception() {
        final String requestId = "requestId";
        final Long accountId = 5L;
        final Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload requestPayload =
                Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload.builder().build();
        final Request request = Request.builder()
                .requestResources(List.of(RequestResource.builder()
                        .resourceType(ResourceType.ACCOUNT)
                        .resourceId(accountId.toString())
                        .build()))
                .payload(requestPayload)
                .build();

        final FileInfoDTO officialNotice = FileInfoDTO.builder().name("notice.pdf").build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(cca3ExistingFacilitiesMigrationAccountProcessingCreateDocumentService.createUnderlyingAgreementDocument(requestId)).thenAnswer(answer -> {
            CompletableFuture<?> future = new CompletableFuture<>();
            future.completeExceptionally(new BusinessException(ErrorCode.DOCUMENT_TEMPLATE_FILE_GENERATION_ERROR, "una.pdf"));
            return future;
        });
        when(cca3ExistingFacilitiesMigrationAccountProcessingOfficialNoticeService.generateActivatedOfficialNotice(requestId))
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
                .generateActivatedOfficialNotice(requestId);
        verifyNoInteractions(underlyingAgreementQueryService, underlyingAgreementService);
    }

    @Test
    void generateDocuments_throws_runtime_exception() {
        final String requestId = "requestId";
        final Long accountId = 5L;
        final Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload requestPayload =
                Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload.builder().build();
        final Request request = Request.builder()
                .requestResources(List.of(RequestResource.builder()
                        .resourceType(ResourceType.ACCOUNT)
                        .resourceId(accountId.toString())
                        .build()))
                .payload(requestPayload)
                .build();

        final FileInfoDTO officialNotice = FileInfoDTO.builder().name("notice.pdf").build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(cca3ExistingFacilitiesMigrationAccountProcessingCreateDocumentService.createUnderlyingAgreementDocument(requestId)).thenAnswer(answer -> {
            CompletableFuture<?> future = new CompletableFuture<>();
            future.completeExceptionally(new RuntimeException("Runtime exception"));
            return future;
        });
        when(cca3ExistingFacilitiesMigrationAccountProcessingOfficialNoticeService.generateActivatedOfficialNotice(requestId))
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
                .generateActivatedOfficialNotice(requestId);
        verifyNoInteractions(underlyingAgreementQueryService, underlyingAgreementService);
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
        verifyNoInteractions(underlyingAgreementQueryService, underlyingAgreementService, cca3ExistingFacilitiesMigrationAccountProcessingCreateDocumentService,
                cca3ExistingFacilitiesMigrationAccountProcessingOfficialNoticeService);
    }

    @Test
    void generateDocuments_throws_interrupted_exception() {
        final String requestId = "requestId";
        final Long accountId = 5L;
        final Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload requestPayload =
                Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload.builder().build();
        final Request request = Request.builder()
                .requestResources(List.of(RequestResource.builder()
                        .resourceType(ResourceType.ACCOUNT)
                        .resourceId(accountId.toString())
                        .build()))
                .payload(requestPayload)
                .build();

        final FileInfoDTO officialNotice = FileInfoDTO.builder().name("notice.pdf").build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(cca3ExistingFacilitiesMigrationAccountProcessingCreateDocumentService.createUnderlyingAgreementDocument(requestId)).thenAnswer(answer -> {
            CompletableFuture<?> future = new CompletableFuture<>();
            Thread.currentThread().interrupt();
            return future;
        });
        when(cca3ExistingFacilitiesMigrationAccountProcessingOfficialNoticeService.generateActivatedOfficialNotice(requestId))
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
                .generateActivatedOfficialNotice(requestId);
        verifyNoInteractions(underlyingAgreementQueryService, underlyingAgreementService);
    }
}
