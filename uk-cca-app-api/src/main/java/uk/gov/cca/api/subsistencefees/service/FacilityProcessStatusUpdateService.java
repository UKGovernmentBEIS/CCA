package uk.gov.cca.api.subsistencefees.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.subsistencefees.domain.FacilityProcessStatus;
import uk.gov.cca.api.subsistencefees.domain.dto.FacilityProcessStatusCreationDTO;
import uk.gov.cca.api.subsistencefees.repository.FacilityProcessStatusRepository;
import uk.gov.cca.api.subsistencefees.transform.SubsistenceFeesMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FacilityProcessStatusUpdateService {

    private final FacilityProcessStatusRepository facilityProcessStatusRepository;
    private final SubsistenceFeesMapper SUBSISTENCE_FEES_MAPPER = Mappers.getMapper(SubsistenceFeesMapper.class);

    @Transactional
    public void createFacilityProcessStatusData(@NotEmpty @Valid List<@NotNull FacilityProcessStatusCreationDTO> dtoList) {
        List<FacilityProcessStatus> facilityProcessStatuses = dtoList.stream()
                .map(SUBSISTENCE_FEES_MAPPER::toFacilityProcessStatus)
                .toList();

        facilityProcessStatusRepository.saveAll(facilityProcessStatuses);
    }
}
