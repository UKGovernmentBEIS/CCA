package uk.gov.cca.api.web.orchestrator.facility.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.facility.domain.FacilityDataStatus;
import uk.gov.cca.api.facility.domain.dto.FacilityDataDetailsDTO;
import uk.gov.cca.api.facility.service.FacilityDataQueryService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.SurplusGainedDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.SurplusQueryService;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.FacilityCertificationStatus;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.dto.FacilityCertificationStatusUpdateDTO;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.service.FacilityCertificationService;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.CertificationPeriodType;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.dto.FacilityCertificationStatusDTO;
import uk.gov.cca.api.web.orchestrator.facility.dto.FacilityInfoDTO;

import java.math.BigDecimal;
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
    private SurplusQueryService surplusQueryService;

    @Test
    void getFacilityInfo() {
        final String facilityBusinessId = "facilityId";
        final Long facilityId = 1L;

        final FacilityDataDetailsDTO facilityDataDetailsDTO = FacilityDataDetailsDTO.builder()
                .id(facilityId)
                .facilityBusinessId(facilityBusinessId)
                .siteName("siteName")
                .status(FacilityDataStatus.LIVE)
                .build();
        final SurplusGainedDTO surplusGainedDTO = SurplusGainedDTO.builder()
                .surplusGained(BigDecimal.valueOf(252))
                .hasHistory(false)
                .schemeVersion(SchemeVersion.CCA_3)
                .build();

        when(facilityDataQueryService.getFacilityData(facilityId)).thenReturn(facilityDataDetailsDTO);
        when(surplusQueryService.getFacilitySurplusGainedWithHistoryFlag(facilityId)).thenReturn(surplusGainedDTO);

        // invoke
        final FacilityInfoDTO result = orchestrator.getFacilityInfo(facilityId);

        // verify
        verify(facilityDataQueryService, times(1)).getFacilityData(facilityId);
        verify(surplusQueryService, times(1)).getFacilitySurplusGainedWithHistoryFlag(facilityId);

        assertThat(result.getFacilityBusinessId()).isEqualTo(facilityBusinessId);
        assertThat(result.getSurplusDetails()).isEqualTo(surplusGainedDTO);
    }

    @Test
    void getFacilityCertifications() {
        final Long facilityId = 1L;

        final FacilityCertificationStatusDTO certificationDetailsDTO = FacilityCertificationStatusDTO.builder()
                .certificationPeriod(CertificationPeriodType.CP6)
                .certificationPeriodStartDate(LocalDate.of(2020, 1, 1))
                .certificationPeriodEndDate(LocalDate.of(2020, 2, 1))
                .status(FacilityCertificationStatus.CERTIFIED)
                .startDate(LocalDate.of(2020, 1, 1))
                .build();

        when(facilityCertificationService.getFacilityCertifications(facilityId)).thenReturn(List.of(certificationDetailsDTO));

        // Invoke
        final List<FacilityCertificationStatusDTO> result = orchestrator.getFacilityCertifications(facilityId);

        // Verify
        assertThat(result).hasSize(1).containsExactly(certificationDetailsDTO);
        verify(facilityCertificationService, times(1)).getFacilityCertifications(facilityId);
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