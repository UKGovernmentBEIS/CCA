package uk.gov.cca.api.subsistencefees.domain.dto.transform;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubsistenceFeesMoaTargetUnitSearchResultsInfo {

	private List<SubsistenceFeesMoaTargetUnitSearchResultInfo> subsistenceFeesMoaTargetUnitSearchResultInfo;
	private Long total;
}
