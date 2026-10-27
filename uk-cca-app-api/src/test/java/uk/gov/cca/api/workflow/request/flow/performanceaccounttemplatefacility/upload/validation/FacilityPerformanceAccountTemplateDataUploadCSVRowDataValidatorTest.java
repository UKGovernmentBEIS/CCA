package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.facility.domain.dto.FacilityDTO;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.common.domain.ActionCategoryType;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.common.domain.EnergyConsumptionOrCarbonEmissionsImpactedType;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.domain.SupplyDemandSideMeasure;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.common.validation.FacilityPerformanceAccountTemplateDataViolation;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataCsvErrorEntry;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataUploadCsvData;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FacilityPerformanceAccountTemplateDataUploadCSVRowDataValidatorTest {

    @InjectMocks
    private FacilityPerformanceAccountTemplateDataUploadCSVRowDataValidator validator;

    @Mock
    private DataValidator<FacilityPerformanceAccountTemplateDataUploadCsvData> dataValidator;

    @Test
    void validate() {
        final String filename = "filename";
        final Year targetYear = Year.of(2026);
        final Long facilityId1 = 1L;
        final String facilityBusinessId1 = "facilityBusinessId1";
        final Long facilityId2 = 2L;
        final String facilityBusinessId2 = "facilityBusinessId2";
        final Map<String, FacilityDTO> persistedFacilities = Map.of(
                "facilityBusinessId1", FacilityDTO.builder()
                        .id(facilityId1)
                        .facilityBusinessId(facilityBusinessId1)
                        .createdDate(LocalDateTime.of(2026, 6, 1, 0, 0, 0))
                        .build(),
                "facilityBusinessId2", FacilityDTO.builder()
                        .id(facilityId2)
                        .facilityBusinessId(facilityBusinessId2)
                        .createdDate(LocalDateTime.of(2026, 6, 1, 0, 0, 0))
                        .build());
        final Integer rowNumber = 0;
        final List<FacilityPerformanceAccountTemplateDataCsvErrorEntry> csvRowErrors = new ArrayList<>();
        final FacilityPerformanceAccountTemplateDataUploadCsvData rowData = FacilityPerformanceAccountTemplateDataUploadCsvData.builder()
                .filename(filename)
                .rowNumber(rowNumber)
                .facilityBusinessId(facilityBusinessId1)
                .actionCategoryType(ActionCategoryType.ENERGY_MANAGEMENT)
                .supplyDemandSideMeasure(SupplyDemandSideMeasure.DEMAND_SIDE)
                .savingActionsImplemented("saving actions implemented")
                .reasonsForImplementation("reasons")
                .implementationDate(LocalDate.of(2026, 5, 2))
                .fixedEnergyConsumptionOrCarbonEmissionsImpacted(EnergyConsumptionOrCarbonEmissionsImpactedType.FIXED)
                .energyConsumptionOrCarbonEmissionsImpactedPercentage(BigDecimal.valueOf(45.5))
                .expectedExtentOfChangeImplementedPercentage(BigDecimal.valueOf(45.5))
                .expectedSavingsFromTheChangeImplementedPercentage(BigDecimal.valueOf(45.5))
                .notes("notes")
                .build();

        when(dataValidator.validate(rowData)).thenReturn(Optional.empty());

        // invoke
        List<FacilityPerformanceAccountTemplateDataViolation> result =
                validator.validate(filename, targetYear, persistedFacilities, rowNumber, csvRowErrors, rowData);

        // verify
        assertThat(result.isEmpty()).isTrue();
        verify(dataValidator, times(1)).validate(rowData);
    }

    @Test
    void validate_not_valid() {
        final String filename = "filename";
        final Year targetYear = Year.of(2026);
        final Long facilityId1 = 1L;
        final String facilityBusinessId1 = "facilityBusinessId1";
        final Long facilityId2 = 2L;
        final String facilityBusinessId2 = "facilityBusinessId2";
        final Map<String, FacilityDTO> facilityByBusinessId = Map.of(
                "facilityBusinessId1", FacilityDTO.builder()
                        .id(facilityId1)
                        .facilityBusinessId(facilityBusinessId1)
                        .createdDate(LocalDateTime.of(2026, 6, 1, 0, 0, 0))
                        .build(),
                "facilityBusinessId2", FacilityDTO.builder()
                        .id(facilityId2)
                        .facilityBusinessId(facilityBusinessId2)
                        .createdDate(LocalDateTime.of(2027, 6, 1, 0, 0, 0))
                        .build());
        final Integer rowNumber = 0;
        final List<FacilityPerformanceAccountTemplateDataCsvErrorEntry> csvRowErrors = new ArrayList<>();
        final FacilityPerformanceAccountTemplateDataUploadCsvData rowData = FacilityPerformanceAccountTemplateDataUploadCsvData.builder()
                .filename(filename)
                .rowNumber(rowNumber)
                .facilityBusinessId(facilityBusinessId2)
                .actionCategoryType(ActionCategoryType.ENERGY_MANAGEMENT)
                .supplyDemandSideMeasure(SupplyDemandSideMeasure.DEMAND_SIDE)
                .savingActionsImplemented("saving actions implemented")
                .reasonsForImplementation("reasons")
                .implementationDate(LocalDate.of(2027, 5, 2))
                .fixedEnergyConsumptionOrCarbonEmissionsImpacted(EnergyConsumptionOrCarbonEmissionsImpactedType.FIXED)
                .energyConsumptionOrCarbonEmissionsImpactedPercentage(BigDecimal.valueOf(45.5))
                .expectedExtentOfChangeImplementedPercentage(BigDecimal.valueOf(45.5))
                .expectedSavingsFromTheChangeImplementedPercentage(BigDecimal.valueOf(45.5))
                .notes("notes")
                .build();

        when(dataValidator.validate(rowData)).thenReturn(Optional.of(new FacilityPerformanceAccountTemplateDataViolation(FacilityPerformanceAccountTemplateDataUploadCsvData.class.getName(),
                FacilityPerformanceAccountTemplateDataViolation.FacilityPerformanceAccountTemplateDataViolationMessage.INVALID_PERFORMANCE_ACCOUNT_TEMPLATE_DATA)));

        // invoke
        List<FacilityPerformanceAccountTemplateDataViolation> result =
                validator.validate(filename, targetYear, facilityByBusinessId, rowNumber, csvRowErrors, rowData);

        // verify
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result).contains(
                new FacilityPerformanceAccountTemplateDataViolation(FacilityPerformanceAccountTemplateDataUploadCsvData.class.getName(),
                        FacilityPerformanceAccountTemplateDataViolation.FacilityPerformanceAccountTemplateDataViolationMessage.INVALID_PERFORMANCE_ACCOUNT_TEMPLATE_DATA));
        verify(dataValidator, times(1)).validate(rowData);
    }

}
