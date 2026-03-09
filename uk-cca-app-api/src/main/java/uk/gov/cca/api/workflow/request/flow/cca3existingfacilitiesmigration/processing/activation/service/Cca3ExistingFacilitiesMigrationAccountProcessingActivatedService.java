package uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.activation.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.facility.service.FacilityDataUpdateService;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementSchemeService;
import uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementValidationContext;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.domain.Cca3FacilityMigrationData;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.activation.domain.Cca3ExistingFacilitiesMigrationAccountProcessingActivatedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.activation.transform.Cca3ExistingFacilitiesMigrationAccountProcessingActivationMapper;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.common.domain.Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.common.domain.DefaultNoticeRecipient;
import uk.gov.cca.api.workflow.request.flow.common.service.CcaRequestActionUserInfoResolver;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaOfficialNoticeSendService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class Cca3ExistingFacilitiesMigrationAccountProcessingActivatedService {

    private final RequestService requestService;
    private final UnderlyingAgreementSchemeService underlyingAgreementSchemeService;
    private final FacilityDataUpdateService facilityDataUpdateService;
    private final CcaRequestActionUserInfoResolver ccaRequestActionUserInfoResolver;
    private final CcaOfficialNoticeSendService ccaOfficialNoticeSendService;
    private static final Cca3ExistingFacilitiesMigrationAccountProcessingActivationMapper MAPPER = Mappers.getMapper(Cca3ExistingFacilitiesMigrationAccountProcessingActivationMapper.class);

    @Transactional
    public void activateMigratedUnderlyingAgreement(final String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload requestPayload =
                (Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload) request.getPayload();

        // Update UNA
        UnderlyingAgreementContainer underlyingAgreementContainer = MAPPER.toUnderlyingAgreementContainer(
                requestPayload.getUnderlyingAgreement(),
                requestPayload.getAccountReferenceData().getSectorAssociationDetails().getSchemeDataMap(),
                requestPayload.getUnderlyingAgreementAttachments());
        UnderlyingAgreementValidationContext underlyingAgreementValidationContext = UnderlyingAgreementValidationContext.builder()
                .requestCreationDate(request.getCreationDate())
                .schemeVersion(SchemeVersion.CCA_3)
                .build();

        underlyingAgreementSchemeService.migrateUnderlyingAgreementToScheme(underlyingAgreementContainer, request.getAccountId(),
                underlyingAgreementValidationContext);

        // Update facility data scheme
        Set<String> cca3facilities = requestPayload.getFacilityMigrationDataList().stream()
                .filter(Cca3FacilityMigrationData::getParticipatingInCca3Scheme)
                .map(Cca3FacilityMigrationData::getFacilityBusinessId)
                .collect(Collectors.toSet());
        facilityDataUpdateService.updateFacilitiesDataParticipatingScheme(cca3facilities, SchemeVersion.CCA_3);
    }

    @Transactional
    public void addRequestAction(final String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload requestPayload =
                (Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload) request.getPayload();

        // Get users' information
        final CcaDecisionNotification ccaDecisionNotification = requestPayload.getDecisionNotification();
        final Map<String, RequestActionUserInfo> usersInfo = ccaRequestActionUserInfoResolver
                .getUsersInfo(ccaDecisionNotification, request);

        // Get Default notice contacts
        final List<DefaultNoticeRecipient > defaultContacts = ccaOfficialNoticeSendService
                .getOfficialNoticeToDefaultRecipients(request);

        // Create request action
        final Cca3ExistingFacilitiesMigrationAccountProcessingActivatedRequestActionPayload actionPayload =
                MAPPER.toActivatedRequestActionPayload(requestPayload,
                        CcaRequestActionPayloadType.CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_ACTIVATED_PAYLOAD,
                        usersInfo, defaultContacts);


        requestService.addActionToRequest(request,
                actionPayload,
                CcaRequestActionType.CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_ACTIVATED,
                requestPayload.getRegulatorAssignee());
    }
}
