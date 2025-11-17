package uk.gov.cca.api.workflow.request.flow.subsistencefees.sectormoa.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.account.domain.dto.NoticeRecipientType;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationContactDTO;
import uk.gov.cca.api.subsistencefees.domain.MoaType;
import uk.gov.cca.api.subsistencefees.domain.dto.EligibleFacilityDTO;
import uk.gov.cca.api.subsistencefees.service.SubsistenceFeesRunQueryService;
import uk.gov.cca.api.subsistencefees.service.SubsistenceFeesRunUpdateService;
import uk.gov.cca.api.subsistencefees.service.SubsistenceFeesTransactionIdGeneratorService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.flow.common.domain.DefaultNoticeRecipient;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaOfficialNoticeSendService;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain.SectorMoaRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain.SubsistenceFeesRunRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.service.MoaFacilitiesService;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.sectormoa.domain.SectorMoaGeneratedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.sectormoa.domain.SectorMoaRequestPayload;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.subsistencefeesrun.domain.SubsistenceFeesRunRequestPayload;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestResource;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.time.Year;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SectorMoaGenerateServiceTest {

    @InjectMocks
    private SectorMoaGenerateService sectorMoaGenerateService;

    @Mock
    private RequestService requestService;

    @Mock
    private SectorMoaGenerateDocumentsService sectorMoaGenerateDocumentsService;

    @Mock
    private SectorMoaOfficialNoticeService sectorMoaOfficialNoticeService;

    @Mock
    private SubsistenceFeesRunQueryService subsistenceFeesRunQueryService;

    @Mock
    private SubsistenceFeesTransactionIdGeneratorService transactionIdGeneratorService;

    @Mock
    private SubsistenceFeesRunUpdateService subsistenceFeesRunUpdateService;

    @Mock
    private MoaFacilitiesService facilitiesService;

    @Mock
    private CcaOfficialNoticeSendService officialNoticeSendService;

    @Test
    void generateMoa() {
        final long sectorId = 1L;
        final Year chargingYear = Year.of(2025);
        final Long runId = 1L;
        final String documentUuid = UUID.randomUUID().toString();
        final String parentRequestId = "parentRequestId";
        final String requestId = "requestId";
        final String submitterId = UUID.randomUUID().toString();

        final Request parentRequest = Request.builder()
                .id(parentRequestId)
                .metadata(SubsistenceFeesRunRequestMetadata.builder()
                        .chargingYear(chargingYear)
                        .build())
                .payload(SubsistenceFeesRunRequestPayload.builder()
                        .submitterId(submitterId)
                        .runId(1L)
                        .build())
                .build();

        final SubsistenceFeesRunRequestMetadata parentMetadata = (SubsistenceFeesRunRequestMetadata) parentRequest.getMetadata();
        parentMetadata.setChargingYear(chargingYear);

        SectorMoaRequestMetadata metadata = SectorMoaRequestMetadata.builder()
                .parentRequestId(parentRequestId)
                .sectorAcronym("Acronym")
                .build();

        final FileInfoDTO sectorMoaDocument = FileInfoDTO.builder()
                .uuid(documentUuid)
                .build();

        final Request request = Request.builder()
                .metadata(metadata)
                .payload(SectorMoaRequestPayload.builder()
                        .sectorAssociationId(sectorId)
                        .sectorMoaDocument(sectorMoaDocument)
                        .build())
                .id(requestId)
                .build();
        addResourcesToRequest(sectorId, request);

        final String transactionId = "transactionId";
        final List<EligibleFacilityDTO> facilities = List.of(EligibleFacilityDTO.builder()
                .facilityBusinessId("facilityId")
                .targetUnitBusinessId("businessId")
                .build());

        final SectorAssociationContactDTO sectorAssociationContactDTO = SectorAssociationContactDTO.builder()
                .email("email")
                .firstName("firstName")
                .lastName("lastName")
                .build();

        List<DefaultNoticeRecipient> recipients = List.of(DefaultNoticeRecipient.builder()
                .name(sectorAssociationContactDTO.getFullName())
                .email(sectorAssociationContactDTO.getEmail())
                .recipientType(NoticeRecipientType.SECTOR_CONTACT)
                .build());

        when(requestService.findRequestById(parentRequestId)).thenReturn(parentRequest);
        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(transactionIdGeneratorService.generateTransactionIdForSectorMOAs()).thenReturn(transactionId);
        when(subsistenceFeesRunQueryService.getSectorEligibleFacilitiesForSubsistenceFeesRun(sectorId, chargingYear)).thenReturn(facilities);
        when(officialNoticeSendService.getOfficialNoticeToDefaultRecipients(request)).thenReturn(recipients);

        // invoke
        assertDoesNotThrow(() -> sectorMoaGenerateService.generateMoa(requestId));

        // verify
        verify(requestService, times(2)).findRequestById(any());
        verify(transactionIdGeneratorService, times(1)).generateTransactionIdForSectorMOAs();
        verify(subsistenceFeesRunQueryService, times(1)).getSectorEligibleFacilitiesForSubsistenceFeesRun(sectorId, chargingYear);
        verify(sectorMoaGenerateDocumentsService, times(1)).generateDocuments(request, facilities);
        verify(sectorMoaOfficialNoticeService, times(1)).sendOfficialNotice(request);
        verify(subsistenceFeesRunUpdateService, times(1)).persistMoa(sectorId, transactionId, runId, MoaType.SECTOR_MOA, facilities, documentUuid);
        verify(facilitiesService, times(1)).flagMoaFacilities(runId, chargingYear, MoaType.SECTOR_MOA, facilities);
        verify(officialNoticeSendService, times(1)).getOfficialNoticeToDefaultRecipients(request);
        verify(requestService, times(1)).addActionToRequest(request,
                SectorMoaGeneratedRequestActionPayload.builder()
                        .payloadType(CcaRequestActionPayloadType.SECTOR_MOA_GENERATED_PAYLOAD)
                        .paymentRequestId(parentRequestId)
                        .transactionId(transactionId)
                        .chargingYear(chargingYear)
                        .recipients(recipients)
                        .moaDocument(sectorMoaDocument)
                        .build(),
                CcaRequestActionType.SECTOR_MOA_GENERATED, submitterId);

        assertThat(metadata.getTransactionId()).isEqualTo(transactionId);
    }

    private void addResourcesToRequest(Long sectorAssociationId, Request request) {
        RequestResource sectorResource = RequestResource.builder()
                .resourceType(CcaResourceType.SECTOR_ASSOCIATION)
                .resourceId(sectorAssociationId.toString())
                .request(request)
                .build();

        request.getRequestResources().add(sectorResource);
    }
}
