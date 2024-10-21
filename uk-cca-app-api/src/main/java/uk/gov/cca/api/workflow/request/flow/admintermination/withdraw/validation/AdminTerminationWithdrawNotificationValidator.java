package uk.gov.cca.api.workflow.request.flow.admintermination.withdraw.validation;

import org.springframework.stereotype.Service;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.workflow.request.flow.admintermination.common.validation.AdminTerminationDecisionNotificationValidator;
import uk.gov.cca.api.workflow.request.flow.admintermination.withdraw.domain.AdminTerminationWithdrawReasonDetails;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaDecisionNotification;
import uk.gov.cca.api.workflow.request.flow.common.validation.decisionnotification.CcaDecisionNotificationUsersValidator;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

@Service
public class AdminTerminationWithdrawNotificationValidator extends AdminTerminationDecisionNotificationValidator {
    public AdminTerminationWithdrawNotificationValidator(DataValidator<CcaDecisionNotification> validator, CcaDecisionNotificationUsersValidator ccaDecisionNotificationUsersValidator) {
        super(validator, ccaDecisionNotificationUsersValidator);
    }

    @Override
    protected String getSectionName() {
        return AdminTerminationWithdrawReasonDetails.class.getName();
    }

    public BusinessValidationResult validate(final RequestTask requestTask, final CcaDecisionNotification decisionNotification,
                                             final AppUser appUser) {
        return super.validateDecisionNotification(requestTask, decisionNotification, appUser);
    }
}
