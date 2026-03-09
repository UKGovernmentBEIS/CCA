package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.BusinessViolation;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.VariationRegulatorLedDetermination;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VariationRegulatorLedDeterminationValidatorTest {

    @InjectMocks
    private VariationRegulatorLedDeterminationValidator validator;

    @Mock
    private DataValidator<VariationRegulatorLedDetermination> dataValidator;

    @Test
    void validate() {
        final VariationRegulatorLedDetermination determination = VariationRegulatorLedDetermination.builder()
                .variationImpactsAgreement(true)
                .build();

        when(dataValidator.validate(determination)).thenReturn(Optional.empty());

        // Invoke
        BusinessValidationResult result = validator.validate(determination);

        // Verify
        assertThat(result.isValid()).isTrue();
        verify(dataValidator, times(1)).validate(determination);
    }

    @Test
    void validate_empty_not_valid() {
        // Invoke
        BusinessValidationResult result = validator.validate(null);

        // Verify
        assertThat(result.isValid()).isFalse();
    }

    @Test
    void validate_not_valid() {
        final VariationRegulatorLedDetermination determination = VariationRegulatorLedDetermination.builder()
                .variationImpactsAgreement(true)
                .build();

        when(dataValidator.validate(determination)).thenReturn(Optional.of(new BusinessViolation()));

        // Invoke
        BusinessValidationResult result = validator.validate(determination);

        // Verify
        assertThat(result.isValid()).isFalse();
        verify(dataValidator, times(1)).validate(determination);
    }
}
