package uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountBusinessInfoDTO;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.validation.BusinessViolation;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.domain.Cca3FacilityMigrationData;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.workflow.request.flow.common.domain.RequestCreateActionEmptyPayload;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Cca3ExistingFacilitiesMigrationRunValidatorTest {

    @InjectMocks
    private Cca3ExistingFacilitiesMigrationRunValidator validator;

    @Mock
    private Cca3ExistingFacilitiesMigrationCreateValidator cca3ExistingFacilitiesMigrationCreateValidator;

    @Mock
    private DataValidator<Cca3FacilityMigrationData> dataValidator;

    @Test
    void validateCreation() {
        when(cca3ExistingFacilitiesMigrationCreateValidator
                .validateAction(CompetentAuthorityEnum.ENGLAND, RequestCreateActionEmptyPayload.builder().build()))
                .thenReturn(RequestCreateValidationResult.builder().valid(true).build());

        // Invoke
        validator.validateCreation();

        // Verify
        verify(cca3ExistingFacilitiesMigrationCreateValidator, times(1))
                .validateAction(CompetentAuthorityEnum.ENGLAND, RequestCreateActionEmptyPayload.builder().build());
    }

    @Test
    void validateCreation_not_valid() {
        when(cca3ExistingFacilitiesMigrationCreateValidator
                .validateAction(CompetentAuthorityEnum.ENGLAND, RequestCreateActionEmptyPayload.builder().build()))
                .thenReturn(RequestCreateValidationResult.builder().valid(false).build());

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class, () -> validator.validateCreation());

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(CcaErrorCode.CCA3_EXISTING_FACILITIES_MIGRATION_RUN_EXIST);
        verify(cca3ExistingFacilitiesMigrationCreateValidator, times(1))
                .validateAction(CompetentAuthorityEnum.ENGLAND, RequestCreateActionEmptyPayload.builder().build());
    }

    @Test
    void validate() {
        final Cca3FacilityMigrationData facilityMigration = Cca3FacilityMigrationData.builder().facilityBusinessId("facilityId").build();
        final Map<String, List<Cca3FacilityMigrationData>> facilitiesMap = Map.of(
                "accountId", List.of(facilityMigration)
        );
        final List<TargetUnitAccountBusinessInfoDTO> liveAccounts = List.of(
                TargetUnitAccountBusinessInfoDTO.builder().businessId("accountId").build()
        );
        List<String> errors = new ArrayList<>();

        when(dataValidator.validate(facilityMigration)).thenReturn(Optional.empty());

        // Invoke
        validator.validate(facilitiesMap, liveAccounts, errors);

        // Verify
        assertThat(errors).isEmpty();
        verify(dataValidator, times(1)).validate(facilityMigration);
    }

    @Test
    void validate_data_not_valid() {
        final Cca3FacilityMigrationData facilityMigration = Cca3FacilityMigrationData.builder().facilityBusinessId("facilityId").build();
        final Map<String, List<Cca3FacilityMigrationData>> facilitiesMap = Map.of(
                "accountId", List.of(facilityMigration)
        );
        final List<TargetUnitAccountBusinessInfoDTO> liveAccounts = List.of(
                TargetUnitAccountBusinessInfoDTO.builder().businessId("accountId").build()
        );
        List<String> errors = new ArrayList<>();

        when(dataValidator.validate(facilityMigration))
                .thenReturn(Optional.of(new BusinessViolation("section", "error")));

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class, () ->
                validator.validate(facilitiesMap, liveAccounts, errors));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(CcaErrorCode.CCA3_EXISTING_FACILITIES_MIGRATION_RUN_CSV_FAILED);
        assertThat(errors).isNotEmpty();
        verify(dataValidator, times(1)).validate(facilityMigration);
    }

    @Test
    void validate_duplicate_facility() {
        final Cca3FacilityMigrationData facilityMigration1 = Cca3FacilityMigrationData.builder().facilityBusinessId("facilityId1").build();
        final Cca3FacilityMigrationData facilityMigration2 = Cca3FacilityMigrationData.builder().facilityBusinessId("facilityId2").build();
        final Cca3FacilityMigrationData facilityMigration3 = Cca3FacilityMigrationData.builder().facilityBusinessId("facilityId1").build();
        final Map<String, List<Cca3FacilityMigrationData>> facilitiesMap = Map.of(
                "accountId", List.of(facilityMigration1, facilityMigration2, facilityMigration3)
        );
        final List<TargetUnitAccountBusinessInfoDTO> liveAccounts = List.of(
                TargetUnitAccountBusinessInfoDTO.builder().businessId("accountId").build()
        );
        List<String> errors = new ArrayList<>();

        when(dataValidator.validate(any())).thenReturn(Optional.empty());

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class, () ->
                validator.validate(facilitiesMap, liveAccounts, errors));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(CcaErrorCode.CCA3_EXISTING_FACILITIES_MIGRATION_RUN_CSV_FAILED);
        assertThat(errors).isNotEmpty();
        verify(dataValidator, times(3)).validate(any());
    }

    @Test
    void validate_account_not_valid() {
        final Cca3FacilityMigrationData facilityMigration = Cca3FacilityMigrationData.builder().facilityBusinessId("facilityId").build();
        final Map<String, List<Cca3FacilityMigrationData>> facilitiesMap = Map.of(
                "accountId", List.of(facilityMigration)
        );
        final List<TargetUnitAccountBusinessInfoDTO> liveAccounts = List.of(
                TargetUnitAccountBusinessInfoDTO.builder().businessId("accountId2").build()
        );
        List<String> errors = new ArrayList<>();

        when(dataValidator.validate(facilityMigration)).thenReturn(Optional.empty());

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class, () ->
                validator.validate(facilitiesMap, liveAccounts, errors));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(CcaErrorCode.CCA3_EXISTING_FACILITIES_MIGRATION_RUN_CSV_FAILED);
        assertThat(errors).isNotEmpty();
        verify(dataValidator, times(1)).validate(facilityMigration);
    }
}
