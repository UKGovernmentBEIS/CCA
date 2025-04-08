package uk.gov.cca.api.facility.service;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.facility.domain.FacilityData;
import uk.gov.cca.api.facility.domain.dto.FacilityDataCreationDTO;
import uk.gov.cca.api.facility.domain.dto.FacilityDataUpdateDTO;
import uk.gov.cca.api.facility.repository.FacilityDataRepository;
import uk.gov.cca.api.facility.transform.FacilityAddressMapper;
import uk.gov.netz.api.common.exception.BusinessException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@Service
@AllArgsConstructor
public class FacilityDataUpdateService {

    private final FacilityDataRepository repository;
    private final FacilityDataQueryService facilityDataQueryService;

    private static final FacilityAddressMapper FACILITY_ADDRESS_MAPPER = Mappers.getMapper(FacilityAddressMapper.class);

    @Transactional
    public void createFacilitiesData(@NotEmpty @Valid List<@NotNull FacilityDataCreationDTO> dtoList) {
        List<FacilityData> facilitiesData = dtoList.stream()
                .map(dto -> FacilityData.builder()
                        .facilityId(dto.getFacilityId())
                        .accountId(dto.getAccountId())
                        .siteName(dto.getSiteName())
                        .address(FACILITY_ADDRESS_MAPPER.toFacilityAddress(dto.getAddress()))
                        .createdDate(dto.getCreatedDate())
                        .chargeStartDate(dto.getChargeStartDate())
                        .build())
                .toList();

        repository.saveAll(facilitiesData);
    }

    @Transactional
    public void updateFacilitiesData(@NotEmpty @Valid List<@NotNull FacilityDataUpdateDTO> dtoList) {
        Map<String, FacilityDataUpdateDTO> dtoMap = dtoList.stream()
                .collect(Collectors.toMap(FacilityDataUpdateDTO::getFacilityId, dto -> dto));

        List<FacilityData> persistedFacilitiesData = repository.findAllByFacilityIdIn(dtoMap.keySet());


        if (persistedFacilitiesData.size() != dtoMap.keySet().size()) {
            throw new BusinessException(RESOURCE_NOT_FOUND);
        }

        for (FacilityData facilityData : persistedFacilitiesData) {
            FacilityDataUpdateDTO dto = dtoMap.get(facilityData.getFacilityId());
            facilityData.setSiteName(dto.getSiteName());
            LocalDate closedDate = dto.getClosedDate();
            if (closedDate != null) {
                if (facilityData.getSchemeExitDate() == null) {
                    facilityData.setSchemeExitDate(closedDate);
                }
                facilityData.setClosedDate(closedDate.atStartOfDay());
            }
            FACILITY_ADDRESS_MAPPER.setAddress(facilityData.getAddress(), dto.getFacilityAddress());
        }

        repository.saveAll(persistedFacilitiesData);
    }

    @Transactional
    public void terminateFacilities(Long accountId, LocalDateTime terminationDateTime) {
        List<FacilityData> activeFacilitiesData = repository.findFacilityDataByAccountIdAndClosedDateIsNull(accountId);

        for (FacilityData facility : activeFacilitiesData) {
            facility.setClosedDate(terminationDateTime);
            if(facility.getSchemeExitDate() == null) {
                facility.setSchemeExitDate(terminationDateTime.toLocalDate());
            }
        }

        repository.saveAll(activeFacilitiesData);
    }

    @Transactional
    public void updateFacilitySchemeExitDate(String facilityId, LocalDate schemeExitDate) {
        FacilityData facility = facilityDataQueryService.getFacilityDataById(facilityId);
        facility.setSchemeExitDate(schemeExitDate);
    }

}
