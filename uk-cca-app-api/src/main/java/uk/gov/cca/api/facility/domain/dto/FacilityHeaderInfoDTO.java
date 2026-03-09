package uk.gov.cca.api.facility.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.common.domain.ResourceHeaderInfoDTO;
import uk.gov.cca.api.facility.domain.FacilityDataStatus;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class FacilityHeaderInfoDTO extends ResourceHeaderInfoDTO {

    private String businessId;
    private FacilityDataStatus status;
}
