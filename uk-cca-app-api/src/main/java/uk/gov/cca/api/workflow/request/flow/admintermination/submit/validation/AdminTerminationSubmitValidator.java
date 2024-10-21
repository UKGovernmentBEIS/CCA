package uk.gov.cca.api.workflow.request.flow.admintermination.submit.validation;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.workflow.request.flow.admintermination.common.validation.AdminTerminationViolation;
import uk.gov.cca.api.workflow.request.flow.admintermination.submit.domain.AdminTerminationReasonDetails;
import uk.gov.cca.api.workflow.request.flow.admintermination.submit.domain.AdminTerminationSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.common.validation.FileAttachmentsExistenceValidator;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminTerminationSubmitValidator {

    private final DataValidator<AdminTerminationReasonDetails> validator;
    private final FileAttachmentsExistenceValidator fileAttachmentsExistenceValidator;

    public BusinessValidationResult validate(final AdminTerminationSubmitRequestTaskPayload taskPayload) {
        List<AdminTerminationViolation> violations = new ArrayList<>();

        // Validate data
        if(ObjectUtils.isEmpty(taskPayload.getAdminTerminationReasonDetails())) {
            violations.add(new AdminTerminationViolation(AdminTerminationReasonDetails.class.getName(),
                    AdminTerminationViolation.AdminTerminationViolationMessage.INVALID_ADMIN_TERMINATION_REASON_DATA));
        }
        else {
            validator.validate(taskPayload.getAdminTerminationReasonDetails())
                    .map(businessViolation ->
                            new AdminTerminationViolation(AdminTerminationReasonDetails.class.getName(),
                                    AdminTerminationViolation.AdminTerminationViolationMessage.INVALID_ADMIN_TERMINATION_REASON_DATA,
                                    businessViolation.getData()))
                    .ifPresent(violations::add);
        }

        // Validate files
        if(!fileAttachmentsExistenceValidator
                .valid(taskPayload.getReferencedAttachmentIds(), taskPayload.getAdminTerminationAttachments().keySet())) {
            violations.add(new AdminTerminationViolation(AdminTerminationViolation.AdminTerminationViolationMessage.ATTACHMENT_NOT_FOUND));
        }

        return BusinessValidationResult.builder()
                .valid(violations.isEmpty())
                .violations(violations)
                .build();
    }
}
