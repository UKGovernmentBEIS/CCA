package uk.gov.cca.api.workflow.request.application.item.service;

import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.workflow.request.application.item.domain.ItemPage;
import uk.gov.cca.api.workflow.request.application.item.domain.dto.ItemDTOResponse;

public interface ItemResponseService {

    ItemDTOResponse toItemDTOResponse(ItemPage itemPage, AppUser appUser);
}
