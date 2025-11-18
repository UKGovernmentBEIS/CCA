package uk.gov.cca.api.facilityaudit.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.facilityaudit.domain.FacilityAuditReasonType;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FacilityAuditDTO {

    private List<FacilityAuditReasonType> reasons;

    private String comments;
}
