package uk.gov.cca.api.facilityaudit.domain.dto;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.facilityaudit.domain.FacilityAuditReasonType;
import uk.gov.netz.api.common.validation.SpELExpression;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#auditRequired) == (#reasons?.size() gt 0)}",
		message = "facilityAudit.edit.reasons")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#auditRequired) == (#comments != null)}",
		message = "facilityAudit.edit.comments")
public class FacilityAuditUpdateDTO {

	@NotNull
	private Boolean auditRequired;

	private List<FacilityAuditReasonType> reasons;

	@Size(max = 10000)
	private String comments;
}
