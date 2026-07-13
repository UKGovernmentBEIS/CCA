package uk.gov.cca.api.targetperiodreporting.performancedatafacility.transform;

import org.apache.commons.lang3.ObjectUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityBaselineAndTargets;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Cca3FacilityBaselineAndTargets;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityBaselineData;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityBaselineEnergyConsumption;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityTargetComposition;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityTargets;
import uk.gov.cca.api.underlyingagreement.domain.facilities.ProductVariableEnergyConsumptionData;
import uk.gov.netz.api.common.config.MapperConfig;

import java.time.Year;
import java.util.List;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface PerformanceDataFacilityReferenceDataMapper {

    @Mapping(target = "baselineVariableEnergy", expression = "java(facilityBaselineEnergyConsumption.getTotalBaselineVariableEnergy(baselineData.getBaselineDate()).orElse(null))")
    PerformanceDataFacilityBaselineAndTargets toPerformanceDataFacilityBaselineAndTargets(FacilityTargetComposition targetComposition,
                                                                                    FacilityBaselineData baselineData,
                                                                                    FacilityBaselineEnergyConsumption facilityBaselineEnergyConsumption,
                                                                                    FacilityTargets facilityTargets,
                                                                                    Year targetPeriodYear);

    @AfterMapping
    default void setProducts(@MappingTarget PerformanceDataFacilityBaselineAndTargets baselineTargets, Year targetPeriodYear) {
        if(!ObjectUtils.isEmpty(baselineTargets.getVariableEnergyConsumptionDataByProduct())) {
            List<ProductVariableEnergyConsumptionData> products = baselineTargets.getVariableEnergyConsumptionDataByProduct().stream()
                    .filter(p -> p.getBaselineYear().getValue() <= targetPeriodYear.getValue())
                    .toList();
            baselineTargets.setVariableEnergyConsumptionDataByProduct(products);
        }
    }

    default PerformanceDataFacilityBaselineAndTargets toPerformanceDataFacilityBaselineAndTargets(Cca3FacilityBaselineAndTargets baselineAndTargets,
                                                                                      Year targetPeriodYear) {
        return this.toPerformanceDataFacilityBaselineAndTargets(baselineAndTargets.getTargetComposition(), baselineAndTargets.getBaselineData(),
                baselineAndTargets.getFacilityBaselineEnergyConsumption(), baselineAndTargets.getFacilityTargets(), targetPeriodYear);
    }
}
