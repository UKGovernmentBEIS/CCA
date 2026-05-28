package uk.gov.cca.api.targetperiodreporting.performancedatafacility.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.targetperiodreporting.performancedatafacility.repository.PerformanceDataFacilityStatusRepository;

import java.time.Year;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PerformanceDataFacilityStatusQueryServiceTest {

    @InjectMocks
    private PerformanceDataFacilityStatusQueryService service;

    @Mock
    private PerformanceDataFacilityStatusRepository performanceDataFacilityStatusRepository;

    @Test
    void getPerformanceDataFacilityStatus() {
        final Long facilityId = 1L;
        final Year targetPeriodYear = Year.of(2018);

        // Invoke
        service.getPerformanceDataFacilityStatus(facilityId, targetPeriodYear);

        // Verify
        verify(performanceDataFacilityStatusRepository).findByFacilityIdAndTargetPeriodYear(facilityId, targetPeriodYear);
    }

    @Test
    void getLastUploadedPerformanceDataContainer() {
        final Long facilityId = 1L;
        final Year targetPeriodYear = Year.of(2018);

        // Invoke
        service.getLastUploadedPerformanceDataContainer(facilityId, targetPeriodYear);

        // Verify
        verify(performanceDataFacilityStatusRepository).findByFacilityIdAndTargetPeriodYear(facilityId, targetPeriodYear);
    }
}
