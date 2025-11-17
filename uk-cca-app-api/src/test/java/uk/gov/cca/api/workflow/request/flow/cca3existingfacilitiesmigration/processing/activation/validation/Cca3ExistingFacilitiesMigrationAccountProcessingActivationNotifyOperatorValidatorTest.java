package uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.activation.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.activation.domain.Cca3ExistingFacilitiesMigrationAccountProcessingActivationRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaNotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.common.validation.decisionnotification.DecisionNotificationValidator;
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
class Cca3ExistingFacilitiesMigrationAccountProcessingActivationNotifyOperatorValidatorTest {

    @InjectMocks
    private Cca3ExistingFacilitiesMigrationAccountProcessingActivationNotifyOperatorValidator validator;

    @Mock
    private Cca3ExistingFacilitiesMigrationAccountProcessingActivationSubmitValidator cca3ExistingFacilitiesMigrationAccountProcessingActivationSubmitValidator;

    @Mock
    private DecisionNotificationValidator decisionNotificationValidator;

    @Test
    void validate() {
        final AppUser user = AppUser.builder().userId("userId").build();
        final CcaDecisionNotification decisionNotification = CcaDecisionNotification.builder()
                .sectorUsers(Set.of("sector1", "sector2"))
                .build();
        final CcaNotifyOperatorForDecisionRequestTaskActionPayload actionPayload =
                CcaNotifyOperatorForDecisionRequestTaskActionPayload.builder()
                        .decisionNotification(decisionNotification)
                        .build();
        final Cca3ExistingFacilitiesMigrationAccountProcessingActivationRequestTaskPayload taskPayload =
                Cca3ExistingFacilitiesMigrationAccountProcessingActivationRequestTaskPayload.builder()
                        .payloadType(CcaRequestTaskPayloadType.CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_ACTIVATION_PAYLOAD)
                        .build();
        final RequestTask requestTask = RequestTask.builder().payload(taskPayload).build();

        when(cca3ExistingFacilitiesMigrationAccountProcessingActivationSubmitValidator.validate(taskPayload))
                .thenReturn(BusinessValidationResult.valid());
        when(decisionNotificationValidator.validateDecisionNotification(requestTask, decisionNotification, user))
                .thenReturn(BusinessValidationResult.valid());

        // Invoke
        validator.validate(requestTask, actionPayload, user);

        // Verify
        verify(cca3ExistingFacilitiesMigrationAccountProcessingActivationSubmitValidator, times(1))
                .validate(taskPayload);
        verify(decisionNotificationValidator, times(1))
                .validateDecisionNotification(requestTask, decisionNotification, user);
    }

    @Test
    void validate_not_valid() {
        final AppUser user = AppUser.builder().userId("userId").build();
        final CcaDecisionNotification decisionNotification = CcaDecisionNotification.builder()
                .sectorUsers(Set.of("sector1", "sector2"))
                .build();
        final CcaNotifyOperatorForDecisionRequestTaskActionPayload actionPayload =
                CcaNotifyOperatorForDecisionRequestTaskActionPayload.builder()
                        .decisionNotification(decisionNotification)
                        .build();
        final Cca3ExistingFacilitiesMigrationAccountProcessingActivationRequestTaskPayload taskPayload =
                Cca3ExistingFacilitiesMigrationAccountProcessingActivationRequestTaskPayload.builder()
                        .payloadType(CcaRequestTaskPayloadType.CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_ACTIVATION_PAYLOAD)
                        .build();
        final RequestTask requestTask = RequestTask.builder().payload(taskPayload).build();

        when(cca3ExistingFacilitiesMigrationAccountProcessingActivationSubmitValidator.validate(taskPayload))
                .thenReturn(BusinessValidationResult.invalid(List.of()));
        when(decisionNotificationValidator.validateDecisionNotification(requestTask, decisionNotification, user))
                .thenReturn(BusinessValidationResult.valid());

        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class, () ->
                validator.validate(requestTask, actionPayload, user));

        // Verify
        assertThat(businessException.getErrorCode()).isEqualTo(CcaErrorCode.INVALID_CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_ACTIVATION);
        verify(cca3ExistingFacilitiesMigrationAccountProcessingActivationSubmitValidator, times(1))
                .validate(taskPayload);
        verify(decisionNotificationValidator, times(1))
                .validateDecisionNotification(requestTask, decisionNotification, user);
    }
}
