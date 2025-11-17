package uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.activation.validation;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.validation.Cca3ExistingFacilitiesMigrationViolation;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.activation.domain.Cca3ExistingFacilitiesMigrationAccountProcessingActivationDetails;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.activation.domain.Cca3ExistingFacilitiesMigrationAccountProcessingActivationRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.common.validation.FileAttachmentsExistenceValidator;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class Cca3ExistingFacilitiesMigrationAccountProcessingActivationSubmitValidator {

    private final DataValidator<Cca3ExistingFacilitiesMigrationAccountProcessingActivationDetails> validator;
    private final FileAttachmentsExistenceValidator fileAttachmentsExistenceValidator;

    public BusinessValidationResult validate(final Cca3ExistingFacilitiesMigrationAccountProcessingActivationRequestTaskPayload taskPayload) {
        List<Cca3ExistingFacilitiesMigrationViolation> violations = new ArrayList<>();

        // Validate data
        if(ObjectUtils.isEmpty(taskPayload.getActivationDetails())) {
            violations.add(new Cca3ExistingFacilitiesMigrationViolation(Cca3ExistingFacilitiesMigrationAccountProcessingActivationRequestTaskPayload.class.getName(),
                    Cca3ExistingFacilitiesMigrationViolation.Cca3ExistingFacilitiesMigrationViolationMessage.INVALID_ACTIVATION_DETAILS_DATA));
        }
        else {
            validator.validate(taskPayload.getActivationDetails())
                    .map(businessViolation ->
                            new Cca3ExistingFacilitiesMigrationViolation(Cca3ExistingFacilitiesMigrationAccountProcessingActivationRequestTaskPayload.class.getName(),
                                    Cca3ExistingFacilitiesMigrationViolation.Cca3ExistingFacilitiesMigrationViolationMessage.INVALID_ACTIVATION_DETAILS_DATA,
                                    businessViolation.getData()))
                    .ifPresent(violations::add);
        }

        // Validate files
        if(!fileAttachmentsExistenceValidator
                .valid(taskPayload.getReferencedAttachmentIds(), taskPayload.getActivationAttachments().keySet())) {
            violations.add(new Cca3ExistingFacilitiesMigrationViolation(Cca3ExistingFacilitiesMigrationViolation.Cca3ExistingFacilitiesMigrationViolationMessage.ATTACHMENT_NOT_FOUND));
        }

        return BusinessValidationResult.builder()
                .valid(violations.isEmpty())
                .violations(violations)
                .build();
    }
}
