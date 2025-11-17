package uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.ValidatorHelper;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.AuditTrackCorrectiveActionsRequestTaskPayload;
import uk.gov.netz.api.common.exception.BusinessException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditTrackCorrectiveActionsCompleteValidator {

    private final AuditTrackCorrectiveActionsValidator auditTrackCorrectiveActionsValidator;
    private final AuditTrackCorrectiveActionsFollowUpResponsesValidator auditTrackCorrectiveActionsFollowUpResponsesValidator;

    public void validate(final AuditTrackCorrectiveActionsRequestTaskPayload taskPayload) {
        List<BusinessValidationResult> validationResults = new ArrayList<>();

        validationResults.add(auditTrackCorrectiveActionsValidator.validate(taskPayload));

        validationResults.add(auditTrackCorrectiveActionsFollowUpResponsesValidator.validateCompletedResponses(taskPayload));

        boolean isValid = validationResults.stream().allMatch(BusinessValidationResult::isValid);

        if (!isValid) {
            throw new BusinessException(CcaErrorCode.INVALID_FACILITY_AUDIT, ValidatorHelper.extractViolations(validationResults));
        }
    }
}
