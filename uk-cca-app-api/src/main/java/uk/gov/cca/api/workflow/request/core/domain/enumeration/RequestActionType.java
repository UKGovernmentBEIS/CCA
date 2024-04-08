package uk.gov.cca.api.workflow.request.core.domain.enumeration;

/**
 * Request Action Types.
 */
public enum RequestActionType {

    // rfi
    RFI_SUBMITTED,
    RFI_CANCELLED,
    RFI_EXPIRED,
    RFI_RESPONSE_SUBMITTED,

    // Request for Determination Extension (RDE)
    RDE_SUBMITTED,
    RDE_ACCEPTED,
    RDE_REJECTED,
    RDE_FORCE_ACCEPTED,
    RDE_FORCE_REJECTED,
    RDE_EXPIRED,
    RDE_CANCELLED,
    
    //payment related request actions
    PAYMENT_MARKED_AS_PAID,
    PAYMENT_MARKED_AS_RECEIVED,
    PAYMENT_COMPLETED,
    PAYMENT_CANCELLED,

    // common action type for requests terminated by the system
    REQUEST_TERMINATED,
    VERIFICATION_STATEMENT_CANCELLED
}
