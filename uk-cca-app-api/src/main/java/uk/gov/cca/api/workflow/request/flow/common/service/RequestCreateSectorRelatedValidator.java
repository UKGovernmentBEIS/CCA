package uk.gov.cca.api.workflow.request.flow.common.service;

import java.util.Set;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;

@RequiredArgsConstructor
public abstract class RequestCreateSectorRelatedValidator implements RequestCreateBySectorAssociationValidator {

	private final CcaRequestCreateValidatorService ccaRequestCreateValidatorService;

    @Override
    public RequestCreateValidationResult validateAction(final Long sectorId) {
        return ccaRequestCreateValidatorService
                .validate(sectorId, CcaResourceType.SECTOR_ASSOCIATION, this.getMutuallyExclusiveRequests());
    }

    protected abstract Set<String> getMutuallyExclusiveRequests();
}
