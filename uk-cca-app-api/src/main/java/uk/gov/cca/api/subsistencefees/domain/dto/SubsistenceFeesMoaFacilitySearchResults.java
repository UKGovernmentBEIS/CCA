package uk.gov.cca.api.subsistencefees.domain.dto;

import java.util.Collections;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubsistenceFeesMoaFacilitySearchResults {

	private List<SubsistenceFeesMoaFacilitySearchResultInfoDTO> subsistenceFeesMoaFacilities;
    private Long total;
    
    public static SubsistenceFeesMoaFacilitySearchResults emptySubsistenceFeesFacilitiesSearchResults() {
        return SubsistenceFeesMoaFacilitySearchResults.builder()
        		.subsistenceFeesMoaFacilities(Collections.emptyList())
        		.total(0L)
        		.build();
    }
}
