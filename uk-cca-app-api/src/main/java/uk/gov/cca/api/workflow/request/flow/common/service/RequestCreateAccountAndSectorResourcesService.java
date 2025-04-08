package uk.gov.cca.api.workflow.request.flow.common.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.account.service.TargetUnitAccountQueryService;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;

@Service
@RequiredArgsConstructor
public class RequestCreateAccountAndSectorResourcesService {

	private final TargetUnitAccountQueryService targetUnitAccountQueryService;
	
	public Map<String, String> createRequestResources(Long accountId) {
		Long sectorAssociationId = targetUnitAccountQueryService.getAccountSectorAssociationId(accountId);
		return Map.of(
				ResourceType.ACCOUNT, accountId.toString(), 
				CcaResourceType.SECTOR_ASSOCIATION, sectorAssociationId.toString()
				);
	}
}
