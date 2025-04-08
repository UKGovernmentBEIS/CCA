package uk.gov.cca.api.subsistencefees.domain.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubsistenceFeesMoaTargetUnitSearchResults {

	private List<SubsistenceFeesMoaTargetUnitSearchResultInfoDTO> subsistenceFeesMoaTargetUnits;
    private Long total;
}
