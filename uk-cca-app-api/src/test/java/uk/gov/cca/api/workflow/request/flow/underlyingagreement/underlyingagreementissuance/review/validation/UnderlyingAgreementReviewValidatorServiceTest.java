package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.validation;

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
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaReviewDecisionType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.validation.CCA2BaselineAndTargetsValidatorService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementFacilityReviewDecision;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.validation.EditedUnderlyingAgreementTargetUnitDetailsValidatorService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.domain.UnderlyingAgreementReviewRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementReviewValidatorServiceTest {

    @InjectMocks
    private UnderlyingAgreementReviewValidatorService validatorService;

    @Mock
    private UnderlyingAgreementValidatorService underlyingAgreementValidatorService;

    @Mock
    private CCA2BaselineAndTargetsValidatorService cca2BaselineAndTargetsValidatorService;

    @Mock
    private EditedUnderlyingAgreementTargetUnitDetailsValidatorService editedUnderlyingAgreementTargetUnitDetailsValidatorService;

    @Mock
    private ProposedUnderlyingAgreementTargetUnitDetailsValidatorService proposedUnderlyingAgreementTargetUnitDetailsValidatorService;

    @Mock
    private UnderlyingAgreementFacilitiesFinalizationValidatorService underlyingAgreementFacilitiesFinalizationValidatorService;
    
    @Test
    void validateEditedUnderlyingAgreement() {
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
        final UnderlyingAgreementContainer editedUnaContainer = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(underlyingAgreement)
                .build();
        final SchemeVersion workflowSchemeVersion = SchemeVersion.CCA_3;
        final UnderlyingAgreementReviewRequestTaskPayload taskPayload = UnderlyingAgreementReviewRequestTaskPayload.builder()
                .workflowSchemeVersion(workflowSchemeVersion)
                .underlyingAgreement(UnderlyingAgreementPayload.builder()
                        .underlyingAgreementTargetUnitDetails(targetUnitDetails)
                        .underlyingAgreement(underlyingAgreement)
                        .build())
                .underlyingAgreementProposed(UnderlyingAgreementPayload.builder()
                        .underlyingAgreementTargetUnitDetails(targetUnitDetails)
                        .underlyingAgreement(underlyingAgreement)
                        .build())
                .build();
        final Request request = Request.builder()
                .creationDate(LocalDateTime.of(2025, 1, 1, 0, 0, 0))
                .payload(UnderlyingAgreementRequestPayload.builder()
                        .underlyingAgreement(UnderlyingAgreementPayload.builder()
                                .underlyingAgreement(UnderlyingAgreement.builder()
                                        .facilities(facilities)
                                        .build())
                                .build())
                        .underlyingAgreementProposed(UnderlyingAgreementPayload.builder()
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
                .schemeVersion(workflowSchemeVersion)
                .requestCreationDate(request.getCreationDate())
                .build();

        List<BusinessValidationResult> validationResults = new ArrayList<>();
        validationResults.add(BusinessValidationResult.valid());
        when(underlyingAgreementValidatorService.getValidationResults(editedUnaContainer, context))
                .thenReturn(validationResults);
        when(editedUnderlyingAgreementTargetUnitDetailsValidatorService.validate(requestTask))
                .thenReturn(BusinessValidationResult.valid());
        when(cca2BaselineAndTargetsValidatorService.validateEmpty(editedUnaContainer))
                .thenReturn(BusinessValidationResult.valid());

        // Invoke
        validatorService.validateEditedUnderlyingAgreement(requestTask);

        // Verify
        verify(underlyingAgreementValidatorService, times(1))
                .getValidationResults(editedUnaContainer, context);
        verify(editedUnderlyingAgreementTargetUnitDetailsValidatorService, times(1))
                .validate(requestTask);
        verify(cca2BaselineAndTargetsValidatorService, times(1))
                .validateEmpty(editedUnaContainer);
    }

    @Test
    void validateProposedUnderlyingAgreement() {
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
        final UnderlyingAgreementContainer proposedUnaContainer = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(underlyingAgreement)
                .build();
        final SchemeVersion workflowSchemeVersion = SchemeVersion.CCA_3;
        final UnderlyingAgreementReviewRequestTaskPayload taskPayload = UnderlyingAgreementReviewRequestTaskPayload.builder()
                .workflowSchemeVersion(workflowSchemeVersion)
                .underlyingAgreement(UnderlyingAgreementPayload.builder()
                        .underlyingAgreementTargetUnitDetails(targetUnitDetails)
                        .underlyingAgreement(underlyingAgreement)
                        .build())
                .underlyingAgreementProposed(UnderlyingAgreementPayload.builder()
                        .underlyingAgreementTargetUnitDetails(targetUnitDetails)
                        .underlyingAgreement(underlyingAgreement)
                        .build())
                .build();
        final Request request = Request.builder()
                .creationDate(LocalDateTime.of(2025, 1, 1, 0, 0))
                .payload(UnderlyingAgreementRequestPayload.builder()
                        .underlyingAgreement(UnderlyingAgreementPayload.builder()
                                .underlyingAgreement(UnderlyingAgreement.builder()
                                        .facilities(facilities)
                                        .build())
                                .build())
                        .underlyingAgreementProposed(UnderlyingAgreementPayload.builder()
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
        when(underlyingAgreementValidatorService.getValidationResults(proposedUnaContainer, context))
                .thenReturn(validationResults);
        when(proposedUnderlyingAgreementTargetUnitDetailsValidatorService.validate(requestTask))
                .thenReturn(BusinessValidationResult.valid());
        when(cca2BaselineAndTargetsValidatorService.validateEmpty(proposedUnaContainer))
                .thenReturn(BusinessValidationResult.valid());
        when(underlyingAgreementFacilitiesFinalizationValidatorService.validate(facilities))
				.thenReturn(BusinessValidationResult.valid());

        // Invoke
        validatorService.validateProposedUnderlyingAgreement(requestTask);

        // Verify
        verify(underlyingAgreementValidatorService, times(1))
                .getValidationResults(proposedUnaContainer, context);
        verify(underlyingAgreementValidatorService, never())
        		.getValidationResultsExceptFacilities(proposedUnaContainer, context);
        verify(proposedUnderlyingAgreementTargetUnitDetailsValidatorService, times(1))
                .validate(requestTask);
        verify(underlyingAgreementFacilitiesFinalizationValidatorService, times(1)).validate(facilities);
    }
    
    @Test
    void validateProposedUnderlyingAgreement_no_facilities() {
        final Set<Facility> facilities = Set.of();
        final UnderlyingAgreementTargetUnitDetails targetUnitDetails = UnderlyingAgreementTargetUnitDetails.builder()
                .operatorName("operatorName")
                .build();
        final UnderlyingAgreement underlyingAgreement = UnderlyingAgreement.builder()
                .facilities(facilities)
                .authorisationAndAdditionalEvidence(AuthorisationAndAdditionalEvidence.builder()
                        .authorisationAttachmentIds(Set.of(UUID.randomUUID()))
                        .build())
                .build();
        final UnderlyingAgreementContainer proposedUnaContainer = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(underlyingAgreement)
                .build();
        final SchemeVersion workflowSchemeVersion = SchemeVersion.CCA_3;
        final UnderlyingAgreementReviewRequestTaskPayload taskPayload = UnderlyingAgreementReviewRequestTaskPayload.builder()
                .workflowSchemeVersion(workflowSchemeVersion)
                .underlyingAgreement(UnderlyingAgreementPayload.builder()
                        .underlyingAgreementTargetUnitDetails(targetUnitDetails)
                        .underlyingAgreement(underlyingAgreement)
                        .build())
                .underlyingAgreementProposed(UnderlyingAgreementPayload.builder()
                        .underlyingAgreementTargetUnitDetails(targetUnitDetails)
                        .underlyingAgreement(underlyingAgreement)
                        .build())
                .facilitiesReviewGroupDecisions(Map.of("ASD-1234", UnderlyingAgreementFacilityReviewDecision.builder()
                		.type(CcaReviewDecisionType.REJECTED)
                		.build()))
                .build();
        final Request request = Request.builder()
                .creationDate(LocalDateTime.of(2025, 1, 1, 0, 0))
                .payload(UnderlyingAgreementRequestPayload.builder()
                        .underlyingAgreement(UnderlyingAgreementPayload.builder()
                                .underlyingAgreement(UnderlyingAgreement.builder()
                                        .facilities(facilities)
                                        .build())
                                .build())
                        .underlyingAgreementProposed(UnderlyingAgreementPayload.builder()
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
        when(underlyingAgreementValidatorService.getValidationResultsExceptFacilities(proposedUnaContainer, context))
                .thenReturn(validationResults);
        when(proposedUnderlyingAgreementTargetUnitDetailsValidatorService.validate(requestTask))
                .thenReturn(BusinessValidationResult.valid());
        when(cca2BaselineAndTargetsValidatorService.validateEmpty(proposedUnaContainer))
        		.thenReturn(BusinessValidationResult.valid());
        when(underlyingAgreementFacilitiesFinalizationValidatorService.validate(facilities))
				.thenReturn(BusinessValidationResult.valid());

        // Invoke
        validatorService.validateProposedUnderlyingAgreement(requestTask);

        // Verify
        verify(underlyingAgreementValidatorService, times(1))
                .getValidationResultsExceptFacilities(proposedUnaContainer, context);
        verify(underlyingAgreementValidatorService, never())
        		.getValidationResults(proposedUnaContainer, context);
        verify(proposedUnderlyingAgreementTargetUnitDetailsValidatorService, times(1))
                .validate(requestTask);
        verify(cca2BaselineAndTargetsValidatorService, times(1))
                .validateEmpty(proposedUnaContainer);
        verify(underlyingAgreementFacilitiesFinalizationValidatorService, times(1)).validate(facilities);
        
    }

    @Test
    void validateProposedUnderlyingAgreement_not_valid() {
        final UnderlyingAgreementTargetUnitDetails targetUnitDetails = UnderlyingAgreementTargetUnitDetails.builder()
                .operatorName("operatorName")
                .build();
        Set<Facility> facilities = Set.of(Facility.builder()
		        .status(FacilityStatus.NEW)
		        .facilityItem(FacilityItem.builder().facilityId("2").build())
		        .build());
		final UnderlyingAgreement underlyingAgreement = UnderlyingAgreement.builder()
                .facilities(facilities)
                .authorisationAndAdditionalEvidence(AuthorisationAndAdditionalEvidence.builder()
                        .authorisationAttachmentIds(Set.of(UUID.randomUUID()))
                        .build())
                .build();
        final UnderlyingAgreementContainer proposedUnaContainer = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(underlyingAgreement)
                .build();
        final SchemeVersion workflowSchemeVersion = SchemeVersion.CCA_3;
        UnderlyingAgreementReviewRequestTaskPayload taskPayload = UnderlyingAgreementReviewRequestTaskPayload.builder()
                .workflowSchemeVersion(workflowSchemeVersion)
                .underlyingAgreement(UnderlyingAgreementPayload.builder()
                        .underlyingAgreementTargetUnitDetails(targetUnitDetails)
                        .underlyingAgreement(underlyingAgreement)
                        .build())
                .underlyingAgreementProposed(UnderlyingAgreementPayload.builder()
                        .underlyingAgreementTargetUnitDetails(targetUnitDetails)
                        .underlyingAgreement(underlyingAgreement)
                        .build())
                .build();
        final Request request = Request.builder()
                .payload(UnderlyingAgreementRequestPayload.builder()
                        .underlyingAgreement(UnderlyingAgreementPayload.builder()
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
        when(underlyingAgreementValidatorService.getValidationResults(proposedUnaContainer, context))
                .thenReturn(validationResults);
        when(proposedUnderlyingAgreementTargetUnitDetailsValidatorService.validate(requestTask))
                .thenReturn(BusinessValidationResult.invalid(List.of()));
        when(cca2BaselineAndTargetsValidatorService.validateEmpty(proposedUnaContainer))
                .thenReturn(BusinessValidationResult.valid());
        when(underlyingAgreementFacilitiesFinalizationValidatorService.validate(facilities))
				.thenReturn(BusinessValidationResult.valid());

        // Invoke
        List<BusinessValidationResult> results = validatorService.validateProposedUnderlyingAgreement(requestTask);

        // Verify
        assertThat(results.stream().allMatch(BusinessValidationResult::isValid)).isFalse();
        verify(underlyingAgreementValidatorService, times(1))
                .getValidationResults(proposedUnaContainer, context);
        verify(proposedUnderlyingAgreementTargetUnitDetailsValidatorService, times(1))
                .validate(requestTask);
        verify(cca2BaselineAndTargetsValidatorService, times(1))
                .validateEmpty(proposedUnaContainer);
        verify(underlyingAgreementFacilitiesFinalizationValidatorService, times(1)).validate(facilities);
    }
}
