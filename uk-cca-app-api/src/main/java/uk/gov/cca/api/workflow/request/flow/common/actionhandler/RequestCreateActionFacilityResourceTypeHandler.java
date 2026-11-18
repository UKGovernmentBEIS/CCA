package uk.gov.cca.api.workflow.request.flow.common.actionhandler;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.facility.service.FacilityDataQueryService;
import uk.gov.cca.api.workflow.request.flow.common.service.RequestCreateByFacilityValidator;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.workflow.request.core.domain.RequestCreateActionPayload;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestCreateActionResourceTypeHandler;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.netz.api.workflow.request.flow.common.service.RequestCreateByRequestValidator;

@Service
@RequiredArgsConstructor
public class RequestCreateActionFacilityResourceTypeHandler <T extends RequestCreateActionPayload> implements RequestCreateActionResourceTypeHandler<T> {

	private final List<RequestCreateByFacilityValidator> requestCreateByFacilityValidators;
    private final List<RequestCreateByRequestValidator<T>> requestCreateByFacilityRelatedRequestValidators;
	private final List<RequestFacilityCreateActionHandler<T>> requestFacilityCreateActionHandler;
	private final FacilityDataQueryService facilityDataQueryService;
	
	@Override
    public String process(String resourceId, String requestType, T payload, AppUser appUser) {
        Long facilityId = Long.parseLong(resourceId);

        // Lock the facility
        facilityDataQueryService.exclusiveLockFacility(facilityId);

        // Get validation results
        List<RequestCreateValidationResult> validationResults = new ArrayList<>();

        validationResults.add(
                requestCreateByFacilityValidators
                        .stream()
                        .filter(requestCreateValidator -> requestCreateValidator.getRequestType().equals(requestType))
                        .findFirst()
                        .map(requestCreateByFacilityValidator ->
                                requestCreateByFacilityValidator.validateAction(facilityId))
                        .orElse(RequestCreateValidationResult.builder().valid(true).isAvailable(true).build())
        );
        validationResults.add(
                requestCreateByFacilityRelatedRequestValidators
                        .stream()
                        .filter(requestCreateValidator -> requestCreateValidator.getRequestType().equals(requestType))
                        .findFirst()
                        .map(requestCreateByFacilityRelatedRequestValidator ->
                                requestCreateByFacilityRelatedRequestValidator.validateAction(facilityId, payload))
                        .orElse(RequestCreateValidationResult.builder().valid(true).isAvailable(true).build())
        );

        boolean isValid = validationResults.stream().allMatch(RequestCreateValidationResult::isValid);
        boolean isAvailable = validationResults.stream().allMatch(RequestCreateValidationResult::isAvailable);
        if (!isValid || !isAvailable) {
            throw new BusinessException(ErrorCode.REQUEST_CREATE_ACTION_NOT_ALLOWED, validationResults);
        }
        
        return requestFacilityCreateActionHandler.stream()
                .filter(handler -> handler.getRequestType().equals(requestType))
                .findFirst()
                .map(handler -> handler.process(facilityId, payload, appUser))
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, requestType));
    }

    @Override
    public String getResourceType() {
        return CcaResourceType.FACILITY;
    }
}
