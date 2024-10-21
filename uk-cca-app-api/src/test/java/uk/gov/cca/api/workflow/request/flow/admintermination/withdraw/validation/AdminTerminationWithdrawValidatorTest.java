package uk.gov.cca.api.workflow.request.flow.admintermination.withdraw.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.BusinessViolation;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.workflow.request.flow.admintermination.withdraw.domain.AdminTerminationWithdrawReasonDetails;
import uk.gov.cca.api.workflow.request.flow.admintermination.withdraw.domain.AdminTerminationWithdrawRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.common.validation.FileAttachmentsExistenceValidator;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdminTerminationWithdrawValidatorTest {

    @InjectMocks
    private AdminTerminationWithdrawValidator adminTerminationWithdrawValidator;

    @Mock
    private DataValidator<AdminTerminationWithdrawReasonDetails> validator;

    @Mock
    private FileAttachmentsExistenceValidator fileAttachmentsExistenceValidator;

    @Test
    void validate() {
        final AdminTerminationWithdrawReasonDetails reasonDetails = AdminTerminationWithdrawReasonDetails.builder()
                .explanation("My explanation")
                .build();
        final AdminTerminationWithdrawRequestTaskPayload taskPayload =
                AdminTerminationWithdrawRequestTaskPayload.builder()
                        .adminTerminationWithdrawReasonDetails(reasonDetails)
                        .build();

        when(validator.validate(reasonDetails))
                .thenReturn(Optional.empty());
        when(fileAttachmentsExistenceValidator.valid(Set.of(), Set.of()))
                .thenReturn(true);

        // Invoke
        BusinessValidationResult result = adminTerminationWithdrawValidator.validate(taskPayload);

        // Verify
        assertThat(result.isValid()).isTrue();
        verify(validator, times(1)).validate(reasonDetails);
        verify(fileAttachmentsExistenceValidator, times(1))
                .valid(Set.of(), Set.of());
    }

    @Test
    void validate_not_valid() {
        final AdminTerminationWithdrawReasonDetails reasonDetails = AdminTerminationWithdrawReasonDetails.builder()
                .explanation("My explanation")
                .build();
        final AdminTerminationWithdrawRequestTaskPayload taskPayload =
                AdminTerminationWithdrawRequestTaskPayload.builder()
                        .adminTerminationWithdrawReasonDetails(reasonDetails)
                        .build();

        when(validator.validate(reasonDetails))
                .thenReturn(Optional.of(new BusinessViolation()));
        when(fileAttachmentsExistenceValidator.valid(Set.of(), Set.of()))
                .thenReturn(false);

        // Invoke
        BusinessValidationResult result = adminTerminationWithdrawValidator.validate(taskPayload);

        // Verify
        assertThat(result.isValid()).isFalse();
        verify(validator, times(1)).validate(reasonDetails);
        verify(fileAttachmentsExistenceValidator, times(1))
                .valid(Set.of(), Set.of());
    }

    @Test
    void validate_empty_data_not_valid() {
        final AdminTerminationWithdrawRequestTaskPayload taskPayload =
                AdminTerminationWithdrawRequestTaskPayload.builder().build();

        when(fileAttachmentsExistenceValidator.valid(Set.of(), Set.of()))
                .thenReturn(true);

        // Invoke
        BusinessValidationResult result = adminTerminationWithdrawValidator.validate(taskPayload);

        // Verify
        assertThat(result.isValid()).isFalse();
        verify(fileAttachmentsExistenceValidator, times(1))
                .valid(Set.of(), Set.of());
        verifyNoInteractions(validator);
    }
}
