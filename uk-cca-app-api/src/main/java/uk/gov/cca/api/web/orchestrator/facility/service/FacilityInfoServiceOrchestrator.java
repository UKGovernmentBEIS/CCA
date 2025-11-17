package uk.gov.cca.api.web.orchestrator.facility.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.facility.domain.dto.FacilityDataDetailsDTO;
import uk.gov.cca.api.facility.service.FacilityDataQueryService;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.FacilityCertificationStatus;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.dto.FacilityCertificationDTO;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.dto.FacilityCertificationStatusUpdateDTO;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.service.FacilityCertificationService;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.CertificationPeriodDTO;
import uk.gov.cca.api.targetperiodreporting.targetperiod.service.CertificationPeriodService;
import uk.gov.cca.api.web.orchestrator.facility.dto.FacilityCertificationDetailsDTO;
import uk.gov.cca.api.web.orchestrator.facility.dto.FacilityInfoDTO;
import uk.gov.cca.api.web.orchestrator.facility.transform.FacilityInfoMapper;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FacilityInfoServiceOrchestrator {

    private final FacilityCertificationService facilityCertificationService;
    private final FacilityDataQueryService facilityDataQueryService;
    private final CertificationPeriodService certificationPeriodService;

    private static final FacilityInfoMapper FACILITY_INFO_MAPPER = Mappers.getMapper(FacilityInfoMapper.class);
    private static final FacilityCertificationDTO NOT_YET_DEFINED =
            FacilityCertificationDTO.builder().certificationStatus(FacilityCertificationStatus.NOT_YET_DEFINED).build();

    public FacilityInfoDTO getFacilityInfo(Long facilityId) {

        FacilityDataDetailsDTO facilityDetails = facilityDataQueryService.getFacilityData(facilityId);

        Map<Long, FacilityCertificationDTO> facilityCertificationByPeriod =
                facilityCertificationService.getFacilityCertifications(facilityDetails.getId())
                        .stream()
                        .collect(Collectors.toMap(FacilityCertificationDTO::getCertificationPeriodId, Function.identity()));

        LocalDate currentDate = LocalDate.now();
        List<FacilityCertificationDetailsDTO> facilityCertificationDetails = certificationPeriodService.getAllCertificationPeriods().stream()
                .sorted(Comparator.comparing(CertificationPeriodDTO::getStartDate))
                .filter(cp -> cp.getStartDate().isBefore(currentDate) || cp.getStartDate().isEqual(currentDate))
                .map(cp -> FACILITY_INFO_MAPPER.toFacilityCertificationDetails(facilityCertificationByPeriod.getOrDefault(cp.getId(), NOT_YET_DEFINED), cp))
                .toList();

        return FACILITY_INFO_MAPPER.toFacilityInfoDTO(facilityDetails, facilityCertificationDetails);
    }

    public void updateFacilityCertificationStatus(Long facilityId,
                                                  FacilityCertificationStatusUpdateDTO statusUpdateDTO) {

        facilityCertificationService
                .updateOrCreateFacilityCertificationStatus(facilityId, statusUpdateDTO);
    }
}