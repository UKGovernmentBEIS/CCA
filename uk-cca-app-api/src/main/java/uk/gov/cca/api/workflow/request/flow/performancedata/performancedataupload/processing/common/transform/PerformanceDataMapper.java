package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.transform;

import static uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.common.utils.PerformanceDataUploadUtility.OTHER_FUEL_NAME_DEFAULT_PATTERN;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.FuelUsed;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataContainer;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.FixedConversionFactor;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceData;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain.ActualTargetPeriodPerformance;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain.TP6PerformanceData;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

@Mapper(componentModel = "spring", config = MapperConfig.class, imports = {PerformanceDataSubmissionType.class})
public interface PerformanceDataMapper {
    
    @Mapping(target = "targetsPreviousPerformance.targetType", source = "tp6PerformanceData.targetType")
    @Mapping(target = "targetsPreviousPerformance", source = "tp6PerformanceData.targetUnitDetails")
    @Mapping(target = "actualPerformance", source = "tp6PerformanceData.actualTargetPeriodPerformance")
    @Mapping(target = "actualPerformance.fuelsUsed", source = "tp6PerformanceData", qualifiedByName = "mapFuelUsed")
    @Mapping(target = "performanceResult", source = "tp6PerformanceData.performanceResult")
    @Mapping(target = "surplusBuyOutDetermination", source = "tp6PerformanceData.primaryDetermination")
    PerformanceDataContainer toTP6PerformanceDataContainer(TP6PerformanceData tp6PerformanceData,
                                                           FileInfoDTO targetPeriodReport);
    
    default PerformanceDataContainer toPerformanceDataContainer(PerformanceData performanceData,
                                                                FileInfoDTO targetPeriodReport, TargetPeriodType targetPeriodType) {
        return switch (targetPeriodType) {
            case TP6 -> toTP6PerformanceDataContainer((TP6PerformanceData) performanceData, targetPeriodReport);
            default -> null;
        };
    }
    
    @Named("mapFuelUsed")
    static List<FuelUsed> mapFuelUsed(TP6PerformanceData tp6PerformanceData) {
        if (tp6PerformanceData == null || tp6PerformanceData.getActualTargetPeriodPerformance() == null) {
            return new ArrayList<>();
        }
        
        List<FuelUsed> fuelsUsed = new ArrayList<>();
        ActualTargetPeriodPerformance actualTargetPeriodPerformance = tp6PerformanceData
                .getActualTargetPeriodPerformance();
        
        // Map Carbon Factors
        if (!CollectionUtils.isEmpty(actualTargetPeriodPerformance.getCarbonFactors())) {
            actualTargetPeriodPerformance.getCarbonFactors().stream()
                    .filter(fuel -> !(OTHER_FUEL_NAME_DEFAULT_PATTERN.matcher(fuel.getName()).matches() && fuel.getConsumption().compareTo(BigDecimal.ZERO) == 0))
                    .map(fuel -> FuelUsed.builder().consumption(fuel.getConsumption())
                            .conversionFactor(fuel.getConversionFactor()).name(fuel.getName()).build())
                    .forEach(fuelsUsed::add);
        }
        
        // Map Energy Data
        if (!MapUtils.isEmpty(actualTargetPeriodPerformance.getEnergyData())) {
            actualTargetPeriodPerformance.getEnergyData().entrySet().stream()
                    .map(entry -> new FuelUsed(entry.getKey().getDescription(),
                            FixedConversionFactor.getTP6ValueByMeasurementType(entry.getKey(),
                                    tp6PerformanceData.getTargetUnitDetails().getEnergyCarbonUnit()),
                            entry.getValue()))
                    .forEach(fuelsUsed::add);
        }
        
        return fuelsUsed;
    }
    
    @AfterMapping
    default void setSecondary(TP6PerformanceData tp6PerformanceData, @MappingTarget PerformanceDataContainer performanceDataContainer) {
        performanceDataContainer.getSurplusBuyOutDetermination().setTotalPriBuyOutCarbon(tp6PerformanceData.getPrimaryDetermination().getPriBuyOutCarbon());
        if (PerformanceDataSubmissionType.SECONDARY.equals(tp6PerformanceData.getSubmissionType())) {
            performanceDataContainer.getSurplusBuyOutDetermination().setSecondaryBuyOutCo2(tp6PerformanceData.getSecondaryDetermination().getSecondaryBuyOutCo2());
            performanceDataContainer.getSurplusBuyOutDetermination().setSecondaryBuyOutCost(tp6PerformanceData.getSecondaryDetermination().getSecondaryBuyOutCost());
            performanceDataContainer.getSurplusBuyOutDetermination().setPrevBuyOutCo2(tp6PerformanceData.getSecondaryDetermination().getPrevBuyOutCo2());
            performanceDataContainer.getSurplusBuyOutDetermination().setPrevSurplusUsed(tp6PerformanceData.getSecondaryDetermination().getPrevSurplusUsed());
            performanceDataContainer.getSurplusBuyOutDetermination().setPrevSurplusGained(tp6PerformanceData.getSecondaryDetermination().getPrevSurplusGained());
        }
    }
}
