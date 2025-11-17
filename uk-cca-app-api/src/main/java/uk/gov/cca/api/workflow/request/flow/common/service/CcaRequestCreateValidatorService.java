package uk.gov.cca.api.workflow.request.flow.common.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestType;
import uk.gov.netz.api.workflow.request.core.service.RequestQueryService;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestCreateRequestTypeValidationResult;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;

@Service
@RequiredArgsConstructor
public class CcaRequestCreateValidatorService {

	private final RequestQueryService requestQueryService;
	
	public RequestCreateValidationResult validate(final Long resourceId, final String resourceType, Set<String> mutuallyExclusiveRequestsTypes) {
		final RequestCreateValidationResult validationResult = RequestCreateValidationResult.builder().valid(true)
				.build();
		
		final RequestCreateRequestTypeValidationResult validationConflictingRequestsTypesResult = validateConflictingRequestTypes(
				resourceId, resourceType, mutuallyExclusiveRequestsTypes);
		if (!validationConflictingRequestsTypesResult.isValid()) {
			validationResult.setValid(false);
			validationResult.setReportedRequestTypes(validationConflictingRequestsTypesResult.getReportedRequestTypes());
		}
	
		return validationResult;
	}
	
	public RequestCreateRequestTypeValidationResult validateConflictingRequestTypes(final Long resourceId,
            String resourceType, Set<String> mutuallyExclusiveRequestsTypes) {
		final RequestCreateRequestTypeValidationResult validationResult = RequestCreateRequestTypeValidationResult.builder().valid(true)
				.build();
		
		if (!mutuallyExclusiveRequestsTypes.isEmpty()) {
			final List<Request> inProgressRequests = requestQueryService.findInProgressRequestsByResource(resourceId, resourceType);
			final Set<String> conflictingRequests = inProgressRequests.stream().map(Request::getType).map(RequestType::getCode)
					.filter(mutuallyExclusiveRequestsTypes::contains).collect(Collectors.toSet());
		
			if (!conflictingRequests.isEmpty()) {
				validationResult.setValid(false);
				validationResult.setReportedRequestTypes(conflictingRequests);
			}
		}
		
		return validationResult;
	}
}
