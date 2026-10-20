package uk.gov.cca.api.account.service;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.account.domain.TargetUnitAccount;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountInfoDTO;
import uk.gov.cca.api.account.repository.TargetUnitAccountSearchRepository;
import uk.gov.netz.api.account.domain.dto.AccountSearchCriteria;
import uk.gov.netz.api.account.domain.dto.AccountSearchResults;
import uk.gov.netz.api.account.service.AccountBaseSearchService;

@Service
@RequiredArgsConstructor
public class CcaAccountSearchService extends AccountBaseSearchService<TargetUnitAccount> {

    private final TargetUnitAccountSearchRepository targetUnitAccountSearchRepository;
    
    public AccountSearchResults searchAccounts(List<Long> sectorAssociationIds, AccountSearchCriteria accountSearchCriteria) {
		final Page<TargetUnitAccount> pageResults = targetUnitAccountSearchRepository.searchAccounts(
				getPageRequest(accountSearchCriteria), 
				sectorAssociationIds, 
				getSearchTerm(accountSearchCriteria));
		
		return buildAccountSearchResults(pageResults);
    }
    
	public Page<TargetUnitAccountInfoDTO> searchAccountsWithSiteContact(Long sectorAssociationId, String contactType,
			AccountSearchCriteria accountSearchCriteria) {
		return targetUnitAccountSearchRepository.searchAccountsWithSiteContact(
				getPageRequest(accountSearchCriteria), 
				sectorAssociationId,
				contactType);
    }
    
	public Page<TargetUnitAccountInfoDTO> searchAccountsWithSiteContactAndAccountsIds(Long sectorAssociationId,
			Set<Long> accountsIds, String contactType, AccountSearchCriteria accountSearchCriteria) {
		return targetUnitAccountSearchRepository.searchAccountsWithSiteContactAndAccountsIds(
				getPageRequest(accountSearchCriteria),
				sectorAssociationId, 
				accountsIds, 
				contactType);
    }
    
}