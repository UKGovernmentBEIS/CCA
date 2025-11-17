package uk.gov.cca.api.workflow.request.flow.common.service;

import java.util.Set;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;

@RequiredArgsConstructor
public abstract class RequestCreateFacilityRelatedValidator implements RequestCreateByFacilityValidator {

	private final CcaRequestCreateValidatorService ccaRequestCreateValidatorService;

    @Override
    public RequestCreateValidationResult validateAction(final Long facilityId) {
        return ccaRequestCreateValidatorService
                .validate(facilityId, CcaResourceType.FACILITY, this.getMutuallyExclusiveRequests());
    }

    protected abstract Set<String> getMutuallyExclusiveRequests();

}
