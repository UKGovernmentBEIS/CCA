package uk.gov.cca.api.facility.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.common.domain.SchemeVersion;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FacilityDTO {

    private Long id;

    private String facilityBusinessId;

    private String siteName;

    private LocalDateTime createdDate;

    private LocalDate closedDate;

    private Long accountId;

    private Set<SchemeVersion> participatingSchemeVersions;
}
