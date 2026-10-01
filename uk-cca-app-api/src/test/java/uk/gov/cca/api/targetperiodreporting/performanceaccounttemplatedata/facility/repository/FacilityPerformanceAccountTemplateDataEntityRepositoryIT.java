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
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.common.domain.ActionCategoryType;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.common.domain.EnergyConsumptionOrCarbonEmissionsImpactedType;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.domain.FacilityPerformanceAccountTemplateDataContainer;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.domain.FacilityPerformanceAccountTemplateDataEntity;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.domain.FacilityPerformanceAccountTemplateSavingAction;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.facility.domain.SupplyDemandSideMeasure;
import uk.gov.netz.api.common.AbstractContainerBaseTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@EnableAutoConfiguration
@EnableJpaAuditing
@ContextConfiguration(classes = FacilityPerformanceAccountTemplateDataEntityRepository.class)
@DataJpaTest
@Import(ObjectMapper.class)
class FacilityPerformanceAccountTemplateDataEntityRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private FacilityPerformanceAccountTemplateDataEntityRepository repository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        createFacilityPerformanceAccountTemplateDataEntity(1L, 2026, 1);
        createFacilityPerformanceAccountTemplateDataEntity(1L, 2027, 2);
        createFacilityPerformanceAccountTemplateDataEntity(2L, 2026, 1);
        createFacilityPerformanceAccountTemplateDataEntity(3L, 2026, 2);

        flushAndClear();
    }

    @Test
    void findByFacilityIdAndTargetPeriodYear() {

        Optional<FacilityPerformanceAccountTemplateDataEntity> result =
                repository.findByFacilityIdAndTargetPeriodYear(1L, Year.of(2026));

        assertThat(result).isPresent();
        FacilityPerformanceAccountTemplateDataEntity entity = result.get();
        assertThat(entity.getTargetPeriodYear()).isEqualTo(Year.of(2026));
        assertThat(entity.getFacilityId()).isEqualTo(1L);
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }

    private void createFacilityPerformanceAccountTemplateDataEntity(final Long facilityId, final int targetPeriodYear, final int reportVersion) {
        final List<FacilityPerformanceAccountTemplateSavingAction> savingActions = List.of(FacilityPerformanceAccountTemplateSavingAction.builder()
                .actionCategoryType(ActionCategoryType.ENERGY_MANAGEMENT)
                .supplyDemandSideMeasure(SupplyDemandSideMeasure.DEMAND_SIDE)
                .savingActionsImplemented("saving")
                .implementationDate(LocalDate.of(targetPeriodYear, Month.APRIL, 1))
                .reasonsForImplementation("Reasons")
                .fixedEnergyConsumptionOrCarbonEmissionsImpacted(EnergyConsumptionOrCarbonEmissionsImpactedType.FIXED_AND_VARIABLE)
                .energyConsumptionOrCarbonEmissionsImpactedPercentage(BigDecimal.valueOf(12.6))
                .expectedExtentOfChangeImplementedPercentage(BigDecimal.valueOf(20.6))
                .expectedSavingsFromTheChangeImplementedPercentage(BigDecimal.valueOf(20.6))
                .estimatedChangeInEnergyConsumptionPercentage(BigDecimal.valueOf(20.6))
                .notes("notes")
                .build());
        final FacilityPerformanceAccountTemplateDataContainer container = FacilityPerformanceAccountTemplateDataContainer.builder()
                .savingActions(savingActions)
                .build();
        final FacilityPerformanceAccountTemplateDataEntity entity = FacilityPerformanceAccountTemplateDataEntity.builder()
                .facilityId(facilityId)
                .targetPeriodYear(Year.of(targetPeriodYear))
                .data(container)
                .reportVersion(reportVersion)
                .build();
        entityManager.persist(entity);
    }
}