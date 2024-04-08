package uk.gov.cca.api.workflow.request.core.domain.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestType.DUMMY_REQUEST_TYPE;

@Getter
@AllArgsConstructor
public enum RequestCreateActionType {
    DUMMY_REQUEST_CREATE_ACTION_TYPE(DUMMY_REQUEST_TYPE)
    ;

    private final RequestType type;
}
