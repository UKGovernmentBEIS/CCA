package uk.gov.cca.api.subsistencefees.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.subsistencefees.domain.FacilityProcessStatus;
import uk.gov.cca.api.subsistencefees.domain.MoaType;
import uk.gov.cca.api.subsistencefees.domain.dto.FacilityProcessStatusCreationDTO;
import uk.gov.cca.api.subsistencefees.repository.FacilityProcessStatusRepository;

import java.time.Year;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FacilityProcessStatusUpdateServiceTest {

    @InjectMocks
    private FacilityProcessStatusUpdateService facilityProcessStatusUpdateService;

    @Mock
    private FacilityProcessStatusRepository processStatusRepository;

    @Test
    void testCreateFacilityProcessStatusData() {
        Year chargingYear = Year.of(2025);
        FacilityProcessStatusCreationDTO facilityProcessStatusCreationDTO1 = FacilityProcessStatusCreationDTO.builder()
                .runId(1L)
                .moaType(MoaType.SECTOR_MOA)
                .chargingYear(chargingYear)
                .facilityId(1L)
                .build();
        FacilityProcessStatusCreationDTO facilityProcessStatusCreationDTO2 = FacilityProcessStatusCreationDTO.builder()
                .runId(1L)
                .moaType(MoaType.SECTOR_MOA)
                .chargingYear(chargingYear)
                .facilityId(2L)
                .build();

        List<FacilityProcessStatusCreationDTO> dtoList = List.of(facilityProcessStatusCreationDTO1, facilityProcessStatusCreationDTO2);

        // invoke
        facilityProcessStatusUpdateService.createFacilityProcessStatusData(dtoList);

        ArgumentCaptor<List<FacilityProcessStatus>> captor = ArgumentCaptor.forClass(List.class);
        verify(processStatusRepository).saveAll(captor.capture());

        List<FacilityProcessStatus> savedFacilities = captor.getValue();

        assertEquals(2, savedFacilities.size());

        FacilityProcessStatus facilityProcessStatus1 = savedFacilities.get(0);
        assertEquals(facilityProcessStatusCreationDTO1.getRunId(), facilityProcessStatus1.getRunId());
        assertEquals(facilityProcessStatusCreationDTO1.getFacilityId(), facilityProcessStatus1.getFacilityId());
        assertEquals(facilityProcessStatusCreationDTO1.getChargingYear(), facilityProcessStatus1.getChargingYear());

        FacilityProcessStatus facilityProcessStatus2 = savedFacilities.get(1);
        assertEquals(facilityProcessStatusCreationDTO2.getRunId(), facilityProcessStatus2.getRunId());
        assertEquals(facilityProcessStatusCreationDTO2.getFacilityId(), facilityProcessStatus2.getFacilityId());
        assertEquals(facilityProcessStatusCreationDTO2.getChargingYear(), facilityProcessStatus2.getChargingYear());
    }
}
