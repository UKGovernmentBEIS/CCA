package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.regulatorledsubmit.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaNotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.common.validation.decisionnotification.DecisionNotificationValidator;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementVariationRegulatorLedSubmitNotifyOperatorValidatorServiceTest {

    @InjectMocks
    private UnderlyingAgreementVariationRegulatorLedSubmitNotifyOperatorValidatorService service;

    @Mock
    private UnderlyingAgreementVariationRegulatorLedSubmitValidatorService underlyingAgreementVariationRegulatorLedSubmitValidatorService;

    @Mock
    private DecisionNotificationValidator decisionNotificationValidator;

    @Test
    void validate() {
        final AppUser appUser = AppUser.builder().userId("user").build();
        final RequestTask requestTask = RequestTask.builder().id(1L).build();
        final CcaDecisionNotification decisionNotification = CcaDecisionNotification.builder()
                .sectorUsers(Set.of("sector"))
                .build();
        final CcaNotifyOperatorForDecisionRequestTaskActionPayload taskActionPayload =
                CcaNotifyOperatorForDecisionRequestTaskActionPayload.builder()
                        .decisionNotification(decisionNotification)
                        .build();

        List<BusinessValidationResult> validationResults = new ArrayList<>();
        validationResults.add(BusinessValidationResult.valid());
        when(underlyingAgreementVariationRegulatorLedSubmitValidatorService.validateSubmit(requestTask))
                .thenReturn(validationResults);
        when(decisionNotificationValidator.validateDecisionNotification(requestTask, decisionNotification, appUser))
                .thenReturn(BusinessValidationResult.valid());

        // Invoke
        service.validate(requestTask, taskActionPayload, appUser);

        // Verify
        verify(underlyingAgreementVariationRegulatorLedSubmitValidatorService, times(1))
                .validateSubmit(requestTask);
        verify(decisionNotificationValidator, times(1))
                .validateDecisionNotification(requestTask, decisionNotification, appUser);
    }

    @Test
    void validate_not_valid() {
        final AppUser appUser = AppUser.builder().userId("user").build();
        final RequestTask requestTask = RequestTask.builder().id(1L).build();
        final CcaDecisionNotification decisionNotification = CcaDecisionNotification.builder()
                .sectorUsers(Set.of("sector"))
                .build();
        final CcaNotifyOperatorForDecisionRequestTaskActionPayload taskActionPayload =
                CcaNotifyOperatorForDecisionRequestTaskActionPayload.builder()
                        .decisionNotification(decisionNotification)
                        .build();

        List<BusinessValidationResult> validationResults = new ArrayList<>();
        validationResults.add(BusinessValidationResult.valid());
        when(underlyingAgreementVariationRegulatorLedSubmitValidatorService.validateSubmit(requestTask))
                .thenReturn(validationResults);
        when(decisionNotificationValidator.validateDecisionNotification(requestTask, decisionNotification, appUser))
                .thenReturn(BusinessValidationResult.invalid(List.of()));

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.validate(requestTask, taskActionPayload, appUser));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(CcaErrorCode.INVALID_UNDERLYING_AGREEMENT_VARIATION_REGULATOR_LED);
        verify(underlyingAgreementVariationRegulatorLedSubmitValidatorService, times(1))
                .validateSubmit(requestTask);
        verify(decisionNotificationValidator, times(1))
                .validateDecisionNotification(requestTask, decisionNotification, appUser);
    }
}
