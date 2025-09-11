package uk.gov.cca.api.targetperiodreporting.targetperiod.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.CertificationPeriod;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.CertificationPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriod;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.CertificationPeriodDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.CertificationPeriodInfoDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.repository.CertificationPeriodRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CertificationPeriodServiceTest {

    @InjectMocks
    private CertificationPeriodService certificationPeriodService;

    @Mock
    private CertificationPeriodRepository certificationPeriodRepository;

    @Test
    void getAllCertificationPeriods() {
        final CertificationPeriod certificationPeriod = CertificationPeriod.builder()
                .id(1L)
                .targetPeriod(TargetPeriod.builder().businessId(TargetPeriodType.TP6).build())
                .businessId(CertificationPeriodType.CP7)
                .name("name")
                .certificationBatchTriggerDate(LocalDate.of(2025, 7, 2))
                .startDate(LocalDate.of(2025, 7, 1))
                .endDate(LocalDate.of(2027, 3, 31))
                .build();

        final CertificationPeriodDTO dto = CertificationPeriodDTO.builder()
                .id(1L)
                .targetPeriodType(TargetPeriodType.TP6)
                .certificationPeriodType(CertificationPeriodType.CP7)
                .certificationBatchTriggerDate(LocalDate.of(2025, 7, 2))
                .startDate(LocalDate.of(2025, 7, 1))
                .endDate(LocalDate.of(2027, 3, 31))
                .build();

        when(certificationPeriodRepository.findAll()).thenReturn(List.of(certificationPeriod));

        // Invoke
        List<CertificationPeriodDTO> result = certificationPeriodService.getAllCertificationPeriods();

        // Verify
        assertThat(result.getFirst()).isEqualTo(dto);
        verify(certificationPeriodRepository).findAll();
    }

    @Test
    void getCertificationPeriodByType() {
        final CertificationPeriodType type = CertificationPeriodType.CP7;
        final CertificationPeriod certificationPeriod = CertificationPeriod.builder()
                .id(1L)
                .targetPeriod(TargetPeriod.builder().businessId(TargetPeriodType.TP6).build())
                .businessId(CertificationPeriodType.CP7)
                .name("name")
                .certificationBatchTriggerDate(LocalDate.of(2025, 7, 2))
                .startDate(LocalDate.of(2025, 7, 1))
                .endDate(LocalDate.of(2027, 3, 31))
                .build();

        final CertificationPeriodDTO dto = CertificationPeriodDTO.builder()
                .id(1L)
                .targetPeriodType(TargetPeriodType.TP6)
                .certificationPeriodType(CertificationPeriodType.CP7)
                .certificationBatchTriggerDate(LocalDate.of(2025, 7, 2))
                .startDate(LocalDate.of(2025, 7, 1))
                .endDate(LocalDate.of(2027, 3, 31))
                .build();

        when(certificationPeriodRepository.findByBusinessId(type)).thenReturn(Optional.of(certificationPeriod));

        // Invoke
        CertificationPeriodDTO result = certificationPeriodService.getCertificationPeriodByType(type);

        // Verify
        assertThat(result).isEqualTo(dto);
        verify(certificationPeriodRepository).findByBusinessId(type);
    }

    @Test
    void getCertificationPeriodByTriggerDate() {
        final LocalDate triggerDate = LocalDate.of(2025, 7, 2);
        final CertificationPeriod certificationPeriod = CertificationPeriod.builder()
                .id(1L)
                .targetPeriod(TargetPeriod.builder().businessId(TargetPeriodType.TP6).build())
                .businessId(CertificationPeriodType.CP7)
                .name("name")
                .certificationBatchTriggerDate(LocalDate.of(2025, 7, 2))
                .startDate(LocalDate.of(2025, 7, 1))
                .endDate(LocalDate.of(2027, 3, 31))
                .build();

        final CertificationPeriodDTO dto = CertificationPeriodDTO.builder()
                .id(1L)
                .targetPeriodType(TargetPeriodType.TP6)
                .certificationPeriodType(CertificationPeriodType.CP7)
                .certificationBatchTriggerDate(LocalDate.of(2025, 7, 2))
                .startDate(LocalDate.of(2025, 7, 1))
                .endDate(LocalDate.of(2027, 3, 31))
                .build();

        when(certificationPeriodRepository.findByCertificationBatchTriggerDate(triggerDate)).thenReturn(Optional.of(certificationPeriod));

        // Invoke
        CertificationPeriodDTO result = certificationPeriodService.getCertificationPeriodByTriggerDate(triggerDate);

        // Verify
        assertThat(result).isEqualTo(dto);
        verify(certificationPeriodRepository).findByCertificationBatchTriggerDate(triggerDate);
    }

    @Test
    void getCurrentCertificationPeriod() {
        final LocalDate currentDate = LocalDate.now();

        final CertificationPeriod certificationPeriod = CertificationPeriod.builder()
                .id(1L)
                .targetPeriod(TargetPeriod.builder().businessId(TargetPeriodType.TP5).build())
                .businessId(CertificationPeriodType.CP6)
                .name("name")
                .certificationBatchTriggerDate(LocalDate.of(2023, 7, 2))
                .startDate(LocalDate.of(2023, 7, 1))
                .endDate(currentDate)
                .build();

        final CertificationPeriodInfoDTO certificationPeriodInfoDTO = CertificationPeriodInfoDTO.builder()
                .id(1L)
                .startDate(LocalDate.of(2023, 7, 1))
                .endDate(currentDate)
                .build();

        when(certificationPeriodRepository.findCertificationPeriodByDate(currentDate))
                .thenReturn(Optional.ofNullable(certificationPeriod));

        // invoke
        CertificationPeriodInfoDTO period = certificationPeriodService.getCurrentCertificationPeriod();

        // verify
        verify(certificationPeriodRepository, times(1)).findCertificationPeriodByDate(currentDate);
        assertThat(period).isEqualTo(certificationPeriodInfoDTO);
    }

    @Test
    void getCertificationPeriodById() {

        final Long certificationPeriodId = 2L;
        final CertificationPeriod certificationPeriod = CertificationPeriod.builder()
                .id(certificationPeriodId)
                .targetPeriod(TargetPeriod.builder().businessId(TargetPeriodType.TP6).build())
                .businessId(CertificationPeriodType.CP7)
                .name("name")
                .certificationBatchTriggerDate(LocalDate.of(2025, 7, 2))
                .startDate(LocalDate.of(2025, 7, 1))
                .endDate(LocalDate.of(2027, 3, 31))
                .build();

        when(certificationPeriodRepository.findById(certificationPeriodId))
                .thenReturn(Optional.of(certificationPeriod));

        final CertificationPeriodDTO result = certificationPeriodService
                .getCertificationPeriodById(certificationPeriodId);

        verify(certificationPeriodRepository).findById(certificationPeriodId);
        assertEquals(certificationPeriod.getCertificationBatchTriggerDate(), result.getCertificationBatchTriggerDate());
        assertEquals(certificationPeriod.getEndDate(), result.getEndDate());
        assertEquals(certificationPeriod.getStartDate(), result.getStartDate());
        assertEquals(certificationPeriod.getBusinessId(), result.getCertificationPeriodType());
        assertEquals(certificationPeriod.getTargetPeriod().getBusinessId(), result.getTargetPeriodType());
    }
}
