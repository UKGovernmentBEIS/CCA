package uk.gov.cca.api.workflow.request.flow.common.validation.decisionnotification;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.netz.api.workflow.request.flow.common.validation.DecisionNotificationUsersValidator;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DecisionNotificationValidatorTest {

    @InjectMocks
    private DecisionNotificationValidator decisionNotificationValidator;

    @Mock
    private DataValidator<DecisionNotification> decisionNotificationDataValidator;

    @Mock
    private DecisionNotificationUsersValidator decisionNotificationUsersValidator;

    @Test
    void validate_valid() {
        final AppUser appUser = AppUser.builder().userId("userId").build();
        final RequestTask requestTask = RequestTask.builder().id(1L).build();
        final DecisionNotification decisionNotification = DecisionNotification.builder()
                .operators(Set.of("operator1"))
                .externalContacts(Set.of(1L))
                .signatory("99afc062-5760-4a6f-b1f7-a07d02019882")
                .build();

        when(decisionNotificationDataValidator.validate(decisionNotification)).thenReturn(Optional.empty());
        when(decisionNotificationUsersValidator.areUsersValid(requestTask, decisionNotification, appUser)).thenReturn(true);

        // invoke
        BusinessValidationResult result = decisionNotificationValidator.validate(requestTask, decisionNotification, appUser);

        // verify
        assertThat(result.isValid()).isTrue();
        verify(decisionNotificationDataValidator, times(1)).validate(decisionNotification);
        verify(decisionNotificationUsersValidator, times(1)).areUsersValid(requestTask, decisionNotification, appUser);
    }

    @Test
    void validate_not_valid() {
        final AppUser appUser = AppUser.builder().userId("userId").build();
        final RequestTask requestTask = RequestTask.builder().id(1L).build();
        final DecisionNotification decisionNotification = DecisionNotification.builder()
                .operators(Set.of("operator1"))
                .externalContacts(Set.of(1L))
                .signatory("99afc062-5760-4a6f-b1f7-a07d02019882")
                .build();

        when(decisionNotificationDataValidator.validate(decisionNotification)).thenReturn(Optional.empty());
        when(decisionNotificationUsersValidator.areUsersValid(requestTask, decisionNotification, appUser)).thenReturn(false);

        // invoke
        BusinessValidationResult result = decisionNotificationValidator.validate(requestTask, decisionNotification, appUser);

        // verify
        assertThat(result.isValid()).isFalse();
        verify(decisionNotificationDataValidator, times(1)).validate(decisionNotification);
        verify(decisionNotificationUsersValidator, times(1)).areUsersValid(requestTask, decisionNotification, appUser);
    }
}
