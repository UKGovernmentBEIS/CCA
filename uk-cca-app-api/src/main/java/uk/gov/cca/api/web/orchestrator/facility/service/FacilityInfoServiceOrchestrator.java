package uk.gov.cca.api.web.orchestrator.facility.service;

import java.util.List;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.facility.domain.dto.FacilityDataDetailsDTO;
import uk.gov.cca.api.facility.service.FacilityDataQueryService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.SurplusGainedDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.FacilitySurplusService;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.dto.FacilityCertificationStatusDTO;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.domain.dto.FacilityCertificationStatusUpdateDTO;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.service.FacilityCertificationService;
import uk.gov.cca.api.web.orchestrator.facility.dto.FacilityInfoDTO;
import uk.gov.cca.api.web.orchestrator.facility.transform.FacilityInfoMapper;

@Service
@RequiredArgsConstructor
public class FacilityInfoServiceOrchestrator {

    private final FacilityCertificationService facilityCertificationService;
    private final FacilityDataQueryService facilityDataQueryService;
    private final FacilitySurplusService surplusService;

    private static final FacilityInfoMapper FACILITY_INFO_MAPPER = Mappers.getMapper(FacilityInfoMapper.class);

    public FacilityInfoDTO getFacilityInfo(Long facilityId) {
        FacilityDataDetailsDTO facilityDetails = facilityDataQueryService.getFacilityData(facilityId);
        SurplusGainedDTO surplusDetails = surplusService.getSurplusGainedWithHistoryFlag(facilityId);

        return FACILITY_INFO_MAPPER.toFacilityInfoDTO(facilityDetails, surplusDetails);
    }

    public List<FacilityCertificationStatusDTO> getFacilityCertifications(Long facilityId) {
        return facilityCertificationService.getFacilityCertifications(facilityId);
    }

    public void updateFacilityCertificationStatus(Long facilityId,
                                                  FacilityCertificationStatusUpdateDTO statusUpdateDTO) {

        facilityCertificationService
                .updateOrCreateFacilityCertificationStatus(facilityId, statusUpdateDTO);
    }
}