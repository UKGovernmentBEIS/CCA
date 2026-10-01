package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.domain.FacilityPerformanceAccountTemplateSavingAction;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataUploadCsvData;
import uk.gov.netz.api.common.config.MapperConfig;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface FacilityPerformanceTemplateDataUploadMapper {

    @Mapping(target = "estimatedChangeInEnergyConsumptionPercentage", expression = "java(toEstimatedChangeInEnergyConsumptionPercentage(uploadCsvData, targetPeriodYear))")
    FacilityPerformanceAccountTemplateSavingAction toSavingAction(FacilityPerformanceAccountTemplateDataUploadCsvData uploadCsvData, Year targetPeriodYear);

    default BigDecimal toEstimatedChangeInEnergyConsumptionPercentage(FacilityPerformanceAccountTemplateDataUploadCsvData data, Year targetPeriodYear) {
        LocalDate implementationDate = data.getImplementationDate();

        // in case of NO_ACTION
        if (implementationDate == null) {
            return null;
        }

        LocalDate startDate = targetPeriodYear.atDay(1);
        LocalDate endDate = targetPeriodYear.atMonth(Month.DECEMBER).atEndOfMonth();

        BigDecimal proRatedMultiplier = !implementationDate.isBefore(startDate) && !implementationDate.isAfter(endDate)
                ? BigDecimal.ONE.subtract(BigDecimal.valueOf(implementationDate.getMonthValue() - 1L).divide(BigDecimal.valueOf(Month.DECEMBER.getValue()), MathContext.DECIMAL128))
                : BigDecimal.ONE;

        BigDecimal estimatedChangeInEnergyConsumptionPercentage = data.getEnergyConsumptionOrCarbonEmissionsImpactedPercentage().divide(BigDecimal.valueOf(100), MathContext.DECIMAL128)
                .multiply(data.getExpectedExtentOfChangeImplementedPercentage().divide(BigDecimal.valueOf(100), MathContext.DECIMAL128))
                .multiply(data.getExpectedSavingsFromTheChangeImplementedPercentage().divide(BigDecimal.valueOf(100), MathContext.DECIMAL128))
                .multiply(BigDecimal.valueOf(100), MathContext.DECIMAL128);

        // multiplying the calculated value by the prorated multiplier
        return estimatedChangeInEnergyConsumptionPercentage
                .multiply(proRatedMultiplier)
                .setScale(7, RoundingMode.HALF_DOWN);
    }
}
