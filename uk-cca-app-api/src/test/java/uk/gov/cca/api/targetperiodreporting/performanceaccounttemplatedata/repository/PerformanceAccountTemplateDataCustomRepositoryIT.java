package uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.Map;

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

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityManager;
import uk.gov.cca.api.account.domain.AccountAddress;
import uk.gov.cca.api.account.domain.CcaAccountContactType;
import uk.gov.cca.api.account.domain.CcaEmissionTradingScheme;
import uk.gov.cca.api.account.domain.FinancialIndependenceStatus;
import uk.gov.cca.api.account.domain.TargetUnitAccount;
import uk.gov.cca.api.account.domain.TargetUnitAccountOperatorType;
import uk.gov.cca.api.account.domain.TargetUnitAccountStatus;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriod;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.PerformanceAccountTemplateDataContainer;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.PerformanceAccountTemplateDataEntity;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.PerformanceAccountTemplateDataStatus;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.PerformanceAccountTemplateDataSubmissionType;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.TargetType;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.TargetUnitIdentityAndPerformance;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.dto.SectorPerformanceAccountTemplateDataReportItemDTO;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.dto.SectorPerformanceAccountTemplateDataReportListDTO;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.dto.SectorPerformanceAccountTemplateDataReportSearchCriteria;
import uk.gov.netz.api.common.AbstractContainerBaseTest;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@EnableAutoConfiguration
@EnableJpaAuditing
@ContextConfiguration(classes = PerformanceAccountTemplateDataCustomRepository.class)
@DataJpaTest
@Import(ObjectMapper.class)
class PerformanceAccountTemplateDataCustomRepositoryIT extends AbstractContainerBaseTest {

	@Autowired
	private PerformanceAccountTemplateDataCustomRepository cut;
	
	@Autowired
	private EntityManager entityManager;
	
	private Year targetPeriodYear = Year.of(2024);
	private Long sectorAssociationId = 1L;
	
	@BeforeEach
    void setUp() {
		TargetPeriod targetPeriod = createTargetPeriod(TargetPeriodType.TP6);
		TargetPeriod targetPeriodAnother = createTargetPeriod(TargetPeriodType.TP5);
		
		TargetUnitAccount account1 = createAccount(-1L, sectorAssociationId, TargetUnitAccountStatus.LIVE, LocalDate.of(2023, 11, 3).atStartOfDay(), null);
		PerformanceAccountTemplateDataEntity pat1 = createPerformanceAccountTemplateDataEntity(account1.getId(),
				targetPeriod, targetPeriodYear, PerformanceAccountTemplateDataSubmissionType.INTERIM, 1);
		
		TargetUnitAccount account2 = createAccount(-2L, sectorAssociationId, TargetUnitAccountStatus.LIVE, LocalDate.of(2024, 11, 3).atStartOfDay(), null);
		PerformanceAccountTemplateDataEntity pat2 = createPerformanceAccountTemplateDataEntity(account2.getId(),
				targetPeriodAnother, Year.of(2021), PerformanceAccountTemplateDataSubmissionType.INTERIM, 1);
		
		TargetUnitAccount account3 = createAccount(-3L, sectorAssociationId, TargetUnitAccountStatus.LIVE, LocalDate.of(2025, 2, 3).atStartOfDay(), null);
		PerformanceAccountTemplateDataEntity pat3 = createPerformanceAccountTemplateDataEntity(account3.getId(),
				targetPeriod, targetPeriodYear, PerformanceAccountTemplateDataSubmissionType.INTERIM, 1);
		
		TargetUnitAccount account4 = createAccount(-4L, sectorAssociationId, TargetUnitAccountStatus.LIVE, LocalDate.of(2018, 11, 3).atStartOfDay(), null);
        
		createAccount(-5L, sectorAssociationId, TargetUnitAccountStatus.NEW, LocalDate.of(2023, 11, 3).atStartOfDay(), null);
        
		createAccount(-6L, -1l, TargetUnitAccountStatus.LIVE, LocalDate.of(2023, 11, 3).atStartOfDay(), null);
        
		TargetUnitAccount account7 = createAccount(-7L, sectorAssociationId, TargetUnitAccountStatus.TERMINATED, LocalDate.of(2024, 11, 3).atStartOfDay(), LocalDate.of(2025, 1, 1).atStartOfDay());
        PerformanceAccountTemplateDataEntity pat7 = createPerformanceAccountTemplateDataEntity(account7.getId(),
				targetPeriod, targetPeriodYear, PerformanceAccountTemplateDataSubmissionType.FINAL, 1);
        
        createAccount(-8L, sectorAssociationId, TargetUnitAccountStatus.TERMINATED, LocalDate.of(2016, 11, 3).atStartOfDay(), LocalDate.of(2017, 5, 3).atStartOfDay());
    	
    	flushAndClear();
    }
	
	@Test
	void getSectorPerformanceAccountTemplateDataReportListBySearchCriteria() {
    	SectorPerformanceAccountTemplateDataReportSearchCriteria criteria = SectorPerformanceAccountTemplateDataReportSearchCriteria
				.builder()
				.targetPeriodType(TargetPeriodType.TP6)
				.paging(PagingRequest.builder().pageNumber(0).pageSize(30).build())
				.build();
    	
		SectorPerformanceAccountTemplateDataReportListDTO result = cut
				.getSectorPerformanceAccountTemplateDataReportListBySearchCriteria(sectorAssociationId, criteria,
						targetPeriodYear);
		
		assertThat(result.getItems()).extracting(SectorPerformanceAccountTemplateDataReportItemDTO::getAccountId)
				.containsExactly(-1L, -4L, -7L);
	}
	
	@Test
	void getSectorPerformanceAccountTemplateDataReportListBySearchCriteria_business_account_id() {
    	SectorPerformanceAccountTemplateDataReportSearchCriteria criteria = SectorPerformanceAccountTemplateDataReportSearchCriteria
				.builder()
				.targetPeriodType(TargetPeriodType.TP6)
				.targetUnitAccountBusinessId("businessId-1")
				.paging(PagingRequest.builder().pageNumber(0).pageSize(30).build())
				.build();
    	
		SectorPerformanceAccountTemplateDataReportListDTO result = cut
				.getSectorPerformanceAccountTemplateDataReportListBySearchCriteria(sectorAssociationId, criteria,
						targetPeriodYear);
		
		assertThat(result.getItems()).extracting(SectorPerformanceAccountTemplateDataReportItemDTO::getAccountId)
				.containsExactly(-1L);
	}
	
	@Test
	void getSectorPerformanceAccountTemplateDataReportListBySearchCriteria_status_submitted() {
    	SectorPerformanceAccountTemplateDataReportSearchCriteria criteria = SectorPerformanceAccountTemplateDataReportSearchCriteria
				.builder()
				.targetPeriodType(TargetPeriodType.TP6)
				.status(PerformanceAccountTemplateDataStatus.SUBMITTED)
				.paging(PagingRequest.builder().pageNumber(0).pageSize(30).build())
				.build();
    	
		SectorPerformanceAccountTemplateDataReportListDTO result = cut
				.getSectorPerformanceAccountTemplateDataReportListBySearchCriteria(sectorAssociationId, criteria,
						targetPeriodYear);
		
		assertThat(result.getItems()).extracting(SectorPerformanceAccountTemplateDataReportItemDTO::getAccountId)
				.containsExactly(-1L, -7L);
	}
	
	@Test
	void getSectorPerformanceAccountTemplateDataReportListBySearchCriteria_status_outstanding() {
    	SectorPerformanceAccountTemplateDataReportSearchCriteria criteria = SectorPerformanceAccountTemplateDataReportSearchCriteria
				.builder()
				.targetPeriodType(TargetPeriodType.TP6)
				.status(PerformanceAccountTemplateDataStatus.OUTSTANDING)
				.paging(PagingRequest.builder().pageNumber(0).pageSize(30).build())
				.build();
    	
		SectorPerformanceAccountTemplateDataReportListDTO result = cut
				.getSectorPerformanceAccountTemplateDataReportListBySearchCriteria(sectorAssociationId, criteria,
						targetPeriodYear);
		
		assertThat(result.getItems()).extracting(SectorPerformanceAccountTemplateDataReportItemDTO::getAccountId)
				.containsExactly(-4L);
	}
	
	@Test
	void getSectorPerformanceAccountTemplateDataReportListBySearchCriteria_type_interim() {
    	SectorPerformanceAccountTemplateDataReportSearchCriteria criteria = SectorPerformanceAccountTemplateDataReportSearchCriteria
				.builder()
				.targetPeriodType(TargetPeriodType.TP6)
				.submissionType(PerformanceAccountTemplateDataSubmissionType.INTERIM)
				.paging(PagingRequest.builder().pageNumber(0).pageSize(30).build())
				.build();
    	
		SectorPerformanceAccountTemplateDataReportListDTO result = cut
				.getSectorPerformanceAccountTemplateDataReportListBySearchCriteria(sectorAssociationId, criteria,
						targetPeriodYear);
		
		assertThat(result.getItems()).extracting(SectorPerformanceAccountTemplateDataReportItemDTO::getAccountId)
				.containsExactly(-1L);
	}
	
	@Test
	void getSectorPerformanceAccountTemplateDataReportListBySearchCriteria_type_final() {
    	SectorPerformanceAccountTemplateDataReportSearchCriteria criteria = SectorPerformanceAccountTemplateDataReportSearchCriteria
				.builder()
				.targetPeriodType(TargetPeriodType.TP6)
				.submissionType(PerformanceAccountTemplateDataSubmissionType.FINAL)
				.paging(PagingRequest.builder().pageNumber(0).pageSize(30).build())
				.build();
    	
		SectorPerformanceAccountTemplateDataReportListDTO result = cut
				.getSectorPerformanceAccountTemplateDataReportListBySearchCriteria(sectorAssociationId, criteria,
						targetPeriodYear);
		
		assertThat(result.getItems()).extracting(SectorPerformanceAccountTemplateDataReportItemDTO::getAccountId)
				.containsExactly(-7L);
	}
	
	private TargetUnitAccount createAccount(Long id, Long sectorId, TargetUnitAccountStatus status, LocalDateTime acceptedDate, LocalDateTime terminationDate) {
    	TargetUnitAccount account = TargetUnitAccount.builder()
    		.id(id)
			.businessId("businessId" + id)
			.name("name" + id)
	        .sectorAssociationId(sectorId)
	        .status(status)
	        .acceptedDate(acceptedDate)
	        .terminatedDate(terminationDate)
	        .financialIndependenceStatus(FinancialIndependenceStatus.NON_FINANCIALLY_INDEPENDENT)
	        .competentAuthority(CompetentAuthorityEnum.ENGLAND)
	        .emissionTradingScheme(CcaEmissionTradingScheme.DUMMY_EMISSION_TRADING_SCHEME)
	        .operatorType(TargetUnitAccountOperatorType.LIMITED_COMPANY)
	        .createdBy("user1")
	        .address(createAddress())
	        .contacts(Map.of(CcaAccountContactType.TU_SITE_CONTACT, "userId1"))
	        .build();
    	entityManager.persist(account);
    	return account;
    }
	
	private AccountAddress createAddress() {
		AccountAddress address = AccountAddress.builder().line1("123 Test Street").city("Test City").postcode("12345")
				.country("Test Country").build();
		entityManager.persist(address);
		return address;
    }
	
	private TargetPeriod createTargetPeriod(TargetPeriodType type) {
		TargetPeriod targetPeriod = TargetPeriod.builder()
				.businessId(type)
				.name(type.name())
				.startDate(LocalDate.now())
				.endDate(LocalDate.now())
				.performanceDataStartDate(LocalDate.now())
				.performanceDataEndDate(LocalDate.now())
				.buyOutStartDate(LocalDate.now())
				.buyOutEndDate(LocalDate.now())
				.secondaryReportingStartDate(LocalDate.now())
				.build();
		entityManager.persist(targetPeriod);
    	return targetPeriod;
	}
	
	private PerformanceAccountTemplateDataEntity createPerformanceAccountTemplateDataEntity(Long accountId,
			TargetPeriod targetPeriod, Year targetPeriodYear,
			PerformanceAccountTemplateDataSubmissionType submissionType, int reportVersion) {
		
		TargetUnitIdentityAndPerformance targetUnitIdentityAndPerformance = TargetUnitIdentityAndPerformance.builder()
				.targetType(TargetType.ABSOLUTE).build();
		PerformanceAccountTemplateDataContainer data = PerformanceAccountTemplateDataContainer.builder()
				.targetUnitIdentityAndPerformance(targetUnitIdentityAndPerformance)
				.file(new FileInfoDTO("name", "uuid"))
				.build();
		
		PerformanceAccountTemplateDataEntity pat = PerformanceAccountTemplateDataEntity.builder()
				.data(data)
				.accountId(accountId)
				.targetPeriod(targetPeriod)
				.targetPeriodYear(targetPeriodYear)
				.submissionType(submissionType)
				.reportVersion(reportVersion)
				.build();
		entityManager.persist(pat);
    	return pat;
	}

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
	
}
