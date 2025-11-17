package uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.validation;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountBusinessInfoDTO;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.domain.Cca3FacilityMigrationData;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.workflow.request.flow.common.domain.RequestCreateActionEmptyPayload;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class Cca3ExistingFacilitiesMigrationRunValidator {

    private final Cca3ExistingFacilitiesMigrationCreateValidator cca3ExistingFacilitiesMigrationCreateValidator;
    private final DataValidator<Cca3FacilityMigrationData> validator;

    public void validateCreation() {
        final RequestCreateValidationResult validationResult = cca3ExistingFacilitiesMigrationCreateValidator
                .validateAction(CompetentAuthorityEnum.ENGLAND, RequestCreateActionEmptyPayload.builder().build());

        if(!validationResult.isValid()) {
            throw new BusinessException(CcaErrorCode.CCA3_EXISTING_FACILITIES_MIGRATION_RUN_EXIST);
        }
    }

    public void validate(final Map<String, List<Cca3FacilityMigrationData>> facilitiesMap,
                         final List<TargetUnitAccountBusinessInfoDTO> liveAccounts,
                         List<String> errors) {
        // Validate data
        facilitiesMap.values()
                .forEach(facilities -> validateData(facilities, errors));

        // Validate duplications
        validateDuplicateFacilities(facilitiesMap, errors);

        // Validate accounts existence
        validateEligibleAccounts(facilitiesMap, liveAccounts, errors);

        if(!errors.isEmpty()) {
            log.error("Error validating csv file {}", errors);
            throw new BusinessException(CcaErrorCode.CCA3_EXISTING_FACILITIES_MIGRATION_RUN_CSV_FAILED, errors.toArray());
        }
    }

    private void validateData(final List<Cca3FacilityMigrationData> facilities, List<String> errors) {
        facilities.forEach(facility ->
                validator.validate(facility)
                    .map(violation -> Arrays.stream(violation.getData()).map(data -> facility.getFacilityBusinessId() + data.toString()).toList())
                    .ifPresent(errors::addAll)
        );
    }

    private void validateDuplicateFacilities(final Map<String, List<Cca3FacilityMigrationData>> facilitiesMap, List<String> errors) {
        List<String> facilityIds = facilitiesMap.values().stream()
                .flatMap(Collection::stream)
                .map(Cca3FacilityMigrationData::getFacilityBusinessId)
                .filter(StringUtils::isNotBlank)
                .toList();
        Set<String> duplicates = facilityIds.stream()
                .filter(f -> Collections.frequency(facilityIds, f) > 1)
                .collect(Collectors.toSet());

        if(!duplicates.isEmpty()) {
            errors.add(String.format("%s exist more than once", duplicates));
        }
    }

    private void validateEligibleAccounts(Map<String, List<Cca3FacilityMigrationData>> facilitiesMap, List<TargetUnitAccountBusinessInfoDTO> liveAccounts,
                                          List<String> errors) {
        Set<String> accountsForMigration = facilitiesMap.keySet();
        Set<String> liveAccountsBusinessIds = liveAccounts.stream()
                .map(TargetUnitAccountBusinessInfoDTO::getBusinessId).collect(Collectors.toSet());

        Set<String> diffs = SetUtils.difference(accountsForMigration, liveAccountsBusinessIds);

        if(!diffs.isEmpty()) {
            diffs.forEach(diff -> errors.add(String.format("Account %s is not eligible for migration", diff)));
        }
    }
}
