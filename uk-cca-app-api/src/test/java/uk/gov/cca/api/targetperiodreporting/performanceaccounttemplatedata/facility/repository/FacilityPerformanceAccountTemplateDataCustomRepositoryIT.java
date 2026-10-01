package uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Testcontainers;

import uk.gov.cca.api.account.domain.AccountAddress;
import uk.gov.cca.api.account.domain.CcaAccountContactType;
import uk.gov.cca.api.account.domain.CcaEmissionTradingScheme;
import uk.gov.cca.api.account.domain.FinancialIndependenceStatus;
import uk.gov.cca.api.account.domain.TargetUnitAccount;
import uk.gov.cca.api.account.domain.TargetUnitAccountOperatorType;
import uk.gov.cca.api.account.domain.TargetUnitAccountStatus;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.facility.domain.FacilityAddress;
import uk.gov.cca.api.facility.domain.FacilityData;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.common.domain.ActionCategoryType;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.common.domain.PerformanceAccountTemplateDataStatus;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.common.domain.dto.SectorPerformanceAccountTemplateDataReportItemDTO;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.common.domain.dto.SectorPerformanceAccountTemplateDataReportListDTO;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.common.domain.dto.SectorPerformanceAccountTemplateDataReportSearchCriteria;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.domain.FacilityPerformanceAccountTemplateDataContainer;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.domain.FacilityPerformanceAccountTemplateDataEntity;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.domain.FacilityPerformanceAccountTemplateSavingAction;
import uk.gov.netz.api.common.AbstractContainerBaseTest;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@EnableAutoConfiguration
@EnableJpaAuditing
@ContextConfiguration(classes = FacilityPerformanceAccountTemplateDataCustomRepository.class)
@DataJpaTest
@Import(ObjectMapper.class)
class FacilityPerformanceAccountTemplateDataCustomRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private FacilityPerformanceAccountTemplateDataCustomRepository cut;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        TargetUnitAccount account1 = createAccount(-1L, 11L);
        TargetUnitAccount account2 = createAccount(-2L, 11L);
        TargetUnitAccount account3 = createAccount(-3L, 22L);

        FacilityData facility1 = createFacilityData(-1L, account1.getId(), SchemeVersion.CCA_3, LocalDate.of(2026, 1, 1), null);
        createFacilityPerformanceAccountTemplateData(facility1.getId(), Year.of(2026));
        FacilityData facility2 = createFacilityData(-2L, account1.getId(), SchemeVersion.CCA_3, LocalDate.of(2026, 1, 1), null);
        createFacilityPerformanceAccountTemplateData(facility2.getId(), Year.of(2024));
        createFacilityData(-3L, account1.getId(), SchemeVersion.CCA_3, LocalDate.of(2026, 1, 1), null);
        createFacilityData(-4L, account1.getId(), SchemeVersion.CCA_3, LocalDate.of(2027, 1, 1), null);
        createFacilityData(-5L, account1.getId(), SchemeVersion.CCA_3, LocalDate.of(2026, 1, 1),  LocalDate.of(2027, 2, 1));
        createFacilityData(-6L, account1.getId(), SchemeVersion.CCA_3, LocalDate.of(2026, 1, 1), LocalDate.of(2027, 1, 1));

        FacilityData facility3 = createFacilityData(-7L, account2.getId(), SchemeVersion.CCA_3, LocalDate.of(2026, 1, 1), null);
        createFacilityPerformanceAccountTemplateData(facility3.getId(), Year.of(2026));
        FacilityData facility4 = createFacilityData(-8L, account2.getId(), SchemeVersion.CCA_2, LocalDate.of(2026, 1, 1), null);
        createFacilityPerformanceAccountTemplateData(facility4.getId(), Year.of(2026));
        createFacilityData(-9L, account1.getId(), SchemeVersion.CCA_3, LocalDate.of(2026, 1, 1), null);

        FacilityData facility5 = createFacilityData(-10L, account3.getId(), SchemeVersion.CCA_3, LocalDate.of(2026, 1, 1), null);
        createFacilityPerformanceAccountTemplateData(facility5.getId(), Year.of(2026));

        flushAndClear();
    }

    @Test
    void getSectorFacilityPerformanceDataReportListBySearchCriteria() {
        final Long sectorAssociationId = 11L;
        SectorPerformanceAccountTemplateDataReportSearchCriteria criteria = SectorPerformanceAccountTemplateDataReportSearchCriteria.builder()
                .targetPeriodYear(Year.of(2026))
                .paging(PagingRequest.builder().pageNumber(0).pageSize(25).build())
                .build();

        // Invoke
        SectorPerformanceAccountTemplateDataReportListDTO results = cut
                .getSectorFacilityPerformanceDataReportListBySearchCriteria(sectorAssociationId, criteria);

        // Verify
        assertThat(results.getTotal()).isEqualTo(6);
        assertThat(results.getItems()).extracting(SectorPerformanceAccountTemplateDataReportItemDTO::getBusinessId)
                .containsExactlyInAnyOrder("facility_businessId_-1", "facility_businessId_-7",
                        "facility_businessId_-2", "facility_businessId_-3", "facility_businessId_-5", "facility_businessId_-9");
    }

    @Test
    void getSectorFacilityPerformanceDataReportListBySearchCriteria_SUBMITTED() {
        final Long sectorAssociationId = 11L;
        SectorPerformanceAccountTemplateDataReportSearchCriteria criteria = SectorPerformanceAccountTemplateDataReportSearchCriteria.builder()
                .targetPeriodYear(Year.of(2026))
                .status(PerformanceAccountTemplateDataStatus.SUBMITTED)
                .paging(PagingRequest.builder().pageNumber(0).pageSize(25).build())
                .build();

        // Invoke
        SectorPerformanceAccountTemplateDataReportListDTO results = cut
                .getSectorFacilityPerformanceDataReportListBySearchCriteria(sectorAssociationId, criteria);

        // Verify
        assertThat(results.getTotal()).isEqualTo(2);
        assertThat(results.getItems()).extracting(SectorPerformanceAccountTemplateDataReportItemDTO::getBusinessId)
                .containsExactlyInAnyOrder("facility_businessId_-1", "facility_businessId_-7");
    }

    @Test
    void getSectorFacilityPerformanceDataReportListBySearchCriteria_with_terms() {
        final Long sectorAssociationId = 11L;
        SectorPerformanceAccountTemplateDataReportSearchCriteria criteria = SectorPerformanceAccountTemplateDataReportSearchCriteria.builder()
                .term("facility_businessId_-1")
                .targetPeriodYear(Year.of(2026))
                .paging(PagingRequest.builder().pageNumber(0).pageSize(25).build())
                .build();

        // Invoke
        SectorPerformanceAccountTemplateDataReportListDTO results = cut
                .getSectorFacilityPerformanceDataReportListBySearchCriteria(sectorAssociationId, criteria);

        // Verify
        assertThat(results.getTotal()).isEqualTo(1);
        assertThat(results.getItems()).extracting(SectorPerformanceAccountTemplateDataReportItemDTO::getBusinessId)
                .containsExactly("facility_businessId_-1");
    }

    @Test
    void getSectorFacilityPerformanceDataReportListBySearchCriteria_OUTSTANDING() {
        final Long sectorAssociationId = 11L;
        SectorPerformanceAccountTemplateDataReportSearchCriteria criteria = SectorPerformanceAccountTemplateDataReportSearchCriteria.builder()
                .targetPeriodYear(Year.of(2026))
                .status(PerformanceAccountTemplateDataStatus.OUTSTANDING)
                .paging(PagingRequest.builder().pageNumber(0).pageSize(25).build())
                .build();

        // Invoke
        SectorPerformanceAccountTemplateDataReportListDTO results = cut
                .getSectorFacilityPerformanceDataReportListBySearchCriteria(sectorAssociationId, criteria);

        // Verify
        assertThat(results.getTotal()).isEqualTo(4);
        assertThat(results.getItems()).extracting(SectorPerformanceAccountTemplateDataReportItemDTO::getBusinessId)
                .containsExactlyInAnyOrder("facility_businessId_-2", "facility_businessId_-3",
                        "facility_businessId_-5", "facility_businessId_-9");
    }

    private TargetUnitAccount createAccount(Long id, Long sectorId) {
        AccountAddress address = AccountAddress.builder()
                .line1("123 Test Street")
                .city("Test City")
                .postcode("12345")
                .country("Test Country")
                .build();
        entityManager.persist(address);

        TargetUnitAccount account = TargetUnitAccount.builder()
                .id(id)
                .businessId("businessId" + id)
                .name("name" + id)
                .sectorAssociationId(sectorId)
                .status(TargetUnitAccountStatus.LIVE)
                .acceptedDate(LocalDate.of(2023, 11, 3).atStartOfDay())
                .financialIndependenceStatus(FinancialIndependenceStatus.NON_FINANCIALLY_INDEPENDENT)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .emissionTradingScheme(CcaEmissionTradingScheme.DUMMY_EMISSION_TRADING_SCHEME)
                .operatorType(TargetUnitAccountOperatorType.LIMITED_COMPANY)
                .createdBy("user1")
                .address(address)
                .contacts(Map.of(CcaAccountContactType.TU_SITE_CONTACT, "userId1"))
                .build();
        entityManager.persist(account);

        return account;
    }

    private FacilityData createFacilityData(Long id, Long accountId, SchemeVersion schemeVersion, LocalDate createdDate, LocalDate closedDate) {
        FacilityAddress address = FacilityAddress.builder()
                .line1("123 Test Street")
                .city("Test City")
                .postcode("12345")
                .country("Test Country")
                .build();
        entityManager.persist(address);

        FacilityData facility = FacilityData.builder()
                .facilityBusinessId("facility_businessId_" + id)
                .accountId(accountId)
                .participatingSchemeVersions(Set.of(schemeVersion))
                .siteName("facilityName" + id)
                .address(address)
                .createdDate(createdDate.atStartOfDay())
                .closedDate(Optional.ofNullable(closedDate).map(LocalDate::atStartOfDay).orElse(null))
                .build();

        entityManager.persist(facility);

        return facility;
    }

    private void createFacilityPerformanceAccountTemplateData(Long facilityId, Year targetPeriodYear) {
        FacilityPerformanceAccountTemplateDataContainer container = FacilityPerformanceAccountTemplateDataContainer.builder()
                .savingActions(List.of(FacilityPerformanceAccountTemplateSavingAction.builder()
                        .actionCategoryType(ActionCategoryType.NO_ACTION)
                        .notes("Notes")
                        .build()))
                .build();

        FacilityPerformanceAccountTemplateDataEntity entity = FacilityPerformanceAccountTemplateDataEntity.builder()
                .data(container)
                .facilityId(facilityId)
                .targetPeriodYear(targetPeriodYear)
                .reportVersion(1)
                .build();
        entityManager.persist(entity);
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
