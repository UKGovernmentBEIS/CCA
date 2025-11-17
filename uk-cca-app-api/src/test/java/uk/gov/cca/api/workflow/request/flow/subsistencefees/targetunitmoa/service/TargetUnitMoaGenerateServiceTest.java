package uk.gov.cca.api.workflow.request.flow.subsistencefees.targetunitmoa.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.account.domain.dto.NoticeRecipientType;
import uk.gov.cca.api.subsistencefees.domain.MoaType;
import uk.gov.cca.api.subsistencefees.domain.dto.EligibleFacilityDTO;
import uk.gov.cca.api.subsistencefees.service.SubsistenceFeesRunQueryService;
import uk.gov.cca.api.subsistencefees.service.SubsistenceFeesRunUpdateService;
import uk.gov.cca.api.subsistencefees.service.SubsistenceFeesTransactionIdGeneratorService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.flow.common.domain.DefaultNoticeRecipient;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaOfficialNoticeSendService;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain.SubsistenceFeesRunRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain.TargetUnitMoaRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.service.MoaFacilitiesService;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.sectormoa.domain.SectorMoaGeneratedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.subsistencefeesrun.domain.SubsistenceFeesRunRequestPayload;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.targetunitmoa.domain.TargetUnitMoaRequestPayload;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
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
class TargetUnitMoaGenerateServiceTest {

    @InjectMocks
    private TargetUnitMoaGenerateService targetUnitMoaGenerateService;

    @Mock
    private RequestService requestService;

    @Mock
    private TargetUnitMoaGenerateDocumentsService targetUnitMoaGenerateDocumentsService;

    @Mock
    private TargetUnitMoaOfficialNoticeService targetUnitMoaOfficialNoticeService;

    @Mock
    private SubsistenceFeesTransactionIdGeneratorService transactionIdGeneratorService;

    @Mock
    private SubsistenceFeesRunQueryService subsistenceFeesRunQueryService;

    @Mock
    private SubsistenceFeesRunUpdateService subsistenceFeesRunUpdateService;

    @Mock
    private MoaFacilitiesService facilitiesService;

    @Mock
    private CcaOfficialNoticeSendService officialNoticeSendService;

    @Test
    void generateMoa() {
        final long accountId = 1L;
        final Long runId = 1L;
        final String documentUuid = UUID.randomUUID().toString();
        final Year chargingYear = Year.of(2025);
        final String parentRequestId = "parentRequestId";
        final String requestId = "requestId";
        final String submitterId = UUID.randomUUID().toString();
        final FileInfoDTO targetUnitMoaDocument = FileInfoDTO.builder()
                .uuid(documentUuid)
                .build();
        final Request parentRequest = Request.builder()
                .id(parentRequestId)
                .metadata(SubsistenceFeesRunRequestMetadata.builder()
                        .chargingYear(chargingYear)
                        .build())
                .payload(SubsistenceFeesRunRequestPayload.builder()
                        .submitterId(submitterId)
                        .runId(runId)
                        .build())
                .build();
        final SubsistenceFeesRunRequestMetadata parentMetadata = (SubsistenceFeesRunRequestMetadata) parentRequest.getMetadata();
        parentMetadata.setChargingYear(chargingYear);
        TargetUnitMoaRequestMetadata metadata = TargetUnitMoaRequestMetadata.builder()
                .parentRequestId(parentRequestId)
                .build();
        final Request request = Request.builder()
                .metadata(metadata)
                .payload(TargetUnitMoaRequestPayload.builder()
                        .targetUnitMoaDocument(targetUnitMoaDocument)
                        .build())
                .id(requestId)
                .build();
        addResourcesToRequest(accountId, request);

        final String transactionId = "transactionId";
        final List<EligibleFacilityDTO> facilities = List.of(EligibleFacilityDTO.builder()
                .facilityBusinessId("facilityId")
                .targetUnitBusinessId("businessId")
                .build());

        final List<DefaultNoticeRecipient> recipients = List.of(DefaultNoticeRecipient.builder()
                        .recipientType(NoticeRecipientType.SECTOR_CONTACT)
                        .email("sector@cca.com")
                        .name("Sector Contact")
                        .build(),
                DefaultNoticeRecipient.builder()
                        .recipientType(NoticeRecipientType.RESPONSIBLE_PERSON)
                        .email("responsible-person@cca.com")
                        .name("Responsible Person")
                        .build(),
                DefaultNoticeRecipient.builder()
                        .recipientType(NoticeRecipientType.ADMINISTRATIVE_CONTACT)
                        .email("administrative-contact@cca.com")
                        .name("Administrative Contact")
                        .build());

        when(requestService.findRequestById(parentRequestId)).thenReturn(parentRequest);
        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(transactionIdGeneratorService.generateTransactionIdForTargetUnitMOAs()).thenReturn(transactionId);
        when(subsistenceFeesRunQueryService.getAccountEligibleFacilitiesForSubsistenceFeesRun(accountId, chargingYear)).thenReturn(facilities);
        when(officialNoticeSendService.getOfficialNoticeToDefaultRecipients(request)).thenReturn(recipients);

        // invoke
        assertDoesNotThrow(() -> targetUnitMoaGenerateService.generateMoa(requestId));

        // verify
        verify(requestService, times(2)).findRequestById(any());
        verify(transactionIdGeneratorService, times(1)).generateTransactionIdForTargetUnitMOAs();
        verify(subsistenceFeesRunQueryService, times(1)).getAccountEligibleFacilitiesForSubsistenceFeesRun(accountId, chargingYear);
        verify(targetUnitMoaGenerateDocumentsService, times(1)).generateDocuments(request, facilities);
        verify(targetUnitMoaOfficialNoticeService, times(1)).sendOfficialNotice(request);
        verify(subsistenceFeesRunUpdateService, times(1)).persistMoa(accountId, transactionId, runId, MoaType.TARGET_UNIT_MOA, facilities, documentUuid);
        verify(facilitiesService, times(1)).flagMoaFacilities(runId, chargingYear, MoaType.TARGET_UNIT_MOA, facilities);
        verify(officialNoticeSendService, times(1)).getOfficialNoticeToDefaultRecipients(request);
        verify(requestService, times(1)).addActionToRequest(request,
                SectorMoaGeneratedRequestActionPayload.builder()
                        .payloadType(CcaRequestActionPayloadType.TARGET_UNIT_MOA_GENERATED_PAYLOAD)
                        .paymentRequestId(parentRequestId)
                        .transactionId(transactionId)
                        .chargingYear(chargingYear)
                        .recipients(recipients)
                        .moaDocument(targetUnitMoaDocument)
                        .build(),
                CcaRequestActionType.TARGET_UNIT_MOA_GENERATED,
                submitterId);

        assertThat(metadata.getTransactionId()).isEqualTo(transactionId);
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
