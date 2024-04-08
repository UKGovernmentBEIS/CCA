package uk.gov.cca.api.workflow.request.core.transform;

import uk.gov.cca.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.netz.api.common.domain.RoleType;
import uk.gov.cca.api.workflow.request.core.domain.RequestAction;
import uk.gov.cca.api.workflow.request.core.domain.dto.RequestActionDTO;

import java.util.Set;

public interface RequestActionCustomMapper {

    RequestActionDTO toRequestActionDTO(RequestAction requestAction);

    RequestActionType getRequestActionType();

    Set<RoleType> getUserRoleTypes();
}
