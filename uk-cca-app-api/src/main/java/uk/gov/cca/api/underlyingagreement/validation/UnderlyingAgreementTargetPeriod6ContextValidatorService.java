package uk.gov.cca.api.underlyingagreement.validation;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.AgreementCompositionType;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.BaselineData;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetComposition;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetPeriod6Details;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.Targets;
import uk.gov.netz.api.files.attachments.service.FileAttachmentService;
import uk.gov.netz.api.files.common.FileType;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_AGREEMENT_COMPOSITION_TYPE;
import static uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_ATTACHMENT_TYPE;
import static uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_TARGETS;
import static uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_TARGET_COMPOSITION_PERFORMANCE;
import static uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_TARGET_UNIT_THROUGHPUT_MEASURED;
import static uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_TARGET_UNIT_TYPE;
import static uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_THROUGHPUT_UNIT;

@Service
public class UnderlyingAgreementTargetPeriod6ContextValidatorService extends UnderlyingAgreementSectionConstraintValidatorService<TargetPeriod6Details> implements UnderlyingAgreementSectionContextValidator {

    private final FileAttachmentService fileAttachmentService;

    public UnderlyingAgreementTargetPeriod6ContextValidatorService(DataValidator<TargetPeriod6Details> validator, FileAttachmentService fileAttachmentService) {
        super(validator);
        this.fileAttachmentService = fileAttachmentService;
    }

    @Override
    public BusinessValidationResult validate(final UnderlyingAgreementContainer container) {
        TargetPeriod6Details section = container.getUnderlyingAgreement().getTargetPeriod6Details();

        List<UnderlyingAgreementViolation> violations = super.validateEmptySection(section);
        if(violations.isEmpty()){
            violations = this.validateSection(section, container);
            
            // Validate calculated target for ABSOLUTE and TP6
            validateTargetTP6(section, violations);
        }

        return BusinessValidationResult.builder().valid(violations.isEmpty()).violations(violations).build();
    }

    @Override
    protected List<UnderlyingAgreementViolation> validateSection(final TargetPeriod6Details section, final UnderlyingAgreementContainer container) {
        List<UnderlyingAgreementViolation> violations = new ArrayList<>();

        // Validate data
        super.validate(section).ifPresent(violations::add);

        // Business validations
        validateTargetComposition(section.getTargetComposition(), container.getSectorMeasurementType(), 
        		container.getSectorThroughputUnit(), violations);
        validateBaselineData(section, violations);
        validateTargets(section, violations);
        validateFiles(section, violations);

        return violations;
    }

	@Override
    protected String getSectionName() {
        return TargetPeriod6Details.class.getName();
    }

    private void validateTargetComposition(final TargetComposition targetComposition, 
    		MeasurementType sectorMeasurementType, String throughputUnit, List<UnderlyingAgreementViolation> violations) {
    	
        // Validate measurement type with sector/subsector scheme
        if(!targetComposition.getMeasurementType().getCategory().equals(sectorMeasurementType.getCategory())) {
            violations.add(new UnderlyingAgreementViolation(this.getSectionName(), INVALID_TARGET_UNIT_TYPE, targetComposition.getMeasurementType()));
        }
        
        // Validate is target unit throughput measured with sector/subsector scheme
        if(ObjectUtils.isEmpty(throughputUnit) && !ObjectUtils.isEmpty(targetComposition.getIsTargetUnitThroughputMeasured())) {
            violations.add(new UnderlyingAgreementViolation(this.getSectionName(), INVALID_TARGET_UNIT_THROUGHPUT_MEASURED, targetComposition.getIsTargetUnitThroughputMeasured()));
        }
        
        // Validate is throughput unit with both sector/subsector scheme and target composition data
        if((Boolean.TRUE.equals(targetComposition.getIsTargetUnitThroughputMeasured()) 
        		|| (ObjectUtils.isEmpty(throughputUnit) 
        				&& !AgreementCompositionType.NOVEM.equals(targetComposition.getAgreementCompositionType()))) 
        		&& ObjectUtils.isEmpty(targetComposition.getThroughputUnit())) {
            violations.add(new UnderlyingAgreementViolation(this.getSectionName(), INVALID_THROUGHPUT_UNIT, targetComposition.getThroughputUnit()));
        }
    }

    private void validateBaselineData(final TargetPeriod6Details section, List<UnderlyingAgreementViolation> violations) {
        BaselineData baselineData = section.getBaselineData();

        if (!AgreementCompositionType.RELATIVE.equals(section.getTargetComposition().getAgreementCompositionType())) {

            // Should not exist for NOVEM & ABSOLUTE
            if(!ObjectUtils.isEmpty(baselineData.getPerformance())) {
                violations.add(new UnderlyingAgreementViolation(this.getSectionName(), INVALID_AGREEMENT_COMPOSITION_TYPE));
            }
            if(AgreementCompositionType.ABSOLUTE.equals(section.getTargetComposition().getAgreementCompositionType()) &&
                    (ObjectUtils.isEmpty(baselineData.getUsedReportingMechanism())
                        || ObjectUtils.isEmpty(baselineData.getThroughput()))) {
                    violations.add(new UnderlyingAgreementViolation(this.getSectionName(), INVALID_AGREEMENT_COMPOSITION_TYPE));
                }

        }
        else {
            // Mandatory for RELATIVE
            if(ObjectUtils.isEmpty(baselineData.getUsedReportingMechanism())
                    || ObjectUtils.isEmpty(baselineData.getThroughput())
                    || ObjectUtils.isEmpty(baselineData.getPerformance())) {
                violations.add(new UnderlyingAgreementViolation(this.getSectionName(), INVALID_AGREEMENT_COMPOSITION_TYPE));
            }

            // Validate Performance
            Optional.ofNullable(baselineData.getThroughput()).ifPresentOrElse(
                    throughput -> {
                        BigDecimal performance = throughput.signum() == 0
                                ? BigDecimal.ZERO
                                : baselineData.getEnergy().divide(throughput, 7, RoundingMode.HALF_UP);
                        if(performance.compareTo(baselineData.getPerformance()) != 0) {
                            violations.add(new UnderlyingAgreementViolation(this.getSectionName(), INVALID_TARGET_COMPOSITION_PERFORMANCE, baselineData.getPerformance()));
                        }
                    },
                    () -> violations.add(new UnderlyingAgreementViolation(this.getSectionName(), INVALID_TARGET_COMPOSITION_PERFORMANCE))
            );
        }
    }
    
    private void validateTargets(final TargetPeriod6Details section, List<UnderlyingAgreementViolation> violations) {
    	TargetComposition targetComposition = section.getTargetComposition();
    	BaselineData baselineData = section.getBaselineData();
    	Targets targets = section.getTargets();
    	// Calculated target should not exist for NOVEM
		if (AgreementCompositionType.NOVEM.equals(targetComposition.getAgreementCompositionType()) 
				&& !ObjectUtils.isEmpty(targets.getTarget())) {
			violations.add(new UnderlyingAgreementViolation(this.getSectionName(), INVALID_TARGETS));
		}
		// Validate calculated target for RELATIVE
		if (AgreementCompositionType.RELATIVE.equals(targetComposition.getAgreementCompositionType())) {
			Optional.ofNullable(baselineData.getThroughput()).ifPresent(
                    throughput -> {
                        BigDecimal baseline = throughput.signum() == 0
                                ? BigDecimal.ZERO
                                : baselineData.getEnergy().divide(throughput, MathContext.DECIMAL128);
                        if(UnderlyingAgreementValidationHelper.calculateTarget(baseline, targets.getImprovement()).compareTo(targets.getTarget()) != 0) {
                            violations.add(new UnderlyingAgreementViolation(this.getSectionName(), INVALID_TARGETS, targets.getTarget()));
                        }
                    });
		}
	}

    private void validateFiles(final TargetPeriod6Details section, List<UnderlyingAgreementViolation> violations) {
        Optional<UUID> calculatorFile = Optional.ofNullable(section.getTargetComposition().getCalculatorFile());

        if(calculatorFile.isPresent()) {
            FileDTO file = fileAttachmentService.getFileDTO(calculatorFile.get().toString());
            if(!FileType.XLSX.getMimeTypes().contains(file.getFileType())
            		&& !FileType.XLS.getMimeTypes().contains(file.getFileType())) {
                violations.add(new UnderlyingAgreementViolation(this.getSectionName(), INVALID_ATTACHMENT_TYPE));
            }
        }
    }
    
    private void validateTargetTP6(TargetPeriod6Details section, List<UnderlyingAgreementViolation> violations) {
		if(AgreementCompositionType.ABSOLUTE.equals(section.getTargetComposition().getAgreementCompositionType()) && 
				UnderlyingAgreementValidationHelper.calculateTarget(section.getBaselineData().getEnergy(), section.getTargets().getImprovement())
					.compareTo(section.getTargets().getTarget()) != 0) {
		    violations.add(new UnderlyingAgreementViolation(this.getSectionName(), INVALID_TARGETS, section.getTargets().getTarget()));
		}
	}
}
