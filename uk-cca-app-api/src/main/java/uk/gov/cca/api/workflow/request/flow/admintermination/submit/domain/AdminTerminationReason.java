package uk.gov.cca.api.workflow.request.flow.admintermination.submit.domain;

import lombok.Getter;

@Getter
public enum AdminTerminationReason {
    SITE_CLOSURE_SCHEME("Due to site closure and withdrawal from the scheme.", AdminTerminationReasonCategory.ADMINISTRATIVE),
    NOT_SIGN_AGREEMENT("You didn’t provide your assent for this agreement.", AdminTerminationReasonCategory.ADMINISTRATIVE),
    TRANSFER_OF_OWNERSHIP("The ownership of all the facilities in your agreement has transferred to a different operator.", AdminTerminationReasonCategory.ADMINISTRATIVE),
    DATA_NOT_PROVIDED("You didn’t provide your actual baseline data for your greenfield site.", AdminTerminationReasonCategory.ADMINISTRATIVE),
    FAILURE_TO_COMPLY("You contravened the agreement.", AdminTerminationReasonCategory.REGULATORY),
    FAILURE_TO_AGREE("You failed to agree a variation in a target proposed under the terms of the underlying agreement.", AdminTerminationReasonCategory.REGULATORY),
    FAILURE_TO_PAY("Failure to pay any financial penalty imposed on the account holder by the administrator.", AdminTerminationReasonCategory.REGULATORY);

    private String description;
    private AdminTerminationReasonCategory category;

    AdminTerminationReason(String description, AdminTerminationReasonCategory category) {
        this.description = description;
        this.category = category;
    }
}
