package uk.gov.cca.api.targetperiodreporting.facilitycertification.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.FacilityCertification;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.FacilityCertificationStatus;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.dto.FacilityCertificationStatusDTO;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.dto.FacilityCertificationStatusUpdateDTO;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.repository.FacilityCertificationRepository;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.validation.FacilityCertificationValidationService;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.CertificationPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.CertificationPeriodDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.CertificationPeriodService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FacilityCertificationServiceTest {

    @InjectMocks
    private FacilityCertificationService facilityCertificationService;

    @Mock
    private CertificationPeriodService certificationPeriodService;

    @Mock
    private FacilityCertificationRepository facilityCertificationRepository;

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
    void getFacilityCertifications() {
        final Long facilityId = 1L;

        final FacilityCertification facilityCertification = FacilityCertification.builder()
                .facilityId(facilityId)
                .certificationPeriodId(11L)
                .certificationStatus(FacilityCertificationStatus.CERTIFIED)
                .startDate(LocalDate.of(2020, 1, 1))
                .build();
        final CertificationPeriodDTO cp6 = CertificationPeriodDTO.builder()
                .id(11L)
                .certificationPeriodType(CertificationPeriodType.CP6)
                .schemeVersion(SchemeVersion.CCA_2)
                .startDate(LocalDate.of(2020, 1, 1))
                .endDate(LocalDate.of(2020, 2, 1))
                .build();
        final CertificationPeriodDTO cp7 = CertificationPeriodDTO.builder()
                .id(22L)
                .certificationPeriodType(CertificationPeriodType.CP7)
                .schemeVersion(SchemeVersion.CCA_3)
                .startDate(LocalDate.of(2026, 1, 1))
                .endDate(LocalDate.of(2027, 1, 1))
                .build();

        final FacilityCertificationStatusDTO certified = FacilityCertificationStatusDTO.builder()
                .certificationPeriodId(11L)
                .schemeVersion(SchemeVersion.CCA_2)
                .certificationPeriod(CertificationPeriodType.CP6)
                .certificationPeriodStartDate(LocalDate.of(2020, 1, 1))
                .certificationPeriodEndDate(LocalDate.of(2020, 2, 1))
                .status(FacilityCertificationStatus.CERTIFIED)
                .startDate(LocalDate.of(2020, 1, 1))
                .build();
        final FacilityCertificationStatusDTO notCertified = FacilityCertificationStatusDTO.builder()
                .certificationPeriodId(22L)
                .schemeVersion(SchemeVersion.CCA_3)
                .certificationPeriod(CertificationPeriodType.CP7)
                .certificationPeriodStartDate(LocalDate.of(2026, 1, 1))
                .certificationPeriodEndDate(LocalDate.of(2027, 1, 1))
                .status(FacilityCertificationStatus.NOT_YET_DEFINED)
                .build();

        when(facilityCertificationRepository.findAllByFacilityId(facilityId)).thenReturn(List.of(facilityCertification));
        when(certificationPeriodService.getAllCertificationPeriods()).thenReturn(List.of(cp6, cp7));

        // Invoke
        final List<FacilityCertificationStatusDTO> result = facilityCertificationService.getFacilityCertifications(facilityId);

        // Verify
        assertThat(result).hasSize(2).containsExactly(notCertified, certified);
        verify(facilityCertificationRepository, times(1)).findAllByFacilityId(facilityId);
        verify(certificationPeriodService, times(1)).getAllCertificationPeriods();
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

        verify(validationService, times(1))
                .validateFacilityCertificationByCertificationPeriod(eq(facilityCertificationStatusUpdateDTO), any());
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

        verify(validationService, times(1))
                .validateFacilityCertificationByCertificationPeriod(eq(facilityCertificationStatusUpdateDTO), any());
        verify(facilityCertificationRepository, times(1))
                .findByFacilityIdAndCertificationPeriodId(facilityId, 1L);
        verify(facilityCertificationRepository, times(1))
                .save(any(FacilityCertification.class));
    }
}
