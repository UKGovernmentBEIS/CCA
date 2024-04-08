package uk.gov.cca.api.workflow.request.core.domain.enumeration;

public enum RequestTaskActionPayloadType {

    RFI_SUBMIT_PAYLOAD,
    RFI_RESPONSE_SUBMIT_PAYLOAD,
    
    RDE_SUBMIT_PAYLOAD,
    RDE_RESPONSE_SUBMIT_PAYLOAD,
    RDE_FORCE_DECISION_PAYLOAD,
    
    PAYMENT_MARK_AS_RECEIVED_PAYLOAD,
    PAYMENT_CANCEL_PAYLOAD,

    EMPTY_PAYLOAD
}
