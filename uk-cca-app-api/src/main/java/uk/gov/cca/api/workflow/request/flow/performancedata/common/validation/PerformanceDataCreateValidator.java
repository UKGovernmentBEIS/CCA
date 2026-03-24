package uk.gov.cca.api.workflow.request.flow.performancedata.common.validation;

import lombok.RequiredArgsConstructor;

import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;
import uk.gov.cca.api.workflow.request.flow.common.service.CcaRequestCreateValidatorService;
import uk.gov.cca.api.workflow.request.flow.common.service.RequestCreateBySectorAssociationValidator;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
public abstract class PerformanceDataCreateValidator implements RequestCreateBySectorAssociationValidator {

    private final TargetPeriodService targetPeriodService;
    private final CcaRequestCreateValidatorService ccaRequestCreateValidatorService;

    @Override
    public RequestCreateValidationResult validateAction(Long sectorAssociationId) {
        final TargetPeriodDetailsDTO targetPeriodDetails = targetPeriodService
                .getTargetPeriodDetailsByTargetPeriodType(TargetPeriodType.TP6);
        final Optional<LocalDate> reportingEndDate = targetPeriodDetails.getTargetPeriodYearsContainer()
                .getTargetPeriodReportingEndDate();

        if(reportingEndDate.isPresent() && LocalDate.now().isAfter(reportingEndDate.get())) {
            return RequestCreateValidationResult.builder().isAvailable(false).build();
        }

        return ccaRequestCreateValidatorService.validate(
                sectorAssociationId, CcaResourceType.SECTOR_ASSOCIATION, this.getMutuallyExclusiveRequests());
    }

    protected abstract Set<String> getMutuallyExclusiveRequests();
}
