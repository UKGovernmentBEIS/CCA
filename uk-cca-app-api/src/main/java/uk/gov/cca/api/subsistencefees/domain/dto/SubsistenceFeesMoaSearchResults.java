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
public class SubsistenceFeesMoaSearchResults {

	private List<SubsistenceFeesMoaSearchResultInfoDTO> subsistenceFeesMoas;
    private Long total;

    public static SubsistenceFeesMoaSearchResults emptySubsistenceFeesMoasSearchResults() {
        return SubsistenceFeesMoaSearchResults.builder().subsistenceFeesMoas(Collections.emptyList()).total(0L).build();
    }
}
