package uk.gov.cca.api.targetperiodreporting.performancedatafacility.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityBaselineAndTargets;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataFacilityTargetPeriodResultType;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.dto.SectorFacilityPerformanceDataReportItemDTO;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.dto.SectorFacilityPerformanceDataReportListDTO;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.dto.SectorFacilityPerformanceDataReportSearchCriteria;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.repository.PerformanceDataFacilityDataCustomRepository;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.validation.PerformanceDataFacilityValidator;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodYear;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementQueryService;
import uk.gov.netz.api.common.exception.BusinessException;

@Service
@Validated
@RequiredArgsConstructor
public class SectorPerformanceDataFacilityDataReportService {

    private final TargetPeriodService targetPeriodService;
    private final PerformanceDataFacilityDataCustomRepository performanceDataFacilityDataCustomRepository;
    private final UnderlyingAgreementQueryService underlyingAgreementQueryService;
    private final PerformanceDataFacilityReferenceDataService performanceDataFacilityReferenceDataService;
    private final PerformanceDataFacilityValidator performanceDataFacilityValidator;
    
    
	public SectorFacilityPerformanceDataReportListDTO getSectorFacilityPerformanceDataReportList(
			Long sectorAssociationId, @Valid @NotNull SectorFacilityPerformanceDataReportSearchCriteria criteria) {
		
		List<TargetPeriodYear> targetPeriodYears = targetPeriodService.getTargetPeriodYearsByTypeAndReportType(criteria.getTargetPeriodType(), criteria.getTargetPeriodReportType().name());
		
		if(CollectionUtils.isEmpty(targetPeriodYears)) {
			return SectorFacilityPerformanceDataReportListDTO.emptySectorFacilityPerformanceDataReportList();
		}
		if(targetPeriodYears.size() > 1) {
			throw new BusinessException(CcaErrorCode.PERFORMANCE_DATA_FACILITY_INVALID_SECTOR_REPORT_STATUS_CRITERIA);
		}
		
		TargetPeriodYear targetPeriodYear = targetPeriodYears.get(0);
		
		SectorFacilityPerformanceDataReportListDTO submitted;
		SectorFacilityPerformanceDataReportListDTO outstanding;
		List<SectorFacilityPerformanceDataReportItemDTO> eligibleOutstanding = new ArrayList<>();
		List<SectorFacilityPerformanceDataReportItemDTO> merged = new ArrayList<>();		

		if (!PerformanceDataFacilityTargetPeriodResultType.OUTSTANDING.equals(criteria.getReportStatus())) {
			submitted = performanceDataFacilityDataCustomRepository
					.getSectorFacilityPerformanceDataReportListSubmittedBySearchCriteria(sectorAssociationId, criteria, targetPeriodYear);
			merged.addAll(submitted.getPerformanceDataReportItems());
		}

		if (criteria.getSubType() == null && (criteria.getReportStatus() == null
				|| criteria.getReportStatus().equals(PerformanceDataFacilityTargetPeriodResultType.OUTSTANDING))) {

			outstanding = performanceDataFacilityDataCustomRepository
					.getSectorFacilityPerformanceDataReportListOutstandingBySearchCriteria(sectorAssociationId, criteria, targetPeriodYear);
			
			if(outstanding.getTotal() > 0) {
				Set<Long> accountIds = outstanding.getPerformanceDataReportItems().stream()
		                .map(SectorFacilityPerformanceDataReportItemDTO::getAccountId).collect(Collectors.toSet());
		        final Map<Long, UnderlyingAgreementContainer> underlyingAgreementAccountMap = underlyingAgreementQueryService
		                .getUnderlyingAgreementContainersByAccounts(accountIds);
		        
		        outstanding.getPerformanceDataReportItems().forEach(item -> {
		        	final PerformanceDataFacilityBaselineAndTargets baselineAndTargets = performanceDataFacilityReferenceDataService
		                    .getFacilityOriginalBaselineAndTargets(item.getFacilityBusinessId(), targetPeriodYear.getTargetYear(), underlyingAgreementAccountMap.get(item.getAccountId()));
		        	BusinessValidationResult resultBaselineDateEligibility = performanceDataFacilityValidator.validateFacilityBaselineDateEligibility(targetPeriodYear, baselineAndTargets);
		        	BusinessValidationResult resultProductsEligibility = performanceDataFacilityValidator.validateFacilityProductsEligibility(targetPeriodYear, baselineAndTargets);
		        	if(resultBaselineDateEligibility.isValid() && resultProductsEligibility.isValid()) {
		        		eligibleOutstanding.add(item);
		        	}
		        });
			}
			
			merged.addAll(eligibleOutstanding);
		}

		merged.sort(Comparator.comparing(SectorFacilityPerformanceDataReportItemDTO::getFacilityBusinessId,
				Comparator.nullsLast(Comparator.naturalOrder())));

		List<SectorFacilityPerformanceDataReportItemDTO> page = merged.stream()
				.skip((long) criteria.getPaging().getPageNumber() * criteria.getPaging().getPageSize())
				.limit(criteria.getPaging().getPageSize()).toList();

		return SectorFacilityPerformanceDataReportListDTO.builder()
				.performanceDataReportItems(page)
				.total((long) merged.size())
				.build();
	}

}
