package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.validation;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.common.validation.PerformanceDataUploadViolation;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceDataCalculatedMetrics;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceDataReferenceDetails;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain.PrimaryDetermination;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain.TP6ParseExcelCellsReferenceEnum;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain.TP6PerformanceData;

import java.util.ArrayList;
import java.util.List;

@Service
public class TP6PerformanceDataPrimaryDeterminationContextValidator extends TP6PerformanceDataSectionConstraintValidatorService<PrimaryDetermination>
        implements TP6PerformanceDataSectionContextValidator {

    @Override
    public BusinessValidationResult validate(final PerformanceDataReferenceDetails referenceDetails, final TP6PerformanceData performanceData) {
        PrimaryDetermination determination = performanceData.getPrimaryDetermination();

        if(!ObjectUtils.isEmpty(determination)) {
            List<PerformanceDataUploadViolation> violations = this.validateSection(determination, referenceDetails, performanceData);

            return BusinessValidationResult.builder().valid(violations.isEmpty()).violations(violations).build();
        }

        return BusinessValidationResult.valid();
    }

    @Override
    protected List<PerformanceDataUploadViolation> validateSection(final PrimaryDetermination section, final PerformanceDataReferenceDetails referenceDetails, final TP6PerformanceData performanceData) {
        List<PerformanceDataUploadViolation> violations = new ArrayList<>();
        final PerformanceDataCalculatedMetrics calculatedData = referenceDetails.getPerformanceDataCalculatedMetrics();

        // Validate tpCarbonFactor
        violations.addAll(TP6PerformanceDataUploadValidationHelper.getViolations(this.getSectionName(),
                TP6PerformanceDataUploadValidationHelper
                        .validateCalculateValueEquals(TP6ParseExcelCellsReferenceEnum.TARGET_PERIOD_CARBON_FACTOR)
                        .process(calculatedData.getTpCarbonFactor(), section.getTpCarbonFactor())
        ));

        // Validate energyCarbonUnderTarget
        violations.addAll(TP6PerformanceDataUploadValidationHelper.getViolations(this.getSectionName(),
                TP6PerformanceDataUploadValidationHelper
                        .validateCalculateValueEquals(TP6ParseExcelCellsReferenceEnum.AMOUNT_ENERGY_USED_UNDER_TARGET)
                        .process(calculatedData.getEnergyCarbonUnderTarget(), section.getEnergyCarbonUnderTarget())
        ));

        // Validate carbonUnderTarget
        violations.addAll(TP6PerformanceDataUploadValidationHelper.getViolations(this.getSectionName(),
                TP6PerformanceDataUploadValidationHelper
                        .validateCalculateValueEquals(TP6ParseExcelCellsReferenceEnum.AMOUNT_CO2_EMITTED_UNDER_TARGET)
                        .process(calculatedData.getCarbonUnderTarget(), section.getCarbonUnderTarget())
        ));

        // Validate co2Emissions
        violations.addAll(TP6PerformanceDataUploadValidationHelper.getViolations(this.getSectionName(),
                TP6PerformanceDataUploadValidationHelper
                        .validateCalculateValueEquals(TP6ParseExcelCellsReferenceEnum.CARBON_DIOXIDE_EMITTED)
                        .process(calculatedData.getCo2Emissions(), section.getCo2Emissions())
        ));

        // Validate surplusUsed
        violations.addAll(TP6PerformanceDataUploadValidationHelper.getViolations(this.getSectionName(),
                TP6PerformanceDataUploadValidationHelper
                        .validateCalculateValueEquals(TP6ParseExcelCellsReferenceEnum.BANKED_SURPLUS_USED)
                        .process(calculatedData.getSurplusUsed(), section.getSurplusUsed())
        ));

        // Validate surplusGained
        violations.addAll(TP6PerformanceDataUploadValidationHelper.getViolations(this.getSectionName(),
                TP6PerformanceDataUploadValidationHelper
                        .validateCalculateValueEquals(TP6ParseExcelCellsReferenceEnum.SURPLUS_GAINED)
                        .process(calculatedData.getSurplusGained(), section.getSurplusGained())
        ));

        // Validate priBuyOutCarbon
        violations.addAll(TP6PerformanceDataUploadValidationHelper.getViolations(this.getSectionName(),
                TP6PerformanceDataUploadValidationHelper
                        .validateCalculateValueEquals(TP6ParseExcelCellsReferenceEnum.BUY_OUT_REQUIRED)
                        .process(calculatedData.getPriBuyOutCarbon(), section.getPriBuyOutCarbon())
        ));

        // Validate priBuyOutCost
        violations.addAll(TP6PerformanceDataUploadValidationHelper.getViolations(this.getSectionName(),
                TP6PerformanceDataUploadValidationHelper
                        .validateCalculateValueEquals(TP6ParseExcelCellsReferenceEnum.BUY_OUT_COST)
                        .process(calculatedData.getPriBuyOutCost(), section.getPriBuyOutCost())
        ));

        return violations;
    }

    @Override
    protected String getSectionName() {
        return PrimaryDetermination.class.getName();
    }
}
