package uk.gov.cca.api.subsistencefees.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.cca.api.account.domain.AccountAddress;
import uk.gov.cca.api.account.domain.CcaEmissionTradingScheme;
import uk.gov.cca.api.account.domain.FinancialIndependenceStatus;
import uk.gov.cca.api.account.domain.TargetUnitAccount;
import uk.gov.cca.api.account.domain.TargetUnitAccountOperatorType;
import uk.gov.cca.api.account.domain.TargetUnitAccountStatus;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.facility.domain.FacilityAddress;
import uk.gov.cca.api.facility.domain.FacilityData;
import uk.gov.cca.api.subsistencefees.domain.FacilityProcessStatus;
import uk.gov.cca.api.subsistencefees.domain.MoaType;
import uk.gov.cca.api.subsistencefees.domain.dto.EligibleFacilityDTO;
import uk.gov.netz.api.common.AbstractContainerBaseTest;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class FacilityProcessStatusRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private FacilityProcessStatusRepository repository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        // set up addresses
        FacilityAddress address1 = getFacilityAddress();
        entityManager.persist(address1);

        FacilityAddress address2 = getFacilityAddress();
        entityManager.persist(address2);

        FacilityAddress address3 = getFacilityAddress();
        entityManager.persist(address3);

        FacilityAddress address4 = getFacilityAddress();
        entityManager.persist(address4);

        FacilityAddress address5 = getFacilityAddress();
        entityManager.persist(address5);

        FacilityAddress address6 = getFacilityAddress();
        entityManager.persist(address6);

        FacilityAddress address7 = getFacilityAddress();
        entityManager.persist(address7);

        FacilityAddress address8 = getFacilityAddress();
        entityManager.persist(address8);

        FacilityAddress address9 = getFacilityAddress();
        entityManager.persist(address9);

        FacilityAddress address10 = getFacilityAddress();
        entityManager.persist(address10);

        FacilityAddress address11 = getFacilityAddress();
        entityManager.persist(address11);


        FacilityAddress address12 = getFacilityAddress();
        entityManager.persist(address12);

        // set up TargetUnitAccount
        TargetUnitAccount nonEligibleIndependentAccount = TargetUnitAccount.builder()
                .id(1L)
                .financialIndependenceStatus(FinancialIndependenceStatus.FINANCIALLY_INDEPENDENT)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .status(TargetUnitAccountStatus.LIVE)
                .emissionTradingScheme(CcaEmissionTradingScheme.DUMMY_EMISSION_TRADING_SCHEME)
                .sectorAssociationId(3L)
                .operatorType(TargetUnitAccountOperatorType.LIMITED_COMPANY)
                .createdBy("user3")
                .creationDate(LocalDateTime.now())
                .address(AccountAddress.builder()
                        .line1("123 Test Street")
                        .city("Test City")
                        .postcode("12345")
                        .country("Test Country")
                        .build())
                .businessId("businessId1")
                .name("Target Unit Account 1")
                .build();
        entityManager.persist(nonEligibleIndependentAccount);

        TargetUnitAccount eligibleNonIndependentAccount = TargetUnitAccount.builder()
                .id(2L)
                .financialIndependenceStatus(FinancialIndependenceStatus.NON_FINANCIALLY_INDEPENDENT)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .status(TargetUnitAccountStatus.LIVE)
                .emissionTradingScheme(CcaEmissionTradingScheme.DUMMY_EMISSION_TRADING_SCHEME)
                .sectorAssociationId(2L)
                .operatorType(TargetUnitAccountOperatorType.LIMITED_COMPANY)
                .createdBy("user3")
                .creationDate(LocalDateTime.now())
                .address(AccountAddress.builder()
                        .line1("123 Test Street")
                        .city("Test City")
                        .postcode("12345")
                        .country("Test Country")
                        .build())
                .businessId("businessId2")
                .name("Target Unit Account 2")
                .build();
        entityManager.persist(eligibleNonIndependentAccount);

        TargetUnitAccount eligibleIndependentAccount = TargetUnitAccount.builder()
                .id(3L)
                .financialIndependenceStatus(FinancialIndependenceStatus.FINANCIALLY_INDEPENDENT)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .status(TargetUnitAccountStatus.LIVE)
                .emissionTradingScheme(CcaEmissionTradingScheme.DUMMY_EMISSION_TRADING_SCHEME)
                .sectorAssociationId(3L)
                .operatorType(TargetUnitAccountOperatorType.LIMITED_COMPANY)
                .createdBy("user3")
                .creationDate(LocalDateTime.now())
                .address(AccountAddress.builder()
                        .line1("123 Test Street")
                        .city("Test City")
                        .postcode("12345")
                        .country("Test Country")
                        .build())
                .businessId("businessId3")
                .name("Target Unit Account 1")
                .build();
        entityManager.persist(eligibleIndependentAccount);

        // set up facilities
        final FacilityData eligibleAccountFacility = FacilityData.builder()
                .facilityBusinessId("ADS_1-F00014") // already charged
                .accountId(nonEligibleIndependentAccount.getId())
                .participatingSchemeVersions(Set.of(SchemeVersion.CCA_2))
                .siteName("site1")
                .schemeExitDate(null)
                .chargeStartDate(LocalDate.of(2025, 1, 1))
                .address(address1)
                .createdDate(LocalDateTime.of(2024, 12, 29, 12, 0))
                .build();
        entityManager.persist(eligibleAccountFacility);

        final FacilityData eligibleSectorFacility1 = FacilityData.builder()
                .facilityBusinessId("ADS_1-F00015")
                .accountId(eligibleNonIndependentAccount.getId())
                .participatingSchemeVersions(Set.of(SchemeVersion.CCA_2))
                .siteName("facil2")
                .address(address2)
                .schemeExitDate(null)
                .chargeStartDate(LocalDate.of(2024, 12, 29))
                .createdDate(LocalDateTime.of(2024, 12, 29, 12, 0))
                .build();
        entityManager.persist(eligibleSectorFacility1);

        final FacilityData eligibleSectorFacility2 = FacilityData.builder()
                .facilityBusinessId("ADS_1-F00025")
                .accountId(eligibleNonIndependentAccount.getId())
                .participatingSchemeVersions(Set.of(SchemeVersion.CCA_2))
                .siteName("facil25")
                .address(address6)
                .schemeExitDate(null)
                .chargeStartDate(null)
                .createdDate(LocalDateTime.of(2024, 12, 29, 12, 0))
                .build();
        entityManager.persist(eligibleSectorFacility2);

        final FacilityData eligibleSectorFacility3 = FacilityData.builder()
                .facilityBusinessId("ADS_1-F00035")
                .accountId(eligibleNonIndependentAccount.getId())
                .participatingSchemeVersions(Set.of(SchemeVersion.CCA_2))
                .siteName("facil35")
                .address(address7)
                .schemeExitDate(null)
                .chargeStartDate(LocalDate.of(2025, 4, 30))
                .createdDate(LocalDateTime.of(2024, 12, 29, 12, 0))
                .build();
        entityManager.persist(eligibleSectorFacility3);

        final FacilityData nonEligibleSectorFacility1 = FacilityData.builder()
                .facilityBusinessId("ADS_1-F00045")
                .accountId(eligibleNonIndependentAccount.getId())
                .participatingSchemeVersions(Set.of(SchemeVersion.CCA_2))
                .siteName("facil45")
                .address(address8)
                .schemeExitDate(null)
                .chargeStartDate(LocalDate.of(2026, 4, 30))
                .createdDate(LocalDateTime.of(2024, 12, 29, 12, 0))
                .build();
        entityManager.persist(nonEligibleSectorFacility1);

        final FacilityData nonEligibleSectorFacility2 = FacilityData.builder()
                .facilityBusinessId("ADS_1-F00055")
                .accountId(eligibleNonIndependentAccount.getId())
                .participatingSchemeVersions(Set.of(SchemeVersion.CCA_2))
                .siteName("facil55")
                .address(address9)
                .schemeExitDate(null)
                .chargeStartDate(null)
                .createdDate(LocalDateTime.of(2026, 1, 1, 12, 0))
                .build();
        entityManager.persist(nonEligibleSectorFacility2);

        final FacilityData nonEligibleAccountFacility1 = FacilityData.builder()
                .facilityBusinessId("ADS_1-F00016")
                .accountId(nonEligibleIndependentAccount.getId())
                .participatingSchemeVersions(Set.of(SchemeVersion.CCA_2))
                .siteName("terminal3")
                .address(address3)
                .chargeStartDate(LocalDate.of(2024, 12, 1))
                .closedDate(LocalDateTime.of(2024, 8, 1, 12, 0))
                .schemeExitDate(LocalDate.of(2024, 8, 1))
                .createdDate(LocalDateTime.of(2024, 7, 1, 12, 0))
                .build();
        entityManager.persist(nonEligibleAccountFacility1);

        final FacilityData nonEligibleAccountFacility2 = FacilityData.builder()
                .facilityBusinessId("ADS_1-F00017")
                .accountId(nonEligibleIndependentAccount.getId())
                .participatingSchemeVersions(Set.of(SchemeVersion.CCA_2))
                .siteName("terminal3")
                .address(address4)
                .chargeStartDate(LocalDate.of(2025, 1, 1))
                .schemeExitDate(LocalDate.of(2024, 8, 1))
                .closedDate(LocalDateTime.of(2024, 8, 1, 12, 0))
                .createdDate(LocalDateTime.of(2024, 7, 1, 12, 0))
                .build();
        entityManager.persist(nonEligibleAccountFacility2);

        final FacilityData eligibleAccountFacilityForEligbleAccount1 = FacilityData.builder()
                .facilityBusinessId("ADS_1-F00018")
                .accountId(eligibleIndependentAccount.getId())
                .participatingSchemeVersions(Set.of(SchemeVersion.CCA_2))
                .siteName("facil5")
                .address(address5)
                .schemeExitDate(null)
                .chargeStartDate(LocalDate.of(2025, 1, 1))
                .createdDate(LocalDateTime.of(2024, 12, 29, 12, 0))
                .build();
        entityManager.persist(eligibleAccountFacilityForEligbleAccount1);

        final FacilityData eligibleAccountFacilityForEligibleAccount2 = FacilityData.builder()
                .facilityBusinessId("ADS_1-F00028")
                .accountId(eligibleIndependentAccount.getId())
                .participatingSchemeVersions(Set.of(SchemeVersion.CCA_2))
                .siteName("facil28")
                .address(address10)
                .schemeExitDate(null)
                .chargeStartDate(null)
                .createdDate(LocalDateTime.of(2024, 12, 29, 12, 0))
                .build();
        entityManager.persist(eligibleAccountFacilityForEligibleAccount2);

        final FacilityData nonEligibleAccountFacilityForEligibleAccount1 = FacilityData.builder()
                .facilityBusinessId("ADS_1-F00038")
                .accountId(eligibleIndependentAccount.getId())
                .participatingSchemeVersions(Set.of(SchemeVersion.CCA_2))
                .siteName("facil38")
                .address(address11)
                .schemeExitDate(null)
                .chargeStartDate(null)
                .createdDate(LocalDateTime.of(2026, 12, 29, 12, 0))
                .build();
        entityManager.persist(nonEligibleAccountFacilityForEligibleAccount1);

        final FacilityData nonEligibleAccountFacilityForEligibleAccount2 = FacilityData.builder()
                .facilityBusinessId("ADS_1-F00048")
                .accountId(eligibleIndependentAccount.getId())
                .participatingSchemeVersions(Set.of(SchemeVersion.CCA_2))
                .siteName("facil48")
                .address(address12)
                .schemeExitDate(null)
                .chargeStartDate(LocalDate.of(2026, 1, 1))
                .createdDate(LocalDateTime.of(2024, 12, 29, 12, 0))
                .build();
        entityManager.persist(nonEligibleAccountFacilityForEligibleAccount2);

        // set up FacilityProcessStatus - add facility "ADS_1-F00014"
        FacilityProcessStatus facilityProcessStatus = FacilityProcessStatus.builder()
                .facilityId(1L)
                .runId(1L)
                .moaType(MoaType.TARGET_UNIT_MOA)
                .chargingYear(Year.of(2025))
                .build();
        entityManager.persist(facilityProcessStatus);

        flushAndClear();
    }

    @Test
    @Order(1)
    void findTargetUnitAccountsForSubsistenceFeesRun() {
        final long eligibleAccountId = 3L;
        final Year chargingYear = Year.of(2025);
        final LocalDate firstDateOfChargingYear = LocalDate.of(chargingYear.getValue(), 1, 1);
        final LocalDate endDateOfChargingYear = LocalDate.of(chargingYear.getValue(), 12, 31);

        Set<Long> accountIds =
                repository.findTargetUnitAccountsForSubsistenceFeesRun(chargingYear, firstDateOfChargingYear, endDateOfChargingYear);

        assertThat(accountIds).hasSize(1).contains(eligibleAccountId);
    }

    @Test
    void findSectorFacilitiesForSubsistenceFeesRun_with_true_result() {
        final long sectorAssociationId = 2L;
        final Year chargingYear = Year.of(2025);
        final LocalDate firstDateOfChargingYear = LocalDate.of(chargingYear.getValue(), 1, 1);
        final LocalDate endDateOfChargingYear = LocalDate.of(chargingYear.getValue(), 12, 31);

        List<EligibleFacilityDTO> eligibleFacilitiesForSubsistenceFeesRun =
                repository.findSectorFacilitiesForSubsistenceFeesRun(sectorAssociationId, chargingYear, firstDateOfChargingYear, endDateOfChargingYear);

        assertThat(eligibleFacilitiesForSubsistenceFeesRun).isNotEmpty().hasSize(3);
    }

    @Test
    void findSectorFacilitiesForSubsistenceFeesRun_with_empty_result() {
        final long sectorAssociationId = 3L;
        final Year chargingYear = Year.of(2025);
        final LocalDate firstDateOfChargingYear = LocalDate.of(chargingYear.getValue(), 1, 1);
        final LocalDate endDateOfChargingYear = LocalDate.of(chargingYear.getValue(), 12, 31);

        List<EligibleFacilityDTO> eligibleFacilitiesForSubsistenceFeesRun =
                repository.findSectorFacilitiesForSubsistenceFeesRun(sectorAssociationId, chargingYear, firstDateOfChargingYear, endDateOfChargingYear);

        assertThat(eligibleFacilitiesForSubsistenceFeesRun).isEmpty();
    }

    @Test
    void findAccountFacilitiesForSubsistenceFeesRun() {
        final long accountId = 3L;
        final Year chargingYear = Year.of(2025);
        final LocalDate firstDateOfChargingYear = LocalDate.of(chargingYear.getValue(), 1, 1);
        final LocalDate endDateOfChargingYear = LocalDate.of(chargingYear.getValue(), 12, 31);

        List<EligibleFacilityDTO> facilities =
                repository.findAccountFacilitiesForSubsistenceFeesRun(accountId, chargingYear, firstDateOfChargingYear, endDateOfChargingYear);

        assertThat(facilities).hasSize(2);
        assertThat(facilities.getFirst().getFacilityBusinessId()).isEqualTo("ADS_1-F00018");
    }

    @Test
    void findAllByRunIdAndFacilityIdIn() {
        List<FacilityProcessStatus> result = repository.findAllByRunIdAndFacilityIdIn(1L, Collections.singleton(1L));
        assertThat(result).hasSize(1);
    }

    @AfterEach
    void tearDown() {
        repository.deleteAll();
        flushAndClear();
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }

    private FacilityAddress getFacilityAddress() {
        return FacilityAddress.builder()
                .line1("123 Test Street")
                .city("Test City")
                .postcode("12345")
                .country("Test Country")
                .build();
    }
}