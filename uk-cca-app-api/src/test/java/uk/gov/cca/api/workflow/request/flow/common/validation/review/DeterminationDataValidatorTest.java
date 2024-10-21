package uk.gov.cca.api.workflow.request.flow.common.validation.review;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.BusinessViolation;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.Determination;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.DeterminationType;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DeterminationDataValidatorTest {

    @InjectMocks
    private DeterminationDataValidator determinationDataValidator;

    @Mock
    private DataValidator<Determination> dataValidator;

    @Test
    void validateDetermination() {
        final Determination determination = Determination.builder().type(DeterminationType.ACCEPTED).additionalInformation("text").build();

        when(dataValidator.validate(determination))
                .thenReturn(Optional.empty());

        BusinessValidationResult result = determinationDataValidator
                .validateDetermination(determination);

        assertThat(result.isValid()).isTrue();
        verify(dataValidator, times(1)).validate(determination);
    }

    @Test
    void validateDetermination_not_valid() {
        final Determination determination = Determination.builder().type(DeterminationType.ACCEPTED).additionalInformation("text").build();

        when(dataValidator.validate(determination))
                .thenReturn(Optional.of(new BusinessViolation()));

        BusinessValidationResult result = determinationDataValidator
                .validateDetermination(determination);

        assertThat(result.isValid()).isFalse();
        verify(dataValidator, times(1)).validate(determination);
    }
}
