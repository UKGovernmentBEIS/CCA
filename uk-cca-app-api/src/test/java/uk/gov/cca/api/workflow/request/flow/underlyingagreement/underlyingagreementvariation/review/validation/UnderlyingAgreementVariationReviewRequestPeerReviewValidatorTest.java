package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.Determination;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.DeterminationType;
import uk.gov.cca.api.workflow.request.flow.common.validation.peerreview.CcaPeerReviewValidator;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.validation.UnderlyingAgreementVariationReviewDecisionDataValidator;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.domain.UnderlyingAgreementVariationReviewRequestTaskPayload;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementVariationReviewRequestPeerReviewValidatorTest {

    @InjectMocks
    public UnderlyingAgreementVariationReviewRequestPeerReviewValidator underlyingAgreementVariationReviewRequestPeerReviewValidator;

    @Mock
    private UnderlyingAgreementVariationReviewValidatorService underlyingAgreementVariationReviewValidatorService;

    @Mock
    private UnderlyingAgreementVariationReviewDecisionDataValidator underlyingAgreementVariationReviewDecisionDataValidator;

    @Mock
    private CcaPeerReviewValidator peerReviewValidator;

    @Test
    void validate() {
        final String peerReviewer = UUID.randomUUID().toString();
        final Determination determination = Determination.builder().type(DeterminationType.ACCEPTED).additionalInformation("text").build();
        final UnderlyingAgreementVariationReviewRequestTaskPayload taskPayload =
                UnderlyingAgreementVariationReviewRequestTaskPayload
                        .builder()
                        .payloadType(CcaRequestTaskPayloadType.UNDERLYING_AGREEMENT_VARIATION_APPLICATION_REVIEW_PAYLOAD)
                        .reviewSectionsCompleted(Map.of(UnderlyingAgreementTargetUnitDetails.class.getName(), "COMPLETED"))
                        .determination(determination)
                        .build();
        final RequestTask requestTask = RequestTask.builder()
                .payload(taskPayload)
                .build();
        final PeerReviewRequestTaskActionPayload payload = PeerReviewRequestTaskActionPayload.builder()
                .peerReviewer(peerReviewer)
                .build();
        final AppUser appUser = AppUser.builder().userId("user").build();

        List<BusinessValidationResult> validationResults = new ArrayList<>();
        validationResults.add(BusinessValidationResult.valid());

        when(underlyingAgreementVariationReviewValidatorService.validateEditedUnderlyingAgreement(requestTask))
                .thenReturn(validationResults);
        when(underlyingAgreementVariationReviewDecisionDataValidator.validateReviewDecisionData(requestTask))
                .thenReturn(BusinessValidationResult.valid());
        when(peerReviewValidator.validate(requestTask, payload, appUser, CcaRequestTaskType.UNDERLYING_AGREEMENT_VARIATION_APPLICATION_PEER_REVIEW))
                .thenReturn(BusinessValidationResult.valid());

        // Invoke
        underlyingAgreementVariationReviewRequestPeerReviewValidator.validate(requestTask, payload, appUser);

        // Verify
        verify(underlyingAgreementVariationReviewDecisionDataValidator, times(1))
                .validateReviewDecisionData(requestTask);
        verify(underlyingAgreementVariationReviewValidatorService, times(1))
                .validateEditedUnderlyingAgreement(requestTask);
        verify(peerReviewValidator, times(1))
                .validate(requestTask, payload, appUser, CcaRequestTaskType.UNDERLYING_AGREEMENT_VARIATION_APPLICATION_PEER_REVIEW);
    }
}
