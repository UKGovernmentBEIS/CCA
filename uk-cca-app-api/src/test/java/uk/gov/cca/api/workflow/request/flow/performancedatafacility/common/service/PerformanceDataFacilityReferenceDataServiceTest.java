package uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYear;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYearsContainer;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Cca3FacilityBaselineAndTargets;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityBaselineData;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityBaselineEnergyConsumption;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityDetails;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityItem;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityTargetComposition;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityTargets;
import uk.gov.cca.api.underlyingagreement.domain.facilities.ProductVariableEnergyConsumptionData;
import uk.gov.cca.api.underlyingagreement.domain.facilities.TargetImprovementType;
import uk.gov.cca.api.underlyingagreement.domain.facilities.VariableEnergyDepictionType;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementQueryService;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityBaselineAndTargets;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.domain.PerformanceDataFacilityReferenceData;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerformanceDataFacilityReferenceDataServiceTest {

    @InjectMocks
    private PerformanceDataFacilityReferenceDataService performanceDataFacilityDigitalFormReferenceDataService;

    @Mock
    private UnderlyingAgreementQueryService underlyingAgreementQueryService;

    @Mock
    private TargetPeriodService targetPeriodService;

    @Test
    void getReferenceData() {
        final Long accountId = 1L;
        final String facilityBusinessId = "facilityBusinessId";
        final Year targetPeriodYear = Year.of(2018);
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP7;

        final TargetPeriodDetailsDTO targetPeriod = TargetPeriodDetailsDTO.builder()
                .targetPeriodYearsContainer(TargetPeriodYearsContainer.builder()
                        .targetPeriodYears(List.of(TargetPeriodYear.builder().build()))
                        .build())
                .build();
        final UnderlyingAgreementContainer underlyingAgreement = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(Facility.builder()
                                .facilityItem(FacilityItem.builder()
                                        .facilityId(facilityBusinessId)
                                        .facilityDetails(FacilityDetails.builder()
                                                .participatingSchemeVersions(Set.of(SchemeVersion.CCA_3))
                                                .build())
                                        .cca3BaselineAndTargets(Cca3FacilityBaselineAndTargets.builder()
                                                .targetComposition(FacilityTargetComposition.builder()
                                                        .measurementType(MeasurementType.ENERGY_KWH)
                                                        .build())
                                                .baselineData(FacilityBaselineData.builder()
                                                        .baselineDate(LocalDate.of(2018, 1, 1))
                                                        .isTwelveMonths(true)
                                                        .usedReportingMechanism(true)
                                                        .energyCarbonFactor(BigDecimal.valueOf(0.01))
                                                        .build())
                                                .facilityBaselineEnergyConsumption(FacilityBaselineEnergyConsumption.builder()
                                                        .totalFixedEnergy(BigDecimal.TEN)
                                                        .variableEnergyType(VariableEnergyDepictionType.BY_PRODUCT)
                                                        .baselineVariableEnergy(BigDecimal.TWO)
                                                        .totalThroughput(BigDecimal.ONE)
                                                        .throughputUnit("unit")
                                                        .variableEnergyConsumptionDataByProduct(List.of(
                                                                ProductVariableEnergyConsumptionData.builder().productName("name").baselineYear(targetPeriodYear).build(),
                                                                ProductVariableEnergyConsumptionData.builder().productName("name2").baselineYear(Year.of(2019)).build()
                                                        ))
                                                        .build())
                                                .facilityTargets(FacilityTargets.builder()
                                                        .improvements(Map.of(TargetImprovementType.TP8, BigDecimal.ONE))
                                                        .build())
                                                .build())
                                        .build())
                                .build()))
                        .build())
                .build();
        final PerformanceDataFacilityReferenceData expected = PerformanceDataFacilityReferenceData.builder()
                .baselineAndTargets(PerformanceDataFacilityBaselineAndTargets.builder()
                        .baselineDate(LocalDate.of(2018, 1, 1))
                        .isTwelveMonths(true)
                        .energyCarbonFactor(BigDecimal.valueOf(0.01))
                        .measurementType(MeasurementType.ENERGY_KWH)
                        .usedReportingMechanism(true)
                        .improvements(Map.of(TargetImprovementType.TP8, BigDecimal.ONE))
                        .totalFixedEnergy(BigDecimal.TEN)
                        .variableEnergyType(VariableEnergyDepictionType.BY_PRODUCT)
                        .baselineVariableEnergy(BigDecimal.TWO)
                        .totalThroughput(BigDecimal.ONE)
                        .throughputUnit("unit")
                        .baselineEnergyCarbonIntensity(BigDecimal.TWO)
                        .variableEnergyConsumptionDataByProduct(List.of(
                                ProductVariableEnergyConsumptionData.builder().productName("name").baselineYear(targetPeriodYear).build()
                        ))
                        .build())
                .tpMultiplier(BigDecimal.ONE)
                .build();

        when(targetPeriodService.getTargetPeriodDetailsByTargetPeriodType(targetPeriodType)).thenReturn(targetPeriod);
        when(underlyingAgreementQueryService.getUnderlyingAgreementContainerByAccountId(accountId)).thenReturn(underlyingAgreement);

        // Invoke
        final PerformanceDataFacilityReferenceData result = performanceDataFacilityDigitalFormReferenceDataService
                .getReferenceData(accountId, facilityBusinessId, targetPeriodYear, targetPeriodType);

        // Verify
        assertThat(result).isEqualTo(expected);
        verify(targetPeriodService, times(1)).getTargetPeriodDetailsByTargetPeriodType(targetPeriodType);
        verify(underlyingAgreementQueryService, times(1)).getUnderlyingAgreementContainerByAccountId(accountId);
    }
}
