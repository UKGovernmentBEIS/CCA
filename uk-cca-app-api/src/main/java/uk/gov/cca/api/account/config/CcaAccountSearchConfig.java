package uk.gov.cca.api.account.config;

import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import uk.gov.cca.api.account.domain.TargetUnitAccount;
import uk.gov.cca.api.account.domain.dto.CcaAccountSearchResultInfoDTO;
import uk.gov.cca.api.account.repository.CcaAccountSearchEntityPaths;
import uk.gov.cca.api.account.repository.CcaAccountSearchProjectionMapper;
import uk.gov.cca.api.account.repository.CcaAccountSearchResultRowMapper;
import uk.gov.netz.api.account.search.query.AccountSearchQueryRepository;
import uk.gov.netz.api.account.search.query.AccountSearchQueryRepositoryImpl;
import uk.gov.netz.api.account.search.query.AccountSearchSortMapper;

@Configuration(proxyBeanMethods = false)
public class CcaAccountSearchConfig {

    @Bean
    CcaAccountSearchEntityPaths ccaAccountSearchEntityPaths() {
        return new CcaAccountSearchEntityPaths();
    }

    @Bean
    AccountSearchSortMapper<TargetUnitAccount> ccaAccountSearchSortMapper() {
        return new AccountSearchSortMapper<>();
    }

    @Bean
    CcaAccountSearchProjectionMapper ccaAccountSearchProjectionMapper() {
        return new CcaAccountSearchProjectionMapper();
    }

    @Bean
    CcaAccountSearchResultRowMapper ccaAccountSearchResultRowMapper() {
        return new CcaAccountSearchResultRowMapper();
    }

    @Bean
    AccountSearchQueryRepository<TargetUnitAccount, CcaAccountSearchResultInfoDTO> ccaAccountSearchQueryRepository(
            EntityManager entityManager,
            AccountSearchSortMapper<TargetUnitAccount> ccaAccountSearchSortMapper,
            CcaAccountSearchProjectionMapper ccaAccountSearchProjectionMapper,
            CcaAccountSearchResultRowMapper ccaAccountSearchResultRowMapper) {
        return new AccountSearchQueryRepositoryImpl<>(
                entityManager,
                ccaAccountSearchSortMapper,
                ccaAccountSearchProjectionMapper,
                ccaAccountSearchResultRowMapper);
    }
}
