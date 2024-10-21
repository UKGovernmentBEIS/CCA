package uk.gov.cca.api.workflow.request.flow.admintermination.submit.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.flow.admintermination.common.validation.AdminTerminationViolation;
import uk.gov.cca.api.workflow.request.flow.admintermination.submit.domain.AdminTerminationSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaNotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminTerminationSubmitNotifyOperatorValidatorTest {

    @InjectMocks
    private AdminTerminationSubmitNotifyOperatorValidator validator;

    @Mock
    private AdminTerminationSubmitValidator adminTerminationSubmitValidator;

    @Mock
    private AdminTerminationSubmitDecisionNotificationValidator adminTerminationSubmitDecisionNotificationValidator;

    @Test
    void validate() {
        final AdminTerminationSubmitRequestTaskPayload taskPayload =
                AdminTerminationSubmitRequestTaskPayload.builder()
                        .payloadType(CcaRequestTaskPayloadType.ADMIN_TERMINATION_SUBMIT_PAYLOAD)
                        .build();
        final RequestTask requestTask = RequestTask.builder()
                .payload(taskPayload)
                .build();
        final CcaDecisionNotification decisionNotification = CcaDecisionNotification.builder()
                .sectorUsers(Set.of("sector"))
                .build();
        final CcaNotifyOperatorForDecisionRequestTaskActionPayload payload =
                CcaNotifyOperatorForDecisionRequestTaskActionPayload.builder()
                        .decisionNotification(decisionNotification)
                        .build();
        final AppUser appUser = AppUser.builder().userId("user").build();

        when(adminTerminationSubmitValidator.validate(taskPayload))
                .thenReturn(BusinessValidationResult.valid());
        when(adminTerminationSubmitDecisionNotificationValidator.validate(requestTask, decisionNotification, appUser))
                .thenReturn(BusinessValidationResult.valid());

        // Invoke
        validator.validate(requestTask, payload, appUser);

        // Verify
        verify(adminTerminationSubmitValidator, times(1))
                .validate(taskPayload);
        verify(adminTerminationSubmitDecisionNotificationValidator, times(1))
                .validate(requestTask, decisionNotification, appUser);
    }

    @Test
    void validate_not_valid() {
        final AdminTerminationSubmitRequestTaskPayload taskPayload =
                AdminTerminationSubmitRequestTaskPayload.builder()
                        .payloadType(CcaRequestTaskPayloadType.ADMIN_TERMINATION_SUBMIT_PAYLOAD)
                        .build();
        final RequestTask requestTask = RequestTask.builder()
                .payload(taskPayload)
                .build();
        final CcaDecisionNotification decisionNotification = CcaDecisionNotification.builder()
                .sectorUsers(Set.of("sector"))
                .build();
        final CcaNotifyOperatorForDecisionRequestTaskActionPayload payload =
                CcaNotifyOperatorForDecisionRequestTaskActionPayload.builder()
                        .decisionNotification(decisionNotification)
                        .build();
        final AppUser appUser = AppUser.builder().userId("user").build();

        when(adminTerminationSubmitValidator.validate(taskPayload))
                .thenReturn(BusinessValidationResult.invalid(List.of(
                        new AdminTerminationViolation(AdminTerminationViolation.AdminTerminationViolationMessage.INVALID_ADMIN_TERMINATION_REASON_DATA))
                ));
        when(adminTerminationSubmitDecisionNotificationValidator.validate(requestTask, decisionNotification, appUser))
                .thenReturn(BusinessValidationResult.valid());

        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class, () ->
                validator.validate(requestTask, payload, appUser));

        // Verify
        assertThat(businessException.getErrorCode()).isEqualTo(CcaErrorCode.INVALID_ADMIN_TERMINATION);
        verify(adminTerminationSubmitValidator, times(1))
                .validate(taskPayload);
        verify(adminTerminationSubmitDecisionNotificationValidator, times(1))
                .validate(requestTask, decisionNotification, appUser);
    }
}
