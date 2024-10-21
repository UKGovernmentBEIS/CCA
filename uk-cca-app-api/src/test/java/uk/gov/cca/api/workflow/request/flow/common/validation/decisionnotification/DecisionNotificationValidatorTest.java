package uk.gov.cca.api.workflow.request.flow.common.validation.decisionnotification;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.BusinessViolation;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.common.validation.FileAttachmentsExistenceValidator;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.util.List;
import java.util.Map;
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
    private DataValidator<CcaDecisionNotification> ccaDecisionNotificationDataValidator;

    @Mock
    private CcaDecisionNotificationUsersValidator ccaDecisionNotificationUsersValidator;

    @Mock
    private FileAttachmentsExistenceValidator fileAttachmentsExistenceValidator;

    @Test
    void validateDecisionNotification() {
        final AppUser appUser = AppUser.builder().userId("userId").build();
        final RequestTask requestTask = RequestTask.builder().id(1L).build();
        final CcaDecisionNotification decisionNotification = CcaDecisionNotification.builder()
                .sectorUsers(Set.of("sector1", "sector2"))
                .build();

        when(ccaDecisionNotificationDataValidator.validate(decisionNotification)).thenReturn(Optional.empty());
        when(ccaDecisionNotificationUsersValidator.validate(requestTask, decisionNotification, appUser))
                .thenReturn(List.of());

        BusinessValidationResult result = decisionNotificationValidator
                .validateDecisionNotification(requestTask, decisionNotification, appUser);

        assertThat(result.isValid()).isTrue();
        verify(ccaDecisionNotificationDataValidator, times(1)).validate(decisionNotification);
        verify(ccaDecisionNotificationUsersValidator, times(1))
                .validate(requestTask, decisionNotification, appUser);
    }

    @Test
    void validateDecisionNotification_not_valid() {
        final AppUser appUser = AppUser.builder().userId("userId").build();
        final RequestTask requestTask = RequestTask.builder().id(1L).build();
        final CcaDecisionNotification decisionNotification = CcaDecisionNotification.builder()
                .sectorUsers(Set.of("sector1", "sector2"))
                .build();

        when(ccaDecisionNotificationDataValidator.validate(decisionNotification)).thenReturn(Optional.of(new BusinessViolation()));
        when(ccaDecisionNotificationUsersValidator.validate(requestTask, decisionNotification, appUser))
                .thenReturn(List.of(new BusinessViolation("", Set.of("sector1"))));

        BusinessValidationResult result = decisionNotificationValidator
                .validateDecisionNotification(requestTask, decisionNotification, appUser);

        assertThat(result.isValid()).isFalse();
        verify(ccaDecisionNotificationDataValidator, times(1)).validate(decisionNotification);
        verify(ccaDecisionNotificationUsersValidator, times(1))
                .validate(requestTask, decisionNotification, appUser);
    }

    @Test
    void validateUnderlyingAgreementFiles() {

        when(fileAttachmentsExistenceValidator.valid(Set.of(), Set.of())).thenReturn(true);

        BusinessValidationResult result = decisionNotificationValidator.validateUnderlyingAgreementFiles(Set.of(), Map.of());

        assertThat(result.isValid()).isTrue();
        verify(fileAttachmentsExistenceValidator, times(1)).valid(Set.of(), Set.of());
    }

    @Test
    void validateUnderlyingAgreementFiles_not_valid() {

        when(fileAttachmentsExistenceValidator.valid(Set.of(), Set.of())).thenReturn(false);

        BusinessValidationResult result = decisionNotificationValidator.validateUnderlyingAgreementFiles(Set.of(), Map.of());
        // Verify
        assertThat(result.isValid()).isFalse();
        verify(fileAttachmentsExistenceValidator, times(1)).valid(Set.of(), Set.of());
    }
}