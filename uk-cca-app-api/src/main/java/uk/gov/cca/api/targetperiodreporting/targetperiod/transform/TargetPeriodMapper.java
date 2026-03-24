package uk.gov.cca.api.targetperiodreporting.targetperiod.transform;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriod;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYear;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodYearDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodInfoDTO;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.netz.api.common.exception.BusinessException;

import java.time.Year;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface TargetPeriodMapper {

    TargetPeriodDetailsDTO toTargetPeriodDetailsDTO(TargetPeriod entity);

    @Mapping(target = "buyOutEndDate", source = "entity.buyOutPrimaryPaymentDeadline")
    TargetPeriodYearDTO toTargetPeriodDTO(TargetPeriod entity, Year targetYear);

    @AfterMapping
    default void setTargetPeriodYear(@MappingTarget TargetPeriodYearDTO targetPeriodDTO, TargetPeriod entity, Year targetYear) {
        TargetPeriodYear targetYearPeriod = entity.getTargetPeriodYearsContainer()
                .getTargetPeriodYear(targetYear)
                .orElseThrow(() -> new BusinessException(CcaErrorCode.TARGET_PERIOD_YEAR_NOT_FOUND));

        targetPeriodDTO.setPerformanceDataStartDate(targetYearPeriod.getReportingStartDate());
        targetPeriodDTO.setPerformanceDataEndDate(targetYearPeriod.getReportingEndDate());
    }

    TargetPeriodInfoDTO toTargetPeriodInfoDTO(TargetPeriod entity);
}

