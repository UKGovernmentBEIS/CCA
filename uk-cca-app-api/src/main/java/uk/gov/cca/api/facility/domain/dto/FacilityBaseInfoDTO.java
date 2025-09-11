package uk.gov.cca.api.facility.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FacilityBaseInfoDTO {

    private Long id;

    private String facilityId;

    private String siteName;
}
