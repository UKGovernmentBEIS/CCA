package uk.gov.cca.api.workflow.request.flow.subsistencefees.common.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.subsistencefees.domain.MoaType;
import uk.gov.cca.api.subsistencefees.domain.dto.EligibleFacilityDTO;
import uk.gov.cca.api.subsistencefees.domain.dto.FacilityProcessStatusCreationDTO;
import uk.gov.cca.api.subsistencefees.service.FacilityProcessStatusUpdateService;

import java.time.Year;
import java.util.List;

@Service
@AllArgsConstructor
public class MoaFacilitiesService {

    private final FacilityProcessStatusUpdateService facilityProcessStatusUpdateService;

    @Transactional
    public void flagMoaFacilities(Long runId, Year chargingYear, MoaType moaType, List<EligibleFacilityDTO> facilities) {
        final List<FacilityProcessStatusCreationDTO> facilityProcessStatusCreationDTOs = facilities.stream()
                .map(facility ->
                        FacilityProcessStatusCreationDTO.builder()
                                .runId(runId)
                                .chargingYear(chargingYear)
                                .facilityId(facility.getId())
                                .moaType(moaType)
                                .build())
                .toList();

        facilityProcessStatusUpdateService.createFacilityProcessStatusData(facilityProcessStatusCreationDTOs);
    }
}
