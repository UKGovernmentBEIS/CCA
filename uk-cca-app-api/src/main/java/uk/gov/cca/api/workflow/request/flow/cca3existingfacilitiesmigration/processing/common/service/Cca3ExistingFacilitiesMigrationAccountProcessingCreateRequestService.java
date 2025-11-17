package uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementQueryService;
import uk.gov.cca.api.workflow.request.core.domain.AccountReferenceData;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.core.service.AccountReferenceDetailsService;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.domain.Cca3ExistingFacilitiesMigrationRunRequestPayload;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.domain.Cca3FacilityMigrationData;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.domain.Cca3FacilityMigrationAccountState;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.common.domain.Cca3ExistingFacilitiesMigrationAccountProcessingRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.common.domain.Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.common.service.RequestCreateAccountAndSectorResourcesService;
import uk.gov.netz.api.workflow.request.StartProcessRequestService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class Cca3ExistingFacilitiesMigrationAccountProcessingCreateRequestService {

    private final RequestService requestService;
    private final UnderlyingAgreementQueryService underlyingAgreementQueryService;
    private final AccountReferenceDetailsService accountReferenceDetailsService;
    private final RequestCreateAccountAndSectorResourcesService requestCreateAccountAndSectorResourcesService;
    private final StartProcessRequestService startProcessRequestService;

    @Transactional
    public void createRequest(Long accountId, String parentRequestId, String parentRequestBusinessKey) {
        final Request parentRequest = requestService.findRequestById(parentRequestId);
        final Cca3ExistingFacilitiesMigrationRunRequestPayload parentPayload =
                (Cca3ExistingFacilitiesMigrationRunRequestPayload) parentRequest.getPayload();
        final Cca3FacilityMigrationAccountState accountState = parentPayload.getAccountStates().get(accountId);
        final String defaultSignatory = parentPayload.getDefaultSignatory();

        // Get UNA
        final UnderlyingAgreementContainer underlyingAgreementContainer = underlyingAgreementQueryService
                .getUnderlyingAgreementContainerByAccountId(accountId);
        Map<UUID, String> attachments = underlyingAgreementContainer.getUnderlyingAgreementAttachments();

        // Add facilities calculator file to attachments
        accountState.getFacilityMigrationDataList().stream()
                .filter(Cca3FacilityMigrationData::getParticipatingInCca3Scheme)
                .forEach(f -> attachments.put(UUID.fromString(f.getCalculatorFileUuid()), f.getCalculatorFileName()));

        // Get Account reference details
        AccountReferenceData accountReferenceData = accountReferenceDetailsService.getAccountReferenceData(accountId);

        final RequestParams requestParams = RequestParams.builder()
                .type(CcaRequestType.CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING)
                .requestResources(requestCreateAccountAndSectorResourcesService.createRequestResources(accountId))
                .requestMetadata(Cca3ExistingFacilitiesMigrationAccountProcessingRequestMetadata.builder()
                        .type(CcaRequestMetadataType.CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING)
                        .parentRequestId(parentRequestId)
                        .accountBusinessId(accountState.getAccountBusinessId())
                        .cca3Participating(accountState.isCca3Participating())
                        .build())
                .requestPayload(Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload.builder()
                        .payloadType(CcaRequestPayloadType.CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_PAYLOAD)
                        .defaultSignatory(defaultSignatory)
                        .sectorAssociationId(accountReferenceData.getTargetUnitAccountDetails().getSectorAssociationId())
                        .accountReferenceData(accountReferenceData)
                        .underlyingAgreement(underlyingAgreementContainer.getUnderlyingAgreement())
                        .facilityMigrationDataList(accountState.getFacilityMigrationDataList())
                        .underlyingAgreementAttachments(attachments)
                        .build())
                .processVars(Map.of(
                        BpmnProcessConstants.ACCOUNT_ID, accountId,
                        CcaBpmnProcessConstants.CCA3_EXISTING_FACILITIES_MIGRATION_RUN_REQUEST_BUSINESS_KEY, parentRequestBusinessKey,
                        CcaBpmnProcessConstants.CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_STATE, accountState
                ))
                .build();

        startProcessRequestService.startProcess(requestParams);
    }
}
