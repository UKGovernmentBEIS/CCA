package uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountBusinessInfoDTO;
import uk.gov.cca.api.account.service.TargetUnitAccountQueryService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.domain.Cca3ExistingFacilitiesMigrationRunRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.domain.Cca3ExistingFacilitiesMigrationRunRequestPayload;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.domain.Cca3FacilityMigrationData;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.domain.Cca3FacilityMigrationAccountState;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.validation.Cca3ExistingFacilitiesMigrationAccountProcessingCreateValidator;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.validation.Cca3ExistingFacilitiesMigrationRunValidator;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.validation.Cca3ExistingFacilitiesMigrationViolation;
import uk.gov.cca.api.workflow.request.flow.common.constants.CcaBpmnProcessConstants;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaRequestParams;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.authorization.rules.services.resource.RegulatorAuthorityResourceService;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.workflow.request.StartProcessRequestService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class Cca3ExistingFacilitiesMigrationCreateRunService {

    private final TargetUnitAccountQueryService targetUnitAccountQueryService;
    private final StartProcessRequestService startProcessRequestService;
    private final Cca3ExistingFacilitiesMigrationParseCsvService cca3ExistingFacilitiesMigrationParseCsvService;
    private final RegulatorAuthorityResourceService regulatorAuthorityResourceService;
    private final Cca3ExistingFacilitiesMigrationRunValidator cca3ExistingFacilitiesMigrationRunValidator;
    private final Cca3ExistingFacilitiesMigrationAccountProcessingCreateValidator cca3ExistingFacilitiesMigrationAccountProcessingCreateValidator;

    @Transactional
    public void createCca3ExistingFacilitiesMigrationRun() {
        // Validate if already in progress
        cca3ExistingFacilitiesMigrationRunValidator.validateCreation();

        // Find CSV file
        final String csvFile = cca3ExistingFacilitiesMigrationParseCsvService.submitAndGetSourceFile();

        // Get data
        Map<Long, Cca3FacilityMigrationAccountState> accountStates = validateAndGetData(csvFile);

        // Get eligible accounts
        Set<Long> accountIds = accountStates.entrySet().stream()
                .filter(entry -> entry.getValue().getErrors().isEmpty())
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        // Add a default signatory for proposed document generation
        String defaultSignatory = regulatorAuthorityResourceService.findUsersByCompetentAuthority(CompetentAuthorityEnum.ENGLAND)
                .getFirst();

        // Create process
        CcaRequestParams requestParams = CcaRequestParams.builder()
                .type(CcaRequestType.CCA3_EXISTING_FACILITIES_MIGRATION_RUN)
                .requestResources(Map.of(ResourceType.CA, CompetentAuthorityEnum.ENGLAND.name()))
                .requestPayload(Cca3ExistingFacilitiesMigrationRunRequestPayload.builder()
                        .payloadType(CcaRequestPayloadType.CCA3_EXISTING_FACILITIES_MIGRATION_RUN_REQUEST_PAYLOAD)
                        .defaultSignatory(defaultSignatory)
                        .accountStates(accountStates)
                        .csvSourceFile(csvFile)
                        .build())
                .requestMetadata(Cca3ExistingFacilitiesMigrationRunRequestMetadata.builder()
                        .type(CcaRequestMetadataType.CCA3_EXISTING_FACILITIES_MIGRATION_RUN)
                        .build())
                .processVars(Map.of(
                        CcaBpmnProcessConstants.ACCOUNT_IDS, new HashSet<>(accountIds),
                        CcaBpmnProcessConstants.NUMBER_OF_ACCOUNTS_COMPLETED, 0))
                .build();

        // Start process
        Request request = startProcessRequestService.startProcess(requestParams);

        // Set submission date
        LocalDateTime now = LocalDateTime.now();
        request.setSubmissionDate(now);
    }

    private Map<Long, Cca3FacilityMigrationAccountState> validateAndGetData(String csvFile) {
        // Parse CSV
        List<String> errors = new ArrayList<>();
        List<Cca3FacilityMigrationData> facilities = cca3ExistingFacilitiesMigrationParseCsvService.parseSourceFile(csvFile, errors);

        // Get Live accounts
        Map<String, List<Cca3FacilityMigrationData>> facilitiesMap = facilities.stream()
                .filter(f -> StringUtils.isNotBlank(f.getAccountBusinessId()))
                .collect(Collectors.groupingBy(Cca3FacilityMigrationData::getAccountBusinessId,
                        Collectors.mapping(f -> f, Collectors.toList())));

        List<TargetUnitAccountBusinessInfoDTO> liveAccounts = targetUnitAccountQueryService
                .getActiveAccountsByBusinessIds(facilitiesMap.keySet());

        // Validate
        cca3ExistingFacilitiesMigrationRunValidator.validate(facilitiesMap, liveAccounts, errors);

        // Convert to account states
        Map<Long, Cca3FacilityMigrationAccountState> accountStates = new HashMap<>();
        liveAccounts.forEach(account -> {
            Cca3FacilityMigrationAccountState accountState = Cca3FacilityMigrationAccountState.builder()
                    .accountId(account.getAccountId())
                    .accountBusinessId(account.getBusinessId())
                    .facilityMigrationDataList(facilitiesMap.get(account.getBusinessId()))
                    .cca3Participating(facilitiesMap.get(account.getBusinessId()).stream().anyMatch(Cca3FacilityMigrationData::getParticipatingInCca3Scheme))
                    .build();

            // Find accounts migrated
            RequestCreateValidationResult result = cca3ExistingFacilitiesMigrationAccountProcessingCreateValidator
                    .validateAction(account.getAccountId());

            if (!result.isValid()) {
                accountState.getErrors().add(Cca3ExistingFacilitiesMigrationViolation.Cca3ExistingFacilitiesMigrationViolationMessage.ACCOUNT_NOT_ELIGIBLE_FOR_MIGRATION.getMessage());
            }

            accountStates.put(account.getAccountId(), accountState);
        });

        return accountStates;
    }
}
