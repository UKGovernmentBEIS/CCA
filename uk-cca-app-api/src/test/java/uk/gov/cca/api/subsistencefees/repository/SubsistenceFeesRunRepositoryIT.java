package uk.gov.cca.api.subsistencefees.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.cca.api.subsistencefees.domain.FacilityPaymentStatus;
import uk.gov.cca.api.subsistencefees.domain.MoaType;
import uk.gov.cca.api.subsistencefees.domain.SubsistenceFeesMoa;
import uk.gov.cca.api.subsistencefees.domain.SubsistenceFeesMoaFacility;
import uk.gov.cca.api.subsistencefees.domain.SubsistenceFeesMoaTargetUnit;
import uk.gov.cca.api.subsistencefees.domain.SubsistenceFeesRun;
import uk.gov.cca.api.subsistencefees.domain.dto.transform.SubsistenceFeesRunDetailsInfo;
import uk.gov.cca.api.subsistencefees.domain.dto.transform.SubsistenceFeesRunMoaDetailsInfo;
import uk.gov.cca.api.subsistencefees.domain.dto.transform.SubsistenceFeesRunSearchResultInfo;
import uk.gov.netz.api.common.AbstractContainerBaseTest;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class SubsistenceFeesRunRepositoryIT extends AbstractContainerBaseTest {

	@Autowired
    private SubsistenceFeesRunRepository repository;

    @Autowired
    private EntityManager entityManager;

    private final LocalDateTime submissionDate = LocalDateTime.of(2025, 4, 1, 12, 22, 44);

    @BeforeEach
    void setUp() {
        Year chargingYear = Year.of(2025);
        // Set up subsistence fees runs
        SubsistenceFeesRun run1 = SubsistenceFeesRun.builder()
                .businessId("S2501")
                .chargingYear(chargingYear)
                .initialTotalAmount(BigDecimal.valueOf(1000L))
                .submissionDate(submissionDate)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .build();
        entityManager.persist(run1);

        SubsistenceFeesRun run2 = SubsistenceFeesRun.builder()
                .businessId("S2502")
                .chargingYear(chargingYear)
                .initialTotalAmount(BigDecimal.valueOf(500L))
                .submissionDate(submissionDate.plusDays(1))
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .build();
        entityManager.persist(run2);

        SubsistenceFeesRun run3 = SubsistenceFeesRun.builder()
                .businessId("S2503")
                .chargingYear(chargingYear)
                .initialTotalAmount(BigDecimal.valueOf(1500L))
                .submissionDate(submissionDate.plusDays(2))
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .build();
        entityManager.persist(run3);

        // Set up MoAs
        SubsistenceFeesMoa moa1 = SubsistenceFeesMoa.builder()
                .transactionId("CCACM1200")
                .subsistenceFeesRun(run1)
                .moaType(MoaType.SECTOR_MOA)
                .resourceId(1L)
                .initialTotalAmount(BigDecimal.valueOf(400L))
                .regulatorReceivedAmount(BigDecimal.valueOf(100L))
                .fileDocumentUuid("111-111-111")
                .submissionDate(submissionDate)
                .build();
        entityManager.persist(moa1);

        SubsistenceFeesMoa moa2 = SubsistenceFeesMoa.builder()
                .transactionId("CCACM1201")
                .subsistenceFeesRun(run1)
                .moaType(MoaType.SECTOR_MOA)
                .resourceId(2L)
                .initialTotalAmount(BigDecimal.valueOf(400L))
                .regulatorReceivedAmount(BigDecimal.valueOf(200L))
                .fileDocumentUuid("111-111-111")
                .submissionDate(submissionDate)
                .build();
        entityManager.persist(moa2);

        SubsistenceFeesMoa moa3 = SubsistenceFeesMoa.builder()
                .transactionId("CCATM1200")
                .subsistenceFeesRun(run1)
                .moaType(MoaType.TARGET_UNIT_MOA)
                .resourceId(40L)
                .initialTotalAmount(BigDecimal.valueOf(200L))
                .regulatorReceivedAmount(BigDecimal.valueOf(1L))
                .fileDocumentUuid("111-111-111")
                .submissionDate(submissionDate)
                .build();
        entityManager.persist(moa3);

        SubsistenceFeesMoa moa4 = SubsistenceFeesMoa.builder()
                .transactionId("CCACM1202")
                .subsistenceFeesRun(run2)
                .moaType(MoaType.SECTOR_MOA)
                .resourceId(1L)
                .initialTotalAmount(BigDecimal.valueOf(500L))
                .regulatorReceivedAmount(BigDecimal.valueOf(100L))
                .fileDocumentUuid("111-111-111")
                .submissionDate(submissionDate)
                .build();
        entityManager.persist(moa4);

        SubsistenceFeesMoa moa5 = SubsistenceFeesMoa.builder()
                .transactionId("CCACM1203")
                .subsistenceFeesRun(run3)
                .moaType(MoaType.SECTOR_MOA)
                .resourceId(2L)
                .initialTotalAmount(BigDecimal.valueOf(1500L))
                .regulatorReceivedAmount(BigDecimal.valueOf(500L))
                .fileDocumentUuid("111-111-111")
                .submissionDate(submissionDate)
                .build();
        entityManager.persist(moa5);

        // Set up MoA target units
        SubsistenceFeesMoaTargetUnit tu1 = SubsistenceFeesMoaTargetUnit.builder()
                .subsistenceFeesMoa(moa1)
                .accountId(40L)
                .initialTotalAmount(BigDecimal.valueOf(200L))
                .build();
        entityManager.persist(tu1);
        SubsistenceFeesMoaTargetUnit tu2 = SubsistenceFeesMoaTargetUnit.builder()
                .subsistenceFeesMoa(moa1)
                .accountId(41L)
                .initialTotalAmount(BigDecimal.valueOf(200L))
                .build();
        entityManager.persist(tu2);
        SubsistenceFeesMoaTargetUnit tu3 = SubsistenceFeesMoaTargetUnit.builder()
                .subsistenceFeesMoa(moa2)
                .accountId(38L)
                .initialTotalAmount(BigDecimal.valueOf(200L))
                .build();
        entityManager.persist(tu3);
        SubsistenceFeesMoaTargetUnit tu4 = SubsistenceFeesMoaTargetUnit.builder()
                .subsistenceFeesMoa(moa2)
                .accountId(39L)
                .initialTotalAmount(BigDecimal.valueOf(200L))
                .build();
        entityManager.persist(tu4);
        SubsistenceFeesMoaTargetUnit tu5 = SubsistenceFeesMoaTargetUnit.builder()
                .subsistenceFeesMoa(moa3)
                .accountId(40L)
                .initialTotalAmount(BigDecimal.valueOf(100L))
                .build();
        entityManager.persist(tu5);
        SubsistenceFeesMoaTargetUnit tu6 = SubsistenceFeesMoaTargetUnit.builder()
                .subsistenceFeesMoa(moa4)
                .accountId(42L)
                .initialTotalAmount(BigDecimal.valueOf(500L))
                .build();
        entityManager.persist(tu6);
        SubsistenceFeesMoaTargetUnit tu7 = SubsistenceFeesMoaTargetUnit.builder()
                .subsistenceFeesMoa(moa5)
                .accountId(43L)
                .initialTotalAmount(BigDecimal.valueOf(1500L))
                .build();
        entityManager.persist(tu7);

        // Set up MoA facilities
        SubsistenceFeesMoaFacility facility1 = SubsistenceFeesMoaFacility.builder()
                .subsistenceFeesMoaTargetUnit(tu1)
                .facilityId(1L)
                .initialAmount(BigDecimal.valueOf(100L))
                .paymentStatus(FacilityPaymentStatus.COMPLETED)
                .build();
        entityManager.persist(facility1);
        SubsistenceFeesMoaFacility facility2 = SubsistenceFeesMoaFacility.builder()
                .subsistenceFeesMoaTargetUnit(tu1)
                .facilityId(2L)
                .initialAmount(BigDecimal.valueOf(100L))
                .paymentStatus(FacilityPaymentStatus.IN_PROGRESS)
                .build();
        entityManager.persist(facility2);
        SubsistenceFeesMoaFacility facility3 = SubsistenceFeesMoaFacility.builder()
                .subsistenceFeesMoaTargetUnit(tu2)
                .facilityId(3L)
                .initialAmount(BigDecimal.valueOf(100L))
                .paymentStatus(FacilityPaymentStatus.IN_PROGRESS)
                .build();
        entityManager.persist(facility3);
        SubsistenceFeesMoaFacility facility4 = SubsistenceFeesMoaFacility.builder()
                .subsistenceFeesMoaTargetUnit(tu2)
                .facilityId(4L)
                .initialAmount(BigDecimal.valueOf(100L))
                .paymentStatus(FacilityPaymentStatus.IN_PROGRESS)
                .build();
        entityManager.persist(facility4);
        SubsistenceFeesMoaFacility facility5 = SubsistenceFeesMoaFacility.builder()
                .subsistenceFeesMoaTargetUnit(tu3)
                .facilityId(5L)
                .initialAmount(BigDecimal.valueOf(100L))
                .paymentStatus(FacilityPaymentStatus.IN_PROGRESS)
                .build();
        entityManager.persist(facility5);
        SubsistenceFeesMoaFacility facility6 = SubsistenceFeesMoaFacility.builder()
                .subsistenceFeesMoaTargetUnit(tu3)
                .facilityId(7L)
                .initialAmount(BigDecimal.valueOf(100L))
                .paymentStatus(FacilityPaymentStatus.IN_PROGRESS)
                .build();
        entityManager.persist(facility6);
        SubsistenceFeesMoaFacility facility7 = SubsistenceFeesMoaFacility.builder()
                .subsistenceFeesMoaTargetUnit(tu4)
                .facilityId(8L)
                .initialAmount(BigDecimal.valueOf(100L))
                .paymentStatus(FacilityPaymentStatus.IN_PROGRESS)
                .build();
        entityManager.persist(facility7);
        SubsistenceFeesMoaFacility facility8 = SubsistenceFeesMoaFacility.builder()
                .subsistenceFeesMoaTargetUnit(tu4)
                .facilityId(9L)
                .initialAmount(BigDecimal.valueOf(100L))
                .paymentStatus(FacilityPaymentStatus.IN_PROGRESS)
                .build();
        entityManager.persist(facility8);
        SubsistenceFeesMoaFacility facility9 = SubsistenceFeesMoaFacility.builder()
                .subsistenceFeesMoaTargetUnit(tu5)
                .facilityId(9L)
                .initialAmount(BigDecimal.valueOf(100L))
                .paymentStatus(FacilityPaymentStatus.IN_PROGRESS)
                .build();
        entityManager.persist(facility9);
        SubsistenceFeesMoaFacility facility10 = SubsistenceFeesMoaFacility.builder()
                .subsistenceFeesMoaTargetUnit(tu6)
                .facilityId(1L)
                .initialAmount(BigDecimal.valueOf(100L))
                .paymentStatus(FacilityPaymentStatus.IN_PROGRESS)
                .build();
        entityManager.persist(facility10);
        SubsistenceFeesMoaFacility facility11 = SubsistenceFeesMoaFacility.builder()
                .subsistenceFeesMoaTargetUnit(tu7)
                .facilityId(2L)
                .initialAmount(BigDecimal.valueOf(100L))
                .paymentStatus(FacilityPaymentStatus.COMPLETED)
                .build();
        entityManager.persist(facility11);

        flushAndClear();
    }

    @Test
    void findSubsistenceFeesRunsWithAmountsByIds() {
    	SubsistenceFeesRunSearchResultInfo resultInfo1 = new SubsistenceFeesRunSearchResultInfo(
    			1L, "S2501", submissionDate, new BigDecimal("900.00"),new BigDecimal("800.00"), new BigDecimal("301.00"));
    	SubsistenceFeesRunSearchResultInfo resultInfo2 = new SubsistenceFeesRunSearchResultInfo(
    			2L, "S2502", submissionDate.plusDays(1), new BigDecimal("100.00"), new BigDecimal("100.00"), new BigDecimal("100.00"));
    	SubsistenceFeesRunSearchResultInfo resultInfo3 = new SubsistenceFeesRunSearchResultInfo(
    			3L, "S2503", submissionDate.plusDays(2), new BigDecimal("100.00"), BigDecimal.ZERO, new BigDecimal("500.00"));
        List<SubsistenceFeesRunSearchResultInfo> expectedResults = List.of(resultInfo3, resultInfo2, resultInfo1);

        List<SubsistenceFeesRunSearchResultInfo> resultInfos =
                repository.findSubsistenceFeesRunsWithAmountsByIds(Set.of(1L, 2L, 3L));

        assertThat(resultInfos).isEqualTo(expectedResults);
    }

    @Test
    void findSubsistenceFeesRunsWithAmountsByIds_cancelled() {
    	SubsistenceFeesMoaFacility facility1 = entityManager.find(SubsistenceFeesMoaFacility.class, 21L);
    	facility1.setPaymentStatus(FacilityPaymentStatus.CANCELLED);
    	entityManager.persist(facility1);
    	SubsistenceFeesMoaFacility facility2 = entityManager.find(SubsistenceFeesMoaFacility.class, 22L);
    	facility2.setPaymentStatus(FacilityPaymentStatus.CANCELLED);
    	entityManager.persist(facility2);

    	SubsistenceFeesRunSearchResultInfo resultInfo1 = new SubsistenceFeesRunSearchResultInfo(
    			5L,"S2502", submissionDate.plusDays(1), BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("100.00"));
    	SubsistenceFeesRunSearchResultInfo resultInfo2 = new SubsistenceFeesRunSearchResultInfo(
    			6L, "S2503", submissionDate.plusDays(2), BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("500.00"));
        List<SubsistenceFeesRunSearchResultInfo> expectedResults = List.of(resultInfo2, resultInfo1);

        List<SubsistenceFeesRunSearchResultInfo> resultInfos =
                repository.findSubsistenceFeesRunsWithAmountsByIds(Set.of(5L, 6L));

        assertThat(resultInfos).isEqualTo(expectedResults);
    }

    @Test
    void findSubsistenceFeesRunDetailsById() {
    	SubsistenceFeesRunDetailsInfo detailsInfo = new SubsistenceFeesRunDetailsInfo(
    			7L, "S2501", submissionDate, new BigDecimal("1000.00"), new BigDecimal("900.00"));

    	Optional<SubsistenceFeesRunDetailsInfo> resultInfo = repository.findSubsistenceFeesRunDetailsById(7L);

        assertThat(resultInfo).contains(detailsInfo);

        SubsistenceFeesRunMoaDetailsInfo moaDetailsInfo = new SubsistenceFeesRunMoaDetailsInfo(new BigDecimal("301.00"), 2L, 1L);

    	Optional<SubsistenceFeesRunMoaDetailsInfo> moaResultInfo = repository.findSubsistenceFeesRunMoaDetailsById(7L);

        assertThat(moaResultInfo).contains(moaDetailsInfo);
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
}
