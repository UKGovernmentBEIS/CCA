package uk.gov.cca.api.account.repo;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.cca.api.account.domain.Account;
import uk.gov.cca.api.account.domain.dto.AccountContactInfoDTO;
import uk.gov.cca.api.account.domain.dto.AccountContactVbInfoDTO;
import uk.gov.cca.api.account.domain.enumeration.AccountContactType;
import uk.gov.cca.api.account.repository.AccountRepository;
import uk.gov.netz.api.common.AbstractContainerBaseTest;
import uk.gov.cca.api.common.EmissionTradingScheme;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
public abstract class AbstractAccountRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private AccountRepository repo;

    @Autowired
    private EntityManager entityManager;

    public abstract Account buildAccount(Long id, String accountName, CompetentAuthorityEnum ca, Long verificationBodyId,
                                         EmissionTradingScheme emissionTradingScheme);

    @Test
    void findAccountContactsByAccountIdsAndContactType() {
        final Long vbId = 404L;
        Account account1 = createAccount(1L, "account1", CompetentAuthorityEnum.ENGLAND, vbId, mock(EmissionTradingScheme.class));
        account1.getContacts().put(AccountContactType.CA_SITE, "test1");
        account1.getContacts().put(AccountContactType.PRIMARY, "primary1");
        repo.save(account1);

        Account account2 = createAccount(2L, "account2", CompetentAuthorityEnum.ENGLAND, vbId, mock(EmissionTradingScheme.class));
        account2.getContacts().put(AccountContactType.PRIMARY, "primary2");
        repo.save(account2);

        Account account3 = createAccount(3L, "account3", CompetentAuthorityEnum.ENGLAND, vbId, mock(EmissionTradingScheme.class));
        repo.save(account3);

        List<Long> accountIds = List.of(account1.getId(), account2.getId(), account3.getId());

        flushAndClear();

        //invoke
        List<AccountContactInfoDTO> result = repo.findAccountContactsByAccountIdsAndContactType(accountIds,
            AccountContactType.CA_SITE);

        //verify
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAccountId()).isEqualTo(account1.getId());
    }

    @Test
    void findAccountContactsByVbAndContactType() {
        final Long vbId = 404L;
        Account account1 = createAccount(1L, "account1", CompetentAuthorityEnum.ENGLAND, vbId, mock(EmissionTradingScheme.class));
        account1.getContacts().put(AccountContactType.VB_SITE, "test1");
        repo.save(account1);

        Account account2 = createAccount(2L, "account2", CompetentAuthorityEnum.ENGLAND, vbId, mock(EmissionTradingScheme.class));
        account1.getContacts().put(AccountContactType.VB_SITE, "test2");
        repo.save(account2);

        Account account3 = createAccount(3L, "account3", CompetentAuthorityEnum.ENGLAND, vbId, mock(EmissionTradingScheme.class));
        account1.getContacts().put(AccountContactType.VB_SITE, "test3");
        repo.save(account3);

        Page<AccountContactVbInfoDTO> page = repo.findAccountContactsByVbAndContactType(PageRequest.of(0, 1), vbId, AccountContactType.VB_SITE);
        assertThat(page).hasSize(1);
    }

        @Test
    void findAccountsByContactTypeAndUserId() {
        final Long vbId = 404L;
        Account account1 = createAccount(1L, "account1", CompetentAuthorityEnum.ENGLAND, vbId, mock(EmissionTradingScheme.class));
        account1.getContacts().put(AccountContactType.CA_SITE, "test1");
        account1.getContacts().put(AccountContactType.PRIMARY, "primary1");
        repo.save(account1);

        Account account2 = createAccount(2L, "account2", CompetentAuthorityEnum.ENGLAND, vbId, mock(EmissionTradingScheme.class));
        account2.getContacts().put(AccountContactType.PRIMARY, "test1");
        repo.save(account2);

        Account account3 = createAccount(3L, "account3", CompetentAuthorityEnum.ENGLAND, vbId, mock(EmissionTradingScheme.class));
        repo.save(account3);

        flushAndClear();

        List<Account> result = repo.findAccountsByContactTypeAndUserId(AccountContactType.CA_SITE, "test1");

        //verify
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(account1.getId());
    }

    @Test
    void findAllByVerificationBodyIn() {
        Account account1 = createAccount(1L, "account1", CompetentAuthorityEnum.ENGLAND, 1L, mock(EmissionTradingScheme.class));
        createAccount(2L, "account2", CompetentAuthorityEnum.ENGLAND, 2L, mock(EmissionTradingScheme.class));
        Account account3 = createAccount(3L, "account3", CompetentAuthorityEnum.ENGLAND, 3L,mock(EmissionTradingScheme.class));
        Account account4 = createAccount(4L, "account4", CompetentAuthorityEnum.ENGLAND, 3L, mock(EmissionTradingScheme.class));

        Set<Account> result = repo.findAllByVerificationWithContactsBodyIn(Set.of(1L, 3L));

        assertThat(result).extracting(Account::getId)
            .containsExactlyInAnyOrder(account1.getId(), account3.getId(), account4.getId());
    }

    @Test
    void findAllByVerificationBodyAndEmissionTradingSchemeIn() {
        Long vbId = 1L;
        Long anotherVbId = 2L;
        Account account1 = createAccount(1L, "account1", CompetentAuthorityEnum.ENGLAND, vbId, mock(EmissionTradingScheme.class));
        Account account2 = createAccount(2L, "account2", CompetentAuthorityEnum.ENGLAND, vbId, mock(EmissionTradingScheme.class));
        createAccount(3L, "account3", CompetentAuthorityEnum.ENGLAND, vbId, mock(EmissionTradingScheme.class));
        createAccount(4L, "account4", CompetentAuthorityEnum.ENGLAND, anotherVbId, mock(EmissionTradingScheme.class));

        Set<Account> result = repo.findAllByVerificationBodyAndEmissionTradingSchemeWithContactsIn(vbId,
            Set.of(mock(EmissionTradingScheme.class), mock(EmissionTradingScheme.class)));

        assertThat(result).extracting(Account::getId)
            .containsExactlyInAnyOrder(account1.getId(), account2.getId());
    }

    private Account createAccount(Long id, String accountName, CompetentAuthorityEnum ca, Long vbId,
                                  EmissionTradingScheme emissionTradingScheme) {
        Account account = buildAccount(id, accountName, ca, vbId, emissionTradingScheme);
        entityManager.persist(account);
        return account;
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
