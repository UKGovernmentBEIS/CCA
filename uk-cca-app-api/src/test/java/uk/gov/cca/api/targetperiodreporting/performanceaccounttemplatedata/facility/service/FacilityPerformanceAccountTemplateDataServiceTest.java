package uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.common.domain.ActionCategoryType;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.common.domain.EnergyConsumptionOrCarbonEmissionsImpactedType;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.domain.FacilityPerformanceAccountTemplateDataContainer;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.domain.FacilityPerformanceAccountTemplateDataEntity;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.domain.FacilityPerformanceAccountTemplateSavingAction;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.domain.SupplyDemandSideMeasure;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.repository.FacilityPerformanceAccountTemplateDataEntityRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FacilityPerformanceAccountTemplateDataServiceTest {

    @InjectMocks
    private FacilityPerformanceAccountTemplateDataService facilityPerformanceAccountTemplateDataService;

    @Mock
    private FacilityPerformanceAccountTemplateDataEntityRepository facilityPerformanceAccountTemplateDataRepository;

    @Test
    void submitFacilityPerformanceAccountTemplateData() {
        final Long facilityId = 1L;
        final Year targetPeriodYear = Year.of(2026);
        final int reportVersion = 1;
        final List<FacilityPerformanceAccountTemplateSavingAction> savingActions = List.of(FacilityPerformanceAccountTemplateSavingAction.builder()
                .actionCategoryType(ActionCategoryType.ENERGY_MANAGEMENT)
                .supplyDemandSideMeasure(SupplyDemandSideMeasure.DEMAND_SIDE)
                .savingActionsImplemented("saving")
                .implementationDate(LocalDate.of(targetPeriodYear.getValue(), Month.APRIL, 1))
                .reasonsForImplementation("Reasons")
                .fixedEnergyConsumptionOrCarbonEmissionsImpacted(EnergyConsumptionOrCarbonEmissionsImpactedType.FIXED_AND_VARIABLE)
                .energyConsumptionOrCarbonEmissionsImpactedPercentage(BigDecimal.valueOf(12.6))
                .expectedExtentOfChangeImplementedPercentage(BigDecimal.valueOf(20.6))
                .expectedSavingsFromTheChangeImplementedPercentage(BigDecimal.valueOf(20.6))
                .estimatedChangeInEnergyConsumptionPercentage(BigDecimal.valueOf(20.6))
                .notes("notes")
                .build());
        final List<FacilityPerformanceAccountTemplateSavingAction> savingActionsOld = List.of(FacilityPerformanceAccountTemplateSavingAction.builder()
                .actionCategoryType(ActionCategoryType.NO_ACTION)
                .notes("notes")
                .build());
        final FacilityPerformanceAccountTemplateDataContainer oldData = FacilityPerformanceAccountTemplateDataContainer.builder()
                .savingActions(savingActionsOld)
                .build();
        final FacilityPerformanceAccountTemplateDataEntity existedEntity = FacilityPerformanceAccountTemplateDataEntity.builder()
                .facilityId(facilityId)
                .targetPeriodYear(targetPeriodYear)
                .data(oldData)
                .reportVersion(1)
                .submissionDate(LocalDate.of(2026, 2, 2).atStartOfDay())
                .build();
        final FacilityPerformanceAccountTemplateDataContainer newData = FacilityPerformanceAccountTemplateDataContainer.builder()
                .savingActions(savingActions)
                .build();

        when(facilityPerformanceAccountTemplateDataRepository.findByFacilityIdAndTargetPeriodYear(facilityId, targetPeriodYear))
                .thenReturn(Optional.ofNullable(existedEntity));
        when(facilityPerformanceAccountTemplateDataRepository.save(any(FacilityPerformanceAccountTemplateDataEntity.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        // invoke
        int result = facilityPerformanceAccountTemplateDataService.submitFacilityPerformanceAccountTemplateData(newData, facilityId, targetPeriodYear);

        // verify
        assertThat(result).isEqualTo(reportVersion + 1);
        assertThat(existedEntity.getData()).isEqualTo(newData);
        assertThat(existedEntity.getReportVersion()).isEqualTo(reportVersion + 1);
    }
}
