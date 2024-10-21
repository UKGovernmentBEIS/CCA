package uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.activation.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.BusinessViolation;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.workflow.request.flow.common.domain.activation.UnderlyingAgreementActivationDetails;
import uk.gov.cca.api.workflow.request.flow.common.validation.FileAttachmentsExistenceValidator;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.activation.domain.UnderlyingAgreementVariationActivationRequestTaskPayload;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UnderlyingAgreementVariationActivationSubmitValidatorTest {

    @InjectMocks
    private UnderlyingAgreementVariationActivationSubmitValidator underlyingAgreementVariationActivationSubmitValidator;

    @Mock
    private DataValidator<UnderlyingAgreementActivationDetails> validator;

    @Mock
    private FileAttachmentsExistenceValidator fileAttachmentsExistenceValidator;

    @Test
    void validate() {
        final UUID file = UUID.randomUUID();
        final UnderlyingAgreementActivationDetails details = UnderlyingAgreementActivationDetails.builder()
                .evidenceFiles(Set.of(file))
                .build();
        final UnderlyingAgreementVariationActivationRequestTaskPayload taskPayload =
                UnderlyingAgreementVariationActivationRequestTaskPayload.builder()
                        .underlyingAgreementActivationDetails(details)
                        .build();

        when(validator.validate(details))
                .thenReturn(Optional.empty());
        when(fileAttachmentsExistenceValidator.valid(Set.of(file), Set.of()))
                .thenReturn(true);

        // Invoke
        BusinessValidationResult result = underlyingAgreementVariationActivationSubmitValidator.validate(taskPayload);

        // Verify
        assertThat(result.isValid()).isTrue();
        verify(validator, times(1)).validate(details);
        verify(fileAttachmentsExistenceValidator, times(1))
                .valid(Set.of(file), Set.of());
    }

    @Test
    void validate_not_valid() {
        final UUID file = UUID.randomUUID();
        final UnderlyingAgreementActivationDetails details = UnderlyingAgreementActivationDetails.builder()
                .evidenceFiles(Set.of(file))
                .build();
        final UnderlyingAgreementVariationActivationRequestTaskPayload taskPayload =
                UnderlyingAgreementVariationActivationRequestTaskPayload.builder()
                        .underlyingAgreementActivationDetails(details)
                        .build();

        when(validator.validate(details))
                .thenReturn(Optional.of(new BusinessViolation()));
        when(fileAttachmentsExistenceValidator.valid(Set.of(file), Set.of()))
                .thenReturn(false);

        // Invoke
        BusinessValidationResult result = underlyingAgreementVariationActivationSubmitValidator.validate(taskPayload);

        // Verify
        assertThat(result.isValid()).isFalse();
        verify(validator, times(1)).validate(details);
        verify(fileAttachmentsExistenceValidator, times(1))
                .valid(Set.of(file), Set.of());
    }

    @Test
    void validate_empty_data_not_valid() {
        final UnderlyingAgreementVariationActivationRequestTaskPayload taskPayload =
                UnderlyingAgreementVariationActivationRequestTaskPayload.builder().build();

        when(fileAttachmentsExistenceValidator.valid(Set.of(), Set.of()))
                .thenReturn(true);

        // Invoke
        BusinessValidationResult result = underlyingAgreementVariationActivationSubmitValidator.validate(taskPayload);

        // Verify
        assertThat(result.isValid()).isFalse();
        verify(fileAttachmentsExistenceValidator, times(1))
                .valid(Set.of(), Set.of());
        verifyNoInteractions(validator);
    }
}
