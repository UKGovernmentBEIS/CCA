package uk.gov.cca.api.account.repository;

import uk.gov.cca.api.account.domain.Account;

import java.util.Optional;

public interface AccountCustomRepository {
    Optional<Account> findByIdForUpdate(Long id);
}
