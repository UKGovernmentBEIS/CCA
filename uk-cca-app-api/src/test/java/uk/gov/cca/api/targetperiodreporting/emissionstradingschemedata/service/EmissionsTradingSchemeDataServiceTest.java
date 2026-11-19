package uk.gov.cca.api.targetperiodreporting.emissionstradingschemedata.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.targetperiodreporting.emissionstradingschemedata.domain.EmissionsTradingSchemeDataContainer;
import uk.gov.cca.api.targetperiodreporting.emissionstradingschemedata.domain.EmissionsTradingSchemeDataEntity;
import uk.gov.cca.api.targetperiodreporting.emissionstradingschemedata.repository.EmissionsTradingSchemeDataEntityRepository;

import java.time.Year;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmissionsTradingSchemeDataServiceTest {

    @InjectMocks
    private EmissionsTradingSchemeDataService emissionsTradingSchemeDataService;

    @Mock
    private EmissionsTradingSchemeDataEntityRepository emissionsTradingSchemeDataEntityRepository;

    @Test
    void submitEmissionsTradingSchemeData() {
        final Long facilityId = 1L;
        final Year targetPeriodYear = Year.of(2026);
        final EmissionsTradingSchemeDataContainer newData = EmissionsTradingSchemeDataContainer.builder()
                .measurementType(MeasurementType.ENERGY_KWH)
                .build();

        EmissionsTradingSchemeDataEntity entity = EmissionsTradingSchemeDataEntity.builder()
                .id(1L)
                .data(EmissionsTradingSchemeDataContainer.builder()
                        .measurementType(MeasurementType.ENERGY_GJ)
                        .build())
                .facilityId(facilityId)
                .targetPeriodYear(targetPeriodYear)
                .reportVersion(1)
                .build();

        when(emissionsTradingSchemeDataEntityRepository.findByFacilityIdAndTargetPeriodYear(facilityId, targetPeriodYear))
                .thenReturn(Optional.ofNullable(entity));
        when(emissionsTradingSchemeDataEntityRepository.save(any(EmissionsTradingSchemeDataEntity.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        // invoke
        int result = emissionsTradingSchemeDataService.submitEmissionsTradingSchemeData(newData, facilityId, targetPeriodYear);

        // verify
        assertThat(result).isEqualTo(2);
        assertThat(entity).isNotNull();
        assertThat(entity.getData()).isEqualTo(newData);
        assertThat(entity.getSubmissionDate()).isNotNull();
        assertThat(entity.getReportVersion()).isEqualTo(2);
        verify(emissionsTradingSchemeDataEntityRepository, times(1))
                .findByFacilityIdAndTargetPeriodYear(facilityId, targetPeriodYear);
        verify(emissionsTradingSchemeDataEntityRepository, times(1))
                .save(any(EmissionsTradingSchemeDataEntity.class));
    }

    @Test
    void submitEmissionsTradingSchemeData_new_entity() {
        final Long facilityId = 1L;
        final Year targetPeriodYear = Year.of(2026);
        final EmissionsTradingSchemeDataContainer dataContainer = EmissionsTradingSchemeDataContainer.builder()
                .measurementType(MeasurementType.ENERGY_KWH)
                .build();

        final EmissionsTradingSchemeDataEntity entity = EmissionsTradingSchemeDataEntity.builder()
                .data(dataContainer)
                .facilityId(facilityId)
                .targetPeriodYear(targetPeriodYear)
                .reportVersion(1)
                .build();

        when(emissionsTradingSchemeDataEntityRepository.findByFacilityIdAndTargetPeriodYear(facilityId, targetPeriodYear))
                .thenReturn(Optional.empty());
        when(emissionsTradingSchemeDataEntityRepository.save(entity))
                .thenAnswer(inv -> inv.getArgument(0));

        // invoke
        int result = emissionsTradingSchemeDataService.submitEmissionsTradingSchemeData(dataContainer, facilityId, targetPeriodYear);

        // verify
        assertThat(result).isEqualTo(1);
        verify(emissionsTradingSchemeDataEntityRepository, times(1))
                .findByFacilityIdAndTargetPeriodYear(facilityId, targetPeriodYear);
        verify(emissionsTradingSchemeDataEntityRepository, times(1))
                .save(entity);
    }

    @Test
    void getEmissionsTradingSchemeData() {
        final Long facilityId = 1L;
        final Year targetPeriodYear = Year.of(2026);
        final EmissionsTradingSchemeDataContainer dataContainer = EmissionsTradingSchemeDataContainer.builder()
                .measurementType(MeasurementType.ENERGY_KWH)
                .build();

        final EmissionsTradingSchemeDataEntity entity = EmissionsTradingSchemeDataEntity.builder()
                .data(dataContainer)
                .facilityId(facilityId)
                .targetPeriodYear(targetPeriodYear)
                .reportVersion(1)
                .build();

        when(emissionsTradingSchemeDataEntityRepository.findByFacilityIdAndTargetPeriodYear(facilityId, targetPeriodYear))
                .thenReturn(Optional.ofNullable(entity));

        // invoke
        Optional<EmissionsTradingSchemeDataContainer> result = emissionsTradingSchemeDataService.getEmissionsTradingSchemeData(facilityId, targetPeriodYear);

        // verify
        assertThat(result).isPresent();
        verify(emissionsTradingSchemeDataEntityRepository, times(1))
                .findByFacilityIdAndTargetPeriodYear(facilityId, targetPeriodYear);
    }
}
