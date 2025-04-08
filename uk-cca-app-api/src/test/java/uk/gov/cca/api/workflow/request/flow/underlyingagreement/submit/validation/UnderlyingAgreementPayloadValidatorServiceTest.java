package uk.gov.cca.api.workflow.request.flow.underlyingagreement.submit.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.authorisation.AuthorisationAndAdditionalEvidence;
import uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementValidatorService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementPayload;
import uk.gov.cca.api.workflow.request.flow.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.validation.EditedUnderlyingAgreementTargetUnitDetailsValidatorService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.submit.domain.UnderlyingAgreementSubmitRequestTaskPayload;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementPayloadValidatorServiceTest {

    @InjectMocks
    private UnderlyingAgreementPayloadValidatorService validatorService;

    @Mock
    private UnderlyingAgreementValidatorService underlyingAgreementValidatorService;

    @Mock
    private EditedUnderlyingAgreementTargetUnitDetailsValidatorService underlyingAgreementTargetUnitDetailsValidatorService;


    @Test
    void validate() {
        final UnderlyingAgreement underlyingAgreement = UnderlyingAgreement.builder()
                .authorisationAndAdditionalEvidence(AuthorisationAndAdditionalEvidence.builder()
                        .authorisationAttachmentIds(Set.of(UUID.randomUUID()))
                        .build())
                .build();
        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(underlyingAgreement)
                .build();
        final UnderlyingAgreementTargetUnitDetails targetUnitDetails = UnderlyingAgreementTargetUnitDetails.builder()
                .operatorName("operatorName")
                .build();
        final UnderlyingAgreementSubmitRequestTaskPayload taskPayload =
                UnderlyingAgreementSubmitRequestTaskPayload.builder()
                        .underlyingAgreement(UnderlyingAgreementPayload.builder()
                                .underlyingAgreementTargetUnitDetails(targetUnitDetails)
                                .underlyingAgreement(underlyingAgreement)
                                .build())
                        .build();

        final RequestTask requestTask = RequestTask.builder().payload(taskPayload).build();

        List<BusinessValidationResult> validationResults = new ArrayList<>();
        validationResults.add(BusinessValidationResult.valid());
        when(underlyingAgreementValidatorService.getValidationResults(container))
                .thenReturn(validationResults);
        when(underlyingAgreementTargetUnitDetailsValidatorService.validate(requestTask))
                .thenReturn(BusinessValidationResult.valid());

        // Invoke
        validatorService.validate(requestTask);

        // Verify
        verify(underlyingAgreementValidatorService, times(1)).getValidationResults(container);
        verify(underlyingAgreementTargetUnitDetailsValidatorService, times(1)).validate(requestTask);
    }

    @Test
    void validate_not_valid() {
        final UnderlyingAgreement underlyingAgreement = UnderlyingAgreement.builder()
                .authorisationAndAdditionalEvidence(AuthorisationAndAdditionalEvidence.builder()
                        .authorisationAttachmentIds(Set.of(UUID.randomUUID()))
                        .build())
                .build();
        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(underlyingAgreement)
                .build();
        final UnderlyingAgreementTargetUnitDetails targetUnitDetails = UnderlyingAgreementTargetUnitDetails.builder()
                .operatorName("operatorName")
                .build();
        final UnderlyingAgreementSubmitRequestTaskPayload taskPayload =
                UnderlyingAgreementSubmitRequestTaskPayload.builder()
                        .underlyingAgreement(UnderlyingAgreementPayload.builder()
                                .underlyingAgreementTargetUnitDetails(targetUnitDetails)
                                .underlyingAgreement(underlyingAgreement)
                                .build())
                        .build();
        final RequestTask requestTask = RequestTask.builder().payload(taskPayload).build();

        List<BusinessValidationResult> validationResults = new ArrayList<>();
        validationResults.add(BusinessValidationResult.invalid(List.of()));
        when(underlyingAgreementValidatorService.getValidationResults(container))
                .thenReturn(validationResults);
        when(underlyingAgreementTargetUnitDetailsValidatorService.validate(requestTask))
                .thenReturn(BusinessValidationResult.valid());

        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class,
                () -> validatorService.validate(requestTask));

        // Verify
        assertThat(businessException.getErrorCode()).isEqualTo(CcaErrorCode.INVALID_UNDERLYING_AGREEMENT);
        verify(underlyingAgreementValidatorService, times(1)).getValidationResults(container);
        verify(underlyingAgreementTargetUnitDetailsValidatorService, times(1)).validate(requestTask);
    }
}
