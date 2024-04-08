package uk.gov.cca.api.workflow.request.flow.common.actionhandler;

import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.workflow.request.core.domain.RequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestTaskActionType;

import java.util.List;

public interface RequestTaskActionHandler<T extends RequestTaskActionPayload> {

    @Transactional
    void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, AppUser appUser, T payload);
    
    List<RequestTaskActionType> getTypes();
}
