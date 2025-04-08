package uk.gov.cca.api.targetperiodreporting.performancedata.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataEntity;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.PerformanceBuyOutSurplusDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.AccountPerformanceReportDetailsDTO;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface AccountPerformanceReportDetailsMapper {

    @Mapping(target = "targetPeriodReport", source = "entity.data.targetPeriodReport")
    @Mapping(target = "throughputUnit", source = "entity.data.targetsPreviousPerformance.throughputUnit")
    @Mapping(target = "energyCarbonUnit", source = "entity.data.targetsPreviousPerformance.energyCarbonUnit")
    @Mapping(target = "targetType", source = "entity.data.targetsPreviousPerformance.targetType")
    @Mapping(target = "tpPerformance", source = "entity.data.performanceResult.tpPerformance")
    @Mapping(target = "percentTarget", source = "entity.data.targetsPreviousPerformance.percentTarget")
    @Mapping(target = "tpPerformancePercent", source = "entity.data.performanceResult.tpPerformancePercent")
    @Mapping(target = "tpOutcome", source = "entity.data.performanceResult.tpOutcome")
    @Mapping(target = "carbonSurplusBuyOutDTO", source = "entity.data.surplusBuyOutDetermination")
    @Mapping(target = "secondaryMoASurplusBuyOutDTO", source = "entity.data.surplusBuyOutDetermination")
    AccountPerformanceReportDetailsDTO toPerformanceReportDetailsDTO(PerformanceDataEntity entity);

    @Mapping(target = "performanceDataId", source = "id")
    @Mapping(target = "energyCarbonUnit", source = "data.targetsPreviousPerformance.energyCarbonUnit")
    @Mapping(target = "tpOutcome", source = "data.performanceResult.tpOutcome")
    @Mapping(target = "bankedSurplus", source = "data.targetsPreviousPerformance.bankedSurplus")
    @Mapping(target = "surplusGained", source = "data.surplusBuyOutDetermination.surplusGained")
    @Mapping(target = "priBuyOutCarbon", source = "data.surplusBuyOutDetermination.priBuyOutCarbon")
    @Mapping(target = "priBuyOutCost", source = "data.surplusBuyOutDetermination.priBuyOutCost")
    @Mapping(target = "totalPriBuyOutCarbon", source = "data.surplusBuyOutDetermination.totalPriBuyOutCarbon")
    PerformanceBuyOutSurplusDetailsDTO toPerformanceBuyOutSurplusDetailsDTO(PerformanceDataEntity entity);
}
