package uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.common.service.CcaRequestCreateValidatorService;
import uk.gov.cca.api.workflow.request.flow.common.service.RequestCreateBySectorAssociationValidator;
import uk.gov.cca.api.workflow.request.flow.common.validation.performancedata.PerformanceDataCreateSchemeValidator;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;

import java.time.LocalDate;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PerformanceDataFacilityDataUploadCreateValidator implements RequestCreateBySectorAssociationValidator {

    private final PerformanceDataCreateSchemeValidator performanceDataCreateSchemeValidator;
    private final CcaRequestCreateValidatorService ccaRequestCreateValidatorService;

    @Override
    public RequestCreateValidationResult validateAction(Long sectorAssociationId) {
        // CSV form only available for CCA3 Scheme
        if(!performanceDataCreateSchemeValidator.isAvailableForScheme(SchemeVersion.CCA_3, LocalDate.now())) {
            return RequestCreateValidationResult.builder().valid(true).isAvailable(false).build();
        }

        return ccaRequestCreateValidatorService.validate(
                sectorAssociationId, CcaResourceType.SECTOR_ASSOCIATION, Set.of(CcaRequestType.PERFORMANCE_DATA_FACILITY_DATA_UPLOAD));
    }

    @Override
    public String getRequestType() {
        return CcaRequestType.PERFORMANCE_DATA_FACILITY_DATA_UPLOAD;
    }
}
