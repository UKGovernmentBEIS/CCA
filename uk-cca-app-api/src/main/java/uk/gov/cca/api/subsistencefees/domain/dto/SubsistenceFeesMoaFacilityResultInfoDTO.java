package uk.gov.cca.api.subsistencefees.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubsistenceFeesMoaFacilityResultInfoDTO {

    private Long moaFacilityId;

    private String facilityId;

    private String facilityName;
}
