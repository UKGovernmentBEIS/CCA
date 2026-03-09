package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityDetails;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityItem;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityStatus;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementSchemeVersionsHelperService;
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
class UnderlyingAgreementAcceptedGenerateDocumentsServiceTest {

	@InjectMocks
    private UnderlyingAgreementAcceptedGenerateDocumentsService service;

    @Mock
    private RequestService requestService;

    @Mock
    private UnderlyingAgreementCreateDocumentService createDocumentService;

    @Mock
    private UnderlyingAgreementOfficialNoticeService officialNoticeService;
    
    @Mock
    private UnderlyingAgreementSchemeVersionsHelperService underlyingAgreementSchemeVersionsHelperService;

    @Test
    void generateDocuments() {
        final String requestId = "1";
        final Long accountId = 5L;
        final String signatory = "signatory";
        final UnderlyingAgreement underlyingAgreement = UnderlyingAgreement.builder()
        		.facilities(Set.of(Facility.builder()
        				.facilityItem(FacilityItem.builder()
        						.facilityDetails(FacilityDetails.builder()
        								.participatingSchemeVersions(Set.of(SchemeVersion.CCA_2)).build()
        								).build()
        						)
        				.status(FacilityStatus.LIVE)
        				.build(), 
        				Facility.builder()
        				.facilityItem(FacilityItem.builder()
        						.facilityDetails(FacilityDetails.builder()
        								.participatingSchemeVersions(Set.of(SchemeVersion.CCA_3)).build()
        								).build()
        						)
        				.status(FacilityStatus.LIVE)
        				.build()
        				))
        		.build();

        final UnderlyingAgreementRequestPayload requestPayload = UnderlyingAgreementRequestPayload.builder()
                .decisionNotification(CcaDecisionNotification.builder()
                        .decisionNotification(DecisionNotification.builder().signatory(signatory).build())
                        .build())
                .underlyingAgreement(UnderlyingAgreementPayload.builder()
                		.underlyingAgreement(underlyingAgreement)
                		.build())
                .underlyingAgreementProposed(UnderlyingAgreementPayload.builder()
                		.underlyingAgreement(underlyingAgreement)
                		.build())
                .build();

        final Request request = Request.builder()
                .payload(requestPayload)
                .build();
        addResourcesToRequest(accountId, request);

        FileInfoDTO document1 = FileInfoDTO.builder()
                .name("una-cca2.pdf")
                .uuid(UUID.randomUUID().toString())
                .build();
        FileInfoDTO document2 = FileInfoDTO.builder()
                .name("una-cca3.pdf")
                .uuid(UUID.randomUUID().toString())
                .build();
        Map<SchemeVersion, FileInfoDTO> documentMap = Map.of(SchemeVersion.CCA_2, document1, SchemeVersion.CCA_3, document2);

        UUID officialNoticePdfUuid = UUID.randomUUID();
        FileInfoDTO officialNotice = FileInfoDTO.builder()
                .name("offnotice.pdf")
                .uuid(officialNoticePdfUuid.toString())
                .build();

        when(createDocumentService.create(requestId, SchemeVersion.CCA_2))
                .thenReturn(CompletableFuture.completedFuture(document1));
        when(createDocumentService.create(requestId, SchemeVersion.CCA_3))
        		.thenReturn(CompletableFuture.completedFuture(document2));
        when(officialNoticeService.generateAcceptedOfficialNotice(requestId))
                .thenReturn(CompletableFuture.completedFuture(officialNotice));
        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(underlyingAgreementSchemeVersionsHelperService.calculateSchemeVersionsFromActiveFacilities(
        		requestPayload.getUnderlyingAgreementProposed().getUnderlyingAgreement().getFacilities()))
				.thenReturn(Set.of(SchemeVersion.CCA_2, SchemeVersion.CCA_3));

        service.generateDocuments(requestId);

        verify(createDocumentService, times(1)).create(requestId, SchemeVersion.CCA_2);
        verify(createDocumentService, times(1)).create(requestId, SchemeVersion.CCA_3);
        verify(officialNoticeService, times(1)).generateAcceptedOfficialNotice(requestId);
        verify(requestService, times(1)).findRequestById(requestId);
        verify(underlyingAgreementSchemeVersionsHelperService, times(1)).calculateSchemeVersionsFromActiveFacilities(
        		requestPayload.getUnderlyingAgreementProposed().getUnderlyingAgreement().getFacilities());

        assertThat(requestPayload.getUnderlyingAgreementDocuments()).isEqualTo(documentMap);
        assertThat(requestPayload.getOfficialNotice()).isEqualTo(officialNotice);
    }

    @Test
    void generateDocuments_throws_business_exception() {
        final String requestId = "1";
        final Long accountId = 5L;
        final String signatory = "signatory";
        final SchemeVersion schemeVersion = SchemeVersion.CCA_2; 
        final UnderlyingAgreement underlyingAgreement = UnderlyingAgreement.builder()
        		.facilities(Set.of(Facility.builder()
        				.facilityItem(FacilityItem.builder()
        						.facilityDetails(FacilityDetails.builder()
        								.participatingSchemeVersions(Set.of(schemeVersion)).build()
        								).build()
        						)
        				.status(FacilityStatus.LIVE)
        				.build()
        				))
        		.build();

        final UnderlyingAgreementRequestPayload requestPayload = UnderlyingAgreementRequestPayload.builder()
                .decisionNotification(CcaDecisionNotification.builder()
                        .decisionNotification(DecisionNotification.builder().signatory(signatory).build())
                        .build())
                .underlyingAgreement(UnderlyingAgreementPayload.builder()
                		.underlyingAgreement(underlyingAgreement)
                		.build())
                .underlyingAgreementProposed(UnderlyingAgreementPayload.builder()
                		.underlyingAgreement(underlyingAgreement)
                		.build())
                .build();
        final Request request = Request.builder()
                .payload(requestPayload)
                .build();
        addResourcesToRequest(accountId, request);

        UUID officialNoticePdfUuid = UUID.randomUUID();
        FileInfoDTO officialNotice = FileInfoDTO.builder()
                .name("offnotice.pdf")
                .uuid(officialNoticePdfUuid.toString())
                .build();

        when(createDocumentService.create(requestId, schemeVersion)).thenAnswer(answer -> {
            CompletableFuture<?> future = new CompletableFuture<>();
            future.completeExceptionally(new BusinessException(ErrorCode.DOCUMENT_TEMPLATE_FILE_GENERATION_ERROR, "una.pdf"));
            return future;
        });
        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(officialNoticeService.generateAcceptedOfficialNotice(requestId))
                .thenReturn(CompletableFuture.completedFuture(officialNotice));
        when(underlyingAgreementSchemeVersionsHelperService.calculateSchemeVersionsFromActiveFacilities(
        		requestPayload.getUnderlyingAgreementProposed().getUnderlyingAgreement().getFacilities()))
				.thenReturn(Set.of(SchemeVersion.CCA_2));

        BusinessException be = assertThrows(BusinessException.class, () -> service.generateDocuments(requestId));
        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.DOCUMENT_TEMPLATE_FILE_GENERATION_ERROR);

        verify(createDocumentService, times(1)).create(requestId, schemeVersion);
        verify(officialNoticeService, times(1)).generateAcceptedOfficialNotice(requestId);
        verify(requestService, times(1)).findRequestById(requestId);
        verify(underlyingAgreementSchemeVersionsHelperService, times(1)).calculateSchemeVersionsFromActiveFacilities(
        		requestPayload.getUnderlyingAgreementProposed().getUnderlyingAgreement().getFacilities());

        assertThat(requestPayload.getUnderlyingAgreementDocuments()).isNull();
        assertThat(requestPayload.getOfficialNotice()).isNull();
    }

    @Test
    void generateDocuments_throws_internal_server_error_exception() {
        final String requestId = "1";
        final Long accountId = 5L;
        final String signatory = "signatory";
        final SchemeVersion schemeVersion = SchemeVersion.CCA_3; 
        final UnderlyingAgreement underlyingAgreement = UnderlyingAgreement.builder()
        		.facilities(Set.of(Facility.builder()
        				.facilityItem(FacilityItem.builder()
        						.facilityDetails(FacilityDetails.builder()
        								.participatingSchemeVersions(Set.of(schemeVersion)).build()
        								).build()
        						)
        				.status(FacilityStatus.LIVE)
        				.build()
        				))
        		.build();

        final UnderlyingAgreementRequestPayload requestPayload = UnderlyingAgreementRequestPayload.builder()
                .decisionNotification(CcaDecisionNotification.builder()
                        .decisionNotification(DecisionNotification.builder().signatory(signatory).build())
                        .build())
                .underlyingAgreement(UnderlyingAgreementPayload.builder()
                		.underlyingAgreement(underlyingAgreement)
                		.build())
                .underlyingAgreementProposed(UnderlyingAgreementPayload.builder()
                		.underlyingAgreement(underlyingAgreement)
                		.build())
                .build();
        final Request request = Request.builder()
                .payload(requestPayload)
                .build();
        addResourcesToRequest(accountId, request);

        UUID officialNoticePdfUuid = UUID.randomUUID();
        FileInfoDTO officialNotice = FileInfoDTO.builder()
                .name("offnotice.pdf")
                .uuid(officialNoticePdfUuid.toString())
                .build();

        when(createDocumentService.create(requestId, schemeVersion)).thenAnswer(answer -> {
            CompletableFuture<?> future = new CompletableFuture<>();
            future.completeExceptionally(new RuntimeException("something unexpected happened"));
            return future;
        });
        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(officialNoticeService.generateAcceptedOfficialNotice(requestId))
                .thenReturn(CompletableFuture.completedFuture(officialNotice));
        when(underlyingAgreementSchemeVersionsHelperService.calculateSchemeVersionsFromActiveFacilities(
        		requestPayload.getUnderlyingAgreementProposed().getUnderlyingAgreement().getFacilities()))
				.thenReturn(Set.of(schemeVersion));

        BusinessException be = assertThrows(BusinessException.class, () -> service.generateDocuments(requestId));
        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.INTERNAL_SERVER);

        verify(createDocumentService, times(1)).create(requestId, schemeVersion);
        verify(officialNoticeService, times(1)).generateAcceptedOfficialNotice(requestId);
        verify(requestService, times(1)).findRequestById(requestId);
        verify(underlyingAgreementSchemeVersionsHelperService, times(1)).calculateSchemeVersionsFromActiveFacilities(
        		requestPayload.getUnderlyingAgreementProposed().getUnderlyingAgreement().getFacilities());
        
        assertThat(requestPayload.getUnderlyingAgreementDocuments()).isNull();
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
