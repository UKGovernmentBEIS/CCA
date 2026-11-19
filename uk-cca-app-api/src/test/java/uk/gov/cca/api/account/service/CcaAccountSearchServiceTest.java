package uk.gov.cca.api.account.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;

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

import uk.gov.cca.api.account.domain.CcaAccountContactType;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountInfoDTO;
import uk.gov.cca.api.account.repository.TargetUnitAccountSearchRepository;
import uk.gov.netz.api.account.domain.dto.AccountSearchCriteria;
import uk.gov.netz.api.account.domain.dto.AccountSearchCriteria.SortBy;
import uk.gov.netz.api.common.domain.PagingRequest;

@ExtendWith(MockitoExtension.class)
class CcaAccountSearchServiceTest {

	@InjectMocks
    private CcaAccountSearchService cut;

    @Mock
    private TargetUnitAccountSearchRepository targetUnitAccountSearchRepository;

	@Test
	void searchAccountsWithSiteContact() {
        final Long sectorAssociationId = 1L;
        final String contactType = CcaAccountContactType.TU_SITE_CONTACT;
        final AccountSearchCriteria accountSearchCriteria = AccountSearchCriteria.builder()
                .paging(PagingRequest.builder().pageNumber(0).pageSize(10).build())
                .sortBy(SortBy.ACCOUNT_BUSINESS_ID)
                .direction(Direction.DESC)
                .term("term ").build();

        final PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("businessId").descending());
        final Page<TargetUnitAccountInfoDTO> pageResult = new PageImpl<>(List.of(
                TargetUnitAccountInfoDTO.builder().accountId(1L).build(),
                TargetUnitAccountInfoDTO.builder().accountId(2L).build()
        ));

        when(targetUnitAccountSearchRepository.searchAccountsWithSiteContact(pageRequest, sectorAssociationId, contactType))
                .thenReturn(pageResult);

        // Invoke
        Page<TargetUnitAccountInfoDTO> result = cut.searchAccountsWithSiteContact(sectorAssociationId, contactType, accountSearchCriteria);

        // Verify
        assertThat(result).isEqualTo(pageResult);
        verify(targetUnitAccountSearchRepository, times(1))
                .searchAccountsWithSiteContact(pageRequest, sectorAssociationId, contactType);
	}

    @Test
    void searchAccountsWithSiteContactAndAccountsIds() {
        final Long sectorAssociationId = 1L;
        final Set<Long> accountsIds = Set.of(1L, 2L);
        final String contactType = CcaAccountContactType.TU_SITE_CONTACT;
        final AccountSearchCriteria accountSearchCriteria = AccountSearchCriteria.builder()
                .paging(PagingRequest.builder().pageNumber(0).pageSize(10).build())
                .sortBy(SortBy.ACCOUNT_BUSINESS_ID)
                .direction(Direction.DESC)
                .term("term ").build();

        final PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("businessId").descending());
        final Page<TargetUnitAccountInfoDTO> pageResult = new PageImpl<>(List.of(
                TargetUnitAccountInfoDTO.builder().accountId(1L).build(),
                TargetUnitAccountInfoDTO.builder().accountId(2L).build()
        ));

        when(targetUnitAccountSearchRepository.searchAccountsWithSiteContactAndAccountsIds(pageRequest, sectorAssociationId, accountsIds, contactType))
                .thenReturn(pageResult);

        // Invoke
        Page<TargetUnitAccountInfoDTO> result = cut
                .searchAccountsWithSiteContactAndAccountsIds(sectorAssociationId, accountsIds, contactType, accountSearchCriteria);

        // Verify
        assertThat(result).isEqualTo(pageResult);
        verify(targetUnitAccountSearchRepository, times(1))
                .searchAccountsWithSiteContactAndAccountsIds(pageRequest, sectorAssociationId, accountsIds, contactType);
    }
}
