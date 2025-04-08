package uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.submit.validation;

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
import uk.gov.cca.api.workflow.request.flow.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationModificationType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.validation.EditedUnderlyingAgreementVariationApplicationReasonDataValidator;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.validation.EditedUnderlyingAgreementVariationDetailsValidatorService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.validation.EditedUnderlyingAgreementVariationTargetUnitDetailsValidatorService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.validation.UnderlyingAgreementVariationFacilityReviewDecisionDataValidator;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.validation.UnderlyingAgreementVariationReviewDecisionDataValidator;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.validation.UnderlyingAgreementVariationViolation;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.submit.domain.UnderlyingAgreementVariationSubmitRequestTaskPayload;
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
class UnderlyingAgreementVariationPayloadValidatorServiceTest {

    @InjectMocks
    private UnderlyingAgreementVariationPayloadValidatorService validatorService;

    @Mock
    private UnderlyingAgreementValidatorService underlyingAgreementValidatorService;

    @Mock
    private EditedUnderlyingAgreementVariationTargetUnitDetailsValidatorService underlyingAgreementVariationTargetUnitDetailsValidatorService;

    @Mock
    private EditedUnderlyingAgreementVariationDetailsValidatorService underlyingAgreementVariationDetailsValidatorService;

    @Mock
    private UnderlyingAgreementVariationSubmitFacilitiesContextValidatorService underlyingAgreementVariationSubmitFacilitiesContextValidatorService;

    @Mock
    private EditedUnderlyingAgreementVariationApplicationReasonDataValidator underlyingAgreementVariationApplicationReasonDataValidator;

    @Mock
    private UnderlyingAgreementVariationReviewDecisionDataValidator underlyingAgreementVariationReviewDecisionDataValidator;

    @Mock
    private UnderlyingAgreementVariationFacilityReviewDecisionDataValidator underlyingAgreementVariationFacilityReviewDecisionDataValidator;

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

        final UnderlyingAgreementVariationDetails underlyingAgreementVariationDetails = UnderlyingAgreementVariationDetails.builder()
                .modifications(List.of(UnderlyingAgreementVariationModificationType.ADD_ONE_OR_MORE_FACILITIES_TO_AGREEMENT))
                .reason("bla bla bla")
                .build();

        final UnderlyingAgreementVariationSubmitRequestTaskPayload taskPayload =
                UnderlyingAgreementVariationSubmitRequestTaskPayload.builder()
                        .underlyingAgreement(UnderlyingAgreementVariationPayload.builder()
                                .underlyingAgreementVariationDetails(underlyingAgreementVariationDetails)
                                .underlyingAgreementTargetUnitDetails(targetUnitDetails)
                                .underlyingAgreement(underlyingAgreement)
                                .build())
                        .build();

        final RequestTask requestTask = RequestTask.builder().payload(taskPayload).build();

        List<BusinessValidationResult> validationResults = new ArrayList<>();
        validationResults.add(BusinessValidationResult.valid());
        when(underlyingAgreementValidatorService.getValidationResults(container))
                .thenReturn(validationResults);
        when(underlyingAgreementVariationTargetUnitDetailsValidatorService.validate(requestTask))
                .thenReturn(BusinessValidationResult.valid());
        when(underlyingAgreementVariationDetailsValidatorService.validate(taskPayload))
                .thenReturn(BusinessValidationResult.valid());
        when(underlyingAgreementVariationSubmitFacilitiesContextValidatorService.validate(container))
                .thenReturn(BusinessValidationResult.valid());
        when(underlyingAgreementVariationApplicationReasonDataValidator.validate(taskPayload))
                .thenReturn(BusinessValidationResult.valid());
        when(underlyingAgreementVariationReviewDecisionDataValidator.validateUnderlyingAgreementVariationReviewDecisions(taskPayload))
                .thenReturn(BusinessValidationResult.valid());
        when(underlyingAgreementVariationFacilityReviewDecisionDataValidator.validateUnderlyingAgreementVariationFacilityReviewDecisions(taskPayload))
                .thenReturn(BusinessValidationResult.valid());

        // Invoke
        validatorService.validate(requestTask);

        // Verify
        verify(underlyingAgreementValidatorService, times(1)).getValidationResults(container);
        verify(underlyingAgreementVariationTargetUnitDetailsValidatorService, times(1)).validate(requestTask);
        verify(underlyingAgreementVariationDetailsValidatorService, times(1)).validate(taskPayload);
        verify(underlyingAgreementVariationSubmitFacilitiesContextValidatorService, times(1)).validate(container);
        verify(underlyingAgreementVariationApplicationReasonDataValidator, times(1)).validate(taskPayload);
        verify(underlyingAgreementVariationReviewDecisionDataValidator, times(1))
                .validateUnderlyingAgreementVariationReviewDecisions(taskPayload);
        verify(underlyingAgreementVariationFacilityReviewDecisionDataValidator, times(1))
                .validateUnderlyingAgreementVariationFacilityReviewDecisions(taskPayload);
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
        // missing mandatory field 'reason'
        final UnderlyingAgreementVariationDetails underlyingAgreementVariationDetails = UnderlyingAgreementVariationDetails.builder()
                .modifications(List.of(UnderlyingAgreementVariationModificationType.ADD_ONE_OR_MORE_FACILITIES_TO_AGREEMENT))
                .build();
        final UnderlyingAgreementVariationSubmitRequestTaskPayload taskPayload =
                UnderlyingAgreementVariationSubmitRequestTaskPayload.builder()
                        .underlyingAgreement(UnderlyingAgreementVariationPayload.builder()
                                .underlyingAgreementVariationDetails(underlyingAgreementVariationDetails)
                                .underlyingAgreementTargetUnitDetails(targetUnitDetails)
                                .underlyingAgreement(underlyingAgreement)
                                .build())
                        .build();
        final RequestTask requestTask = RequestTask.builder().payload(taskPayload).build();

        List<BusinessValidationResult> validationResults = new ArrayList<>();
        List<UnderlyingAgreementVariationViolation> violations = List.of(new UnderlyingAgreementVariationViolation(UnderlyingAgreementVariationDetails.class.getName(),
                UnderlyingAgreementVariationViolation.UnderlyingAgreementVariationViolationMessage.INVALID_SECTION_DATA));
        validationResults.add(BusinessValidationResult.invalid(List.of()));
        when(underlyingAgreementValidatorService.getValidationResults(container))
                .thenReturn(validationResults);
        when(underlyingAgreementVariationTargetUnitDetailsValidatorService.validate(requestTask))
                .thenReturn(BusinessValidationResult.valid());
        when(underlyingAgreementVariationDetailsValidatorService.validate(taskPayload))
                .thenReturn(BusinessValidationResult.builder()
                        .valid(false)
                        .violations(violations)
                        .build());
        when(underlyingAgreementVariationSubmitFacilitiesContextValidatorService.validate(container))
                .thenReturn(BusinessValidationResult.valid());
        when(underlyingAgreementVariationApplicationReasonDataValidator.validate(taskPayload))
                .thenReturn(BusinessValidationResult.valid());
        when(underlyingAgreementVariationReviewDecisionDataValidator.validateUnderlyingAgreementVariationReviewDecisions(taskPayload))
                .thenReturn(BusinessValidationResult.valid());
        when(underlyingAgreementVariationFacilityReviewDecisionDataValidator.validateUnderlyingAgreementVariationFacilityReviewDecisions(taskPayload))
                .thenReturn(BusinessValidationResult.valid());

        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class,
                () -> validatorService.validate(requestTask));

        // Verify
        assertThat(businessException.getErrorCode()).isEqualTo(CcaErrorCode.INVALID_UNDERLYING_AGREEMENT_VARIATION);
        verify(underlyingAgreementValidatorService, times(1)).getValidationResults(container);
        verify(underlyingAgreementVariationTargetUnitDetailsValidatorService, times(1)).validate(requestTask);
        verify(underlyingAgreementVariationDetailsValidatorService, times(1)).validate(taskPayload);
        verify(underlyingAgreementVariationSubmitFacilitiesContextValidatorService, times(1)).validate(container);
        verify(underlyingAgreementVariationApplicationReasonDataValidator, times(1)).validate(taskPayload);
        verify(underlyingAgreementVariationReviewDecisionDataValidator, times(1))
                .validateUnderlyingAgreementVariationReviewDecisions(taskPayload);
        verify(underlyingAgreementVariationFacilityReviewDecisionDataValidator, times(1))
                .validateUnderlyingAgreementVariationFacilityReviewDecisions(taskPayload);
    }
}
