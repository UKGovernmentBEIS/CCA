package uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.submit.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.TargetPeriodService;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.submit.domain.PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerformanceDataFacilityDigitalFormRefreshValidatorTest {

    @InjectMocks
    private PerformanceDataFacilityDigitalFormRefreshValidator validator;

    @Mock
    private TargetPeriodService targetPeriodService;

    @Mock
    private PerformanceDataFacilityDigitalFormSubmitValidator performanceDataFacilityDigitalFormSubmitValidator;

    @Test
    void validate() {
        final PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload taskPayload =
                PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload.builder()
                        .targetPeriodType(TargetPeriodType.TP7)
                        .build();
        final RequestTask requestTask = RequestTask.builder().payload(taskPayload).build();

        final List<TargetPeriodDetailsDTO> targetPeriods = List.of(
                TargetPeriodDetailsDTO.builder().businessId(TargetPeriodType.TP7).build()
        );

        when(targetPeriodService.getTargetPeriodDetailsByTargetPeriodTypes(Set.of(TargetPeriodType.TP7, TargetPeriodType.TP8, TargetPeriodType.TP9)))
                .thenReturn(targetPeriods);

        // Invoke
        validator.validate(requestTask);

        // Verify
        verify(targetPeriodService, times(1))
                .getTargetPeriodDetailsByTargetPeriodTypes(Set.of(TargetPeriodType.TP7, TargetPeriodType.TP8, TargetPeriodType.TP9));
        verify(performanceDataFacilityDigitalFormSubmitValidator, times(1))
                .validateFacilityEligibility(taskPayload, targetPeriods);
    }
}
