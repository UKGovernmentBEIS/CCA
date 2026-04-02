package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.submit.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.authorisation.AuthorisationAndAdditionalEvidence;
import uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementValidatorService;
import uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementFacilitiesFinalizationValidatorService;
import uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementValidationContext;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.validation.CCA2BaselineAndTargetsValidatorService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationModificationType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.validation.EditedUnderlyingAgreementVariationApplicationReasonDataValidator;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.validation.EditedUnderlyingAgreementVariationDetailsValidatorService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.validation.EditedUnderlyingAgreementVariationTargetUnitDetailsValidatorService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.validation.UnderlyingAgreementVariationReviewDecisionDataValidator;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.validation.UnderlyingAgreementVariationSubmitFacilitiesContextValidatorService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.validation.UnderlyingAgreementVariationViolation;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.submit.domain.UnderlyingAgreementVariationSubmitRequestTaskPayload;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private CCA2BaselineAndTargetsValidatorService cca2BaselineAndTargetsValidatorService;

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
    private UnderlyingAgreementFacilitiesFinalizationValidatorService underlyingAgreementFacilitiesFinalizationValidatorService;
    
    @Test
    void validate() {
        final UnderlyingAgreement underlyingAgreement = UnderlyingAgreement.builder()
        		.facilities(Set.of())
                .authorisationAndAdditionalEvidence(AuthorisationAndAdditionalEvidence.builder()
                        .authorisationAttachmentIds(Set.of(UUID.randomUUID()))
                        .build())
                .build();
        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(underlyingAgreement)
                .build();
        final UnderlyingAgreementContainer originalContainer = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(underlyingAgreement)
                .build();
        final UnderlyingAgreementTargetUnitDetails targetUnitDetails = UnderlyingAgreementTargetUnitDetails.builder()
                .operatorName("operatorName")
                .build();

        final UnderlyingAgreementVariationDetails underlyingAgreementVariationDetails = UnderlyingAgreementVariationDetails.builder()
                .modifications(List.of(UnderlyingAgreementVariationModificationType.ADD_ONE_OR_MORE_FACILITIES_TO_AGREEMENT))
                .reason("bla bla bla")
                .build();

        final SchemeVersion workflowSchemeVersion = SchemeVersion.CCA_3;
        final UnderlyingAgreementVariationSubmitRequestTaskPayload taskPayload =
                UnderlyingAgreementVariationSubmitRequestTaskPayload.builder()
                        .workflowSchemeVersion(workflowSchemeVersion)
                        .originalUnderlyingAgreementContainer(originalContainer)
                        .underlyingAgreement(UnderlyingAgreementVariationPayload.builder()
                                .underlyingAgreementVariationDetails(underlyingAgreementVariationDetails)
                                .underlyingAgreementTargetUnitDetails(targetUnitDetails)
                                .underlyingAgreement(underlyingAgreement)
                                .build())
                        .build();

        final Request request = Request.builder()
                .creationDate(LocalDateTime.of(2025, 1, 1, 0, 0))
                .build();
        final RequestTask requestTask = RequestTask.builder()
                .payload(taskPayload)
                .request(request)
                .build();
        final UnderlyingAgreementValidationContext context = UnderlyingAgreementValidationContext.builder()
                .requestCreationDate(request.getCreationDate())
                .schemeVersion(workflowSchemeVersion)
                .build();

        List<BusinessValidationResult> validationResults = new ArrayList<>();
        validationResults.add(BusinessValidationResult.valid());
        when(underlyingAgreementValidatorService.getValidationResults(container, context))
                .thenReturn(validationResults);
        when(cca2BaselineAndTargetsValidatorService.validate(container, originalContainer, LocalDate.of(2025, 1, 1)))
                .thenReturn(BusinessValidationResult.valid());
        when(underlyingAgreementVariationTargetUnitDetailsValidatorService.validate(requestTask))
                .thenReturn(BusinessValidationResult.valid());
        when(underlyingAgreementVariationDetailsValidatorService.validate(taskPayload))
                .thenReturn(BusinessValidationResult.valid());
        when(underlyingAgreementVariationSubmitFacilitiesContextValidatorService.validate(container))
                .thenReturn(BusinessValidationResult.valid());
        when(underlyingAgreementVariationApplicationReasonDataValidator.validate(taskPayload))
                .thenReturn(BusinessValidationResult.valid());
        when(underlyingAgreementVariationReviewDecisionDataValidator.validateFacilityAndGroupReviewDecisions(taskPayload))
                .thenReturn(BusinessValidationResult.valid());
        when(underlyingAgreementFacilitiesFinalizationValidatorService.validate(Set.of()))
				.thenReturn(BusinessValidationResult.valid());

        // Invoke
        validatorService.validate(requestTask);

        // Verify
        verify(underlyingAgreementValidatorService, times(1)).getValidationResults(container, context);
        verify(cca2BaselineAndTargetsValidatorService, times(1)).validate(container, originalContainer, LocalDate.of(2025, 1, 1));
        verify(underlyingAgreementVariationTargetUnitDetailsValidatorService, times(1)).validate(requestTask);
        verify(underlyingAgreementVariationDetailsValidatorService, times(1)).validate(taskPayload);
        verify(underlyingAgreementVariationSubmitFacilitiesContextValidatorService, times(1)).validate(container);
        verify(underlyingAgreementVariationApplicationReasonDataValidator, times(1)).validate(taskPayload);
        verify(underlyingAgreementVariationReviewDecisionDataValidator, times(1))
                .validateFacilityAndGroupReviewDecisions(taskPayload);
        verify(underlyingAgreementFacilitiesFinalizationValidatorService, times(1)).validate(Set.of());
    }

    @Test
    void validate_not_valid() {
        final UnderlyingAgreement underlyingAgreement = UnderlyingAgreement.builder()
        		.facilities(Set.of())
                .authorisationAndAdditionalEvidence(AuthorisationAndAdditionalEvidence.builder()
                        .authorisationAttachmentIds(Set.of(UUID.randomUUID()))
                        .build())
                .build();
        final UnderlyingAgreementContainer container = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(underlyingAgreement)
                .build();
        final UnderlyingAgreementContainer originalContainer = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(underlyingAgreement)
                .build();
        final UnderlyingAgreementTargetUnitDetails targetUnitDetails = UnderlyingAgreementTargetUnitDetails.builder()
                .operatorName("operatorName")
                .build();
        // missing mandatory field 'reason'
        final UnderlyingAgreementVariationDetails underlyingAgreementVariationDetails = UnderlyingAgreementVariationDetails.builder()
                .modifications(List.of(UnderlyingAgreementVariationModificationType.ADD_ONE_OR_MORE_FACILITIES_TO_AGREEMENT))
                .build();
        final SchemeVersion workflowSchemeVersion = SchemeVersion.CCA_3;
        final UnderlyingAgreementVariationSubmitRequestTaskPayload taskPayload =
                UnderlyingAgreementVariationSubmitRequestTaskPayload.builder()
                        .workflowSchemeVersion(workflowSchemeVersion)
                        .originalUnderlyingAgreementContainer(originalContainer)
                        .underlyingAgreement(UnderlyingAgreementVariationPayload.builder()
                                .underlyingAgreementVariationDetails(underlyingAgreementVariationDetails)
                                .underlyingAgreementTargetUnitDetails(targetUnitDetails)
                                .underlyingAgreement(underlyingAgreement)
                                .build())
                        .build();
        
        final Request request = Request.builder()
                .creationDate(LocalDateTime.of(2025, 1, 1, 0, 0))
                .build();
        final RequestTask requestTask = RequestTask.builder()
                .payload(taskPayload)
                .request(request)
                .build();
        final UnderlyingAgreementValidationContext context = UnderlyingAgreementValidationContext.builder()
                .requestCreationDate(request.getCreationDate())
                .schemeVersion(workflowSchemeVersion)
                .build();

        List<BusinessValidationResult> validationResults = new ArrayList<>();
        List<UnderlyingAgreementVariationViolation> violations = List.of(new UnderlyingAgreementVariationViolation(UnderlyingAgreementVariationDetails.class.getName(),
                UnderlyingAgreementVariationViolation.UnderlyingAgreementVariationViolationMessage.INVALID_SECTION_DATA));
        validationResults.add(BusinessValidationResult.invalid(List.of()));
        when(underlyingAgreementValidatorService.getValidationResults(container, context))
                .thenReturn(validationResults);
        when(cca2BaselineAndTargetsValidatorService.validate(container, originalContainer, LocalDate.of(2025, 1, 1)))
                .thenReturn(BusinessValidationResult.valid());
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
        when(underlyingAgreementVariationReviewDecisionDataValidator.validateFacilityAndGroupReviewDecisions(taskPayload))
                .thenReturn(BusinessValidationResult.valid());
        when(underlyingAgreementFacilitiesFinalizationValidatorService.validate(Set.of()))
				.thenReturn(BusinessValidationResult.valid());

        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class,
                () -> validatorService.validate(requestTask));

        // Verify
        assertThat(businessException.getErrorCode()).isEqualTo(CcaErrorCode.INVALID_UNDERLYING_AGREEMENT_VARIATION);
        verify(underlyingAgreementValidatorService, times(1)).getValidationResults(container, context);
        verify(cca2BaselineAndTargetsValidatorService, times(1)).validate(container, originalContainer, LocalDate.of(2025, 1, 1));
        verify(underlyingAgreementVariationTargetUnitDetailsValidatorService, times(1)).validate(requestTask);
        verify(underlyingAgreementVariationDetailsValidatorService, times(1)).validate(taskPayload);
        verify(underlyingAgreementVariationSubmitFacilitiesContextValidatorService, times(1)).validate(container);
        verify(underlyingAgreementVariationApplicationReasonDataValidator, times(1)).validate(taskPayload);
        verify(underlyingAgreementVariationReviewDecisionDataValidator, times(1))
                .validateFacilityAndGroupReviewDecisions(taskPayload);
        verify(underlyingAgreementFacilitiesFinalizationValidatorService, times(1)).validate(Set.of());
    }
}
