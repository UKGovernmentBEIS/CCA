package uk.gov.cca.api.web.orchestrator.sectorassociation.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountInfoResponseDTO;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountSiteContactDTO;
import uk.gov.cca.api.account.service.TargetUnitAccountSiteContactService;
import uk.gov.netz.api.account.domain.dto.AccountSearchCriteria;
import uk.gov.netz.api.authorization.core.domain.AppUser;

@Service
@RequiredArgsConstructor
public class SectorAssociationTargetUnitAccountsServiceOrchestrator {

	private final TargetUnitAccountSiteContactService targetUnitAccountSiteContactService;

	public TargetUnitAccountInfoResponseDTO getTargetUnitAccountsWithSiteContact(AppUser appUser, Long sectorId,
			AccountSearchCriteria accountSearchCriteria) {
		return targetUnitAccountSiteContactService.getTargetUnitAccountsWithSiteContact(appUser, sectorId,
				accountSearchCriteria);
	}

	public void updateTargetUnitAccountSiteContacts(AppUser user, Long sectorId, List<TargetUnitAccountSiteContactDTO> siteContacts) {
		targetUnitAccountSiteContactService.updateTargetUnitAccountSiteContacts(user, sectorId, siteContacts);
	}
}