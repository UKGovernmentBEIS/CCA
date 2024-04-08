package uk.gov.cca.api.workflow.request.core.transform;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.netz.api.common.domain.RoleType;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RequestActionCustomMapperHandler {

    private final List<RequestActionCustomMapper> mappers;

    public Optional<RequestActionCustomMapper> getMapper(final RequestActionType actionType, final RoleType roleType) {
        
        return mappers.stream().filter(m -> m.getRequestActionType().equals(actionType) &&
                                            m.getUserRoleTypes().contains(roleType))
                      .findFirst();
    }
}
