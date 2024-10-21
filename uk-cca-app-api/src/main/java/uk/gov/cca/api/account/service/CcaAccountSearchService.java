package uk.gov.cca.api.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.account.repository.TargetUnitAccountRepository;
import uk.gov.netz.api.account.domain.dto.AccountSearchCriteria;
import uk.gov.netz.api.account.domain.dto.AccountSearchKeywordResultsDTO;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CcaAccountSearchService {

    private final TargetUnitAccountRepository targetUnitAccountRepository;

    public AccountSearchKeywordResultsDTO searchAccounts(List<Long> sectorAssociationIds, AccountSearchCriteria accountSearchCriteria) {
        final String term = accountSearchCriteria.getTerm() != null ? accountSearchCriteria.getTerm().toLowerCase().trim() : "";
        final Pageable pageable = PageRequest.of(
                accountSearchCriteria.getPaging().getPageNumber().intValue(),
                accountSearchCriteria.getPaging().getPageSize().intValue(),
                Sort.by("accountId"));

        final Page<Long> results = targetUnitAccountRepository.searchDistinctAccountIdsByValue(pageable, sectorAssociationIds, term);
        return AccountSearchKeywordResultsDTO
                .builder()
                .accountIds(results.toList())
                .total(results.getTotalElements())
                .build();
    }
}
