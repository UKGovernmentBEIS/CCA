package uk.gov.cca.api.facility.service;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.facility.domain.FacilityData;
import uk.gov.cca.api.facility.domain.dto.FacilityBaseInfoDTO;
import uk.gov.cca.api.facility.domain.dto.FacilityDataCreationDTO;
import uk.gov.cca.api.facility.domain.dto.FacilityDataUpdateDTO;
import uk.gov.cca.api.facility.repository.FacilityDataRepository;
import uk.gov.cca.api.facility.transform.FacilityAddressMapper;
import uk.gov.cca.api.facility.transform.FacilityDetailsMapper;
import uk.gov.netz.api.common.exception.BusinessException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@Service
@AllArgsConstructor
public class FacilityDataUpdateService {

    private final FacilityDataRepository repository;
    private final FacilityDataQueryService facilityDataQueryService;

    private static final FacilityAddressMapper FACILITY_ADDRESS_MAPPER = Mappers.getMapper(FacilityAddressMapper.class);
    private static final FacilityDetailsMapper FACILITY_DETAILS_MAPPER = Mappers.getMapper(FacilityDetailsMapper.class);

    @Transactional
    public Set<FacilityBaseInfoDTO> createFacilitiesData(@NotEmpty @Valid List<@NotNull FacilityDataCreationDTO> dtoList) {
        List<FacilityData> facilitiesData = dtoList.stream()
                .map(dto -> FacilityData.builder()
                        .facilityBusinessId(dto.getFacilityBusinessId())
                        .accountId(dto.getAccountId())
                        .siteName(dto.getSiteName())
                        .address(FACILITY_ADDRESS_MAPPER.toFacilityAddress(dto.getAddress()))
                        .createdDate(dto.getCreatedDate())
                        .chargeStartDate(dto.getChargeStartDate())
                        .participatingSchemeVersions(dto.getParticipatingSchemeVersions())
                        .build())
                .toList();

        return repository.saveAll(facilitiesData).stream()
                .map(FACILITY_DETAILS_MAPPER::toFacilityBaseInfo)
                .collect(Collectors.toSet());
    }

    @Transactional
    public void updateFacilitiesData(@NotEmpty @Valid List<@NotNull FacilityDataUpdateDTO> dtoList) {
        Map<String, FacilityDataUpdateDTO> dtoMap = dtoList.stream()
                .collect(Collectors.toMap(FacilityDataUpdateDTO::getFacilityBusinessId, dto -> dto));

        List<FacilityData> persistedFacilitiesData = repository.findAllByFacilityBusinessIdIn(dtoMap.keySet());

        if (persistedFacilitiesData.size() != dtoMap.size()) {
            throw new BusinessException(RESOURCE_NOT_FOUND);
        }

        for (FacilityData facilityData : persistedFacilitiesData) {
            FacilityDataUpdateDTO dto = dtoMap.get(facilityData.getFacilityBusinessId());
            facilityData.setSiteName(dto.getSiteName());
            LocalDate closedDate = dto.getClosedDate();
            facilityData.setParticipatingSchemeVersions(dto.getParticipatingSchemeVersions());
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
            if (facility.getSchemeExitDate() == null) {
                facility.setSchemeExitDate(terminationDateTime.toLocalDate());
            }
        }

        repository.saveAll(activeFacilitiesData);
    }

    @Transactional
    public void updateFacilitiesDataParticipatingScheme(Set<String> facilityBusinessIds, SchemeVersion schemeVersion) {
        List<FacilityData> persistedFacilitiesData = repository.findAllByFacilityBusinessIdIn(facilityBusinessIds);

        if (persistedFacilitiesData.size() != facilityBusinessIds.size()) {
            throw new BusinessException(RESOURCE_NOT_FOUND);
        }

        persistedFacilitiesData.forEach(facility ->
                facility.getParticipatingSchemeVersions().add(schemeVersion));

        repository.saveAll(persistedFacilitiesData);
    }

    @Transactional
    public void updateFacilitySchemeExitDate(Long facilityId, LocalDate schemeExitDate) {
        FacilityData facility = facilityDataQueryService.getFacilityDataById(facilityId);
        facility.setSchemeExitDate(schemeExitDate);
    }

}
