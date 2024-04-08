package uk.gov.cca.api.workflow.request.application.item.service;

import org.springframework.stereotype.Service;
import uk.gov.cca.api.authorization.core.domain.AppUser;
import uk.gov.cca.api.workflow.request.application.item.domain.ItemPage;
import uk.gov.cca.api.workflow.request.application.item.domain.dto.ItemDTOResponse;

@Service
public class TestItemResponseService implements ItemResponseService {

    @Override
    public ItemDTOResponse toItemDTOResponse(ItemPage itemPage, AppUser appUser) {
        return ItemDTOResponse.emptyItemDTOResponse();
    }
}
