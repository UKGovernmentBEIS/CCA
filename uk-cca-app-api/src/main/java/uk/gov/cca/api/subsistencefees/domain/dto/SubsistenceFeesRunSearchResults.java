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
public class SubsistenceFeesRunSearchResults {

	private List<SubsistenceFeesRunSearchResultInfoDTO> subsistenceFeesRuns;
    private Long total;

    public static SubsistenceFeesRunSearchResults emptySubsistenceFeesRunsSearchResults() {
        return SubsistenceFeesRunSearchResults.builder().subsistenceFeesRuns(Collections.emptyList()).total(0L).build();
    }
}
