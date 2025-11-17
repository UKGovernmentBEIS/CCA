package uk.gov.cca.api.workflow.request.flow.subsistencefees.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.subsistencefees.domain.MoaType;
import uk.gov.cca.api.subsistencefees.domain.dto.EligibleFacilityDTO;
import uk.gov.cca.api.subsistencefees.domain.dto.FacilityProcessStatusCreationDTO;
import uk.gov.cca.api.subsistencefees.service.FacilityProcessStatusUpdateService;

import java.time.Year;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MoaFacilitiesServiceTest {

    @InjectMocks
    private MoaFacilitiesService moaFacilitiesService;

    @Mock
    private FacilityProcessStatusUpdateService facilityProcessStatusUpdateService;

    @Test
    void flagMoaFacilitiesTest() {
        final Long runId = 1L;
        final Year chargingYear = Year.of(2025);
        final MoaType moaType = MoaType.SECTOR_MOA;
        final List<EligibleFacilityDTO> eligibleFacilityDTOs = List.of(EligibleFacilityDTO.builder()
                .id(1L)
                .facilityBusinessId("ADS_1-F00014")
                .siteName("site name 1")
                .targetUnitBusinessId("business id 1")
                .operatorName("Operator name 1")
                .build());

        final List<FacilityProcessStatusCreationDTO> facilityProcessStatusCreationDTOs = eligibleFacilityDTOs.stream()
                .map(facility ->
                        FacilityProcessStatusCreationDTO.builder()
                                .runId(runId)
                                .chargingYear(chargingYear)
                                .facilityId(facility.getId())
                                .moaType(moaType)
                                .build())
                .toList();
        // invoke
        moaFacilitiesService.flagMoaFacilities(runId, chargingYear, moaType, eligibleFacilityDTOs);

        // verify
        verify(facilityProcessStatusUpdateService, times(1)).createFacilityProcessStatusData(facilityProcessStatusCreationDTOs);
    }
}
