package uk.gov.cca.api.workflow.request.flow.admintermination.submit.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.BusinessViolation;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.workflow.request.flow.admintermination.submit.domain.AdminTerminationReason;
import uk.gov.cca.api.workflow.request.flow.admintermination.submit.domain.AdminTerminationReasonDetails;
import uk.gov.cca.api.workflow.request.flow.admintermination.submit.domain.AdminTerminationSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.common.validation.FileAttachmentsExistenceValidator;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminTerminationSubmitValidatorTest {

    @InjectMocks
    private AdminTerminationSubmitValidator adminTerminationSubmitValidator;

    @Mock
    private DataValidator<AdminTerminationReasonDetails> validator;

    @Mock
    private FileAttachmentsExistenceValidator fileAttachmentsExistenceValidator;

    @Test
    void validate() {
        final AdminTerminationReasonDetails adminTerminationReasonDetails = AdminTerminationReasonDetails.builder()
                .reason(AdminTerminationReason.DATA_NOT_PROVIDED)
                .build();
        final AdminTerminationSubmitRequestTaskPayload taskPayload =
                AdminTerminationSubmitRequestTaskPayload.builder()
                        .adminTerminationReasonDetails(adminTerminationReasonDetails)
                        .build();

        when(validator.validate(adminTerminationReasonDetails))
                .thenReturn(Optional.empty());
        when(fileAttachmentsExistenceValidator.valid(Set.of(), Set.of()))
                .thenReturn(true);

        // Invoke
        BusinessValidationResult result = adminTerminationSubmitValidator.validate(taskPayload);

        // Verify
        assertThat(result.isValid()).isTrue();
        verify(validator, times(1)).validate(adminTerminationReasonDetails);
        verify(fileAttachmentsExistenceValidator, times(1))
                .valid(Set.of(), Set.of());
    }

    @Test
    void validate_not_valid() {
        final AdminTerminationReasonDetails adminTerminationReasonDetails = AdminTerminationReasonDetails.builder()
                .reason(AdminTerminationReason.DATA_NOT_PROVIDED)
                .build();
        final AdminTerminationSubmitRequestTaskPayload taskPayload =
                AdminTerminationSubmitRequestTaskPayload.builder()
                        .adminTerminationReasonDetails(adminTerminationReasonDetails)
                        .build();

        when(validator.validate(adminTerminationReasonDetails))
                .thenReturn(Optional.of(new BusinessViolation()));
        when(fileAttachmentsExistenceValidator.valid(Set.of(), Set.of()))
                .thenReturn(false);

        // Invoke
        BusinessValidationResult result = adminTerminationSubmitValidator.validate(taskPayload);

        // Verify
        assertThat(result.isValid()).isFalse();
        verify(validator, times(1)).validate(adminTerminationReasonDetails);
        verify(fileAttachmentsExistenceValidator, times(1))
                .valid(Set.of(), Set.of());
    }

    @Test
    void validate_empty_data_not_valid() {
        final AdminTerminationSubmitRequestTaskPayload taskPayload =
                AdminTerminationSubmitRequestTaskPayload.builder().build();

        when(fileAttachmentsExistenceValidator.valid(Set.of(), Set.of()))
                .thenReturn(true);

        // Invoke
        BusinessValidationResult result = adminTerminationSubmitValidator.validate(taskPayload);

        // Verify
        assertThat(result.isValid()).isFalse();
        verify(fileAttachmentsExistenceValidator, times(1))
                .valid(Set.of(), Set.of());
        verifyNoInteractions(validator);
    }
}
