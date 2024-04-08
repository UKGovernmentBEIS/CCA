package uk.gov.cca.api.workflow.request.core.service;

import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.cca.api.workflow.request.core.domain.Request;
import uk.gov.cca.api.workflow.request.core.domain.RequestTaskPayload;

import java.util.Set;

public interface InitializeRequestTaskHandler {

    RequestTaskPayload initializePayload(Request request);

    Set<RequestTaskType> getRequestTaskTypes();
}
