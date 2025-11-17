package uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.validation;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.workflow.request.flow.common.validation.FileAttachmentsExistenceValidator;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.common.validation.FacilityAuditViolation;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.AuditTrackCorrectiveActions;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.audittrackcorrectiveactions.domain.AuditTrackCorrectiveActionsRequestTaskPayload;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditTrackCorrectiveActionsValidator {

    private final DataValidator<AuditTrackCorrectiveActions> dataValidator;
    private final FileAttachmentsExistenceValidator fileAttachmentsExistenceValidator;

    public BusinessValidationResult validate(final AuditTrackCorrectiveActionsRequestTaskPayload taskPayload) {
        List<FacilityAuditViolation> violations = new ArrayList<>();

        // Validate data
        if (ObjectUtils.isEmpty(taskPayload.getAuditTrackCorrectiveActions())) {
            violations.add(new FacilityAuditViolation(AuditTrackCorrectiveActions.class.getName(),
                    FacilityAuditViolation.FacilityAuditViolationMessage.INVALID_TRACK_CORRECTIVE_ACTIONS_DATA));
        } else {
            dataValidator.validate(taskPayload.getAuditTrackCorrectiveActions())
                    .map(businessViolation ->
                            new FacilityAuditViolation(AuditTrackCorrectiveActions.class.getName(),
                                    FacilityAuditViolation.FacilityAuditViolationMessage.INVALID_TRACK_CORRECTIVE_ACTIONS_DATA,
                                    businessViolation.getData()))
                    .ifPresent(violations::add);
        }

        // validate file existence
        if (!fileAttachmentsExistenceValidator
                .valid(taskPayload.getReferencedAttachmentIds(), taskPayload.getFacilityAuditAttachments().keySet())) {
            violations.add(new FacilityAuditViolation(FacilityAuditViolation.FacilityAuditViolationMessage.ATTACHMENT_NOT_FOUND));
        }

        return BusinessValidationResult.builder()
                .valid(violations.isEmpty())
                .violations(violations)
                .build();
    }
}
