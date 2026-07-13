package uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.submit.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.submit.domain.PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PerformanceDataFacilityDigitalFormRefreshValidator {

    private final TargetPeriodService targetPeriodService;
    private final PerformanceDataFacilityDigitalFormSubmitValidator performanceDataFacilityDigitalFormSubmitValidator;

    public void validate(final RequestTask requestTask) {
        final PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload taskPayload =
                (PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload) requestTask.getPayload();
        final List<TargetPeriodDetailsDTO> targetPeriods = targetPeriodService.getTargetPeriodDetailsByTargetPeriodTypes(
                Set.of(TargetPeriodType.TP7, TargetPeriodType.TP8, TargetPeriodType.TP9));

        // Validate Facility eligibility
        performanceDataFacilityDigitalFormSubmitValidator.validateFacilityEligibility(taskPayload, targetPeriods);
    }
}
