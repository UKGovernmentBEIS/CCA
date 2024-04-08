package uk.gov.cca.api.workflow.request.core.domain.enumeration;

import lombok.Getter;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Request Types.
 */
@Getter
public enum RequestType {
    DUMMY_REQUEST_TYPE("PROCESS_DUMMY_REQUEST_TYPE", "DUMMY", RequestHistoryCategory.PERMIT, true, true);

    /**
     * The id of the bpmn process that will be instantiated for this request type.
     */
    private final String processDefinitionId;

    /**
     * The description of the request type.
     */
    private final String description;

    private final RequestHistoryCategory category;

    private final boolean holdHistory;

    /**
     * Whether request is displayed when in progress status
     */
    private final boolean displayedInProgress;

    RequestType(String processDefinitionId, String description, RequestHistoryCategory category, boolean holdHistory, boolean displayedInProgress) {
        this.processDefinitionId = processDefinitionId;
        this.description = description;
        this.category = category;
        this.holdHistory = holdHistory;
        this.displayedInProgress = displayedInProgress;
    }

    public static Set<RequestType> getCascadableRequestTypes() {
        return Set.of();
    }

    public static Set<RequestType> getNotDisplayedInProgressRequestTypes() {
        return Stream.of(RequestType.values())
                .filter(type -> !type.isDisplayedInProgress())
                .collect(Collectors.toSet());
    }

    public static Set<RequestType> getRequestTypesByCategory(RequestHistoryCategory category) {
        return Stream.of(RequestType.values())
                .filter(type -> type.getCategory() == category)
                .collect(Collectors.toSet());
    }

    public static Set<RequestType> getAvailableForAccountCreateRequestTypes() {
        Set<RequestType> requestTypes = Set.of(DUMMY_REQUEST_TYPE);
        return requestTypes.stream()
                .collect(Collectors.toSet());
    }
}
