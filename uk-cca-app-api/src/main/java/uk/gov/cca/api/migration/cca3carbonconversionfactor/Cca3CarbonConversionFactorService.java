package uk.gov.cca.api.migration.cca3carbonconversionfactor;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.migration.MigrationEndpoint;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementQueryService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.constants.RequestStatuses;
import uk.gov.netz.api.workflow.request.core.service.RequestQueryService;

@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@RequiredArgsConstructor
public class Cca3CarbonConversionFactorService {
	
	private final RequestQueryService requestQueryService;
	private final UnderlyingAgreementQueryService underlyingAgreementQueryService;
	
	public Map<Long, List<String>> findEligibleAccounts() {
		// Find active accounts that participate in CCA3, with their facilities
		Map<Long, List<String>> accountFacilityMap = underlyingAgreementQueryService
	    		.getAccountsWithFacilitiesFromActiveUnderlyingAgreements(SchemeVersion.CCA_3);
		
	    // Filter accounts that have in progress variation or cca3 migration
	    Set<Long> notEligibleAccounts = new HashSet<>(getNotEligibleAccounts());
	    accountFacilityMap.keySet().removeIf(notEligibleAccounts::contains);
	    
		return accountFacilityMap;
	}
	
	private Set<Long> getNotEligibleAccounts() {
		// Find accounts in variation requests
		Set<Long> variationAccountIds = requestQueryService.findRequestsByRequestTypeAndResourceTypeAndResourceId(
        		CcaRequestType.UNDERLYING_AGREEMENT_VARIATION, ResourceType.CA, CompetentAuthorityEnum.ENGLAND.name()).stream()
				.filter(req -> RequestStatuses.IN_PROGRESS.equals(req.getStatus()))
				.map(Request::getAccountId)
				.collect(Collectors.toSet());
		
		// Find accounts in migration requests
		Set<Long> migrationAccountIds = requestQueryService.findRequestsByRequestTypeAndResourceTypeAndResourceId(
        		CcaRequestType.CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING, ResourceType.CA, CompetentAuthorityEnum.ENGLAND.name()).stream()
				.filter(req -> RequestStatuses.IN_PROGRESS.equals(req.getStatus()))
				.map(Request::getAccountId)
				.collect(Collectors.toSet());
		
		Set<Long> result = new HashSet<>(variationAccountIds);
		result.addAll(migrationAccountIds);
		return result;
	}
	
	@Transactional
	public void processAccount(List<String> allErrors, Map<String, BigDecimal> carbonConversionFactorMap, Long accountId, List<String> facilityBusinessIds) {
		Map<String, BigDecimal> applicableFacilities = facilityBusinessIds.stream()
		        .filter(carbonConversionFactorMap::containsKey)
		        .collect(Collectors.toMap(
		                id -> id,
		                carbonConversionFactorMap::get
		        ));
	
		if (!applicableFacilities.isEmpty()) {
			updateConversionFactor(accountId, applicableFacilities, allErrors);
		}
	}

	public void updateConversionFactor(Long accountId, Map<String, BigDecimal> facilityCarbonConversionFactorMap, List<String> errors) {
		try {
			Set<Facility> facilities = underlyingAgreementQueryService.getUnderlyingAgreementContainerByAccountId(accountId)
					.getUnderlyingAgreement()
					.getFacilities();
			
			facilities.forEach(facility -> {
			    BigDecimal factor = facilityCarbonConversionFactorMap.get(facility.getFacilityItem().getFacilityId());

			    if (factor != null) {
			        facility.getFacilityItem()
			                .getCca3BaselineAndTargets()
			                .getBaselineData()
			                .setEnergyCarbonFactor(factor);
			    }
			});
        } catch (Exception e) {
            errors.add(String.format("Error updating facilities %s for account with ID %d: %s",
            		facilityCarbonConversionFactorMap.keySet(), accountId, e.getMessage()));
        }
    }

	public <T> void findEligibleAndProvidedFacilitiesDiff(List<String> allErrors, Map<Long, List<String>> eligibleMap, Map<String, T> providedMap) {
		Set<String> userProvidedFacilityBusinessIds = providedMap.keySet();
	    Set<String> allAccountFacilityBusinessIds = eligibleMap.values()
	            .stream()
	            .flatMap(List::stream)
	            .collect(Collectors.toSet());
	    
	    Set<String> eligibleNotProvidedFacilityBusinessIds = new HashSet<>(allAccountFacilityBusinessIds);
	    eligibleNotProvidedFacilityBusinessIds.removeAll(userProvidedFacilityBusinessIds);
	
	    Set<String> providedNotEligibleFacilityBusinessIds = new HashSet<>(userProvidedFacilityBusinessIds);
	    providedNotEligibleFacilityBusinessIds.removeAll(allAccountFacilityBusinessIds);
	    
	    if (!CollectionUtils.isEmpty(eligibleNotProvidedFacilityBusinessIds)) {
	    	allErrors.add("Eligible facilities that were not provided in the migration file: " + 
	    			String.join(", ", eligibleNotProvidedFacilityBusinessIds));
	    }
	    
	    if (!CollectionUtils.isEmpty(providedNotEligibleFacilityBusinessIds)) {
	    	allErrors.add("Facilities provided in the migration file that are not eligible for migration: " + 
	    			String.join(", ", providedNotEligibleFacilityBusinessIds));
	    }
	}

}
