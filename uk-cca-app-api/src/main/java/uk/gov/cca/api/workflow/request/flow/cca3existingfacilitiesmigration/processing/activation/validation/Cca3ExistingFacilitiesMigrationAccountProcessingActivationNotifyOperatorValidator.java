package uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.activation.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.ValidatorHelper;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.activation.domain.Cca3ExistingFacilitiesMigrationAccountProcessingActivationRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaNotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.common.validation.decisionnotification.DecisionNotificationValidator;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class Cca3ExistingFacilitiesMigrationAccountProcessingActivationNotifyOperatorValidator {

    private final Cca3ExistingFacilitiesMigrationAccountProcessingActivationSubmitValidator cca3ExistingFacilitiesMigrationAccountProcessingActivationSubmitValidator;
    private final DecisionNotificationValidator decisionNotificationValidator;

    public void validate(final RequestTask requestTask, final CcaNotifyOperatorForDecisionRequestTaskActionPayload payload,
                         final AppUser appUser) {
        final Cca3ExistingFacilitiesMigrationAccountProcessingActivationRequestTaskPayload taskPayload =
                (Cca3ExistingFacilitiesMigrationAccountProcessingActivationRequestTaskPayload) requestTask.getPayload();

        List<BusinessValidationResult> validationResults = new ArrayList<>();

        // Validate submit data
        validationResults.add(cca3ExistingFacilitiesMigrationAccountProcessingActivationSubmitValidator.validate(taskPayload));

        // Validate decision notification
        validationResults.add(decisionNotificationValidator
                .validateDecisionNotification(requestTask, payload.getDecisionNotification(), appUser));

        boolean isValid = validationResults.stream().allMatch(BusinessValidationResult::isValid);

        if(!isValid) {
            throw new BusinessException(CcaErrorCode.INVALID_CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_ACTIVATION,
                    ValidatorHelper.extractViolations(validationResults));
        }
    }
}
