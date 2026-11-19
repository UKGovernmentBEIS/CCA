package uk.gov.cca.api.targetperiodreporting.emissionstradingschemedata.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.targetperiodreporting.emissionstradingschemedata.domain.EmissionsTradingSchemeDataContainer;
import uk.gov.cca.api.targetperiodreporting.emissionstradingschemedata.domain.EmissionsTradingSchemeDataEntity;
import uk.gov.cca.api.targetperiodreporting.emissionstradingschemedata.domain.dto.EmissionsTradingSchemeDataReportInfoDTO;
import uk.gov.cca.api.targetperiodreporting.emissionstradingschemedata.repository.EmissionsTradingSchemeDataEntityRepository;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmissionsTradingSchemeDataQueryServiceTest {

    @InjectMocks
    private EmissionsTradingSchemeDataQueryService service;

    @Mock
    private EmissionsTradingSchemeDataEntityRepository emissionsTradingSchemeDataEntityRepository;

    @Test
    void getEmissionsTradingSchemeDataReportInfo() {

        final Year targetPeriodYear = Year.of(2026);
        final int reportVersion = 3;
        final LocalDateTime submissionDate = LocalDateTime.of(2027, 6, 15, 10, 30, 0);
        final Long facilityId = 42L;

        final EmissionsTradingSchemeDataEntity entity = EmissionsTradingSchemeDataEntity.builder()
                .id(1L)
                .facilityId(facilityId)
                .data(EmissionsTradingSchemeDataContainer.builder()
                        .measurementType(MeasurementType.CARBON_KG)
                        .uketsId("UK0012345")
                        .isUltraSmallEmitter(false)
                        .build())
                .targetPeriodYear(targetPeriodYear)
                .reportVersion(reportVersion)
                .submissionDate(submissionDate)
                .build();

        final EmissionsTradingSchemeDataReportInfoDTO expected = EmissionsTradingSchemeDataReportInfoDTO.builder()
                .targetPeriodYear(targetPeriodYear)
                .reportVersion(reportVersion)
                .submissionDate(submissionDate)
                .build();

        when(emissionsTradingSchemeDataEntityRepository.findByFacilityIdAndTargetPeriodYear(facilityId, targetPeriodYear))
                .thenReturn(Optional.of(entity));

        // invoke
        EmissionsTradingSchemeDataReportInfoDTO actual = service.getEmissionsTradingSchemeDataReportInfo(facilityId, targetPeriodYear);

        // verify
        assertThat(actual).isEqualTo(expected);
        verify(emissionsTradingSchemeDataEntityRepository, times(1))
                .findByFacilityIdAndTargetPeriodYear(facilityId, targetPeriodYear);
    }

    @Test
    void getEmissionsTradingSchemeDataReportInfo_empty() {

        final Year targetPeriodYear = Year.of(2026);
        final int reportVersion = 3;
        final LocalDateTime submissionDate = LocalDateTime.of(2027, 6, 15, 10, 30, 0);
        final Long facilityId = 42L;

        when(emissionsTradingSchemeDataEntityRepository.findByFacilityIdAndTargetPeriodYear(facilityId, targetPeriodYear))
                .thenReturn(Optional.empty());

        // invoke
        EmissionsTradingSchemeDataReportInfoDTO actual = service.getEmissionsTradingSchemeDataReportInfo(facilityId, targetPeriodYear);

        // verify
        assertThat(actual).isNull();
        verify(emissionsTradingSchemeDataEntityRepository, times(1))
                .findByFacilityIdAndTargetPeriodYear(facilityId, targetPeriodYear);
    }

}
