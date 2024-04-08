package uk.gov.cca.api.workflow.request.flow.common.actionhandler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestTaskActionType;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RequestTaskUploadAttachmentActionHandlerMapper {

    private final List<RequestTaskUploadAttachmentActionHandler> requestTaskUploadAttachmentActionHandlers;

    public RequestTaskUploadAttachmentActionHandler get(final RequestTaskActionType requestTaskActionType) {
        return requestTaskUploadAttachmentActionHandlers.stream()
            .filter(handler -> handler.getType().equals(requestTaskActionType))
            .findFirst()
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }
}
