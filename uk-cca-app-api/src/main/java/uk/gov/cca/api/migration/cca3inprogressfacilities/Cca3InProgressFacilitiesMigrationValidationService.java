package uk.gov.cca.api.migration.cca3inprogressfacilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.SetUtils;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.migration.MigrationEndpoint;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityItem;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.domain.UnderlyingAgreementReviewRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@RequiredArgsConstructor
public class Cca3InProgressFacilitiesMigrationValidationService {
    
    private static final String VALIDATION_ERROR_PREFIX = "Validation Error";
    private static final String UNA_REQUEST_SUFFIX = "-UNA";
    
    private final DataValidator<Cca3InProgressFacilityVO> validator;
    
	public void validateData(List<Cca3InProgressFacilityVO> facilitiesVOList, List<String> errors) {
		facilitiesVOList.forEach(facilityVO -> {
			validator.validate(facilityVO)
					.map(violation -> Arrays.stream(violation.getData()).map(data -> String
							.format("Row: %d | Validation Error: %s", facilityVO.getRowNumber(), data.toString()))
							.toList())
					.ifPresent(errors::addAll);
		});
	}
    
    public List<String> validate(List<Cca3InProgressFacilityVO> facilitiesVOList, List<RequestTask> unaReviewRequestTasks) {
    	Map<String, FacilityItem> facilitiesFromPayloads = unaReviewRequestTasks.stream()
    			.map(RequestTask::getPayload)
				.map(UnderlyingAgreementReviewRequestTaskPayload.class::cast)
                .map(UnderlyingAgreementReviewRequestTaskPayload::getUnderlyingAgreement)
                .map(UnderlyingAgreementPayload::getUnderlyingAgreement)
                .map(UnderlyingAgreement::getFacilities)
                .flatMap(Set::stream)
                .map(Facility::getFacilityItem)
                .collect(Collectors.toMap(FacilityItem::getFacilityId, f -> f));
    	
		Map<String, String> facilityAccountMapFromPayloads = unaReviewRequestTasks.stream().flatMap(
				task -> ((UnderlyingAgreementPayload) ((UnderlyingAgreementReviewRequestTaskPayload) task.getPayload())
						.getUnderlyingAgreement())
						.getUnderlyingAgreement().getFacilities().stream()
						.map(f -> Map.entry(f.getFacilityItem().getFacilityId(),
								task.getRequest().getId().replace(UNA_REQUEST_SUFFIX, ""))))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a));
    	
    	List<String> errors = new ArrayList<>();
    	validateFacilitiesLists(facilitiesVOList, facilitiesFromPayloads.values(), errors);
    	validatePayloadMatches(facilitiesVOList, facilitiesFromPayloads, facilityAccountMapFromPayloads, errors);
    	
    	return errors;
    }

    private static String validationError(String message, Object... args) {
        return String.format("%s : %s", 
                VALIDATION_ERROR_PREFIX, 
                String.format(message, args));
    }
    
    private static void validateFacilitiesLists(Collection<Cca3InProgressFacilityVO> facilitiesVOList, Collection<FacilityItem> facilitiesFromPayloads, List<String> errors) {
        Set<String> csvFacilityIds = facilitiesVOList.stream()
                .map(Cca3InProgressFacilityVO::getFacilityId)
                .collect(Collectors.toSet());

        Set<String> payloadFacilityIds = facilitiesFromPayloads.stream()
                .map(FacilityItem::getFacilityId)
                .collect(Collectors.toSet());

        // Check facilities from CSV exist in payloads
        SetUtils.difference(csvFacilityIds, payloadFacilityIds)
                .forEach(facilityId ->
                        errors.add(validationError("Facility %s from CSV not found in any in-progress UnA", facilityId)));

        // Check facilities from payloads exist in CSV
        SetUtils.difference(payloadFacilityIds, csvFacilityIds)
                .forEach(facilityId ->
                        errors.add(validationError("Facility %s from in-progress UnA not found in CSV", facilityId)));
    }
    
	private void validatePayloadMatches(List<Cca3InProgressFacilityVO> facilitiesVOList,
			Map<String, FacilityItem> facilitiesFromPayloads, Map<String, String> facilityAccountMapFromPayloads, List<String> errors) {

		facilitiesVOList.forEach(facilityVO -> {			
			validateAccountMatches(facilityVO, facilityAccountMapFromPayloads.get(facilityVO.getFacilityId()), errors);
			validateFacilityItemMatches(facilityVO, facilitiesFromPayloads.get(facilityVO.getFacilityId()), errors);
		});
	}
    
    private void validateAccountMatches(Cca3InProgressFacilityVO facilityVO, String accountBusinessId, List<String> errors) {
		if (accountBusinessId == null) {
			return;
		}
		
		if (!accountBusinessId.equals(facilityVO.getTargetUnitId())) {
			errors.add(validationError("Row: %d | Mismatch for Facility %s: Target Unit ID %s do not match payload Target Unit ID %s",
					facilityVO.getRowNumber(),facilityVO.getFacilityId(),facilityVO.getTargetUnitId(),accountBusinessId));
		}

	}

	private void validateFacilityItemMatches(Cca3InProgressFacilityVO facilityVO, FacilityItem facilityItem, List<String> errors) {		
		if (facilityItem == null || facilityVO.getApplicationReason() == null) {
				return;
		}
		
		if(!facilityItem.getFacilityDetails().getApplicationReason().equals(facilityVO.getApplicationReason())) {
			errors.add(validationError("Row: %d | Mismatch for Facility %s: Application reason %s do not match payload application reason %s",
					facilityVO.getRowNumber(), facilityVO.getFacilityId(), facilityVO.getApplicationReason(), facilityItem.getFacilityDetails().getApplicationReason()));
		}

		// Rule: Matching Scheme Versions
		Set<SchemeVersion> payloadSchemes = facilityItem.getFacilityDetails().getParticipatingSchemeVersions();
		Set<SchemeVersion> csvSchemes = facilityVO.getParticipatingSchemeVersions();
		if (!payloadSchemes.equals(csvSchemes)) {
			errors.add(validationError("Row: %d | Mismatch for Facility %s: CSV schemes %s do not match payload schemes %s",
					facilityVO.getRowNumber(), facilityVO.getFacilityId(), csvSchemes, payloadSchemes));
		}

	}
    
}
