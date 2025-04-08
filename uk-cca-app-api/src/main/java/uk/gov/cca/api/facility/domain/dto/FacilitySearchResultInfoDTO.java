package uk.gov.cca.api.facility.domain.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import uk.gov.cca.api.facility.domain.FacilityDataStatus;

import java.time.LocalDate;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class FacilitySearchResultInfoDTO {

    private String id;

    private String siteName;

    private LocalDate schemeExitDate;

    private FacilityDataStatus status;
}
