package uk.gov.cca.api.migration.underlyingagreement.request;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.account.domain.TargetUnitAccount;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.migration.MigrationConstants;
import uk.gov.cca.api.migration.MigrationEndpoint;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementEntity;
import uk.gov.cca.api.underlyingagreement.repository.UnderlyingAgreementRepository;
import uk.gov.cca.api.workflow.request.core.domain.AccountReferenceData;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.core.domain.constants.CcaRequestStatuses;
import uk.gov.cca.api.workflow.request.core.service.AccountReferenceDetailsService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.transform.UnderlyingAgreementTargetUnitDetailsMapper;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementPayload;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.files.documents.service.FileDocumentService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestAction;
import uk.gov.netz.api.workflow.request.core.service.RequestCreateService;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@RequiredArgsConstructor
public class UnderlyingAgreementRequestMigrationService {

    private final RequestCreateService requestCreateService;
    private final FileDocumentService fileDocumentService;
    private final AccountReferenceDetailsService accountReferenceDetailsService;
    private final UnderlyingAgreementTargetUnitDetailsMapper targetUnitDetailsMapper;
    private final UnderlyingAgreementRepository underlyingAgreementRepository;


    @Transactional
    public void migrateRequest(LocalDateTime creationDate, final TargetUnitAccount account) throws Exception {
        
        UnderlyingAgreementEntity entity = underlyingAgreementRepository.findUnderlyingAgreementToMigrateRequestByAccountId(account.getId())
                .orElseThrow(() -> new Exception("Underlying agreement is not eligible"));
        
        // createRequest
        RequestParams requestParams = RequestParams.builder()
                .type(CcaRequestType.UNDERLYING_AGREEMENT)
                .requestResources(createRequestResources(account.getId(), account.getSectorAssociationId()))
                .creationDate(creationDate)
                .requestId(String.format("%s-%s", account.getBusinessId(), "UNA"))
                .build();

        Request request = requestCreateService.createRequest(requestParams, CcaRequestStatuses.MIGRATED);
        request.setEndDate(creationDate);

        FileInfoDTO underlyingAgreementDocument = fileDocumentService.getFileInfoDTO(entity.getFileDocumentUuid());
        AccountReferenceData accountReferenceData = accountReferenceDetailsService.getAccountReferenceData(entity.getAccountId());

        UnderlyingAgreementPayload underlyingAgreementPayload = UnderlyingAgreementPayload.builder()
                .underlyingAgreement(entity.getUnderlyingAgreementContainer().getUnderlyingAgreement())
                .underlyingAgreementTargetUnitDetails(targetUnitDetailsMapper
                        .toUnderlyingAgreementTargetUnitDetails(accountReferenceData.getTargetUnitAccountDetails(),
                                accountReferenceData.getSectorAssociationDetails().getSubsectorAssociationName()))
                .build();

        // createTimelineEvent
        final UnderlyingAgreementMigratedRequestActionPayload actionPayload = UnderlyingAgreementMigratedRequestActionPayload.builder()
                .underlyingAgreementDocument(underlyingAgreementDocument)
                .accountReferenceData(accountReferenceData)
                .underlyingAgreement(underlyingAgreementPayload)
                .underlyingAgreementAttachments(entity.getUnderlyingAgreementContainer().getUnderlyingAgreementAttachments())
                .businessId(accountReferenceDetailsService.getTargetUnitAccountDetails(entity.getAccountId()).getBusinessId())
                .activationDate(entity.getActivationDate())
                .payloadType(CcaRequestActionPayloadType.UNDERLYING_AGREEMENT_MIGRATED_PAYLOAD)
                .build();
        
        request.addRequestAction(
                RequestAction.builder()
                        .payload(actionPayload)
                        .type(CcaRequestActionType.UNDERLYING_AGREEMENT_APPLICATION_MIGRATED)
                        .submitterId(null)
                        .submitter(MigrationConstants.MIGRATION_PROCESS_USER)
                        .build());
    }


	private Map<String, String> createRequestResources(Long accountId, Long sectorId) {
		return Map.of(ResourceType.ACCOUNT, accountId.toString(),
				ResourceType.CA, CompetentAuthorityEnum.ENGLAND.name(),
				CcaResourceType.SECTOR_ASSOCIATION, sectorId.toString());
	}

}
