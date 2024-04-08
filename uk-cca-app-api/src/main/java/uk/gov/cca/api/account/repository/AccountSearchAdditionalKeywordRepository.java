package uk.gov.cca.api.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.cca.api.account.domain.AccountSearchAdditionalKeyword;

public interface AccountSearchAdditionalKeywordRepository extends JpaRepository<AccountSearchAdditionalKeyword, Long> {
}
