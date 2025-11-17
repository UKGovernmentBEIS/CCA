package uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.validation;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.workflow.request.flow.common.validation.FileAttachmentsExistenceValidator;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.common.validation.FacilityAuditViolation;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.domain.PreAuditReviewDetails;
import uk.gov.cca.api.workflow.request.flow.facilityaudit.preauditreview.domain.PreAuditReviewSubmitRequestTaskPayload;
import uk.gov.netz.api.common.exception.BusinessException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PreAuditReviewSubmitValidator {

    private final DataValidator<PreAuditReviewDetails> dataValidator;
    private final FileAttachmentsExistenceValidator fileAttachmentsExistenceValidator;

    public void validate(final PreAuditReviewSubmitRequestTaskPayload taskPayload) {
        List<FacilityAuditViolation> violations = new ArrayList<>();

        // Validate data
        if (ObjectUtils.isEmpty(taskPayload.getPreAuditReviewDetails())) {
            violations.add(new FacilityAuditViolation(PreAuditReviewDetails.class.getName(),
                    FacilityAuditViolation.FacilityAuditViolationMessage.INVALID_PRE_AUDIT_MATERIAL_REVIEW_DATA));
        } else {
            dataValidator.validate(taskPayload.getPreAuditReviewDetails())
                    .map(businessViolation ->
                            new FacilityAuditViolation(PreAuditReviewDetails.class.getName(),
                                    FacilityAuditViolation.FacilityAuditViolationMessage.INVALID_PRE_AUDIT_MATERIAL_REVIEW_DATA,
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
