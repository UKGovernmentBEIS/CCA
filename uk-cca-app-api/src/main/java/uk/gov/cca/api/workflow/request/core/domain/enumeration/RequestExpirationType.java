package uk.gov.cca.api.workflow.request.core.domain.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RequestExpirationType {

    APPLICATION_REVIEW("applicationReview"),
    RFI("rfi"),
    RDE("rde"),
    FOLLOW_UP_RESPONSE("followUpResponse"),
    PAYMENT("payment")
    ;
    
    private final String code;
}
