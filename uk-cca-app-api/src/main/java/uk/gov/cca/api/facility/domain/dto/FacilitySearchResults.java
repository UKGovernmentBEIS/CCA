package uk.gov.cca.api.facility.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacilitySearchResults {

    private List<FacilitySearchResultInfoDTO> facilities;
    private Long total;

    public static FacilitySearchResults emptyFacilitySearchResults() {
        return FacilitySearchResults.builder().facilities(Collections.emptyList()).total(0L).build();
    }
}
