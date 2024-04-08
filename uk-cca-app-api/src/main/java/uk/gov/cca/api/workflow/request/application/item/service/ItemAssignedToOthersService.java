package uk.gov.cca.api.workflow.request.application.item.service;

import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.workflow.request.application.item.domain.dto.ItemDTOResponse;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.netz.api.common.domain.RoleType;

public interface ItemAssignedToOthersService {

    ItemDTOResponse getItemsAssignedToOthers(AppUser appUser, PagingRequest paging);

    RoleType getRoleType();
}
