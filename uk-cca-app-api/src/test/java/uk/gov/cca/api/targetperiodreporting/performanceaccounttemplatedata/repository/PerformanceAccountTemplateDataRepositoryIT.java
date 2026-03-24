package uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityManager;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriod;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.PerformanceAccountTemplateDataContainer;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.PerformanceAccountTemplateDataEntity;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.PerformanceAccountTemplateDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.TargetUnitIdentityAndPerformance;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYearsContainer;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYear;
import uk.gov.netz.api.common.AbstractContainerBaseTest;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@EnableAutoConfiguration
@EnableJpaAuditing
@ContextConfiguration(classes = PerformanceAccountTemplateDataRepository.class)
@DataJpaTest
@Import(ObjectMapper.class)
class PerformanceAccountTemplateDataRepositoryIT extends AbstractContainerBaseTest {

	@Autowired
	private PerformanceAccountTemplateDataRepository cut;

	@Autowired
	private EntityManager entityManager;

	@Test
	void findReportVersionByAccountIdAndTargetPeriodYear_no_records() {
		Long accountId = 1L;
		Year targetPeriodYear = Year.of(2024);
		
		var result = cut.findReportVersionByAccountIdAndTargetPeriodYear(accountId, targetPeriodYear);
		
		assertThat(result).isZero();
	}
	
	@Test
	void findReportVersionByAccountIdAndTargetPeriodYear_irrelevant_Records() {
		Long accountId = 1L;
		Year targetPeriodYear = Year.of(2024);
		
		TargetPeriod targetPeriod = createTargetPeriod(TargetPeriodType.TP6);
		entityManager.persist(targetPeriod);
		
		PerformanceAccountTemplateDataEntity pat1 = createPerformanceAccountTemplateEntity(2L, targetPeriod, targetPeriodYear, PerformanceAccountTemplateDataSubmissionType.FINAL, 1);
		entityManager.persist(pat1);
		
		PerformanceAccountTemplateDataEntity pat2 = createPerformanceAccountTemplateEntity(accountId, targetPeriod, Year.of(2023), PerformanceAccountTemplateDataSubmissionType.FINAL, 1);
		entityManager.persist(pat2);
		
		flushAndClear();
		
		var result = cut.findReportVersionByAccountIdAndTargetPeriodYear(accountId, targetPeriodYear);
		assertThat(result).isZero();
	}
	
	@Test
	void findReportVersionByAccountIdAndTargetPeriodYear() {
		Long accountId = 1L;
		Year targetPeriodYear = Year.of(2024);
		
		TargetPeriod targetPeriod = createTargetPeriod(TargetPeriodType.TP6);
		entityManager.persist(targetPeriod);
		
		PerformanceAccountTemplateDataEntity pat1 = createPerformanceAccountTemplateEntity(accountId, targetPeriod, targetPeriodYear, PerformanceAccountTemplateDataSubmissionType.FINAL, 5);
		entityManager.persist(pat1);
		
		PerformanceAccountTemplateDataEntity pat2 = createPerformanceAccountTemplateEntity(accountId, targetPeriod, Year.of(2023), PerformanceAccountTemplateDataSubmissionType.FINAL, 2);
		entityManager.persist(pat2);
		
		flushAndClear();
		
		var result = cut.findReportVersionByAccountIdAndTargetPeriodYear(accountId, targetPeriodYear);
		assertThat(result).isEqualTo(5);
	}
	
	@Test
	void findTopByAccountIdAndTargetPeriodBusinessIdByOrderByIdDesc_empty() {
		Long accountId = 1L;
		TargetPeriodType targetPeriodType = TargetPeriodType.TP6;
		
		TargetPeriod targetPeriod = createTargetPeriod(targetPeriodType);
		entityManager.persist(targetPeriod);
		
		TargetPeriod anotherTargetPeriod = createTargetPeriod(TargetPeriodType.TP5);
		entityManager.persist(anotherTargetPeriod);
		
		Optional<PerformanceAccountTemplateDataEntity> result = cut
				.findTopByAccountIdAndTargetPeriodBusinessIdOrderByIdDesc(accountId, targetPeriodType);
		assertThat(result).isEmpty();
	}
	
	@Test
	void findTopByAccountIdAndTargetPeriodBusinessIdByOrderByIdDesc() {
		Long accountId = 1L;
		Year targetPeriodYear = Year.of(2024);
		Year anotherTtargetPeriodYear = Year.of(2025);
		TargetPeriodType targetPeriodType = TargetPeriodType.TP6;
		
		TargetPeriod targetPeriod = createTargetPeriod(targetPeriodType);
		entityManager.persist(targetPeriod);
		
		TargetPeriod anotherTargetPeriod = createTargetPeriod(TargetPeriodType.TP5);
		entityManager.persist(anotherTargetPeriod);
		
		PerformanceAccountTemplateDataEntity pat1 = createPerformanceAccountTemplateEntity(accountId, targetPeriod,  targetPeriodYear, PerformanceAccountTemplateDataSubmissionType.INTERIM, 1);
		entityManager.persist(pat1);
		
		PerformanceAccountTemplateDataEntity pat2 = createPerformanceAccountTemplateEntity(accountId, targetPeriod, anotherTtargetPeriodYear, PerformanceAccountTemplateDataSubmissionType.FINAL, 1);
		entityManager.persist(pat2);
		
		PerformanceAccountTemplateDataEntity pat3 = createPerformanceAccountTemplateEntity(accountId, anotherTargetPeriod, Year.of(2023), PerformanceAccountTemplateDataSubmissionType.FINAL, 1);
		entityManager.persist(pat3);
		
		flushAndClear();
		
		var result = cut.findTopByAccountIdAndTargetPeriodBusinessIdOrderByIdDesc(accountId, targetPeriodType);

		assertThat(result).isPresent();
		assertThat(result.get().getId()).isEqualTo(pat2.getId());
		assertThat(result.get().getTargetPeriod().getBusinessId()).isEqualTo(targetPeriodType);
		assertThat(result.get().getTargetPeriod().getName()).isEqualTo(targetPeriodType.name());
	}
	
	private TargetPeriod createTargetPeriod(TargetPeriodType targetPeriodType) {
		return TargetPeriod.builder()
				.businessId(targetPeriodType)
				.name(targetPeriodType.name())
				.startDate(LocalDate.now())
				.endDate(LocalDate.now())
				.targetPeriodYearsContainer(TargetPeriodYearsContainer.builder()
						.targetPeriodYears(List.of(TargetPeriodYear.builder()
								.targetYear(Year.now())
								.startDate(LocalDate.now())
								.endDate(LocalDate.now())
								.reportingStartDate(LocalDate.now())
								.build()))
						.build())
				.buyOutStartDate(LocalDate.now())
				.buyOutPrimaryPaymentDeadline(LocalDate.now())
				.secondaryReportingStartDate(LocalDate.now())
				.build();
	}

	private PerformanceAccountTemplateDataEntity createPerformanceAccountTemplateEntity(Long accountId, TargetPeriod targetPeriod, Year targetPeriodYear, 
			PerformanceAccountTemplateDataSubmissionType type, int reportVersion) {
		return PerformanceAccountTemplateDataEntity.builder()
				.accountId(accountId)
				.targetPeriod(targetPeriod)
				.targetPeriodYear(targetPeriodYear)
				.data(PerformanceAccountTemplateDataContainer.builder()
						.targetUnitIdentityAndPerformance(new TargetUnitIdentityAndPerformance())
						.file(new FileInfoDTO("name", "uuid"))
						.build())
				.submissionType(type)
				.reportVersion(reportVersion)
				.build();
	}
	
	private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
