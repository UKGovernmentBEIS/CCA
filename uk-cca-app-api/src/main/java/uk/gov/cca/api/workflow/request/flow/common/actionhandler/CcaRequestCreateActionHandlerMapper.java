package uk.gov.cca.api.workflow.request.flow.common.actionhandler;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.workflow.request.core.domain.RequestCreateActionPayload;
import uk.gov.netz.api.workflow.request.core.validation.EnabledWorkflowValidator;

import java.util.List;

@Component
@AllArgsConstructor
public class CcaRequestCreateActionHandlerMapper {

    private final List<CcaRequestCreateActionHandler<? extends RequestCreateActionPayload>> handlers;
    private final EnabledWorkflowValidator enabledWorkflowValidator;

    public CcaRequestCreateActionHandler get(final String requestType) {

        return handlers.stream()
                .filter(handler -> enabledWorkflowValidator.isWorkflowEnabled(requestType))
                .filter(handler -> requestType.equals(handler.getRequestType()))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }
}


