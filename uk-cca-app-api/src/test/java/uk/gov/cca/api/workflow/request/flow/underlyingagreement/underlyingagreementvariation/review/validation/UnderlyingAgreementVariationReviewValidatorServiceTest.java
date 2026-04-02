package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.authorisation.AuthorisationAndAdditionalEvidence;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityItem;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityStatus;
import uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementValidatorService;
import uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementFacilitiesFinalizationValidatorService;
import uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementValidationContext;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.validation.CCA2BaselineAndTargetsValidatorService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.validation.EditedUnderlyingAgreementVariationApplicationReasonDataValidator;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.validation.EditedUnderlyingAgreementVariationDetailsValidatorService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.validation.EditedUnderlyingAgreementVariationTargetUnitDetailsValidatorService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.domain.UnderlyingAgreementVariationReviewRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementVariationReviewValidatorServiceTest {

    @InjectMocks
    private UnderlyingAgreementVariationReviewValidatorService validatorService;

    @Mock
    private UnderlyingAgreementValidatorService underlyingAgreementValidatorService;

    @Mock
    private CCA2BaselineAndTargetsValidatorService cca2BaselineAndTargetsValidatorService;

    @Mock
    private EditedUnderlyingAgreementVariationTargetUnitDetailsValidatorService editedUnderlyingAgreementVariationTargetUnitDetailsValidatorService;

    @Mock
    private ProposedUnderlyingAgreementVariationTargetUnitDetailsValidatorService proposedUnderlyingAgreementVariationTargetUnitDetailsValidatorService;

    @Mock
    private EditedUnderlyingAgreementVariationDetailsValidatorService editedVariationDetailsValidator;

    @Mock
    private ProposedUnderlyingAgreementVariationDetailsValidatorService proposedVariationDetailsValidator;

    @Mock
    private EditedUnderlyingAgreementVariationApplicationReasonDataValidator editedUnderlyingAgreementVariationApplicationReasonDataValidator;

    @Mock
    private ProposedUnderlyingAgreementVariationApplicationReasonDataValidator proposedUnderlyingAgreementVariationApplicationReasonDataValidator;

    @Mock
    private UnderlyingAgreementFacilitiesFinalizationValidatorService underlyingAgreementFacilitiesFinalizationValidatorService;
    
    @Test
    void validateEditedUnderlyingAgreement() {
    	final LocalDateTime creationDate = LocalDateTime.of(2025, 1, 1, 0, 0);
        final Set<Facility> facilities = Set.of(Facility.builder()
                .status(FacilityStatus.NEW)
                .facilityItem(FacilityItem.builder().facilityId("1").build())
                .build());
        final UnderlyingAgreementTargetUnitDetails targetUnitDetails = UnderlyingAgreementTargetUnitDetails.builder()
                .operatorName("operatorName")
                .build();
        final UnderlyingAgreement underlyingAgreement = UnderlyingAgreement.builder()
                .facilities(facilities)
                .authorisationAndAdditionalEvidence(AuthorisationAndAdditionalEvidence.builder()
                        .authorisationAttachmentIds(Set.of(UUID.randomUUID()))
                        .build())
                .build();
        final UnderlyingAgreementContainer originalContainer = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(underlyingAgreement)
                .build();

        final SchemeVersion workflowSchemeVersion = SchemeVersion.CCA_3;
        final UnderlyingAgreementVariationReviewRequestTaskPayload taskPayload = UnderlyingAgreementVariationReviewRequestTaskPayload.builder()
                .workflowSchemeVersion(workflowSchemeVersion)
                .originalUnderlyingAgreementContainer(originalContainer)
                .underlyingAgreement(UnderlyingAgreementVariationPayload.builder()
                        .underlyingAgreementTargetUnitDetails(targetUnitDetails)
                        .underlyingAgreement(underlyingAgreement)
                        .build())
                .underlyingAgreementProposed(
                        UnderlyingAgreementVariationPayload.builder()
                                .underlyingAgreementTargetUnitDetails(targetUnitDetails)
                                .underlyingAgreement(underlyingAgreement)
                                .build())
                .build();
        final Request request = Request.builder()
                .creationDate(creationDate)
                .payload(UnderlyingAgreementVariationRequestPayload.builder()
                        .underlyingAgreement(UnderlyingAgreementVariationPayload.builder()
                                .underlyingAgreement(UnderlyingAgreement.builder()
                                        .facilities(facilities)
                                        .build())
                                .build())
                        .build())
                .build();
        final RequestTask requestTask = RequestTask.builder()
                .request(request)
                .payload(taskPayload)
                .build();
        final UnderlyingAgreementValidationContext context = UnderlyingAgreementValidationContext.builder()
                .requestCreationDate(request.getCreationDate())
                .schemeVersion(workflowSchemeVersion)
                .build();

        List<BusinessValidationResult> validationResults = new ArrayList<>();
        validationResults.add(BusinessValidationResult.valid());
        when(underlyingAgreementValidatorService.getValidationResults(originalContainer, context))
                .thenReturn(validationResults);
        when(cca2BaselineAndTargetsValidatorService.validate(any(), eq(originalContainer), eq(creationDate.toLocalDate())))
                .thenReturn(BusinessValidationResult.valid());
        when(editedUnderlyingAgreementVariationTargetUnitDetailsValidatorService.validate(requestTask))
                .thenReturn(BusinessValidationResult.valid());
        when(editedVariationDetailsValidator.validate(taskPayload)).thenReturn(BusinessValidationResult.valid());
        when(editedUnderlyingAgreementVariationApplicationReasonDataValidator.validate(taskPayload))
                .thenReturn(BusinessValidationResult.valid());

        // Invoke
        validatorService.validateEditedUnderlyingAgreement(requestTask);

        // Verify
        verify(underlyingAgreementValidatorService, times(1)).getValidationResults(originalContainer, context);
        verify(cca2BaselineAndTargetsValidatorService, times(1)).validate(any(), eq(originalContainer), eq(creationDate.toLocalDate()));
        verify(editedUnderlyingAgreementVariationTargetUnitDetailsValidatorService, times(1))
                .validate(requestTask);
        verify(editedVariationDetailsValidator, times(1))
                .validate(taskPayload);
        verify(editedUnderlyingAgreementVariationApplicationReasonDataValidator, times(1))
                .validate(taskPayload);
    }

    @Test
    void validateEditedUnderlyingAgreement_not_valid() {
    	final LocalDateTime creationDate = LocalDateTime.of(2028, 1, 1, 0, 0);
        final UnderlyingAgreementTargetUnitDetails targetUnitDetails = UnderlyingAgreementTargetUnitDetails.builder()
                .operatorName("operatorName")
                .build();
        final UnderlyingAgreement underlyingAgreement = UnderlyingAgreement.builder()
                .facilities(Set.of(Facility.builder()
                        .status(FacilityStatus.NEW)
                        .facilityItem(FacilityItem.builder().facilityId("2").build())
                        .build()))
                .authorisationAndAdditionalEvidence(AuthorisationAndAdditionalEvidence.builder()
                        .authorisationAttachmentIds(Set.of(UUID.randomUUID()))
                        .build())
                .build();
        final UnderlyingAgreementContainer originalContainer = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(underlyingAgreement)
                .build();

        final SchemeVersion workflowSchemeVersion = SchemeVersion.CCA_3;
        UnderlyingAgreementVariationReviewRequestTaskPayload taskPayload = UnderlyingAgreementVariationReviewRequestTaskPayload.builder()
                .workflowSchemeVersion(workflowSchemeVersion)
                .originalUnderlyingAgreementContainer(originalContainer)
                .underlyingAgreement(UnderlyingAgreementVariationPayload.builder()
                        .underlyingAgreementTargetUnitDetails(targetUnitDetails)
                        .underlyingAgreement(underlyingAgreement)
                        .build())
                .underlyingAgreementProposed(
                        UnderlyingAgreementVariationPayload.builder()
                                .underlyingAgreementTargetUnitDetails(targetUnitDetails)
                                .underlyingAgreement(underlyingAgreement)
                                .build())
                .build();
        final Request request = Request.builder()
                .creationDate(creationDate)
                .payload(UnderlyingAgreementVariationRequestPayload.builder()
                        .originalUnderlyingAgreementContainer(originalContainer)
                        .underlyingAgreement(UnderlyingAgreementVariationPayload.builder()
                                .underlyingAgreement(UnderlyingAgreement.builder()
                                        .facilities(Set.of(Facility.builder()
                                                .status(FacilityStatus.NEW)
                                                .facilityItem(FacilityItem.builder().facilityId("1").build())
                                                .build()))
                                        .build())
                                .build())
                        .build())
                .build();
        final RequestTask requestTask = RequestTask.builder()
                .request(request)
                .payload(taskPayload)
                .build();
        final UnderlyingAgreementValidationContext context = UnderlyingAgreementValidationContext.builder()
                .requestCreationDate(request.getCreationDate())
                .schemeVersion(workflowSchemeVersion)
                .build();

        List<BusinessValidationResult> validationResults = new ArrayList<>();
        validationResults.add(BusinessValidationResult.valid());
        when(underlyingAgreementValidatorService.getValidationResults(originalContainer, context))
                .thenReturn(validationResults);
        when(cca2BaselineAndTargetsValidatorService.validate(any(), eq(originalContainer), eq(creationDate.toLocalDate())))
                .thenReturn(BusinessValidationResult.valid());
        when(editedUnderlyingAgreementVariationTargetUnitDetailsValidatorService.validate(requestTask))
                .thenReturn(BusinessValidationResult.valid());
        when(editedVariationDetailsValidator.validate(taskPayload)).thenReturn(BusinessValidationResult.valid());
        when(editedUnderlyingAgreementVariationApplicationReasonDataValidator.validate(taskPayload))
                .thenReturn(BusinessValidationResult.invalid(List.of()));

        // Invoke
        List<BusinessValidationResult> results = validatorService.validateEditedUnderlyingAgreement(requestTask);
        boolean isValid = results.stream().allMatch(BusinessValidationResult::isValid);

        // Verify
        assertThat(isValid).isFalse();
        verify(underlyingAgreementValidatorService, times(1))
                .getValidationResults(originalContainer, context);
        verify(editedUnderlyingAgreementVariationTargetUnitDetailsValidatorService, times(1))
                .validate(requestTask);
        verify(editedVariationDetailsValidator, times(1))
                .validate(taskPayload);
        verify(editedUnderlyingAgreementVariationApplicationReasonDataValidator, times(1))
                .validate(taskPayload);
    }

    @Test
    void validateProposedUnderlyingAgreement() {
    	final LocalDateTime creationDate = LocalDateTime.of(2028, 1, 1, 0, 0);
        final Set<Facility> facilities = Set.of(Facility.builder()
                .status(FacilityStatus.NEW)
                .facilityItem(FacilityItem.builder().facilityId("1").build())
                .build());
        final UnderlyingAgreementTargetUnitDetails targetUnitDetails = UnderlyingAgreementTargetUnitDetails.builder()
                .operatorName("operatorName")
                .build();
        final UnderlyingAgreement underlyingAgreement = UnderlyingAgreement.builder()
                .facilities(facilities)
                .authorisationAndAdditionalEvidence(AuthorisationAndAdditionalEvidence.builder()
                        .authorisationAttachmentIds(Set.of(UUID.randomUUID()))
                        .build())
                .build();
        final UnderlyingAgreementContainer originalContainer = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(underlyingAgreement)
                .build();

        final SchemeVersion workflowSchemeVersion = SchemeVersion.CCA_3;
        final UnderlyingAgreementVariationReviewRequestTaskPayload taskPayload = UnderlyingAgreementVariationReviewRequestTaskPayload.builder()
                .workflowSchemeVersion(workflowSchemeVersion)
                .originalUnderlyingAgreementContainer(originalContainer)
                .underlyingAgreement(UnderlyingAgreementVariationPayload.builder()
                        .underlyingAgreementTargetUnitDetails(targetUnitDetails)
                        .underlyingAgreement(underlyingAgreement)
                        .build())
                .underlyingAgreementProposed(
                        UnderlyingAgreementVariationPayload.builder()
                                .underlyingAgreementTargetUnitDetails(targetUnitDetails)
                                .underlyingAgreement(underlyingAgreement)
                                .build())
                .build();
        final Request request = Request.builder()
                .creationDate(creationDate)
                .payload(UnderlyingAgreementVariationRequestPayload.builder()
                        .underlyingAgreement(UnderlyingAgreementVariationPayload.builder()
                                .underlyingAgreement(UnderlyingAgreement.builder()
                                        .facilities(facilities)
                                        .build())
                                .build())
                        .build())
                .build();
        final RequestTask requestTask = RequestTask.builder()
                .request(request)
                .payload(taskPayload)
                .build();
        final UnderlyingAgreementValidationContext context = UnderlyingAgreementValidationContext.builder()
                .requestCreationDate(request.getCreationDate())
                .schemeVersion(workflowSchemeVersion)
                .build();

        List<BusinessValidationResult> validationResults = new ArrayList<>();
        validationResults.add(BusinessValidationResult.valid());
        when(underlyingAgreementValidatorService.getValidationResults(originalContainer, context))
                .thenReturn(validationResults);
        when(cca2BaselineAndTargetsValidatorService.validate(any(), eq(originalContainer), eq(creationDate.toLocalDate())))
                .thenReturn(BusinessValidationResult.valid());
        when(proposedUnderlyingAgreementVariationTargetUnitDetailsValidatorService.validate(requestTask))
                .thenReturn(BusinessValidationResult.valid());
        when(proposedVariationDetailsValidator.validate(taskPayload)).thenReturn(BusinessValidationResult.valid());
        when(proposedUnderlyingAgreementVariationApplicationReasonDataValidator.validate(taskPayload))
                .thenReturn(BusinessValidationResult.valid());
        when(underlyingAgreementFacilitiesFinalizationValidatorService.validate(facilities))
				.thenReturn(BusinessValidationResult.valid());

        // Invoke
        validatorService.validateProposedUnderlyingAgreement(requestTask);

        // Verify
        verify(underlyingAgreementValidatorService, times(1))
                .getValidationResults(originalContainer, context);
        verify(cca2BaselineAndTargetsValidatorService, times(1)).validate(any(), eq(originalContainer), eq(creationDate.toLocalDate()));
        verify(proposedUnderlyingAgreementVariationTargetUnitDetailsValidatorService, times(1))
                .validate(requestTask);
        verify(proposedVariationDetailsValidator, times(1))
                .validate(taskPayload);
        verify(proposedUnderlyingAgreementVariationApplicationReasonDataValidator, times(1))
                .validate(taskPayload);
        verify(underlyingAgreementFacilitiesFinalizationValidatorService, times(1)).validate(facilities);
    }
}
