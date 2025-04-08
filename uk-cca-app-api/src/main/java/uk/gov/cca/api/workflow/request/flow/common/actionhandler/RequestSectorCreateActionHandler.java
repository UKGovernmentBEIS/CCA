package uk.gov.cca.api.workflow.request.flow.common.actionhandler;

import org.springframework.transaction.annotation.Transactional;

import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.core.domain.RequestCreateActionPayload;

public interface RequestSectorCreateActionHandler <T extends RequestCreateActionPayload> {

	@Transactional
    String process(Long sectorId, T payload, AppUser appUser);

    String getRequestType();
}
