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
        FacilityProcessStatusCreationDTO facilityProcessStatusCreationDTO_1 = FacilityProcessStatusCreationDTO.builder()
                .runId(1L)
                .moaType(MoaType.SECTOR_MOA)
                .chargingYear(chargingYear)
                .facilityId(1L)
                .build();
        FacilityProcessStatusCreationDTO facilityProcessStatusCreationDTO_2 = FacilityProcessStatusCreationDTO.builder()
                .runId(1L)
                .moaType(MoaType.SECTOR_MOA)
                .chargingYear(chargingYear)
                .facilityId(2L)
                .build();

        List<FacilityProcessStatusCreationDTO> dtoList = List.of(facilityProcessStatusCreationDTO_1, facilityProcessStatusCreationDTO_2);

        // invoke
        facilityProcessStatusUpdateService.createFacilityProcessStatusData(dtoList);

        ArgumentCaptor<List<FacilityProcessStatus>> captor = ArgumentCaptor.forClass(List.class);
        verify(processStatusRepository).saveAll(captor.capture());

        List<FacilityProcessStatus> savedFacilities = captor.getValue();

        assertEquals(2, savedFacilities.size());

        FacilityProcessStatus facilityProcessStatus_1 = savedFacilities.get(0);
        assertEquals(facilityProcessStatusCreationDTO_1.getRunId(), facilityProcessStatus_1.getRunId());
        assertEquals(facilityProcessStatusCreationDTO_1.getFacilityId(), facilityProcessStatus_1.getFacilityId());
        assertEquals(facilityProcessStatusCreationDTO_1.getChargingYear(), facilityProcessStatus_1.getChargingYear());

        FacilityProcessStatus facilityProcessStatus_2 = savedFacilities.get(1);
        assertEquals(facilityProcessStatusCreationDTO_2.getRunId(), facilityProcessStatus_2.getRunId());
        assertEquals(facilityProcessStatusCreationDTO_2.getFacilityId(), facilityProcessStatus_2.getFacilityId());
        assertEquals(facilityProcessStatusCreationDTO_2.getChargingYear(), facilityProcessStatus_2.getChargingYear());
    }
}
