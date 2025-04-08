package uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.review.validation;

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
import uk.gov.cca.api.workflow.request.flow.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationRequestPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.validation.EditedUnderlyingAgreementVariationApplicationReasonDataValidator;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.validation.EditedUnderlyingAgreementVariationDetailsValidatorService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.validation.EditedUnderlyingAgreementVariationTargetUnitDetailsValidatorService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.review.domain.UnderlyingAgreementVariationReviewRequestTaskPayload;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UnderlyingAgreementVariationReviewValidatorServiceTest {

    @InjectMocks
    private UnderlyingAgreementVariationReviewValidatorService validatorService;

    @Mock
    private UnderlyingAgreementValidatorService underlyingAgreementValidatorService;

    @Mock
    private EditedUnderlyingAgreementVariationTargetUnitDetailsValidatorService editedUnderlyingAgreementVariationTargetUnitDetailsValidatorService;

    @Mock
    private ProposedUnderlyingAgreementVariationTargetUnitDetailsValidatorService proposedUnderlyingAgreementVariationTargetUnitDetailsValidatorService;

    @Mock
    private UnderlyingAgreementVariationReviewNotifyOperatorValidator underlyingAgreementVariationReviewNotifyOperatorValidator;

    @Mock
    private EditedUnderlyingAgreementVariationDetailsValidatorService editedVariationDetailsValidator;

    @Mock
    private ProposedUnderlyingAgreementVariationDetailsValidatorService proposedVariationDetailsValidator;

    @Mock
    private EditedUnderlyingAgreementVariationApplicationReasonDataValidator editedUnderlyingAgreementVariationApplicationReasonDataValidator;

    @Mock
    private ProposedUnderlyingAgreementVariationApplicationReasonDataValidator proposedUnderlyingAgreementVariationApplicationReasonDataValidator;

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
        final UnderlyingAgreementVariationReviewRequestTaskPayload taskPayload = UnderlyingAgreementVariationReviewRequestTaskPayload.builder()
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
        final RequestTask requestTask = RequestTask.builder()
                .request(Request.builder()
                        .payload(UnderlyingAgreementVariationRequestPayload.builder()
                                .underlyingAgreement(UnderlyingAgreementVariationPayload.builder()
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
        when(underlyingAgreementValidatorService.getValidationResults(any()))
                .thenReturn(validationResults);
        when(editedUnderlyingAgreementVariationTargetUnitDetailsValidatorService.validate(requestTask))
                .thenReturn(BusinessValidationResult.valid());
        when(proposedUnderlyingAgreementVariationTargetUnitDetailsValidatorService.validate(requestTask))
                .thenReturn(BusinessValidationResult.valid());
        when(editedVariationDetailsValidator.validate(taskPayload)).thenReturn(BusinessValidationResult.valid());
        when(proposedVariationDetailsValidator.validate(taskPayload)).thenReturn(BusinessValidationResult.valid());
        when(underlyingAgreementVariationReviewNotifyOperatorValidator.validate(requestTask, actionPayload, appUser))
                .thenReturn(validationResults);
        when(editedUnderlyingAgreementVariationApplicationReasonDataValidator.validate(taskPayload))
                .thenReturn(BusinessValidationResult.valid());
        when(proposedUnderlyingAgreementVariationApplicationReasonDataValidator.validate(taskPayload))
                .thenReturn(BusinessValidationResult.valid());

        // Invoke
        validatorService.validate(requestTask, actionPayload, appUser);

        // Verify
        verify(underlyingAgreementValidatorService, times(2)).getValidationResults(any());
        verify(editedUnderlyingAgreementVariationTargetUnitDetailsValidatorService, times(1))
                .validate(requestTask);
        verify(editedVariationDetailsValidator, times(1))
                .validate(taskPayload);
        verify(underlyingAgreementVariationReviewNotifyOperatorValidator, times(1))
                .validate(requestTask, actionPayload, appUser);
        verify(editedUnderlyingAgreementVariationApplicationReasonDataValidator, times(1))
                .validate(taskPayload);
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
        UnderlyingAgreementVariationReviewRequestTaskPayload taskPayload = UnderlyingAgreementVariationReviewRequestTaskPayload.builder()
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
        final RequestTask requestTask = RequestTask.builder()
                .request(Request.builder()
                        .payload(UnderlyingAgreementVariationRequestPayload.builder()
                                .underlyingAgreement(UnderlyingAgreementVariationPayload.builder()
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
        when(underlyingAgreementValidatorService.getValidationResults(any()))
                .thenReturn(validationResults);
        when(editedUnderlyingAgreementVariationTargetUnitDetailsValidatorService.validate(requestTask))
                .thenReturn(BusinessValidationResult.valid());
        when(proposedUnderlyingAgreementVariationTargetUnitDetailsValidatorService.validate(requestTask))
                .thenReturn(BusinessValidationResult.valid());
        when(editedVariationDetailsValidator.validate(taskPayload)).thenReturn(BusinessValidationResult.valid());
        when(proposedVariationDetailsValidator.validate(taskPayload)).thenReturn(BusinessValidationResult.valid());
        when(underlyingAgreementVariationReviewNotifyOperatorValidator.validate(requestTask, actionPayload, appUser))
                .thenReturn(validationResults);
        when(editedUnderlyingAgreementVariationApplicationReasonDataValidator.validate(taskPayload))
                .thenReturn(BusinessValidationResult.valid());
        when(proposedUnderlyingAgreementVariationApplicationReasonDataValidator.validate(taskPayload))
                .thenReturn(BusinessValidationResult.valid());

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class, () ->
                validatorService.validate(requestTask, actionPayload, appUser));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(CcaErrorCode.INVALID_UNDERLYING_AGREEMENT_VARIATION_REVIEW);
        verify(underlyingAgreementValidatorService, times(2))
                .getValidationResults(any());
        verify(editedUnderlyingAgreementVariationTargetUnitDetailsValidatorService, times(1))
                .validate(requestTask);
        verify(editedVariationDetailsValidator, times(1))
                .validate(taskPayload);
        verify(underlyingAgreementVariationReviewNotifyOperatorValidator, times(1))
                .validate(requestTask, actionPayload, appUser);
        verify(editedUnderlyingAgreementVariationApplicationReasonDataValidator, times(1))
                .validate(taskPayload);
    }
}
