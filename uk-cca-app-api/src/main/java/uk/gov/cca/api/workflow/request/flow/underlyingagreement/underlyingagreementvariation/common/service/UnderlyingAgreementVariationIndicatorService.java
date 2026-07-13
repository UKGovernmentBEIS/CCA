package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.service;

import java.time.Year;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.service.PerformanceDataFacilityService;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.service.PerformanceDataFacilityStatusService;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.transform.PerformanceDataFacilityReferenceDataMapper;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Cca3FacilityBaselineAndTargets;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementVariationIndicatorService {

	private final PerformanceDataFacilityService performanceDataFacilityService;
    private final PerformanceDataFacilityStatusService performanceDataFacilityStatusService;
    
    private static final PerformanceDataFacilityReferenceDataMapper PERFORMANCE_DATA_FACILITY_MAPPER = Mappers
    		.getMapper(PerformanceDataFacilityReferenceDataMapper.class);
    
    public void updateVariationIndicatorForPerformanceDataFacility(UnderlyingAgreementContainer originalUnaContainer, UnderlyingAgreementContainer currentUnaContainer) {	
		Map<String, Facility> originalCca3Facilities = getCca3Facilities(originalUnaContainer.getUnderlyingAgreement().getFacilities());
		
		// Only CCA3 facilities are eligible for update
		if (!originalCca3Facilities.isEmpty()) {
	    	updateCca3PerformanceDataFacilityVariationIndicator(
	    			originalCca3Facilities, getCca3Facilities(currentUnaContainer.getUnderlyingAgreement().getFacilities()));
	    }
	}
    
    private void updateCca3PerformanceDataFacilityVariationIndicator(Map<String, Facility> originalCca3Facilities, 
    		Map<String, Facility> currentCca3Facilities) {
		// Find applicable target years
    	Set<Year> targetYears = performanceDataFacilityService.getAvailableTargetPeriodYears(SchemeVersion.CCA_3);
    	
    	// Facilities that were excluded or changed to CCA2 are eligible for all target years
    	Set<String> noLongerInCca3FacilityBusinessIds = originalCca3Facilities.keySet().stream()
    	        .filter(k -> !currentCca3Facilities.containsKey(k))
    	        .collect(Collectors.toSet());

    	// Find baseline data changes for each target year for the remaining facilities, and update relevant variation indicator
    	targetYears.forEach(targetYear -> {
    		Set<String> baselineDataChangedFacilityBusinessIds = getBaselineDataChangedFacilityBusinessIds(
    				originalCca3Facilities, currentCca3Facilities, targetYear);
    		performanceDataFacilityStatusService.updateFacilityPerformanceDataVariationIndicator(
    				Stream.concat(baselineDataChangedFacilityBusinessIds.stream(), noLongerInCca3FacilityBusinessIds.stream())
    		        .collect(Collectors.toSet()), targetYear);
    	});
	}
    
    private Set<String> getBaselineDataChangedFacilityBusinessIds(Map<String, Facility> originalCca3Facilities, 
    		Map<String, Facility> currentCca3Facilities, Year targetYear) {
		
    	return originalCca3Facilities.entrySet().stream()
    			.filter(entry -> currentCca3Facilities.containsKey(entry.getKey()))
                .filter(entry ->
                        baselineDataHasChanged(
                                entry.getValue().getFacilityItem().getCca3BaselineAndTargets(),
                                currentCca3Facilities.get(entry.getKey())
                                        .getFacilityItem()
                                        .getCca3BaselineAndTargets(),
                                targetYear))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

	private boolean baselineDataHasChanged(Cca3FacilityBaselineAndTargets originalBaselineAndTargets,
			Cca3FacilityBaselineAndTargets currentBaselineAndTargets, Year targetYear) {
		return !PERFORMANCE_DATA_FACILITY_MAPPER.toPerformanceDataFacilityBaselineAndTargets(originalBaselineAndTargets, targetYear)
				.equals(PERFORMANCE_DATA_FACILITY_MAPPER.toPerformanceDataFacilityBaselineAndTargets(currentBaselineAndTargets, targetYear));
	}

	private Map<String, Facility> getCca3Facilities(Set<Facility> facilities) {
		return facilities.stream()
				.filter(facility -> 
				facility.getFacilityItem().getFacilityDetails().getParticipatingSchemeVersions().contains(SchemeVersion.CCA_3))
				.collect(Collectors.toMap(
	                    facility -> facility.getFacilityItem().getFacilityId(),
	                    Function.identity()
	            ));
	}
}
