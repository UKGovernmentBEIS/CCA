package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.activation.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.dto.UnderlyingAgreementDTO;
import uk.gov.cca.api.underlyingagreement.domain.dto.UnderlyingAgreementDocumentDTO;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityDetails;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityItem;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityStatus;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementQueryService;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.service.UnderlyingAgreementVariationCreateDocumentService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.service.UnderlyingAgreementVariationOfficialNoticeService;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestResource;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementVariationActivatedGenerateDocumentsServiceTest {

    @InjectMocks
    private UnderlyingAgreementVariationActivatedGenerateDocumentsService service;

    @Mock
    private RequestService requestService;

    @Mock
    private UnderlyingAgreementVariationCreateDocumentService createDocumentService;

    @Mock
    private UnderlyingAgreementVariationOfficialNoticeService officialNoticeService;

    @Mock
    private UnderlyingAgreementService underlyingAgreementService;

    @Mock
    private UnderlyingAgreementQueryService underlyingAgreementQueryService;

    @Test
    void generateDocuments() {
        final String requestId = "1";
        final Long accountId = 5L;

        final UnderlyingAgreementVariationRequestPayload requestPayload = UnderlyingAgreementVariationRequestPayload.builder()
                .underlyingAgreementProposed(UnderlyingAgreementVariationPayload.builder()
                        .underlyingAgreement(UnderlyingAgreement.builder()
                                .facilities(Set.of(
                                        Facility.builder()
                                                .status(FacilityStatus.LIVE)
                                                .facilityItem(FacilityItem.builder()
                                                        .facilityDetails(FacilityDetails.builder()
                                                                .participatingSchemeVersions(Set.of(SchemeVersion.CCA_2, SchemeVersion.CCA_3))
                                                                .build())
                                                        .build())
                                                .build(),
                                        Facility.builder()
                                                .status(FacilityStatus.EXCLUDED)
                                                .facilityItem(FacilityItem.builder()
                                                        .facilityDetails(FacilityDetails.builder()
                                                                .participatingSchemeVersions(Set.of(SchemeVersion.CCA_2))
                                                                .build())
                                                        .build())
                                                .build()
                                ))
                                .build())
                        .build())
                .build();
        final Request request = Request.builder()
                .payload(requestPayload)
                .build();
        addResourcesToRequest(accountId, request);
        final UnderlyingAgreementDTO unaDto = UnderlyingAgreementDTO.builder()
                .underlyingAgreementDocuments(List.of(
                        UnderlyingAgreementDocumentDTO.builder().id(1L).schemeVersion(SchemeVersion.CCA_2).build(),
                        UnderlyingAgreementDocumentDTO.builder().id(2L).schemeVersion(SchemeVersion.CCA_3).build()
                ))
                .build();

        UUID documentCca2Uuid = UUID.randomUUID();
        FileInfoDTO documentCca2 = FileInfoDTO.builder()
                .name("una-cca2.pdf")
                .uuid(documentCca2Uuid.toString())
                .build();
        UUID documentCca3Uuid = UUID.randomUUID();
        FileInfoDTO documentCca3 = FileInfoDTO.builder()
                .name("una-cca3.pdf")
                .uuid(documentCca3Uuid.toString())
                .build();
        FileInfoDTO activatedNotice = FileInfoDTO.builder()
                .name("activated.pdf")
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(createDocumentService.create(requestId, SchemeVersion.CCA_2))
                .thenReturn(CompletableFuture.completedFuture(documentCca2));
        when(createDocumentService.create(requestId, SchemeVersion.CCA_3))
                .thenReturn(CompletableFuture.completedFuture(documentCca3));
        when(officialNoticeService.generateActivatedOfficialNotice(requestId))
                .thenReturn(CompletableFuture.completedFuture(activatedNotice));
        when(underlyingAgreementQueryService.getUnderlyingAgreementByAccountId(accountId)).thenReturn(unaDto);

        // Invoke
        service.generateDocuments(requestId);

        // Verify
        assertThat(requestPayload.getUnderlyingAgreementDocuments())
                .containsExactlyInAnyOrderEntriesOf(Map.of(SchemeVersion.CCA_2, documentCca2, SchemeVersion.CCA_3, documentCca3));
        assertThat(requestPayload.getOfficialNotices()).containsOnly(activatedNotice);

        verify(requestService, times(1)).findRequestById(requestId);
        verify(createDocumentService, times(1)).create(requestId, SchemeVersion.CCA_2);
        verify(createDocumentService, times(1)).create(requestId, SchemeVersion.CCA_3);
        verify(officialNoticeService, times(1)).generateActivatedOfficialNotice(requestId);
        verify(underlyingAgreementQueryService, times(1)).getUnderlyingAgreementByAccountId(accountId);
        verify(underlyingAgreementService, times(1))
                .saveFileDocumentUuid(1L, documentCca2Uuid.toString());
        verify(underlyingAgreementService, times(1))
                .saveFileDocumentUuid(2L, documentCca3Uuid.toString());
        verifyNoMoreInteractions(createDocumentService, officialNoticeService, underlyingAgreementService);
    }

    @Test
    void generateDocuments_with_terminated_notice() {
        final String requestId = "1";
        final Long accountId = 5L;

        final UnderlyingAgreementVariationRequestPayload requestPayload = UnderlyingAgreementVariationRequestPayload.builder()
                .underlyingAgreementProposed(UnderlyingAgreementVariationPayload.builder()
                        .underlyingAgreement(UnderlyingAgreement.builder()
                                .facilities(Set.of(
                                        Facility.builder()
                                                .status(FacilityStatus.LIVE)
                                                .facilityItem(FacilityItem.builder()
                                                        .facilityDetails(FacilityDetails.builder()
                                                                .participatingSchemeVersions(Set.of(SchemeVersion.CCA_3))
                                                                .build())
                                                        .build())
                                                .build(),
                                        Facility.builder()
                                                .status(FacilityStatus.EXCLUDED)
                                                .facilityItem(FacilityItem.builder()
                                                        .facilityDetails(FacilityDetails.builder()
                                                                .participatingSchemeVersions(Set.of(SchemeVersion.CCA_2))
                                                                .build())
                                                        .build())
                                                .build()
                                ))
                                .build())
                        .build())
                .build();
        final Request request = Request.builder()
                .payload(requestPayload)
                .build();
        addResourcesToRequest(accountId, request);
        final UnderlyingAgreementDTO unaDto = UnderlyingAgreementDTO.builder()
                .underlyingAgreementDocuments(List.of(
                        UnderlyingAgreementDocumentDTO.builder().id(1L).schemeVersion(SchemeVersion.CCA_2).build(),
                        UnderlyingAgreementDocumentDTO.builder().id(2L).schemeVersion(SchemeVersion.CCA_3).build()
                ))
                .build();

        UUID documentCca3Uuid = UUID.randomUUID();
        FileInfoDTO documentCca3 = FileInfoDTO.builder()
                .name("una-cca3.pdf")
                .uuid(documentCca3Uuid.toString())
                .build();
        FileInfoDTO activatedNotice = FileInfoDTO.builder()
                .name("activated.pdf")
                .build();
        FileInfoDTO terminatedNotice = FileInfoDTO.builder()
                .name("terminated.pdf")
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(createDocumentService.create(requestId, SchemeVersion.CCA_3))
                .thenReturn(CompletableFuture.completedFuture(documentCca3));
        when(officialNoticeService.generateActivatedOfficialNotice(requestId))
                .thenReturn(CompletableFuture.completedFuture(activatedNotice));
        when(officialNoticeService.generateTerminationOfficialNotice(requestId, SchemeVersion.CCA_2))
                .thenReturn(CompletableFuture.completedFuture(terminatedNotice));
        when(underlyingAgreementQueryService.getUnderlyingAgreementByAccountId(accountId)).thenReturn(unaDto);

        // Invoke
        service.generateDocuments(requestId);

        // Verify
        assertThat(requestPayload.getUnderlyingAgreementDocuments())
                .containsExactlyInAnyOrderEntriesOf(Map.of(SchemeVersion.CCA_3, documentCca3));
        assertThat(requestPayload.getOfficialNotices()).containsExactlyInAnyOrder(activatedNotice, terminatedNotice);

        verify(requestService, times(1)).findRequestById(requestId);
        verify(createDocumentService, times(1)).create(requestId, SchemeVersion.CCA_3);
        verify(officialNoticeService, times(1)).generateActivatedOfficialNotice(requestId);
        verify(underlyingAgreementQueryService, times(1)).getUnderlyingAgreementByAccountId(accountId);
        verify(underlyingAgreementService, times(1))
                .saveFileDocumentUuid(2L, documentCca3Uuid.toString());
        verifyNoMoreInteractions(createDocumentService, officialNoticeService, underlyingAgreementService);
    }

    @Test
    void generateDocuments_throws_business_exception() {
        final String requestId = "1";
        final Long accountId = 5L;

        final UnderlyingAgreementVariationRequestPayload requestPayload = UnderlyingAgreementVariationRequestPayload.builder()
                .underlyingAgreementProposed(UnderlyingAgreementVariationPayload.builder()
                        .underlyingAgreement(UnderlyingAgreement.builder()
                                .facilities(Set.of(
                                        Facility.builder()
                                                .status(FacilityStatus.LIVE)
                                                .facilityItem(FacilityItem.builder()
                                                        .facilityDetails(FacilityDetails.builder()
                                                                .participatingSchemeVersions(Set.of(SchemeVersion.CCA_3))
                                                                .build())
                                                        .build())
                                                .build()
                                ))
                                .build())
                        .build())
                .build();
        final Request request = Request.builder()
                .payload(requestPayload)
                .build();
        addResourcesToRequest(accountId, request);

        FileInfoDTO activatedNotice = FileInfoDTO.builder()
                .name("activated.pdf")
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(createDocumentService.create(requestId, SchemeVersion.CCA_3)).thenAnswer(answer -> {
            CompletableFuture<?> future = new CompletableFuture<>();
            future.completeExceptionally(new BusinessException(ErrorCode.DOCUMENT_TEMPLATE_FILE_GENERATION_ERROR, "una.pdf"));
            return future;
        });
        when(officialNoticeService.generateActivatedOfficialNotice(requestId))
                .thenReturn(CompletableFuture.completedFuture(activatedNotice));

        // Invoke
        BusinessException be = assertThrows(BusinessException.class, () -> service.generateDocuments(requestId));

        // Verify
        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.DOCUMENT_TEMPLATE_FILE_GENERATION_ERROR);
        assertThat(requestPayload.getUnderlyingAgreementDocuments()).isNull();
        assertThat(requestPayload.getOfficialNotices()).isNull();

        verify(requestService, times(1)).findRequestById(requestId);
        verify(createDocumentService, times(1)).create(requestId, SchemeVersion.CCA_3);
        verify(officialNoticeService, times(1)).generateActivatedOfficialNotice(requestId);
        verifyNoMoreInteractions(createDocumentService, officialNoticeService);
        verifyNoInteractions(underlyingAgreementService, underlyingAgreementQueryService);
    }

    @Test
    void generateDocuments_throws_interrupted_exception() {
        final String requestId = "1";
        final Long accountId = 5L;

        final UnderlyingAgreementVariationRequestPayload requestPayload = UnderlyingAgreementVariationRequestPayload.builder()
                .underlyingAgreementProposed(UnderlyingAgreementVariationPayload.builder()
                        .underlyingAgreement(UnderlyingAgreement.builder()
                                .facilities(Set.of(
                                        Facility.builder()
                                                .status(FacilityStatus.LIVE)
                                                .facilityItem(FacilityItem.builder()
                                                        .facilityDetails(FacilityDetails.builder()
                                                                .participatingSchemeVersions(Set.of(SchemeVersion.CCA_3))
                                                                .build())
                                                        .build())
                                                .build()
                                ))
                                .build())
                        .build())
                .build();
        final Request request = Request.builder()
                .payload(requestPayload)
                .build();
        addResourcesToRequest(accountId, request);

        FileInfoDTO activatedNotice = FileInfoDTO.builder()
                .name("activated.pdf")
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(createDocumentService.create(requestId, SchemeVersion.CCA_3)).thenAnswer(answer -> {
            CompletableFuture<?> future = new CompletableFuture<>();
            Thread.currentThread().interrupt();
            return future;
        });
        when(officialNoticeService.generateActivatedOfficialNotice(requestId))
                .thenReturn(CompletableFuture.completedFuture(activatedNotice));

        // Invoke
        BusinessException be = assertThrows(BusinessException.class, () -> service.generateDocuments(requestId));

        // Verify
        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.INTERNAL_SERVER);
        assertThat(requestPayload.getUnderlyingAgreementDocuments()).isNull();
        assertThat(requestPayload.getOfficialNotices()).isNull();

        verify(requestService, times(1)).findRequestById(requestId);
        verify(createDocumentService, times(1)).create(requestId, SchemeVersion.CCA_3);
        verify(officialNoticeService, times(1)).generateActivatedOfficialNotice(requestId);
        verifyNoMoreInteractions(createDocumentService, officialNoticeService);
        verifyNoInteractions(underlyingAgreementService, underlyingAgreementQueryService);
    }

    @Test
    void generateDocuments_throws_unknown_exception() {
        final String requestId = "1";
        final Long accountId = 5L;

        final UnderlyingAgreementVariationRequestPayload requestPayload = UnderlyingAgreementVariationRequestPayload.builder()
                .underlyingAgreementProposed(UnderlyingAgreementVariationPayload.builder()
                        .underlyingAgreement(UnderlyingAgreement.builder()
                                .facilities(Set.of(
                                        Facility.builder()
                                                .status(FacilityStatus.LIVE)
                                                .facilityItem(FacilityItem.builder()
                                                        .facilityDetails(FacilityDetails.builder()
                                                                .participatingSchemeVersions(Set.of(SchemeVersion.CCA_3))
                                                                .build())
                                                        .build())
                                                .build()
                                ))
                                .build())
                        .build())
                .build();
        final Request request = Request.builder()
                .payload(requestPayload)
                .build();
        addResourcesToRequest(accountId, request);

        FileInfoDTO activatedNotice = FileInfoDTO.builder()
                .name("activated.pdf")
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(createDocumentService.create(requestId, SchemeVersion.CCA_3)).thenAnswer(answer -> {
            CompletableFuture<?> future = new CompletableFuture<>();
            future.completeExceptionally(new RuntimeException("Unknown exception"));
            return future;
        });
        when(officialNoticeService.generateActivatedOfficialNotice(requestId))
                .thenReturn(CompletableFuture.completedFuture(activatedNotice));

        // Invoke
        BusinessException be = assertThrows(BusinessException.class, () -> service.generateDocuments(requestId));

        // Verify
        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.INTERNAL_SERVER);
        assertThat(requestPayload.getUnderlyingAgreementDocuments()).isNull();
        assertThat(requestPayload.getOfficialNotices()).isNull();

        verify(requestService, times(1)).findRequestById(requestId);
        verify(createDocumentService, times(1)).create(requestId, SchemeVersion.CCA_3);
        verify(officialNoticeService, times(1)).generateActivatedOfficialNotice(requestId);
        verifyNoMoreInteractions(createDocumentService, officialNoticeService);
        verifyNoInteractions(underlyingAgreementService, underlyingAgreementQueryService);
    }

    @Test
    void generateDocuments_throws_exception() {
        final String requestId = "1";

        when(requestService.findRequestById(requestId)).thenThrow(new RuntimeException("Unknown"));

        // Invoke
        Exception be = assertThrows(Exception.class, () -> service.generateDocuments(requestId));

        // Verify
        assertThat(be.getMessage()).isEqualTo(ErrorCode.INTERNAL_SERVER.getMessage());
        verify(requestService, times(1)).findRequestById(requestId);
        verifyNoInteractions(underlyingAgreementService, createDocumentService, underlyingAgreementQueryService, officialNoticeService);
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
