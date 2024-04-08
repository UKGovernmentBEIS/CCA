package uk.gov.cca.api.mireport.common.outstandingrequesttasks;

import lombok.experimental.UtilityClass;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestTaskType;

import java.util.List;
import java.util.Set;


@UtilityClass
public class RequestTaskTypeFilter {

    public boolean containsExcludedRequestTaskType(RequestTaskType requestTaskType) {
        Set<RequestTaskType> excludedRequestTaskTypes = RequestTaskType.getWaitForRequestTaskTypes();
        excludedRequestTaskTypes.addAll(RequestTaskType.getTrackPaymentTypes());
        //TODO: REMOVE IF NOT NEEDED
        excludedRequestTaskTypes.addAll(List.of());
        return excludedRequestTaskTypes.contains(requestTaskType);
    }
}
