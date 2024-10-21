package uk.gov.cca.api.account.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.gov.cca.api.account.domain.*;
import uk.gov.cca.api.account.domain.dto.TargetUnitAccountInfoDTO;
import uk.gov.cca.api.sectorassociation.domain.Location;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociation;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociationContact;
import uk.gov.netz.api.common.AbstractContainerBaseTest;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
public class TargetUnitAccountRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private TargetUnitAccountRepository repository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    public void setUp() {
        AccountAddress address1 = AccountAddress.builder()
            .id(1L)
            .line1("123 Test Street")
            .city("Test City")
            .postcode("12345")
            .country("Test Country")
            .build();

        AccountAddress address2 = AccountAddress.builder()
            .id(2L)
            .line1("456 Test Avenue")
            .city("Another City")
            .postcode("67890")
            .country("Another Country")
            .build();

        AccountAddress address3 = AccountAddress.builder()
            .id(3L)
            .line1("789 Test Road")
            .city("Third City")
            .postcode("54321")
            .country("Third Country")
            .build();

        TargetUnitAccount account1 = TargetUnitAccount.builder()
            .id(1L)
            .financialIndependenceStatus(FinancialIndependenceStatus.NON_FINANCIALLY_INDEPENDENT)
            .competentAuthority(CompetentAuthorityEnum.ENGLAND)
            .status(TargetUnitAccountStatus.LIVE)
            .emissionTradingScheme(CcaEmissionTradingScheme.DUMMY_EMISSION_TRADING_SCHEME)
            .sectorAssociationId(1L)
            .operatorType(TargetUnitAccountOperatorType.LIMITED_COMPANY)
            .createdBy("user1")
            .creationDate(LocalDateTime.now())
            .address(address1)
            .businessId("businessId1")
            .name("Target Unit Account 1")
            .contacts(Map.of(CcaAccountContactType.TU_SITE_CONTACT, "userId1"))
            .build();
        entityManager.merge(account1);

        TargetUnitAccount account2 = TargetUnitAccount.builder()
            .id(2L)
            .financialIndependenceStatus(FinancialIndependenceStatus.NON_FINANCIALLY_INDEPENDENT)
            .competentAuthority(CompetentAuthorityEnum.ENGLAND)
            .status(TargetUnitAccountStatus.LIVE)
            .emissionTradingScheme(CcaEmissionTradingScheme.DUMMY_EMISSION_TRADING_SCHEME)
            .sectorAssociationId(1L)
            .operatorType(TargetUnitAccountOperatorType.LIMITED_COMPANY)
            .createdBy("user2")
            .creationDate(LocalDateTime.now())
            .address(address2)
            .businessId("businessId2")
            .name("Target Unit Account 2")
            .contacts(Map.of(CcaAccountContactType.TU_SITE_CONTACT, "userId2"))
            .build();
        entityManager.merge(account2);

        TargetUnitAccount account3 = TargetUnitAccount.builder()
            .id(3L)
            .financialIndependenceStatus(FinancialIndependenceStatus.NON_FINANCIALLY_INDEPENDENT)
            .competentAuthority(CompetentAuthorityEnum.ENGLAND)
            .status(TargetUnitAccountStatus.NEW)
            .emissionTradingScheme(CcaEmissionTradingScheme.DUMMY_EMISSION_TRADING_SCHEME)
            .sectorAssociationId(2L)
            .operatorType(TargetUnitAccountOperatorType.LIMITED_COMPANY)
            .createdBy("user3")
            .creationDate(LocalDateTime.now())
            .address(address3)
            .businessId("businessId3")
            .name("Target Unit Account 3")
            .build();
        entityManager.merge(account3);

        flushAndClear();
    }

    @AfterEach
    public void tearDown() {
        repository.deleteAll();
        flushAndClear();
    }

    @Test
    void testFindTargetUnitAccountContactsByCaAndContactTypeAndStatusNotIn() {

        SectorAssociation sectorAssociation = createSectorAssociation(1L);
        entityManager.merge(sectorAssociation);
        
        String contactType = CcaAccountContactType.TU_SITE_CONTACT;
        PageRequest pageable = PageRequest.of(0, 10);

        Page<TargetUnitAccountInfoDTO> result = repository.findTargetUnitAccountsWithSiteContact(pageable, sectorAssociation.getId(), contactType);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotEmpty();
    }

    @Test
    void testFindAllByIdIn() {
        List<Long> ids = List.of(1L, 2L, 3L);

        List<TargetUnitAccount> result = repository.findAllByIdIn(ids);

        assertNotNull(result);
        assertThat(result).hasSize(ids.size());
    }

    @Test
    void testFindAllIdsBySectorAssociationId() {

        SectorAssociation sectorAssociation = createSectorAssociation(1L);
        entityManager.merge(sectorAssociation);

        List<Long> result = repository.findAllIdsBySectorAssociationId(sectorAssociation.getId());

        assertNotNull(result);
        assertThat(result).hasSize(2);
    }

    private SectorAssociation createSectorAssociation(Long id) {
        Location location = Location.builder()
            .postcode("12345")
            .line1("123 Main St")
            .city("Springfield")
            .county("CountyName")
            .build();

        SectorAssociationContact contact = SectorAssociationContact.builder()
            .title("Mr.")
            .firstName("John")
            .lastName("Doe")
            .jobTitle("Director")
            .organisationName("Acme Corp")
            .phoneNumber("123456789")
            .email("john.doe@example.com")
            .location(location)
            .build();

        return SectorAssociation.builder()
            .id(id)
            .competentAuthority(CompetentAuthorityEnum.ENGLAND)
            .legalName("Some Association Legal")
            .name("Some Association")
            .acronym("SA")
            .facilitatorUserId("Facilitator User Id")
            .energyEprFactor("Energy Factor")
            .location(location)
            .sectorAssociationContact(contact)
            .build();
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
