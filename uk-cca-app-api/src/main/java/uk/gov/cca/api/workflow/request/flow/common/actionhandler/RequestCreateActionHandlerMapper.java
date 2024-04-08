package uk.gov.cca.api.workflow.request.flow.common.actionhandler;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.workflow.request.core.validation.EnabledWorkflowValidator;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.cca.api.workflow.request.core.domain.RequestCreateActionPayload;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestCreateActionType;

import java.util.List;

/**
 * The RequestCreateActionHandlerMapper for all request create actions.
 */
@Component
@AllArgsConstructor
public class RequestCreateActionHandlerMapper {

    private final List<RequestCreateActionHandler<? extends RequestCreateActionPayload>> handlers;
    private final EnabledWorkflowValidator enabledWorkflowValidator;

    public RequestCreateActionHandler get(final RequestCreateActionType requestCreateActionType) {

        return handlers.stream()
            .filter(handler -> enabledWorkflowValidator.isWorkflowEnabled(requestCreateActionType.getType()))
            .filter(handler -> requestCreateActionType.equals(handler.getType()))
            .findFirst()
            .orElseThrow(() -> {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
            });
    }
}

