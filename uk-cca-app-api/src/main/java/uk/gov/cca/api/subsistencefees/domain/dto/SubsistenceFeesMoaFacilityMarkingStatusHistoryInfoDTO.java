package uk.gov.cca.api.subsistencefees.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubsistenceFeesMoaFacilityMarkingStatusHistoryInfoDTO {

    private String facilityId;

    private String siteName;

    List<SubsistenceFeesMoaFacilityMarkingStatusHistoryDTO> markingStatusHistoryList;
}
