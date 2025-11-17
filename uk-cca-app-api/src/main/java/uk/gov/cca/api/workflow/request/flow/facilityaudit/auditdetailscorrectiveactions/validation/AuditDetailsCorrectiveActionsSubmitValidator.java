package uk.gov.cca.api.workflow.request.flow.facilityaudit.auditdetailscorrectiveactions.validation;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.workflow.request.flow.common.validation.FileAttachmentsExistenceValidator;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.auditdetailscorrectiveactions.domain.AuditDetailsAndCorrectiveActions;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.auditdetailscorrectiveactions.domain.AuditDetailsCorrectiveActionsSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.common.validation.FacilityAuditViolation;
import uk.gov.netz.api.common.exception.BusinessException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditDetailsCorrectiveActionsSubmitValidator {

    private final DataValidator<AuditDetailsAndCorrectiveActions> dataValidator;
    private final FileAttachmentsExistenceValidator fileAttachmentsExistenceValidator;


    public void validate(final AuditDetailsCorrectiveActionsSubmitRequestTaskPayload taskPayload) {
        List<FacilityAuditViolation> violations = new ArrayList<>();

        // Validate data
        if (ObjectUtils.isEmpty(taskPayload.getAuditDetailsAndCorrectiveActions())) {
            violations.add(new FacilityAuditViolation(AuditDetailsAndCorrectiveActions.class.getName(),
                    FacilityAuditViolation.FacilityAuditViolationMessage.INVALID_AUDIT_DETAILS_CORRECTIVE_ACTIONS_DATA));
        } else {
            dataValidator.validate(taskPayload.getAuditDetailsAndCorrectiveActions())
                    .map(businessViolation ->
                            new FacilityAuditViolation(AuditDetailsAndCorrectiveActions.class.getName(),
                                    FacilityAuditViolation.FacilityAuditViolationMessage.INVALID_AUDIT_DETAILS_CORRECTIVE_ACTIONS_DATA,
                                    businessViolation.getData()))
                    .ifPresent(violations::add);
        }

        // Validate files
        if (!fileAttachmentsExistenceValidator
                .valid(taskPayload.getReferencedAttachmentIds(), taskPayload.getFacilityAuditAttachments().keySet())) {
            violations.add(new FacilityAuditViolation(FacilityAuditViolation.FacilityAuditViolationMessage.ATTACHMENT_NOT_FOUND));
        }

        boolean isValid = violations.isEmpty();

        if (!isValid) {
            throw new BusinessException(CcaErrorCode.INVALID_FACILITY_AUDIT, violations);
        }
    }
}