package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.validation;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountDetailsDTO;
import uk.gov.cca.api.common.domain.AgreementCompositionType;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.common.validation.PerformanceDataUploadViolation;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceDataCalculatedMetrics;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceDataReferenceDetails;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain.PerformanceDataTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain.TP6ParseExcelCellsReferenceEnum;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain.TP6PerformanceData;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class TP6PerformanceDataTargetUnitDetailsContextValidator extends TP6PerformanceDataSectionConstraintValidatorService<PerformanceDataTargetUnitDetails>
        implements TP6PerformanceDataSectionContextValidator {

    @Override
    public BusinessValidationResult validate(final PerformanceDataReferenceDetails referenceDetails, final TP6PerformanceData performanceData) {
        PerformanceDataTargetUnitDetails targetUnitDetails = performanceData.getTargetUnitDetails();

        if(!ObjectUtils.isEmpty(targetUnitDetails)) {
            List<PerformanceDataUploadViolation> violations = this.validateSection(targetUnitDetails, referenceDetails, performanceData);

            return BusinessValidationResult.builder().valid(violations.isEmpty()).violations(violations).build();
        }

        return BusinessValidationResult.valid();
    }

    @Override
    protected List<PerformanceDataUploadViolation> validateSection(final PerformanceDataTargetUnitDetails section, final PerformanceDataReferenceDetails referenceDetails, final TP6PerformanceData performanceData) {
        List<PerformanceDataUploadViolation> violations = new ArrayList<>();

        // Validate if section matches predefined values
        validateWithPredefinedData(section, referenceDetails, performanceData, violations);

        // Validate calculations
        validateWitCalculatedData(section, referenceDetails.getPerformanceDataCalculatedMetrics(), violations);

        return violations;
    }

    @Override
    protected String getSectionName() {
        return PerformanceDataTargetUnitDetails.class.getName();
    }

    private void validateWithPredefinedData(final PerformanceDataTargetUnitDetails section, final PerformanceDataReferenceDetails referenceDetails,
                                            final TP6PerformanceData performanceData, List<PerformanceDataUploadViolation> violations) {

        final TargetUnitAccountDetailsDTO accountDetails = referenceDetails.getAccountDetails();
        final UnderlyingAgreement underlyingAgreement = referenceDetails.getUnderlyingAgreement()
                .getUnderlyingAgreementContainer().getUnderlyingAgreement();

        // tuIdentifier
        violations.addAll(TP6PerformanceDataUploadValidationHelper.getViolations(this.getSectionName(),
                TP6PerformanceDataUploadValidationHelper
                        .validateReferenceEquals(TP6ParseExcelCellsReferenceEnum.TU_IDENTIFIER)
                        .process(accountDetails.getBusinessId(), section.getTuIdentifier())
        ));
        // operatorName
        violations.addAll(TP6PerformanceDataUploadValidationHelper.getViolations(this.getSectionName(),
                TP6PerformanceDataUploadValidationHelper
                        .validateReferenceEquals(TP6ParseExcelCellsReferenceEnum.OPERATOR_NAME)
                        .process(accountDetails.getName(), section.getOperatorName())
        ));
        // numOfFacilities
        violations.addAll(TP6PerformanceDataUploadValidationHelper.getViolations(this.getSectionName(),
                TP6PerformanceDataUploadValidationHelper
                        .validateReferenceEquals(TP6ParseExcelCellsReferenceEnum.NUM_OF_FACILITIES)
                        .process(underlyingAgreement.getFacilities().size(), section.getNumOfFacilities())
        ));
        // energyCarbonUnit
        violations.addAll(TP6PerformanceDataUploadValidationHelper.getViolations(this.getSectionName(),
                TP6PerformanceDataUploadValidationHelper
                        .validateReferenceEquals(TP6ParseExcelCellsReferenceEnum.ENERGY_CARBON_UNIT)
                        .process(underlyingAgreement.getTargetPeriod6Details().getTargetComposition().getMeasurementType(), section.getEnergyCarbonUnit())
        ));
        // throughputUnit
        String throughputUnit;
        if(performanceData.getTargetType().equals(AgreementCompositionType.NOVEM)) {
            throughputUnit = null;
        }
        else{
            String unaThroughputUnit = underlyingAgreement.getTargetPeriod6Details().getTargetComposition().getThroughputUnit();
            String sectorThroughputUnit = referenceDetails.getUnderlyingAgreement().getUnderlyingAgreementContainer().getSectorThroughputUnit();
            throughputUnit = unaThroughputUnit != null ? unaThroughputUnit : sectorThroughputUnit;
        }

        violations.addAll(TP6PerformanceDataUploadValidationHelper.getViolations(this.getSectionName(),
                TP6PerformanceDataUploadValidationHelper
                        .validateReferenceEquals(TP6ParseExcelCellsReferenceEnum.THROUGHPUT_UNIT)
                        .process(throughputUnit, section.getThroughputUnit())
        ));
        // byStartDate
        violations.addAll(TP6PerformanceDataUploadValidationHelper.getViolations(this.getSectionName(),
                TP6PerformanceDataUploadValidationHelper
                        .validateReferenceEquals(TP6ParseExcelCellsReferenceEnum.BASE_YEAR_START_DATE)
                        .process(underlyingAgreement.getTargetPeriod6Details().getBaselineData().getBaselineDate(), section.getByStartDate())
        ));
        // byEnergyCarbon
        violations.addAll(TP6PerformanceDataUploadValidationHelper.getViolations(this.getSectionName(),
                TP6PerformanceDataUploadValidationHelper
                        .validateReferenceValueEquals(TP6ParseExcelCellsReferenceEnum.BASE_YEAR_ENERGY)
                        .process(underlyingAgreement.getTargetPeriod6Details().getBaselineData().getEnergy(), section.getByEnergyCarbon())
        ));
        // byThroughput
        violations.addAll(TP6PerformanceDataUploadValidationHelper.getViolations(this.getSectionName(),
                TP6PerformanceDataUploadValidationHelper
                        .validateReferenceValueEquals(TP6ParseExcelCellsReferenceEnum.BASE_YEAR_THROUGHPUT)
                        .process(underlyingAgreement.getTargetPeriod6Details().getBaselineData().getThroughput(), section.getByThroughput())
        ));
        // percentTarget
        BigDecimal improvement = ObjectUtils.isEmpty(underlyingAgreement.getTargetPeriod6Details().getTargets().getImprovement())
                ? BigDecimal.ZERO
                : underlyingAgreement.getTargetPeriod6Details().getTargets().getImprovement().movePointLeft(2).setScale(9, RoundingMode.HALF_UP);
        violations.addAll(TP6PerformanceDataUploadValidationHelper.getViolations(this.getSectionName(),
                TP6PerformanceDataUploadValidationHelper
                        .validateReferenceValueEquals(TP6ParseExcelCellsReferenceEnum.PERCENT_IMPROVEMENT_TARGET)
                        .process(improvement, section.getPercentTarget())
        ));
        // bankedSurplus
        violations.addAll(TP6PerformanceDataUploadValidationHelper.getViolations(this.getSectionName(),
                TP6PerformanceDataUploadValidationHelper
                        .validateReferenceValueEquals(TP6ParseExcelCellsReferenceEnum.BANKED_SURPLUS_FROM_PREVIOUS_TP)
                        .process(BigDecimal.ZERO, section.getBankedSurplus())
        ));
    }

    private void validateWitCalculatedData(final PerformanceDataTargetUnitDetails section, final PerformanceDataCalculatedMetrics performanceDataCalculatedMetrics,
                                            List<PerformanceDataUploadViolation> violations) {
        // by_performance
        violations.addAll(TP6PerformanceDataUploadValidationHelper.getViolations(this.getSectionName(),
                TP6PerformanceDataUploadValidationHelper
                        .validateCalculateValueEquals(TP6ParseExcelCellsReferenceEnum.BASE_YEAR_PERFORMANCE)
                        .process(performanceDataCalculatedMetrics.getByPerformance(), section.getByPerformance())
        ));
        // numerical_target
        violations.addAll(TP6PerformanceDataUploadValidationHelper.getViolations(this.getSectionName(),
                TP6PerformanceDataUploadValidationHelper
                        .validateCalculateValueEquals(TP6ParseExcelCellsReferenceEnum.NUMERICAL_TARGET)
                        .process(performanceDataCalculatedMetrics.getNumericalTarget(), section.getNumericalTarget())
        ));
    }
}
