package uk.gov.cca.api.workflow.request.core.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.authorization.ccaauth.rules.services.resource.FacilityRequestAuthorizationResourceService;
import uk.gov.cca.api.workflow.request.flow.common.service.RequestCreateByFacilityValidator;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.core.service.AvailableRequestResourceTypeHandler;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;

@Service
@RequiredArgsConstructor
public class AvailableRequestFacilityHandler implements AvailableRequestResourceTypeHandler {

	private final FacilityRequestAuthorizationResourceService facilityRequestAuthorizationResourceService;
	private final List<RequestCreateByFacilityValidator> requestCreateByFacilityValidators;

	@Override
	public Map<String, RequestCreateValidationResult> getAvailableRequestsForResource(
		final String resourceId, final Set<String> requestTypes, final AppUser appUser) {
		final Long facilityId = Long.parseLong(resourceId);
		final Set<String> actions = getAvailableCreateActions(facilityId, appUser, requestTypes);
		
		return actions.stream()
	            .collect(Collectors.toMap(
	                    requestType -> requestType,
	                    requestType -> getFacilityValidationResult(requestType, facilityId)))
	            .entrySet().stream()
	            .filter(a -> a.getValue().isAvailable())
	            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}
	
	private Set<String> getAvailableCreateActions(final Long facilityId, final AppUser appUser,
			final Set<String> availableCreateRequestTypes) {
	    return facilityRequestAuthorizationResourceService
	            .findRequestCreateActionsByFacilityId(appUser, facilityId).stream()
	            .filter(availableCreateRequestTypes::contains)
	            .collect(Collectors.toSet());
	}
	
	private RequestCreateValidationResult getFacilityValidationResult(String type, long facilityId) {
	    return requestCreateByFacilityValidators.stream()
	            .filter(validator -> validator.getRequestType().equals(type))
	            .findFirst()
	            .map(validator -> validator.validateAction(facilityId))
	            .orElse(RequestCreateValidationResult.builder().valid(true).build());
	}
	
	@Override
	public String getResourceType() {
		return CcaResourceType.FACILITY;
	}
}
