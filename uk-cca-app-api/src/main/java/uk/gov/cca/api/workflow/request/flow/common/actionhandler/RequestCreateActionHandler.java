package uk.gov.cca.api.workflow.request.flow.common.actionhandler;

import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.workflow.request.core.domain.RequestCreateActionPayload;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestCreateActionType;

public interface RequestCreateActionHandler<T extends RequestCreateActionPayload> {

    @Transactional
    String process(Long accountId, RequestCreateActionType type, T payload, AppUser appUser);

    RequestCreateActionType getType();
}
