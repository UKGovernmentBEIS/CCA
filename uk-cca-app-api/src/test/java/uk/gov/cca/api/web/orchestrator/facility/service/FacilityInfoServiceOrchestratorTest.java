package uk.gov.cca.api.web.orchestrator.facility.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.facility.domain.FacilityDataStatus;
import uk.gov.cca.api.facility.domain.dto.FacilityDataDetailsDTO;
import uk.gov.cca.api.facility.service.FacilityDataQueryService;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.FacilityCertificationStatus;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.dto.FacilityCertificationDTO;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.dto.FacilityCertificationStatusUpdateDTO;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.service.FacilityCertificationService;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.CertificationPeriodType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.CertificationPeriodDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.CertificationPeriodService;
import uk.gov.cca.api.web.orchestrator.facility.dto.FacilityCertificationDetailsDTO;
import uk.gov.cca.api.web.orchestrator.facility.dto.FacilityInfoDTO;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FacilityInfoServiceOrchestratorTest {

    @InjectMocks
    private FacilityInfoServiceOrchestrator orchestrator;

    @Mock
    private FacilityCertificationService facilityCertificationService;

    @Mock
    private FacilityDataQueryService facilityDataQueryService;

    @Mock
    private CertificationPeriodService certificationPeriodService;

    @Test
    void getFacilityInfo() {
        String facilityBusinessId = "facilityId";
        Long facilityId = 1L;
        Long certificationPeriodId = 2L;
        LocalDate startDate = LocalDate.of(2020, 1, 1);
        LocalDate endDate = LocalDate.of(2020, 2, 1);
        FacilityDataDetailsDTO facilityDataDetailsDTO = FacilityDataDetailsDTO.builder()
                .id(facilityId)
                .facilityBusinessId(facilityBusinessId)
                .siteName("siteName")
                .status(FacilityDataStatus.LIVE)
                .build();

        FacilityCertificationDTO facilityCertificationDTO = FacilityCertificationDTO.builder()
                .facilityId(facilityId)
                .certificationPeriodId(certificationPeriodId)
                .certificationStatus(FacilityCertificationStatus.CERTIFIED)
                .startDate(startDate)
                .build();

        FacilityCertificationDetailsDTO certificationDetailsDTO = FacilityCertificationDetailsDTO.builder()
                .certificationPeriod(CertificationPeriodType.CP6)
                .certificationPeriodStartDate(startDate)
                .certificationPeriodEndDate(endDate)
                .status(FacilityCertificationStatus.CERTIFIED)
                .startDate(startDate)
                .build();

        CertificationPeriodDTO certificationPeriodDTO = CertificationPeriodDTO.builder()
                .id(certificationPeriodId)
                .certificationPeriodType(CertificationPeriodType.CP6)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        when(facilityDataQueryService.getFacilityData(facilityId)).thenReturn(facilityDataDetailsDTO);
        when(facilityCertificationService.getFacilityCertifications(facilityDataDetailsDTO.getId()))
                .thenReturn(List.of(facilityCertificationDTO));
        when(certificationPeriodService.getAllCertificationPeriods()).thenReturn(List.of(certificationPeriodDTO));

        // invoke
        FacilityInfoDTO result = orchestrator.getFacilityInfo(facilityId);

        // verify
        verify(facilityDataQueryService, times(1)).getFacilityData(facilityId);
        verify(facilityCertificationService, times(1)).getFacilityCertifications(facilityDataDetailsDTO.getId());
        verify(certificationPeriodService, times(1)).getAllCertificationPeriods();

        assertThat(result.getFacilityCertificationDetails()).hasSize(1);
        assertThat(result.getFacilityBusinessId()).isEqualTo(facilityBusinessId);
        assertThat(result.getFacilityCertificationDetails().getFirst()).isEqualTo(certificationDetailsDTO);
    }

    @Test
    void updateFacilityCertificationStatus() {

        final Long facilityId = 1L;
        final FacilityCertificationStatusUpdateDTO facilityCertificationStatusUpdateDTO = FacilityCertificationStatusUpdateDTO.builder()
                .certificationStatus(FacilityCertificationStatus.CERTIFIED)
                .certificationPeriodId(2L)
                .build();

        orchestrator.updateFacilityCertificationStatus(facilityId, facilityCertificationStatusUpdateDTO);

        verify(facilityCertificationService, times(1))
                .updateOrCreateFacilityCertificationStatus(facilityId, facilityCertificationStatusUpdateDTO);
    }
}