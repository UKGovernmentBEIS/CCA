package uk.gov.cca.api.underlyingagreement.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.common.domain.AgreementCompositionType;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.BaselineData;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetComposition;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetPeriod5Details;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetPeriod6Details;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.Targets;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementTargetPeriod5ContextValidatorServiceTest {

    @InjectMocks
    private UnderlyingAgreementTargetPeriod5ContextValidatorService validatorService;

    @Mock
    private UnderlyingAgreementTargetPeriod6ContextValidatorService underlyingAgreementTargetPeriod6ContextValidatorService;

    @Test
    void validate() {
        final SchemeVersion schemeVersion = SchemeVersion.CCA_3;
        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .targetPeriod5Details(TargetPeriod5Details.builder()
                                .exist(Boolean.FALSE)
                                .build())
                        .build())
                .build();

        // Invoke
        BusinessValidationResult result = validatorService.validate(container, new UnderlyingAgreementValidationContext(schemeVersion));

        // Verify
        assertThat(result.isValid()).isTrue();
        verifyNoInteractions(underlyingAgreementTargetPeriod6ContextValidatorService);
    }

    @Test
    void validate_no_details() {
        final SchemeVersion schemeVersion = SchemeVersion.CCA_3;
        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .targetPeriod5Details(TargetPeriod5Details.builder()
                                .exist(Boolean.TRUE)
                                .build())
                        .build())
                .build();

        // Invoke
        BusinessValidationResult result = validatorService.validate(container, new UnderlyingAgreementValidationContext(schemeVersion));

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat((List<UnderlyingAgreementViolation>) result.getViolations()).extracting(UnderlyingAgreementViolation::getMessage)
                .containsOnly(UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_TARGET_PERIOD.getMessage());
        verifyNoInteractions(underlyingAgreementTargetPeriod6ContextValidatorService);
    }

    @Test
    void validate_details_should_not_exists() {
        final SchemeVersion schemeVersion = SchemeVersion.CCA_3;
        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .targetPeriod5Details(TargetPeriod5Details.builder()
                                .exist(Boolean.FALSE)
                                .details(TargetPeriod6Details.builder()
                                        .targetComposition(TargetComposition.builder()
                                                .measurementType(MeasurementType.ENERGY_GJ)
                                                .build())
                                        .build())
                                .build())
                        .build())
                .build();

        // Invoke
        BusinessValidationResult result = validatorService.validate(container, new UnderlyingAgreementValidationContext(schemeVersion));

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat((List<UnderlyingAgreementViolation>) result.getViolations()).extracting(UnderlyingAgreementViolation::getMessage)
                .containsOnly(UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_TARGET_PERIOD.getMessage());
        verifyNoInteractions(underlyingAgreementTargetPeriod6ContextValidatorService);
    }

    @Test
    void validate_calculated_target_absolute_correct() {
        final SchemeVersion schemeVersion = SchemeVersion.CCA_3;
        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .targetPeriod5Details(TargetPeriod5Details.builder()
                                .exist(Boolean.TRUE)
                                .details(TargetPeriod6Details.builder()
                                        .targetComposition(TargetComposition.builder()
                                                .measurementType(MeasurementType.ENERGY_GJ)
                                                .agreementCompositionType(AgreementCompositionType.ABSOLUTE)
                                                .build())
                                        .baselineData(BaselineData.builder().energy(BigDecimal.valueOf(1000.123)).build())
                                        .targets(Targets.builder()
                                        		.improvement(BigDecimal.valueOf(20.2))
                                        		.target(BigDecimal.valueOf(1596.196308))
                                        		.build())
                                        .build())
                                .build())
                        .build())
                .build();

        when(underlyingAgreementTargetPeriod6ContextValidatorService
                .validateSection(
                        eq(container.getUnderlyingAgreement().getTargetPeriod5Details().getDetails()),
                        eq(container),
                        argThat(ctx -> ctx.getSchemeVersion().equals(schemeVersion))))
                .thenReturn(List.of());

        // Invoke
        BusinessValidationResult result = validatorService.validate(container, new UnderlyingAgreementValidationContext(schemeVersion));

        // Verify
        assertThat(result.isValid()).isTrue();
        verify(underlyingAgreementTargetPeriod6ContextValidatorService, times(1))
                .validateSection(
                        eq(container.getUnderlyingAgreement().getTargetPeriod5Details().getDetails()),
                        eq(container),
                        argThat(ctx -> ctx.getSchemeVersion().equals(schemeVersion)));
    }
    
    @Test
    void validate_calculated_target_relative_correct() {
        final SchemeVersion schemeVersion = SchemeVersion.CCA_3;
        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .targetPeriod5Details(TargetPeriod5Details.builder()
                                .exist(Boolean.TRUE)
                                .details(TargetPeriod6Details.builder()
                                        .targetComposition(TargetComposition.builder()
                                                .measurementType(MeasurementType.ENERGY_GJ)
                                                .agreementCompositionType(AgreementCompositionType.RELATIVE)
                                                .build())
                                        .baselineData(BaselineData.builder()
                                        		.energy(BigDecimal.valueOf(1000.123))
                                        		.throughput(BigDecimal.valueOf(100.5))
                                        		.build())
                                        .targets(Targets.builder()
                                        		.improvement(BigDecimal.valueOf(20.2))
                                        		.target(BigDecimal.valueOf(7.9412752))
                                        		.build())
                                        .build())
                                .build())
                        .build())
                .build();
        
        when(underlyingAgreementTargetPeriod6ContextValidatorService
                .validateSection(
                        eq(container.getUnderlyingAgreement().getTargetPeriod5Details().getDetails()),
                        eq(container),
                        argThat(ctx -> ctx.getSchemeVersion().equals(schemeVersion))))
                .thenReturn(List.of());

        // Invoke
        BusinessValidationResult result = validatorService.validate(container, new UnderlyingAgreementValidationContext(schemeVersion));

        // Verify
        assertThat(result.isValid()).isTrue();
        verify(underlyingAgreementTargetPeriod6ContextValidatorService, times(1))
                .validateSection(
                        eq(container.getUnderlyingAgreement().getTargetPeriod5Details().getDetails()),
                        eq(container),
                        argThat(ctx -> ctx.getSchemeVersion().equals(schemeVersion)));
    }
    
    @Test
    void validate_calculated_target_relative_incorrect() {
        final SchemeVersion schemeVersion = SchemeVersion.CCA_3;
        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .targetPeriod5Details(TargetPeriod5Details.builder()
                                .exist(Boolean.TRUE)
                                .details(TargetPeriod6Details.builder()
                                        .targetComposition(TargetComposition.builder()
                                                .measurementType(MeasurementType.ENERGY_GJ)
                                                .agreementCompositionType(AgreementCompositionType.ABSOLUTE)
                                                .build())
                                        .baselineData(BaselineData.builder()
                                        		.energy(BigDecimal.valueOf(1000.123))
                                        		.throughput(BigDecimal.valueOf(100.5))
                                        		.build())
                                        .targets(Targets.builder()
                                        		.improvement(BigDecimal.valueOf(20.2))
                                        		.target(BigDecimal.valueOf(7.9412752))
                                        		.build())
                                        .build())
                                .build())
                        .build())
                .build();
        
        when(underlyingAgreementTargetPeriod6ContextValidatorService
                .validateSection(
                        eq(container.getUnderlyingAgreement().getTargetPeriod5Details().getDetails()),
                        eq(container),
                        argThat(ctx -> ctx.getSchemeVersion().equals(schemeVersion))))
                .thenReturn(List.of());

        // Invoke
        BusinessValidationResult result = validatorService.validate(container, new UnderlyingAgreementValidationContext(schemeVersion));

        // Verify
        assertThat(result.isValid()).isFalse();
        assertThat((List<UnderlyingAgreementViolation>) result.getViolations()).extracting(UnderlyingAgreementViolation::getMessage)
        		.containsOnly(UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.INVALID_TARGETS.getMessage());
        verify(underlyingAgreementTargetPeriod6ContextValidatorService, times(1))
                .validateSection(
                        eq(container.getUnderlyingAgreement().getTargetPeriod5Details().getDetails()),
                        eq(container),
                        argThat(ctx -> ctx.getSchemeVersion().equals(schemeVersion)));
    }

    @Test
    void validate_with_details() {
        final SchemeVersion schemeVersion = SchemeVersion.CCA_3;
        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .targetPeriod5Details(TargetPeriod5Details.builder()
                                .exist(Boolean.TRUE)
                                .details(TargetPeriod6Details.builder()
                                        .targetComposition(TargetComposition.builder()
                                                .measurementType(MeasurementType.ENERGY_GJ)
                                                .build())
                                        .build())
                                .build())
                        .build())
                .build();
        
        when(underlyingAgreementTargetPeriod6ContextValidatorService
                .validateSection(
                        eq(container.getUnderlyingAgreement().getTargetPeriod5Details().getDetails()),
                        eq(container),
                        argThat(ctx -> ctx.getSchemeVersion().equals(schemeVersion))))
                .thenReturn(List.of());

        // Invoke
        BusinessValidationResult result = validatorService.validate(container, new UnderlyingAgreementValidationContext(schemeVersion));

        // Verify
        assertThat(result.isValid()).isTrue();
        verify(underlyingAgreementTargetPeriod6ContextValidatorService, times(1))
                .validateSection(
                        eq(container.getUnderlyingAgreement().getTargetPeriod5Details().getDetails()),
                        eq(container),
                        argThat(ctx -> ctx.getSchemeVersion().equals(schemeVersion)));
    }
    
    @Test
    void validate_no_data_valid() {
        final SchemeVersion schemeVersion = SchemeVersion.CCA_3;
        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(UnderlyingAgreement.builder().build())
                .build();

        // Invoke
        BusinessValidationResult result = validatorService.validate(container, new UnderlyingAgreementValidationContext(schemeVersion));

        // Verify
        assertThat(result.isValid()).isTrue();
        verifyNoInteractions(underlyingAgreementTargetPeriod6ContextValidatorService);
    }
}
