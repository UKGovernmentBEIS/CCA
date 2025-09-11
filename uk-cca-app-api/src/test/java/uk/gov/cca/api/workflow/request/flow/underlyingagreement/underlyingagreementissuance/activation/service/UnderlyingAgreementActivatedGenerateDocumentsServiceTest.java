package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.activation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.dto.UnderlyingAgreementDTO;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementQueryService;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementService;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.service.UnderlyingAgreementCreateDocumentService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.service.UnderlyingAgreementOfficialNoticeService;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestResource;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.domain.DecisionNotification;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementActivatedGenerateDocumentsServiceTest {

	@InjectMocks
    private UnderlyingAgreementActivatedGenerateDocumentsService service;

    @Mock
    private RequestService requestService;

    @Mock
    private UnderlyingAgreementCreateDocumentService createDocumentService;

    @Mock
    private UnderlyingAgreementOfficialNoticeService officialNoticeService;
    
    @Mock
    private UnderlyingAgreementService underlyingAgreementService;
    
    @Mock
	private UnderlyingAgreementQueryService underlyingAgreementQueryService;

    @Test
    void generateDocuments() {
        final String requestId = "1";
        final Long accountId = 5L;
        final String signatory = "signatory";
        final UnderlyingAgreement underlyingAgreement = UnderlyingAgreement.builder().build();
        
        final UnderlyingAgreementDTO unaDto = UnderlyingAgreementDTO.builder().id(1L).accountId(accountId).build();

        final UnderlyingAgreementRequestPayload requestPayload = UnderlyingAgreementRequestPayload.builder()
                .decisionNotification(CcaDecisionNotification.builder()
                        .decisionNotification(DecisionNotification.builder().signatory(signatory).build())
                        .build())
                .underlyingAgreement(UnderlyingAgreementPayload.builder()
                		.underlyingAgreement(underlyingAgreement)
                		.build())
                .build();

        final Request request = Request.builder()
                .payload(requestPayload)
                .build();
        addResourcesToRequest(accountId, request);

        UUID pdfUuid = UUID.randomUUID();
        FileInfoDTO document = FileInfoDTO.builder()
                .name("una.pdf")
                .uuid(pdfUuid.toString())
                .build();

        UUID officialNoticePdfUuid = UUID.randomUUID();
        FileInfoDTO officialNotice = FileInfoDTO.builder()
                .name("offnotice.pdf")
                .uuid(officialNoticePdfUuid.toString())
                .build();

        when(createDocumentService.create(requestId, SchemeVersion.CCA_2))
                .thenReturn(CompletableFuture.completedFuture(document));
        when(officialNoticeService.generateActivatedOfficialNotice(requestId))
                .thenReturn(CompletableFuture.completedFuture(officialNotice));
        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(underlyingAgreementQueryService.getUnderlyingAgreementByAccountId(accountId)).thenReturn(unaDto);

        service.generateDocuments(requestId);

        verify(createDocumentService, times(1)).create(requestId, SchemeVersion.CCA_2);
        verify(officialNoticeService, times(1)).generateActivatedOfficialNotice(requestId);
        verify(requestService, times(1)).findRequestById(requestId);
        verify(underlyingAgreementQueryService, times(1)).getUnderlyingAgreementByAccountId(accountId);
        verify(underlyingAgreementService, times(1)).saveFileDocumentUuid(1L, pdfUuid.toString());

        assertThat(requestPayload.getUnderlyingAgreementDocument()).isEqualTo(document);
        assertThat(requestPayload.getOfficialNotice()).isEqualTo(officialNotice);
    }

    @Test
    void generateDocuments_throws_business_exception() {
        final String requestId = "1";
        final String signatory = "signatory";

        final UnderlyingAgreement underlyingAgreement = UnderlyingAgreement.builder().build();

        final UnderlyingAgreementRequestPayload requestPayload = UnderlyingAgreementRequestPayload.builder()
                .decisionNotification(CcaDecisionNotification.builder()
                        .decisionNotification(DecisionNotification.builder().signatory(signatory).build())
                        .build())
                .underlyingAgreement(UnderlyingAgreementPayload.builder()
                		.underlyingAgreement(underlyingAgreement)
                		.build())
                .build();

        UUID officialNoticePdfUuid = UUID.randomUUID();
        FileInfoDTO officialNotice = FileInfoDTO.builder()
                .name("offnotice.pdf")
                .uuid(officialNoticePdfUuid.toString())
                .build();

        when(createDocumentService.create(requestId, SchemeVersion.CCA_2)).thenAnswer(answer -> {
            CompletableFuture<?> future = new CompletableFuture<>();
            future.completeExceptionally(new BusinessException(ErrorCode.DOCUMENT_TEMPLATE_FILE_GENERATION_ERROR, "una.pdf"));
            return future;
        });

        when(officialNoticeService.generateActivatedOfficialNotice(requestId))
                .thenReturn(CompletableFuture.completedFuture(officialNotice));

        BusinessException be = assertThrows(BusinessException.class, () -> service.generateDocuments(requestId));
        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.DOCUMENT_TEMPLATE_FILE_GENERATION_ERROR);

        verify(createDocumentService, times(1)).create(requestId, SchemeVersion.CCA_2);
        verify(officialNoticeService, times(1)).generateActivatedOfficialNotice(requestId);
        verifyNoInteractions(requestService);

        assertThat(requestPayload.getUnderlyingAgreementDocument()).isNull();
        assertThat(requestPayload.getOfficialNotice()).isNull();
    }

    @Test
    void generateDocuments_throws_internal_server_error_exception() {
        final String requestId = "1";
        final String signatory = "signatory";

        final UnderlyingAgreement underlyingAgreement = UnderlyingAgreement.builder().build();

        final UnderlyingAgreementRequestPayload requestPayload = UnderlyingAgreementRequestPayload.builder()
                .decisionNotification(CcaDecisionNotification.builder()
                        .decisionNotification(DecisionNotification.builder().signatory(signatory).build())
                        .build())
                .underlyingAgreement(UnderlyingAgreementPayload.builder()
                		.underlyingAgreement(underlyingAgreement)
                		.build())
                .build();

        UUID officialNoticePdfUuid = UUID.randomUUID();
        FileInfoDTO officialNotice = FileInfoDTO.builder()
                .name("offnotice.pdf")
                .uuid(officialNoticePdfUuid.toString())
                .build();

        when(createDocumentService.create(requestId, SchemeVersion.CCA_2)).thenAnswer(answer -> {
            CompletableFuture<?> future = new CompletableFuture<>();
            future.completeExceptionally(new RuntimeException("something unexpected happened"));
            return future;
        });

        when(officialNoticeService.generateActivatedOfficialNotice(requestId))
                .thenReturn(CompletableFuture.completedFuture(officialNotice));

        BusinessException be = assertThrows(BusinessException.class, () -> service.generateDocuments(requestId));
        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.INTERNAL_SERVER);

        verify(createDocumentService, times(1)).create(requestId, SchemeVersion.CCA_2);
        verify(officialNoticeService, times(1)).generateActivatedOfficialNotice(requestId);
        verifyNoInteractions(requestService);

        assertThat(requestPayload.getUnderlyingAgreementDocument()).isNull();
        assertThat(requestPayload.getOfficialNotice()).isNull();
    }
    
    private void addResourcesToRequest(Long accountId, Request request) {
		RequestResource accountResource = RequestResource.builder()
				.resourceType(ResourceType.ACCOUNT)
				.resourceId(accountId.toString())
				.request(request)
				.build();

        request.getRequestResources().add(accountResource);
	}
}
