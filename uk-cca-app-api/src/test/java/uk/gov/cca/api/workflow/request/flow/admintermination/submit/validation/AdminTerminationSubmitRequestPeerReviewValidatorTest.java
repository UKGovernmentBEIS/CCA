package uk.gov.cca.api.workflow.request.flow.admintermination.submit.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.admintermination.submit.domain.AdminTerminationReason;
import uk.gov.cca.api.workflow.request.flow.admintermination.submit.domain.AdminTerminationReasonDetails;
import uk.gov.cca.api.workflow.request.flow.admintermination.submit.domain.AdminTerminationSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.common.validation.peerreview.CcaPeerReviewValidator;
import uk.gov.cca.api.workflow.request.flow.common.validation.peerreview.CcaPeerReviewViolation;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;

import java.util.List;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminTerminationSubmitRequestPeerReviewValidatorTest {

    @InjectMocks
    private AdminTerminationSubmitRequestPeerReviewValidator validator;

    @Mock
    private AdminTerminationSubmitValidator adminTerminationSubmitValidator;

    @Mock
    private CcaPeerReviewValidator peerReviewValidator;

    @Test
    void validate() {

        final String peerReviewer = "peerReviewer";
        final AdminTerminationSubmitRequestTaskPayload taskPayload = AdminTerminationSubmitRequestTaskPayload.builder()
                .adminTerminationReasonDetails(AdminTerminationReasonDetails.builder()
                        .reason(AdminTerminationReason.FAILURE_TO_COMPLY)
                        .build())
                .build();
        final RequestTask requestTask = RequestTask.builder()
                .payload(taskPayload)
                .build();
        final PeerReviewRequestTaskActionPayload payload = PeerReviewRequestTaskActionPayload.builder()
                .peerReviewer(peerReviewer)
                .build();
        final AppUser appUser = AppUser.builder().userId("regulatorReviewer").build();
        final BusinessValidationResult businessValidationResult = BusinessValidationResult.builder().valid(true).build();

        when(adminTerminationSubmitValidator.validate(taskPayload)).thenReturn(businessValidationResult);
        when(peerReviewValidator.validate(requestTask, payload, appUser, CcaRequestTaskType.ADMIN_TERMINATION_APPLICATION_PEER_REVIEW))
                .thenReturn(businessValidationResult);

        // invoke
        validator.validate(requestTask, payload, appUser);

        // verify
        verify(adminTerminationSubmitValidator, times(1)).validate(taskPayload);
        verify(peerReviewValidator, times(1))
                .validate(requestTask, payload, appUser, CcaRequestTaskType.ADMIN_TERMINATION_APPLICATION_PEER_REVIEW);
    }

    @Test
    void validate_not_valid() {

        final String peerReviewer = "peerReviewer";
        final AdminTerminationSubmitRequestTaskPayload taskPayload = AdminTerminationSubmitRequestTaskPayload.builder()
                .adminTerminationReasonDetails(AdminTerminationReasonDetails.builder()
                        .reason(AdminTerminationReason.FAILURE_TO_COMPLY)
                        .build())
                .build();
        final RequestTask requestTask = RequestTask.builder()
                .payload(taskPayload)
                .build();
        final PeerReviewRequestTaskActionPayload payload = PeerReviewRequestTaskActionPayload.builder()
                .peerReviewer(peerReviewer)
                .build();
        final List<CcaPeerReviewViolation> violations =
                List.of(new CcaPeerReviewViolation(CcaPeerReviewViolation.CcaPeerReviewViolationMessage.INVALID_PEER_REVIEWER_ASSIGNMENT));
        final AppUser appUser = AppUser.builder().userId("regulatorReviewer").build();
        final BusinessValidationResult businessValidationResult = BusinessValidationResult.builder().valid(true).build();
        final BusinessValidationResult businessValidationResultError = BusinessValidationResult.builder()
                .valid(false)
                .violations(violations)
                .build();

        when(adminTerminationSubmitValidator.validate(taskPayload)).thenReturn(businessValidationResult);
        when(peerReviewValidator.validate(requestTask, payload, appUser, CcaRequestTaskType.ADMIN_TERMINATION_APPLICATION_PEER_REVIEW))
                .thenReturn(businessValidationResultError);

        // invoke
        BusinessException businessException =
                assertThrows(BusinessException.class, () -> validator.validate(requestTask, payload, appUser));

        // verify
        Assertions.assertEquals(CcaErrorCode.INVALID_ADMIN_TERMINATION, businessException.getErrorCode());
        verify(adminTerminationSubmitValidator, times(1)).validate(taskPayload);
        verify(peerReviewValidator, times(1))
                .validate(requestTask, payload, appUser, CcaRequestTaskType.ADMIN_TERMINATION_APPLICATION_PEER_REVIEW);
    }
}

