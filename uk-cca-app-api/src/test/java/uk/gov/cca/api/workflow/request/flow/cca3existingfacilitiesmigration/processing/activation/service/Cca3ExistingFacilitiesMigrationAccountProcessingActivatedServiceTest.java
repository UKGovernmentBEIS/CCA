package uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.activation.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.dto.NoticeRecipientType;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.common.domain.SchemeData;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.facility.service.FacilityDataUpdateService;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementService;
import uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementValidationContext;
import uk.gov.cca.api.workflow.request.core.domain.AccountReferenceData;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationDetails;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.domain.Cca3FacilityMigrationData;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.activation.domain.Cca3ExistingFacilitiesMigrationAccountProcessingActivatedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.activation.domain.Cca3ExistingFacilitiesMigrationAccountProcessingActivationDetails;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.common.domain.Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.common.domain.DefaultNoticeRecipient;
import uk.gov.cca.api.workflow.request.flow.common.service.CcaRequestActionUserInfoResolver;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaOfficialNoticeSendService;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestResource;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Cca3ExistingFacilitiesMigrationAccountProcessingActivatedServiceTest {

    @InjectMocks
    private Cca3ExistingFacilitiesMigrationAccountProcessingActivatedService cca3ExistingFacilitiesMigrationAccountProcessingActivatedService;

    @Mock
    private RequestService requestService;

    @Mock
    private UnderlyingAgreementService underlyingAgreementService;

    @Mock
    private FacilityDataUpdateService facilityDataUpdateService;

    @Mock
    private CcaRequestActionUserInfoResolver ccaRequestActionUserInfoResolver;

    @Mock
    private CcaOfficialNoticeSendService ccaOfficialNoticeSendService;

    @Test
    void activateMigratedUnderlyingAgreement() {
        final String requestId = "requestId";
        final Long accountId = 1L;
        final LocalDateTime creationDate = LocalDateTime.now();

        final List<Cca3FacilityMigrationData> facilityMigrations = List.of(
                Cca3FacilityMigrationData.builder()
                        .facilityBusinessId("facility1")
                        .participatingInCca3Scheme(true)
                        .build(),
                Cca3FacilityMigrationData.builder()
                        .facilityBusinessId("facility2")
                        .participatingInCca3Scheme(false)
                        .build()

        );
        final UnderlyingAgreement underlyingAgreement = UnderlyingAgreement.builder().build();
        final Map<UUID, String> underlyingAgreementAttachments = Map.of(UUID.randomUUID(), "filename");
        final Map<SchemeVersion, SchemeData> sectorSchemeDataMap = Map.of(
                SchemeVersion.CCA_3, SchemeData.builder()
                        .sectorMeasurementType(MeasurementType.ENERGY_KWH)
                        .build()
        );
        final Request request = Request.builder()
                .requestResources(List.of(RequestResource.builder()
                        .resourceType(ResourceType.ACCOUNT)
                        .resourceId(accountId.toString())
                        .build())
                )
                .creationDate(creationDate)
                .payload(Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload.builder()
                        .accountReferenceData(AccountReferenceData.builder()
                                .sectorAssociationDetails(SectorAssociationDetails.builder()
                                        .schemeDataMap(sectorSchemeDataMap)
                                        .build())
                                .build())
                        .underlyingAgreement(underlyingAgreement)
                        .facilityMigrationDataList(facilityMigrations)
                        .underlyingAgreementAttachments(underlyingAgreementAttachments)
                        .build())
                .build();

        final UnderlyingAgreementValidationContext context = UnderlyingAgreementValidationContext.builder()
                .requestCreationDate(creationDate)
                .schemeVersion(SchemeVersion.CCA_3)
                .build();
        final UnderlyingAgreementContainer underlyingAgreementContainer = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(underlyingAgreement)
                .schemeDataMap(sectorSchemeDataMap)
                .underlyingAgreementAttachments(underlyingAgreementAttachments)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        // Invoke
        cca3ExistingFacilitiesMigrationAccountProcessingActivatedService
                .activateMigratedUnderlyingAgreement(requestId);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(underlyingAgreementService, times(1))
                .migrateUnderlyingAgreementToScheme(underlyingAgreementContainer, accountId, context);
        verify(facilityDataUpdateService, times(1))
                .updateFacilitiesDataParticipatingScheme(Set.of("facility1"), SchemeVersion.CCA_3);
    }

    @Test
    void addRequestAction() {
        final String requestId = "requestId";

        final Map<String, RequestActionUserInfo> usersInfo = Map.of(
                "sector", RequestActionUserInfo.builder().name("Sector").roleCode("sector_user_administrator").build()
        );
        final List<DefaultNoticeRecipient> defaultContacts = List.of(
                DefaultNoticeRecipient.builder().
                        name("Responsible")
                        .email("responsiblePerson@test.com")
                        .recipientType(NoticeRecipientType.RESPONSIBLE_PERSON)
                        .build()
        );
        final String regulator = "regulator";
        final Cca3ExistingFacilitiesMigrationAccountProcessingActivationDetails activationDetails =
                Cca3ExistingFacilitiesMigrationAccountProcessingActivationDetails.builder()
                        .comments("comments")
                        .build();
        final CcaDecisionNotification decisionNotification = CcaDecisionNotification.builder()
                .sectorUsers(Set.of("sector1", "sector2"))
                .build();
        final FileInfoDTO officialNotice = FileInfoDTO.builder().name("notice").build();
        final FileInfoDTO underlyingAgreementDocument = FileInfoDTO.builder().name("document").build();
        final Map<UUID, String> activationAttachments = Map.of(UUID.randomUUID(), "activation");
        final Request request = Request.builder()
                .payload(Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload.builder()
                        .regulatorAssignee(regulator)
                        .activationDetails(activationDetails)
                        .activationAttachments(activationAttachments)
                        .decisionNotification(decisionNotification)
                        .officialNotice(officialNotice)
                        .underlyingAgreementDocument(underlyingAgreementDocument)
                        .build())
                .build();

        final Cca3ExistingFacilitiesMigrationAccountProcessingActivatedRequestActionPayload actionPayload =
                Cca3ExistingFacilitiesMigrationAccountProcessingActivatedRequestActionPayload.builder()
                        .payloadType(CcaRequestActionPayloadType.CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_ACTIVATED_PAYLOAD)
                        .activationDetails(activationDetails)
                        .activationAttachments(activationAttachments)
                        .decisionNotification(decisionNotification)
                        .defaultContacts(defaultContacts)
                        .usersInfo(usersInfo)
                        .officialNotice(officialNotice)
                        .underlyingAgreementDocument(underlyingAgreementDocument)
                        .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(ccaRequestActionUserInfoResolver.getUsersInfo(decisionNotification, request)).thenReturn(usersInfo);
        when(ccaOfficialNoticeSendService.getOfficialNoticeToDefaultRecipients(request)).thenReturn(defaultContacts);

        // Invoke
        cca3ExistingFacilitiesMigrationAccountProcessingActivatedService
                .addRequestAction(requestId);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(ccaRequestActionUserInfoResolver, times(1)).getUsersInfo(decisionNotification, request);
        verify(ccaOfficialNoticeSendService, times(1)).getOfficialNoticeToDefaultRecipients(request);
        verify(requestService, times(1)).addActionToRequest(request, actionPayload,
                CcaRequestActionType.CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_ACTIVATED, regulator);
    }
}
