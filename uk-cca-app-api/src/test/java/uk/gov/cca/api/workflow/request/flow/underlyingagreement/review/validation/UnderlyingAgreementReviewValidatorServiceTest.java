package uk.gov.cca.api.workflow.request.flow.underlyingagreement.review.validation;

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
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityItem;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityStatus;
import uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementValidatorService;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaNotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementRequestPayload;
import uk.gov.cca.api.workflow.request.flow.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.validation.UnderlyingAgreementTargetUnitDetailsValidatorService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.review.domain.UnderlyingAgreementReviewRequestTaskPayload;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    private UnderlyingAgreementTargetUnitDetailsValidatorService underlyingAgreementTargetUnitDetailsValidatorService;

    @Mock
    private UnderlyingAgreementReviewNotifyOperatorValidator underlyingAgreementReviewNotifyOperatorValidator;

    @Mock
    private UnderlyingAgreementReviewDeterminationDecisionsValidator determinationDecisionsValidator;
    @Test
    void validate() {
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
        final UnderlyingAgreementContainer unaContainer = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(underlyingAgreement)
                .build();
        final UnderlyingAgreementReviewRequestTaskPayload taskPayload = UnderlyingAgreementReviewRequestTaskPayload.builder()
                .underlyingAgreement(UnderlyingAgreementPayload.builder()
                        .underlyingAgreementTargetUnitDetails(targetUnitDetails)
                        .underlyingAgreement(underlyingAgreement)
                        .build())
                .build();
        final RequestTask requestTask = RequestTask.builder()
                .request(Request.builder()
                        .payload(UnderlyingAgreementRequestPayload.builder()
                                .underlyingAgreement(UnderlyingAgreementPayload.builder()
                                        .underlyingAgreement(UnderlyingAgreement.builder()
                                                .facilities(facilities)
                                                .build())
                                        .build())
                                .build())
                        .build())
                .payload(taskPayload)
                .build();
        final CcaNotifyOperatorForDecisionRequestTaskActionPayload actionPayload =
                CcaNotifyOperatorForDecisionRequestTaskActionPayload.builder().build();
        final AppUser appUser = AppUser.builder().build();

        List<BusinessValidationResult> validationResults = new ArrayList<>();
        validationResults.add(BusinessValidationResult.valid());
        when(underlyingAgreementValidatorService.getValidationResults(unaContainer))
                .thenReturn(validationResults);
        when(underlyingAgreementTargetUnitDetailsValidatorService.validate(requestTask))
                .thenReturn(BusinessValidationResult.valid());
        when(determinationDecisionsValidator.validateOverallDecision(taskPayload))
                .thenReturn(BusinessValidationResult.valid());
        when(underlyingAgreementReviewNotifyOperatorValidator.validate(requestTask, actionPayload, appUser))
                .thenReturn(validationResults);

        // Invoke
        validatorService.validate(requestTask, actionPayload, appUser);

        // Verify
        verify(underlyingAgreementValidatorService, times(1))
                .getValidationResults(unaContainer);
        verify(underlyingAgreementTargetUnitDetailsValidatorService, times(1))
                .validate(requestTask);
        verify(determinationDecisionsValidator, times(1))
                .validateOverallDecision(taskPayload);
        verify(underlyingAgreementReviewNotifyOperatorValidator, times(1))
                .validate(requestTask, actionPayload, appUser);
    }

    @Test
    void validate_not_valid() {
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
        final UnderlyingAgreementContainer unaContainer = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(underlyingAgreement)
                .build();
        UnderlyingAgreementReviewRequestTaskPayload taskPayload = UnderlyingAgreementReviewRequestTaskPayload.builder()
                .underlyingAgreement(UnderlyingAgreementPayload.builder()
                        .underlyingAgreementTargetUnitDetails(targetUnitDetails)
                        .underlyingAgreement(underlyingAgreement)
                        .build())
                .build();
        final RequestTask requestTask = RequestTask.builder()
                .request(Request.builder()
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
                        .build())
                .payload(taskPayload)
                .build();
        final CcaNotifyOperatorForDecisionRequestTaskActionPayload actionPayload =
                CcaNotifyOperatorForDecisionRequestTaskActionPayload.builder().build();
        final AppUser appUser = AppUser.builder().build();

        List<BusinessValidationResult> validationResults = new ArrayList<>();
        validationResults.add(BusinessValidationResult.valid());
        when(underlyingAgreementValidatorService.getValidationResults(unaContainer))
                .thenReturn(validationResults);
        when(underlyingAgreementTargetUnitDetailsValidatorService.validate(requestTask))
                .thenReturn(BusinessValidationResult.valid());
        when(determinationDecisionsValidator.validateOverallDecision(taskPayload))
                .thenReturn(BusinessValidationResult.valid());
        when(underlyingAgreementReviewNotifyOperatorValidator.validate(requestTask, actionPayload, appUser))
                .thenReturn(validationResults);

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class, () ->
                validatorService.validate(requestTask, actionPayload, appUser));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(CcaErrorCode.INVALID_UNDERLYING_AGREEMENT_REVIEW);
        verify(underlyingAgreementValidatorService, times(1))
                .getValidationResults(unaContainer);
        verify(underlyingAgreementTargetUnitDetailsValidatorService, times(1))
                .validate(requestTask);
        verify(determinationDecisionsValidator, times(1))
                .validateOverallDecision((UnderlyingAgreementReviewRequestTaskPayload)requestTask.getPayload());
        verify(underlyingAgreementReviewNotifyOperatorValidator, times(1))
                .validate(requestTask, actionPayload, appUser);
    }
}
