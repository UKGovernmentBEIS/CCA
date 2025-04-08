package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.validation;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.common.domain.AgreementCompositionType;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.common.validation.PerformanceDataUploadViolation;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceDataCalculatedMetrics;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceDataReferenceDetails;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain.TP6ParseExcelCellsReferenceEnum;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain.TP6PerformanceData;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain.TargetPeriodPerformanceResult;

import java.util.ArrayList;
import java.util.List;

@Service
public class TP6PerformanceDataTargetPeriodPerformanceResultContextValidator extends TP6PerformanceDataSectionConstraintValidatorService<TargetPeriodPerformanceResult>
        implements TP6PerformanceDataSectionContextValidator {

    @Override
    public BusinessValidationResult validate(PerformanceDataReferenceDetails referenceDetails, TP6PerformanceData performanceData) {
        TargetPeriodPerformanceResult performanceResult = performanceData.getPerformanceResult();

        if(!ObjectUtils.isEmpty(performanceResult)) {
            List<PerformanceDataUploadViolation> violations = this.validateSection(performanceResult, referenceDetails, performanceData);

            return BusinessValidationResult.builder().valid(violations.isEmpty()).violations(violations).build();
        }

        return BusinessValidationResult.valid();
    }

    @Override
    protected List<PerformanceDataUploadViolation> validateSection(TargetPeriodPerformanceResult section, PerformanceDataReferenceDetails referenceDetails, TP6PerformanceData performanceData) {
        List<PerformanceDataUploadViolation> violations = new ArrayList<>();

        // Validate novem data
        validateNovemData(section, performanceData, violations);

        // Validate calculated values
        validateCalculatedValues(section, referenceDetails.getPerformanceDataCalculatedMetrics(), violations);

        // Validate tpOutcome
        validateTpOutcome(section, referenceDetails.getPerformanceDataCalculatedMetrics(), violations);

        return violations;
    }

    @Override
    protected String getSectionName() {
        return TargetPeriodPerformanceResult.class.getName();
    }

    private void validateNovemData(final TargetPeriodPerformanceResult section, final TP6PerformanceData performanceData,
                                   List<PerformanceDataUploadViolation> violations) {
        if(performanceData.getTargetType().equals(AgreementCompositionType.NOVEM)) {
            // targetEnergyCarbonTpThroughput
            violations.addAll(TP6PerformanceDataUploadValidationHelper.getViolations(this.getSectionName(),
                    TP6PerformanceDataUploadValidationHelper
                            .validateNotEmpty(TP6ParseExcelCellsReferenceEnum.TARGET_ENERGY_CARBON_THROUGHPUT)
                            .process(section.getTargetEnergyCarbonTpThroughput())
            ));
            // byEnergyCarbonTpThroughput
            violations.addAll(TP6PerformanceDataUploadValidationHelper.getViolations(this.getSectionName(),
                    TP6PerformanceDataUploadValidationHelper
                            .validateNotEmpty(TP6ParseExcelCellsReferenceEnum.ENERGY_CARBON_THROUGHPUT)
                            .process(section.getByEnergyCarbonTpThroughput())
            ));
        }
        else {
            // targetEnergyCarbonTpThroughput
            violations.addAll(TP6PerformanceDataUploadValidationHelper.getViolations(this.getSectionName(),
                    TP6PerformanceDataUploadValidationHelper
                            .validateIsEmpty(TP6ParseExcelCellsReferenceEnum.TARGET_ENERGY_CARBON_THROUGHPUT)
                            .or(TP6PerformanceDataUploadValidationHelper.validateValueIsZero(TP6ParseExcelCellsReferenceEnum.TARGET_ENERGY_CARBON_THROUGHPUT))
                            .process(section.getTargetEnergyCarbonTpThroughput())
            ));
            // byEnergyCarbonTpThroughput
            violations.addAll(TP6PerformanceDataUploadValidationHelper.getViolations(this.getSectionName(),
                    TP6PerformanceDataUploadValidationHelper
                            .validateIsEmpty(TP6ParseExcelCellsReferenceEnum.ENERGY_CARBON_THROUGHPUT)
                            .or(TP6PerformanceDataUploadValidationHelper.validateValueIsZero(TP6ParseExcelCellsReferenceEnum.ENERGY_CARBON_THROUGHPUT))
                            .process(section.getByEnergyCarbonTpThroughput())
            ));
        }
    }

    private void validateCalculatedValues(final TargetPeriodPerformanceResult section, final PerformanceDataCalculatedMetrics performanceDataCalculatedMetrics,
                                       List<PerformanceDataUploadViolation> violations) {
        // Validate tpPerformance
        violations.addAll(TP6PerformanceDataUploadValidationHelper.getViolations(this.getSectionName(),
                TP6PerformanceDataUploadValidationHelper
                        .validateCalculateValueEquals(TP6ParseExcelCellsReferenceEnum.TARGET_PERIOD_ENERGY)
                        .process(performanceDataCalculatedMetrics.getTpPerformance(), section.getTpPerformance())
        ));

        // Validate tpPerformancePercent
        violations.addAll(TP6PerformanceDataUploadValidationHelper.getViolations(this.getSectionName(),
                TP6PerformanceDataUploadValidationHelper
                        .validateCalculateValueEquals(TP6ParseExcelCellsReferenceEnum.TARGET_PERIOD_IMPROVEMENT_PERCENTAGE)
                        .process(performanceDataCalculatedMetrics.getTpPerformancePercent(), section.getTpPerformancePercent())
        ));
    }

    private void validateTpOutcome(final TargetPeriodPerformanceResult section, final PerformanceDataCalculatedMetrics performanceDataCalculatedMetrics,
                                   List<PerformanceDataUploadViolation> violations) {
        violations.addAll(TP6PerformanceDataUploadValidationHelper.getViolations(this.getSectionName(),
                TP6PerformanceDataUploadValidationHelper
                        .validateCalculateEquals(TP6ParseExcelCellsReferenceEnum.TARGET_PERIOD_RESULT)
                        .process(performanceDataCalculatedMetrics.getTpOutcome(), section.getTpOutcome())
        ));
    }
}
