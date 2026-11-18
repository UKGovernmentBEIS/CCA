package uk.gov.cca.api.account.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import uk.gov.cca.api.account.domain.TargetUnitAccount;
import uk.gov.cca.api.account.domain.TargetUnitAccountStatus;
import uk.gov.cca.api.account.repository.TargetUnitAccountSearchRepository;
import uk.gov.netz.api.account.domain.dto.AccountSearchCriteria;
import uk.gov.netz.api.account.domain.dto.AccountSearchCriteria.SortBy;
import uk.gov.netz.api.account.domain.dto.AccountSearchResultInfoDTO;
import uk.gov.netz.api.account.domain.dto.AccountSearchResults;
import uk.gov.netz.api.common.domain.PagingRequest;

@ExtendWith(MockitoExtension.class)
class CcaAccountSearchServiceTest {

	@InjectMocks
    private CcaAccountSearchService cut;

    @Mock
    private TargetUnitAccountSearchRepository targetUnitAccountSearchRepository;
	
	@Test
	void searchAccounts() {
		List<Long> sectorAssociationIds = List.of(1L, 2L);
		AccountSearchCriteria accountSearchCriteria = AccountSearchCriteria.builder()
				.paging(PagingRequest.builder().pageNumber(0).pageSize(10).build())
				.sortBy(SortBy.ACCOUNT_BUSINESS_ID)
				.direction(Direction.DESC)
				.term("term ").build();
		
		Page<TargetUnitAccount> pageResult = new PageImpl<>(List.of(
				TargetUnitAccount.builder().id(1L).name("name1").businessId("bus1").status(TargetUnitAccountStatus.TERMINATED).build(),
				TargetUnitAccount.builder().id(2L).name("name2").businessId("bus2").status(TargetUnitAccountStatus.LIVE).build()
				));
		
		PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("businessId").descending());
		
		when(targetUnitAccountSearchRepository.searchAccounts(pageRequest, sectorAssociationIds, "term"))
			.thenReturn(pageResult);
		
		AccountSearchResults result = cut.searchAccounts(sectorAssociationIds, accountSearchCriteria);
		
		assertThat(result).isEqualTo(AccountSearchResults.builder()
				.total(2L)
				.accounts(List.of(
					new AccountSearchResultInfoDTO(1L, "name1", "bus1", TargetUnitAccountStatus.TERMINATED),
	        		new AccountSearchResultInfoDTO(2L, "name2", "bus2", TargetUnitAccountStatus.LIVE)
	        		))
				.build());
		
		verify(targetUnitAccountSearchRepository, times(1)).searchAccounts(pageRequest, sectorAssociationIds, "term");
		
	}
}
