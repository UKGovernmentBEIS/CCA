package uk.gov.cca.api.account.service;

import org.springframework.data.domain.Page;
import uk.gov.cca.api.account.domain.Account;
import uk.gov.cca.api.account.domain.dto.AccountContactInfoDTO;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.util.List;

public interface ApprovedAccountQueryService {

    List<Long> getAllApprovedAccountIdsByCa(CompetentAuthorityEnum competentAuthority);

    Page<AccountContactInfoDTO> getApprovedAccountsAndCaSiteContactsByCa(CompetentAuthorityEnum competentAuthority, Integer page, Integer pageSize);

    boolean isAccountApproved(Account account);
}
