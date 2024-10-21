package uk.gov.cca.api.facility.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.facility.domain.FacilityData;
import uk.gov.cca.api.facility.domain.dto.FacilityDataCreationDTO;
import uk.gov.cca.api.facility.domain.dto.FacilityDataUpdateDTO;
import uk.gov.cca.api.facility.repository.FacilityDataRepository;
import uk.gov.netz.api.common.exception.BusinessException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FacilityDataUpdateServiceTest {

    @InjectMocks
    FacilityDataUpdateService service;

    @Mock
    FacilityDataRepository repository;

    @Test
    void createFacilities_shouldSaveAllFacilities() {

        FacilityDataCreationDTO dto1 = FacilityDataCreationDTO.builder()
                .facilityId("FAC001")
                .accountId(1001L)
                .createdDate(LocalDateTime.of(2023, 9, 10, 12, 0))
                .build();

        FacilityDataCreationDTO dto2 = FacilityDataCreationDTO.builder()
                .facilityId("FAC002")
                .accountId(1002L)
                .createdDate(LocalDateTime.of(2023, 9, 11, 12, 0))
                .build();

        List<FacilityDataCreationDTO> dtoList = List.of(dto1, dto2);

        service.createFacilitiesData(dtoList);

        ArgumentCaptor<List<FacilityData>> captor = ArgumentCaptor.forClass(List.class);
        verify(repository).saveAll(captor.capture());

        List<FacilityData> savedFacilities = captor.getValue();

        assertEquals(2, savedFacilities.size());

        FacilityData savedFacility1 = savedFacilities.get(0);
        assertEquals("FAC001", savedFacility1.getFacilityId());
        assertEquals(1001L, savedFacility1.getAccountId());
        assertEquals(LocalDateTime.of(2023, 9, 10, 12, 0), savedFacility1.getCreatedDate());

        FacilityData savedFacility2 = savedFacilities.get(1);
        assertEquals("FAC002", savedFacility2.getFacilityId());
        assertEquals(1002L, savedFacility2.getAccountId());
        assertEquals(LocalDateTime.of(2023, 9, 11, 12, 0), savedFacility2.getCreatedDate());
    }

    @Test
    void updateFacilitiesData_shouldUpdateFacilities() {

        FacilityDataUpdateDTO dto1 = FacilityDataUpdateDTO.builder()
                .facilityId("FAC001")
                .closedDate(LocalDate.of(2024, 9, 1))
                .build();

        FacilityDataUpdateDTO dto2 = FacilityDataUpdateDTO.builder()
                .facilityId("FAC002")
                .closedDate(LocalDate.of(2024, 9, 2))
                .build();

        List<FacilityDataUpdateDTO> dtoList = List.of(dto1, dto2);

        FacilityData facilityData1 = FacilityData.builder()
                .id(1L)
                .facilityId("FAC001")
                .build();

        FacilityData facilityData2 = FacilityData.builder()
                .id(2L)
                .facilityId("FAC002")
                .build();

        List<FacilityData> facilitiesData = List.of(facilityData1, facilityData2);

        when(repository.findAllByFacilityIdIn(Set.of("FAC001", "FAC002"))).thenReturn(facilitiesData);

        service.updateFacilitiesData(dtoList);


        assertEquals(dto1.getClosedDate().atStartOfDay(), facilityData1.getClosedDate());
        assertEquals(dto2.getClosedDate().atStartOfDay(), facilityData2.getClosedDate());

        ArgumentCaptor<List<FacilityData>> captor = ArgumentCaptor.forClass(List.class);
        verify(repository).saveAll(captor.capture());
        List<FacilityData> updatedFacilities = captor.getValue();

        assertEquals(2, updatedFacilities.size());
        assertEquals(dto1.getClosedDate().atStartOfDay(), updatedFacilities.get(0).getClosedDate());
        assertEquals(dto2.getClosedDate().atStartOfDay(), updatedFacilities.get(1).getClosedDate());
    }

    @Test
    void updateFacilitiesData_shouldThrowBusinessException_whenFacilityIdNotFound() {
        FacilityDataUpdateDTO dto1 = FacilityDataUpdateDTO.builder()
                .facilityId("FAC001")
                .closedDate(LocalDate.of(2024, 9, 19))
                .build();

        FacilityDataUpdateDTO dto2 = FacilityDataUpdateDTO.builder()
                .facilityId("FAC002")
                .closedDate(LocalDate.of(2024, 9, 19))
                .build();

        List<FacilityDataUpdateDTO> dtoList = List.of(dto1, dto2);

        FacilityData facilityData1 = FacilityData.builder().facilityId("FAC001").build();
        when(repository.findAllByFacilityIdIn(Set.of("FAC001", "FAC002")))
                .thenReturn(List.of(facilityData1));

        assertThrows(BusinessException.class, () -> service.updateFacilitiesData(dtoList));

        verify(repository, never()).saveAll(any());
    }

    @Test
    void terminateFacilities() {

        final Long accountId = 999L;
        List<FacilityData> facilitiesData = List.of(FacilityData.builder()
                .facilityId("facilityId")
                .createdDate(LocalDateTime.now())
                .accountId(accountId)
                .closedDate(null)
                .build());

        when(repository.findFacilityDataByAccountIdAndClosedDateIsNull(accountId))
                .thenReturn(facilitiesData);

        service.terminateFacilities(accountId);

        verify(repository,times(1)).saveAll(facilitiesData);
    }
}
