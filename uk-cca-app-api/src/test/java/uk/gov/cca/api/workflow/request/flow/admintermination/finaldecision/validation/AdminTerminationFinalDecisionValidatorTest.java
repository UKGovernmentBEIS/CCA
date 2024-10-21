package uk.gov.cca.api.workflow.request.flow.admintermination.finaldecision.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.BusinessViolation;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.workflow.request.flow.admintermination.finaldecision.domain.AdminTerminationFinalDecisionReasonDetails;
import uk.gov.cca.api.workflow.request.flow.admintermination.finaldecision.domain.AdminTerminationFinalDecisionRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.admintermination.finaldecision.domain.AdminTerminationFinalDecisionType;
import uk.gov.cca.api.workflow.request.flow.common.validation.FileAttachmentsExistenceValidator;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminTerminationFinalDecisionValidatorTest {

    @InjectMocks
    private AdminTerminationFinalDecisionValidator adminTerminationFinalDecisionValidator;

    @Mock
    private DataValidator<AdminTerminationFinalDecisionReasonDetails> validator;

    @Mock
    private FileAttachmentsExistenceValidator fileAttachmentsExistenceValidator;

    @Test
    void validate() {
        final AdminTerminationFinalDecisionReasonDetails reasonDetails = AdminTerminationFinalDecisionReasonDetails.builder()
                .finalDecisionType(AdminTerminationFinalDecisionType.TERMINATE_AGREEMENT)
                .explanation("My explanation")
                .build();
        final AdminTerminationFinalDecisionRequestTaskPayload taskPayload =
                AdminTerminationFinalDecisionRequestTaskPayload.builder()
                        .adminTerminationFinalDecisionReasonDetails(reasonDetails)
                        .build();

        when(validator.validate(reasonDetails))
                .thenReturn(Optional.empty());
        when(fileAttachmentsExistenceValidator.valid(Set.of(), Set.of()))
                .thenReturn(true);

        // Invoke
        BusinessValidationResult result = adminTerminationFinalDecisionValidator.validate(taskPayload);

        // Verify
        assertThat(result.isValid()).isTrue();
        verify(validator, times(1)).validate(reasonDetails);
        verify(fileAttachmentsExistenceValidator, times(1))
                .valid(Set.of(), Set.of());
    }

    @Test
    void validate_not_valid() {
        final AdminTerminationFinalDecisionReasonDetails reasonDetails = AdminTerminationFinalDecisionReasonDetails.builder()
                .finalDecisionType(AdminTerminationFinalDecisionType.TERMINATE_AGREEMENT)
                .explanation("My explanation")
                .build();
        final AdminTerminationFinalDecisionRequestTaskPayload taskPayload =
                AdminTerminationFinalDecisionRequestTaskPayload.builder()
                        .adminTerminationFinalDecisionReasonDetails(reasonDetails)
                        .build();

        when(validator.validate(reasonDetails))
                .thenReturn(Optional.of(new BusinessViolation()));
        when(fileAttachmentsExistenceValidator.valid(Set.of(), Set.of()))
                .thenReturn(false);

        // Invoke
        BusinessValidationResult result = adminTerminationFinalDecisionValidator.validate(taskPayload);

        // Verify
        assertThat(result.isValid()).isFalse();
        verify(validator, times(1)).validate(reasonDetails);
        verify(fileAttachmentsExistenceValidator, times(1))
                .valid(Set.of(), Set.of());
    }

    @Test
    void validate_empty_data_not_valid() {
        final AdminTerminationFinalDecisionRequestTaskPayload taskPayload =
                AdminTerminationFinalDecisionRequestTaskPayload.builder().build();

        when(fileAttachmentsExistenceValidator.valid(Set.of(), Set.of()))
                .thenReturn(true);

        // Invoke
        BusinessValidationResult result = adminTerminationFinalDecisionValidator.validate(taskPayload);

        // Verify
        assertThat(result.isValid()).isFalse();
        verify(fileAttachmentsExistenceValidator, times(1))
                .valid(Set.of(), Set.of());
        verifyNoInteractions(validator);
    }
}
