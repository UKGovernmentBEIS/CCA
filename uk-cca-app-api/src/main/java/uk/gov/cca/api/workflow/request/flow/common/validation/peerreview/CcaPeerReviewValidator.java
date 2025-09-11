package uk.gov.cca.api.workflow.request.flow.common.validation.peerreview;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskType;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskTypeService;
import uk.gov.netz.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.netz.api.workflow.request.flow.common.validation.PeerReviewerTaskAssignmentValidator;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CcaPeerReviewValidator {

    private final PeerReviewerTaskAssignmentValidator peerReviewerTaskAssignmentValidator;
    private final RequestTaskTypeService requestTaskTypeService;

    public BusinessValidationResult validate(final RequestTask requestTask,
                                             final PeerReviewRequestTaskActionPayload payload,
                                             final AppUser appUser,
                                             final String requestTaskTypeCode) {
        List<CcaPeerReviewViolation> violations = new ArrayList<>();

        // requestTaskType to be assigned to peerReviewer
        RequestTaskType requestTaskType = requestTaskTypeService.findByCode(requestTaskTypeCode);
        final String peerReviewer = payload.getPeerReviewer();

        try {
            // Validate peer reviewer
            peerReviewerTaskAssignmentValidator
                    .validate(requestTask, requestTaskType, peerReviewer, appUser);
        } catch (BusinessException e) {
            violations.add(new CcaPeerReviewViolation(CcaPeerReviewViolation.CcaPeerReviewViolationMessage.INVALID_PEER_REVIEWER_ASSIGNMENT));
        }

        return BusinessValidationResult.builder()
                .valid(violations.isEmpty())
                .violations(violations)
                .build();
    }
}
