package uk.gov.cca.api.targetperiodreporting.facilitycertification.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.facility.service.FacilityDataQueryService;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.FacilityCertification;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.FacilityCertificationStatus;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.dto.FacilityCertificationStatusUpdateDTO;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.repository.FacilityCertificationRepository;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.validation.FacilityCertificationValidationService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FacilityCertificationServiceTest {

    @InjectMocks
    private FacilityCertificationService facilityCertificationService;

    @Mock
    private FacilityCertificationRepository facilityCertificationRepository;

    @Mock
    private FacilityDataQueryService facilityDataQueryService;

    @Mock
    private FacilityCertificationValidationService validationService;


    @Test
    void certifyFacilities() {
        final Set<Long> facilityIds = Set.of(1L, 2L);
        final long certificationPeriodId = 1L;
        final LocalDate startDate = LocalDate.of(2025, 7, 1);

        final FacilityCertification facilityCertificationEntity = FacilityCertification.builder()
                .facilityId(1L)
                .build();
        final FacilityCertification newFacilityCertificationEntity = FacilityCertification.builder()
                .facilityId(2L)
                .certificationPeriodId(certificationPeriodId)
                .certificationStatus(FacilityCertificationStatus.CERTIFIED)
                .startDate(startDate)
                .build();

        when(facilityCertificationRepository.findAllByFacilityIdInAndCertificationPeriodId(facilityIds, certificationPeriodId))
                .thenReturn(List.of(facilityCertificationEntity));

        // Invoke
        facilityCertificationService.certifyFacilities(facilityIds, certificationPeriodId, startDate);

        // Verify
        assertThat(facilityCertificationEntity.getCertificationStatus()).isEqualTo(FacilityCertificationStatus.CERTIFIED);
        assertThat(facilityCertificationEntity.getStartDate()).isEqualTo(startDate);
        verify(facilityCertificationRepository, times(1))
                .findAllByFacilityIdInAndCertificationPeriodId(facilityIds, certificationPeriodId);
        verify(facilityCertificationRepository, times(1)).saveAll(List.of(newFacilityCertificationEntity));
    }

    @Test
    void certifyFacilities_not_exists_in_db() {
        final Set<Long> facilityIds = Set.of(1L, 2L);
        final long certificationPeriodId = 1L;
        final LocalDate startDate = LocalDate.of(2025, 7, 1);

        when(facilityCertificationRepository.findAllByFacilityIdInAndCertificationPeriodId(facilityIds, certificationPeriodId))
                .thenReturn(List.of());

        // Invoke
        facilityCertificationService.certifyFacilities(facilityIds, certificationPeriodId, startDate);

        // Verify
        verify(facilityCertificationRepository, times(1))
                .findAllByFacilityIdInAndCertificationPeriodId(facilityIds, certificationPeriodId);
        verify(facilityCertificationRepository, times(1)).saveAll(anyList());
    }

    @Test
    void updateOrCreateFacilityCertificationStatus_update() {

        final Long facilityId = 99L;
        final FacilityCertificationStatusUpdateDTO facilityCertificationStatusUpdateDTO = FacilityCertificationStatusUpdateDTO.builder()
                .certificationStatus(FacilityCertificationStatus.CERTIFIED)
                .certificationPeriodId(1L)
                .startDate(LocalDate.of(2025, 6, 1))
                .build();

        FacilityCertification facilityCertification = FacilityCertification.builder()
                .id(666L)
                .facilityId(facilityId)
                .certificationStatus(FacilityCertificationStatus.DECERTIFIED)
                .startDate(null)
                .build();


        when(facilityCertificationRepository
                .findByFacilityIdAndCertificationPeriodId(facilityId, facilityCertificationStatusUpdateDTO.getCertificationPeriodId()))
                .thenReturn(Optional.of(facilityCertification));

        facilityCertificationService.updateOrCreateFacilityCertificationStatus(facilityId, facilityCertificationStatusUpdateDTO);

        verify(facilityCertificationRepository, times(1))
                .findByFacilityIdAndCertificationPeriodId(facilityId, 1L);
        assertEquals(facilityCertification.getCertificationStatus(), facilityCertificationStatusUpdateDTO.getCertificationStatus());
    }

    @Test
    void updateOrCreateFacilityCertificationStatus_create() {
        final Long facilityId = 99L;
        final FacilityCertificationStatusUpdateDTO facilityCertificationStatusUpdateDTO = FacilityCertificationStatusUpdateDTO.builder()
                .certificationStatus(FacilityCertificationStatus.CERTIFIED)
                .certificationPeriodId(1L)
                .startDate(LocalDate.of(2025, 6, 1))
                .build();

        when(facilityCertificationRepository
                .findByFacilityIdAndCertificationPeriodId(facilityId, facilityCertificationStatusUpdateDTO.getCertificationPeriodId()))
                .thenReturn(Optional.empty());


        facilityCertificationService.updateOrCreateFacilityCertificationStatus(facilityId, facilityCertificationStatusUpdateDTO);

        verify(facilityCertificationRepository, times(1))
                .findByFacilityIdAndCertificationPeriodId(facilityId, 1L);
        verify(facilityCertificationRepository, times(1))
                .save(any(FacilityCertification.class));
    }
}
