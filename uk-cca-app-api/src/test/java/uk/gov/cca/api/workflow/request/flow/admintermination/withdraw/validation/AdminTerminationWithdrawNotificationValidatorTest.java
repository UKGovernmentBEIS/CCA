package uk.gov.cca.api.workflow.request.flow.admintermination.withdraw.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.workflow.request.flow.admintermination.withdraw.domain.AdminTerminationWithdrawReasonDetails;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.common.validation.decisionnotification.CcaDecisionNotificationUsersValidator;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminTerminationWithdrawNotificationValidatorTest {

    @InjectMocks
    private AdminTerminationWithdrawNotificationValidator adminTerminationWithdrawNotificationValidator;

    @Mock
    private DataValidator<CcaDecisionNotification> validator;

    @Mock
    private CcaDecisionNotificationUsersValidator ccaDecisionNotificationUsersValidator;

    @Test
    void validate() {
        final AppUser appUser = AppUser.builder().userId("userId").build();
        final RequestTask requestTask = RequestTask.builder().id(1L).build();
        final CcaDecisionNotification decisionNotification = CcaDecisionNotification.builder()
                .sectorUsers(Set.of("sector1", "sector2"))
                .build();

        when(validator.validate(decisionNotification)).thenReturn(Optional.empty());
        when(ccaDecisionNotificationUsersValidator.validate(requestTask, decisionNotification, appUser))
                .thenReturn(List.of());

        // Invoke
        BusinessValidationResult result = adminTerminationWithdrawNotificationValidator
                .validate(requestTask, decisionNotification, appUser);

        // Verify
        assertThat(result.isValid()).isTrue();
        verify(validator, times(1)).validate(decisionNotification);
        verify(ccaDecisionNotificationUsersValidator, times(1))
                .validate(requestTask, decisionNotification, appUser);
    }

    @Test
    void getSectionName() {
        assertThat(adminTerminationWithdrawNotificationValidator.getSectionName())
                .isEqualTo(AdminTerminationWithdrawReasonDetails.class.getName());
    }
}
