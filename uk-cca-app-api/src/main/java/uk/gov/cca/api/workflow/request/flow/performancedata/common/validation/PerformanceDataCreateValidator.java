package uk.gov.cca.api.workflow.request.flow.performancedata.common.validation;

import lombok.RequiredArgsConstructor;

import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.workflow.request.flow.common.service.CcaRequestCreateValidatorService;
import uk.gov.cca.api.workflow.request.flow.common.service.RequestCreateBySectorAssociationValidator;
import uk.gov.cca.api.workflow.request.flow.common.validation.performancedata.PerformanceDataCreateSchemeValidator;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;

import java.time.LocalDate;
import java.util.Set;

@RequiredArgsConstructor
public abstract class PerformanceDataCreateValidator implements RequestCreateBySectorAssociationValidator {

    private final PerformanceDataCreateSchemeValidator performanceDataCreateSchemeValidator;
    private final CcaRequestCreateValidatorService ccaRequestCreateValidatorService;

    @Override
    public RequestCreateValidationResult validateAction(Long sectorAssociationId) {
        // Account performance data only available for CCA2 Scheme
        if(!performanceDataCreateSchemeValidator.isAvailableForScheme(SchemeVersion.CCA_2, LocalDate.now())) {
            return RequestCreateValidationResult.builder().isAvailable(false).build();
        }

        return ccaRequestCreateValidatorService.validate(
                sectorAssociationId, CcaResourceType.SECTOR_ASSOCIATION, this.getMutuallyExclusiveRequests());
    }

    protected abstract Set<String> getMutuallyExclusiveRequests();
}
