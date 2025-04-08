package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.validation;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataContainer;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.PerformanceDataSubmissionType;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.common.validation.PerformanceDataUploadViolation;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceDataCalculatedMetrics;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceDataReferenceDetails;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain.PrimaryDetermination;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain.SecondaryDetermination;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain.TP6ParseExcelCellsReferenceEnum;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain.TP6PerformanceData;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TP6PerformanceDataSecondaryDeterminationContextValidator extends TP6PerformanceDataSectionConstraintValidatorService<SecondaryDetermination>
        implements TP6PerformanceDataSectionContextValidator {

    @Override
    public BusinessValidationResult validate(final PerformanceDataReferenceDetails referenceDetails, final TP6PerformanceData performanceData) {
        SecondaryDetermination determination = performanceData.getSecondaryDetermination();

        if(!ObjectUtils.isEmpty(determination)) {
            List<PerformanceDataUploadViolation> violations = this.validateSection(determination, referenceDetails, performanceData);

            return BusinessValidationResult.builder().valid(violations.isEmpty()).violations(violations).build();
        }

        return BusinessValidationResult.valid();
    }

    @Override
    protected List<PerformanceDataUploadViolation> validateSection(final SecondaryDetermination section, final PerformanceDataReferenceDetails referenceDetails, TP6PerformanceData performanceData) {
        List<PerformanceDataUploadViolation> violations = new ArrayList<>();

        // Validate equality with primary determination
        validateWithPrimaryData(section, performanceData.getPrimaryDetermination(), violations);

        // Validate data for submission type
        validateWithSubmissionType(section, performanceData.getSubmissionType(), referenceDetails, violations);

        // Validate calculations
        validateWitCalculatedData( section, referenceDetails.getPerformanceDataCalculatedMetrics(), violations);

        return violations;
    }

    @Override
    protected String getSectionName() {
        return SecondaryDetermination.class.getName();
    }

    private void validateWithPrimaryData(final SecondaryDetermination section, final PrimaryDetermination primaryDetermination,
                                            List<PerformanceDataUploadViolation> violations) {
        // tpCarbonFactor
        violations.addAll(TP6PerformanceDataUploadValidationHelper.getViolations(this.getSectionName(),
                TP6PerformanceDataUploadValidationHelper
                        .validateCalculateValueEquals(TP6ParseExcelCellsReferenceEnum.SUPPLEMENTARY_TARGET_PERIOD_CARBON_FACTOR)
                        .process(primaryDetermination.getTpCarbonFactor(), section.getTpCarbonFactor())
        ));

        // energyCarbonUnderTarget
        violations.addAll(TP6PerformanceDataUploadValidationHelper.getViolations(this.getSectionName(),
                TP6PerformanceDataUploadValidationHelper
                        .validateCalculateValueEquals(TP6ParseExcelCellsReferenceEnum.SUPPLEMENTARY_AMOUNT_ENERGY_USED_UNDER_TARGET)
                        .process(primaryDetermination.getEnergyCarbonUnderTarget(), section.getEnergyCarbonUnderTarget())
        ));

        // carbonUnderTarget
        violations.addAll(TP6PerformanceDataUploadValidationHelper.getViolations(this.getSectionName(),
                TP6PerformanceDataUploadValidationHelper
                        .validateCalculateValueEquals(TP6ParseExcelCellsReferenceEnum.SUPPLEMENTARY_AMOUNT_CO2_EMITTED_UNDER_TARGET)
                        .process(primaryDetermination.getCarbonUnderTarget(), section.getCarbonUnderTarget())
        ));

        // co2Emissions
        violations.addAll(TP6PerformanceDataUploadValidationHelper.getViolations(this.getSectionName(),
                TP6PerformanceDataUploadValidationHelper
                        .validateCalculateValueEquals(TP6ParseExcelCellsReferenceEnum.SUPPLEMENTARY_CARBON_DIOXIDE_EMITTED)
                        .process(primaryDetermination.getCo2Emissions(), section.getCo2Emissions())
        ));

        // priBuyOutCarbon
        violations.addAll(TP6PerformanceDataUploadValidationHelper.getViolations(this.getSectionName(),
                TP6PerformanceDataUploadValidationHelper
                        .validateCalculateValueEquals(TP6ParseExcelCellsReferenceEnum.TOTAL_TARGET_PERIOD_BUY_OUT_REQUIRED)
                        .process(primaryDetermination.getPriBuyOutCarbon(), section.getPriBuyOutCarbon())
        ));
    }

	private void validateWithSubmissionType(final SecondaryDetermination section,
	                                        PerformanceDataSubmissionType currentSubmissionType,
	                                        PerformanceDataReferenceDetails referenceDetails,
	                                        List<PerformanceDataUploadViolation> violations) {
		if (currentSubmissionType.equals(PerformanceDataSubmissionType.PRIMARY)) {
			validateForPrimarySubmissionType(section, violations);
		} else {
			validateForSecondarySubmissionType(section, referenceDetails.getLastUploadedReport(), violations);
		}
	}

	private void validateForSecondarySubmissionType(SecondaryDetermination section,
													PerformanceDataContainer lastUploadedReport,
													List<PerformanceDataUploadViolation> violations) {
		// prevBuyOutCo2
		final BigDecimal expectedPrevBuyOutCo2 = Optional.ofNullable(lastUploadedReport)
				.map(lastReport -> lastReport.getSurplusBuyOutDetermination().getPriBuyOutCarbon())
				.orElse(BigDecimal.ZERO);

		violations.addAll(TP6PerformanceDataUploadValidationHelper.getViolations(this.getSectionName(),
				TP6PerformanceDataUploadValidationHelper
						.validateReferenceValueEquals(TP6ParseExcelCellsReferenceEnum.PREVIOUS_BUY_OUT_AFTER_USE_OF_SURPLUS)
						.process(expectedPrevBuyOutCo2, section.getPrevBuyOutCo2())
		));

		// prevSurplusUsed
		final BigDecimal expectedPrevSurplusUsed = Optional.ofNullable(lastUploadedReport)
				.map(lastReport -> lastReport.getSurplusBuyOutDetermination().getSurplusUsed())
				.orElse(BigDecimal.ZERO);

		violations.addAll(TP6PerformanceDataUploadValidationHelper.getViolations(this.getSectionName(),
				TP6PerformanceDataUploadValidationHelper
						.validateReferenceValueEquals(TP6ParseExcelCellsReferenceEnum.PREVIOUS_SURPLUS_USED)
						.process(expectedPrevSurplusUsed, section.getPrevSurplusUsed())
		));

		// prevSurplusGained
		final BigDecimal expectedPrevSurplusGained = Optional.ofNullable(lastUploadedReport)
				.map(lastReport -> lastReport.getSurplusBuyOutDetermination().getSurplusGained())
				.orElse(BigDecimal.ZERO);

		violations.addAll(TP6PerformanceDataUploadValidationHelper.getViolations(this.getSectionName(),
				TP6PerformanceDataUploadValidationHelper
						.validateReferenceValueEquals(TP6ParseExcelCellsReferenceEnum.SURPLUS_GAINED_IN_TP)
						.process(expectedPrevSurplusGained, section.getPrevSurplusGained())
		));
	}

	private void validateForPrimarySubmissionType(SecondaryDetermination section,
	                                                 List<PerformanceDataUploadViolation> violations) {

		// validate prevBuyOutCo2
		violations.addAll(TP6PerformanceDataUploadValidationHelper.getViolations(this.getSectionName(),
				TP6PerformanceDataUploadValidationHelper
						.validateIsEmpty(TP6ParseExcelCellsReferenceEnum.PREVIOUS_BUY_OUT_AFTER_USE_OF_SURPLUS)
						.process(section.getPrevBuyOutCo2())
		));

        // prevSurplusUsed
        violations.addAll(TP6PerformanceDataUploadValidationHelper.getViolations(this.getSectionName(),
                TP6PerformanceDataUploadValidationHelper
                        .validateIsEmpty(TP6ParseExcelCellsReferenceEnum.PREVIOUS_SURPLUS_USED)
                        .process(section.getPrevSurplusUsed())
        ));

        // prevSurplusGained
        violations.addAll(TP6PerformanceDataUploadValidationHelper.getViolations(this.getSectionName(),
                TP6PerformanceDataUploadValidationHelper
                        .validateIsEmpty(TP6ParseExcelCellsReferenceEnum.SURPLUS_GAINED_IN_TP)
                        .process(section.getPrevSurplusGained())
        ));
    }

    private void validateWitCalculatedData(final SecondaryDetermination section, final PerformanceDataCalculatedMetrics calculatedData,
                                           List<PerformanceDataUploadViolation> violations) {
        // Validate secondaryBuyOutCo2
        violations.addAll(TP6PerformanceDataUploadValidationHelper.getViolations(this.getSectionName(),
                TP6PerformanceDataUploadValidationHelper
                        .validateCalculateValueEquals(TP6ParseExcelCellsReferenceEnum.SECONDARY_BUY_OUT_REQUIRED)
                        .process(calculatedData.getSecondaryBuyOutCo2(), section.getSecondaryBuyOutCo2())
        ));

        // Validate secondaryBuyOutCost
        violations.addAll(TP6PerformanceDataUploadValidationHelper.getViolations(this.getSectionName(),
                TP6PerformanceDataUploadValidationHelper
                        .validateCalculateValueEquals(TP6ParseExcelCellsReferenceEnum.SECONDARY_BUY_OUT_COST)
                        .process(calculatedData.getSecondaryBuyOutCost(), section.getSecondaryBuyOutCost())
        ));
    }
}
