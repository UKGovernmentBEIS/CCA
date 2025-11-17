package uk.gov.cca.api.workflow.request.core.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.authorization.ccaauth.rules.services.resource.SectorAssociationRequestAuthorizationResourceService;
import uk.gov.cca.api.workflow.request.flow.common.service.RequestCreateBySectorAssociationValidator;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.workflow.request.core.service.AvailableRequestResourceTypeHandler;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;

@Service
@RequiredArgsConstructor
public class AvailableRequestSectorAssociationHandler implements AvailableRequestResourceTypeHandler {
	
	private final SectorAssociationRequestAuthorizationResourceService sectorAssociationRequestAuthorizationResourceService;
	private final List<RequestCreateBySectorAssociationValidator> requestCreateBySectorAssociationValidators;

	@Override
	public Map<String, RequestCreateValidationResult> getAvailableRequestsForResource(
		final String resourceId, final Set<String> requestTypes, final AppUser appUser) {
		final Long sectorAssociationId = Long.parseLong(resourceId);
		final Set<String> actions = getAvailableCreateActions(sectorAssociationId, appUser, requestTypes);
		
		return actions.stream()
	            .collect(Collectors.toMap(
	                    requestType -> requestType,
	                    requestType -> getSectorValidationResult(requestType, sectorAssociationId)))
	            .entrySet().stream()
	            .filter(a -> a.getValue().isAvailable())
	            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}
	
	private Set<String> getAvailableCreateActions(final Long sectorAssociationId, final AppUser appUser,
			final Set<String> availableCreateRequestTypes) {
	    return sectorAssociationRequestAuthorizationResourceService
	            .findRequestCreateActionsBySectorAssociationId(appUser, sectorAssociationId).stream()
	            .filter(availableCreateRequestTypes::contains)
	            .collect(Collectors.toSet());
	}
	
	private RequestCreateValidationResult getSectorValidationResult(String type, long sectorAssociationId) {
	    return requestCreateBySectorAssociationValidators.stream()
	            .filter(validator -> validator.getRequestType().equals(type))
	            .findFirst()
	            .map(validator -> validator.validateAction(sectorAssociationId))
	            .orElse(RequestCreateValidationResult.builder().valid(true).build());
	}
	
	@Override
	public String getResourceType() {
		return CcaResourceType.SECTOR_ASSOCIATION;
	}

}
