package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.validation;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDetailsDTO;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.common.validation.PerformanceDataUploadViolation;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceDataReferenceDetails;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain.ActualTargetPeriodPerformance;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.OtherFuel;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain.TP6ParseExcelCellsReferenceEnum;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain.TP6PerformanceData;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TP6PerformanceDataActualTargetPeriodPerformanceContextValidator extends TP6PerformanceDataSectionConstraintValidatorService<ActualTargetPeriodPerformance>
        implements TP6PerformanceDataSectionContextValidator {

    @Override
    public BusinessValidationResult validate(PerformanceDataReferenceDetails referenceDetails, TP6PerformanceData performanceData) {
        ActualTargetPeriodPerformance performance = performanceData.getActualTargetPeriodPerformance();

        if(!ObjectUtils.isEmpty(performance)) {
            List<PerformanceDataUploadViolation> violations = this.validateSection(performance, referenceDetails, performanceData);

            return BusinessValidationResult.builder().valid(violations.isEmpty()).violations(violations).build();
        }

        return BusinessValidationResult.valid();
    }

    @Override
    protected List<PerformanceDataUploadViolation> validateSection(ActualTargetPeriodPerformance section, PerformanceDataReferenceDetails referenceDetails, TP6PerformanceData performanceData) {
        List<PerformanceDataUploadViolation> violations = new ArrayList<>();

        final TargetUnitAccountDetailsDTO accountDetails = referenceDetails.getAccountDetails();

        // actualTuIdentifier
        violations.addAll(TP6PerformanceDataUploadValidationHelper.getViolations(this.getSectionName(),
                TP6PerformanceDataUploadValidationHelper
                        .validateReferenceEquals(TP6ParseExcelCellsReferenceEnum.ACTUAL_TU_IDENTIFIER)
                        .process(accountDetails.getBusinessId(), section.getActualTuIdentifier())
        ));

        // carbonFactors
        validateCarbonFactors(section.getCarbonFactors(), violations);

        // tpEnergy
        violations.addAll(TP6PerformanceDataUploadValidationHelper.getViolations(this.getSectionName(),
                TP6PerformanceDataUploadValidationHelper
                        .validateCalculateValueEquals(TP6ParseExcelCellsReferenceEnum.TOTAL_ENERGY)
                        .process(referenceDetails.getPerformanceDataCalculatedMetrics().getTpEnergy(), section.getTpEnergy())
        ));

        // tpChpDeliveredElectricity
        violations.addAll(TP6PerformanceDataUploadValidationHelper.getViolations(this.getSectionName(),
                TP6PerformanceDataUploadValidationHelper
                        .validateCalculateValueEquals(TP6ParseExcelCellsReferenceEnum.CHP_DELIVERED_ELECTRICITY)
                        .process(referenceDetails.getPerformanceDataCalculatedMetrics().getTpChpDeliveredElectricity(), section.getTpChpDeliveredElectricity())
        ));

        // reportingThroughput
        BigDecimal reportingThroughput = Optional.ofNullable(section.getAdjustedThroughput())
                .orElse(section.getActualThroughput());
        violations.addAll(TP6PerformanceDataUploadValidationHelper.getViolations(this.getSectionName(),
                TP6PerformanceDataUploadValidationHelper
                        .validateCalculateValueEquals(TP6ParseExcelCellsReferenceEnum.REPORTING_THROUGHPUT)
                        .process(reportingThroughput, section.getReportingThroughput())
        ));

        return violations;
    }

    @Override
    protected String getSectionName() {
        return ActualTargetPeriodPerformance.class.getName();
    }

    private void validateCarbonFactors(List<OtherFuel> customConversionFactors, List<PerformanceDataUploadViolation> violations) {
        boolean containsEmptyValues = customConversionFactors.stream()
                .anyMatch(factor -> ObjectUtils.isEmpty(StringUtils.trim(factor.getName())) && (factor.getConsumption().compareTo(BigDecimal.ZERO) != 0));

        if(containsEmptyValues) {
            violations.add(new PerformanceDataUploadViolation(
                    this.getSectionName(),
                    PerformanceDataUploadViolation.PerformanceDataUploadViolationMessage.INVALID_SECTION_DATA,
                    PerformanceDataUploadViolation.PerformanceDataUploadViolationMessage.INVALID_CARBON_FACTORS_DATA.getMessage()));
        }
    }
}
