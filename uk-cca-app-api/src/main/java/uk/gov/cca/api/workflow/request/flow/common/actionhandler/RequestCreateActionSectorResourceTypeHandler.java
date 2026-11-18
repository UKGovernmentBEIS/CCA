package uk.gov.cca.api.workflow.request.flow.common.actionhandler;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationQueryService;
import uk.gov.cca.api.workflow.request.flow.common.service.RequestCreateBySectorAssociationValidator;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.workflow.request.core.domain.RequestCreateActionPayload;
import uk.gov.netz.api.workflow.request.flow.common.actionhandler.RequestCreateActionResourceTypeHandler;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;

@Service
@RequiredArgsConstructor
public class RequestCreateActionSectorResourceTypeHandler <T extends RequestCreateActionPayload> implements RequestCreateActionResourceTypeHandler<T> {

	private final List<RequestCreateBySectorAssociationValidator> requestCreateBySectorAssociationValidators;
	private final List<RequestSectorCreateActionHandler<T>> requestSectorCreateActionHandler;
	private final SectorAssociationQueryService sectorAssociationQueryService;
	
	@Override
    public String process(String resourceId, String requestType, T payload, AppUser appUser) {
        Long sectorId = Long.parseLong(resourceId);
        
        // Lock the sector association
        sectorAssociationQueryService.exclusiveLockSectorAssociation(sectorId);

        final RequestCreateValidationResult validationResult = requestCreateBySectorAssociationValidators
                .stream()
                .filter(requestCreateValidator -> requestCreateValidator.getRequestType().equals(requestType))
                .findFirst()
                .map(requestCreateBySectorAssociationValidator -> requestCreateBySectorAssociationValidator.validateAction(sectorId))
                .orElse(RequestCreateValidationResult.builder().valid(true).isAvailable(true).build());

        if (!validationResult.isValid() || !validationResult.isAvailable()) {
            throw new BusinessException(ErrorCode.REQUEST_CREATE_ACTION_NOT_ALLOWED, validationResult);
        }
        
        return requestSectorCreateActionHandler.stream()
                .filter(handler -> handler.getRequestType().equals(requestType))
                .findFirst()
                .map(handler -> handler.process(sectorId, payload, appUser))
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, requestType));
    }

    @Override
    public String getResourceType() {
        return CcaResourceType.SECTOR_ASSOCIATION;
    }
}
