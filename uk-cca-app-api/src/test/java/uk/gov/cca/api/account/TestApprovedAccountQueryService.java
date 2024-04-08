package uk.gov.cca.api.account;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import uk.gov.cca.api.account.domain.Account;
import uk.gov.cca.api.account.domain.dto.AccountContactInfoDTO;
import uk.gov.cca.api.account.service.ApprovedAccountQueryService;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.util.List;

public class TestApprovedAccountQueryService implements ApprovedAccountQueryService {
    @Override
    public List<Long> getAllApprovedAccountIdsByCa(CompetentAuthorityEnum competentAuthority) {
        return List.of(1L,2L);
    }

    @Override
    public Page<AccountContactInfoDTO> getApprovedAccountsAndCaSiteContactsByCa(CompetentAuthorityEnum competentAuthority, Integer page, Integer pageSize) {
        AccountContactInfoDTO info = AccountContactInfoDTO.builder().accountId(1L).accountName("name").userId("user").build();
        List<AccountContactInfoDTO> pageContent = List.of(info);
        PageRequest pageRequest = PageRequest.of(0, 1);
        return new PageImpl<>(pageContent, pageRequest, pageContent.size());

    }

    @Override
    public boolean isAccountApproved(Account account) {
        return true;
    }
}
