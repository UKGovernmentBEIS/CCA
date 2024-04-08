package uk.gov.cca.api.workflow.request.core.domain.enumeration;

import lombok.Getter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static uk.gov.cca.api.workflow.request.flow.common.taskhandler.DynamicUserTaskDefinitionKey.CONFIRM_PAYMENT;
import static uk.gov.cca.api.workflow.request.flow.common.taskhandler.DynamicUserTaskDefinitionKey.MAKE_PAYMENT;
import static uk.gov.cca.api.workflow.request.flow.common.taskhandler.DynamicUserTaskDefinitionKey.RDE_RESPONSE_SUBMIT;
import static uk.gov.cca.api.workflow.request.flow.common.taskhandler.DynamicUserTaskDefinitionKey.RFI_RESPONSE_SUBMIT;
import static uk.gov.cca.api.workflow.request.flow.common.taskhandler.DynamicUserTaskDefinitionKey.TRACK_PAYMENT;
import static uk.gov.cca.api.workflow.request.flow.common.taskhandler.DynamicUserTaskDefinitionKey.WAIT_FOR_RDE_RESPONSE;
import static uk.gov.cca.api.workflow.request.flow.common.taskhandler.DynamicUserTaskDefinitionKey.WAIT_FOR_RFI_RESPONSE;

@Getter
public enum RequestTaskType {
    DUMMY_REQUEST_TYPE_APPLICATION_REVIEW(true, RequestType.DUMMY_REQUEST_TYPE) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(RequestTaskActionType.PAYMENT_PAY_BY_CARD, RequestTaskActionType.RDE_FORCE_DECISION);
        }
    },
    DUMMY_REQUEST_TASK_TYPE2(true, RequestType.DUMMY_REQUEST_TYPE)
    ;

    private final boolean assignable;
    private final RequestType requestType;
    private final RequestExpirationType expirationKey;

    private RequestTaskType(boolean assignable, RequestType requestType) {
        this(assignable, requestType, null);
    }

    private RequestTaskType(boolean assignable, RequestType requestType, RequestExpirationType expirationKey) {
        this.assignable = assignable;
        this.requestType = requestType;
        this.expirationKey = expirationKey;
    }

    public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
        return List.of();
    }

    public boolean isExpirable() {
        return expirationKey != null;
    }

    /**
     * term supportingRequestTaskTypes refers to tasks that may be needed in order to complete another task
     * e.g
     * a) peer review task in order to complete review task
     * b) edit task in order to submit task
     */
    public static Set<RequestTaskType> getSupportingRequestTaskTypes() {
        return Set.of(
        );
    }

    public static Set<RequestTaskType> getMakePaymentTypes() {
        return Stream.of(RequestTaskType.values())
                .filter(requestTaskType -> requestTaskType.name().endsWith(MAKE_PAYMENT.name()))
                .collect(Collectors.toSet());
    }

    public static Set<RequestTaskType> getTrackPaymentTypes() {
        return Stream.of(RequestTaskType.values())
                .filter(requestTaskType -> requestTaskType.name().endsWith(TRACK_PAYMENT.name()))
                .collect(Collectors.toSet());
    }

    public static Set<RequestTaskType> getConfirmPaymentTypes() {
        return Stream.of(RequestTaskType.values())
                .filter(requestTaskType -> requestTaskType.name().endsWith(CONFIRM_PAYMENT.name()))
                .collect(Collectors.toSet());
    }

    public static Set<RequestTaskType> getRfiResponseTypes() {
        return Stream.of(RequestTaskType.values())
                .filter(requestTaskType -> requestTaskType.name().endsWith(RFI_RESPONSE_SUBMIT.name()))
                .collect(Collectors.toSet());
    }

    public static Set<RequestTaskType> getRfiWaitForResponseTypes() {
        return Stream.of(RequestTaskType.values())
                .filter(requestTaskType -> requestTaskType.name().endsWith(WAIT_FOR_RFI_RESPONSE.name()))
                .collect(Collectors.toSet());
    }

    public static Set<RequestTaskType> getRdeResponseTypes() {
        return Stream.of(RequestTaskType.values())
                .filter(requestTaskType -> requestTaskType.name().endsWith(RDE_RESPONSE_SUBMIT.name()))
                .collect(Collectors.toSet());
    }

    public static Set<RequestTaskType> getRdeWaitForResponseTypes() {
        return Stream.of(RequestTaskType.values())
                .filter(requestTaskType -> requestTaskType.name().endsWith(WAIT_FOR_RDE_RESPONSE.name()))
                .collect(Collectors.toSet());
    }

    public static Set<RequestTaskType> getRfiRdeWaitForResponseTypes() {
        return Stream.concat(getRfiWaitForResponseTypes().stream(), getRdeWaitForResponseTypes().stream())
                .collect(Collectors.toSet());
    }

    public static Set<RequestTaskType> getWaitForRequestTaskTypes() {
        return Stream.of(RequestTaskType.values())
                .filter(requestTaskType -> requestTaskType.toString().contains("WAIT_FOR"))
                .collect(Collectors.toSet());
    }

    public static Set<RequestTaskType> getTaskTypesRelatedToVerifier() {
        return Set.of();
    }
}
