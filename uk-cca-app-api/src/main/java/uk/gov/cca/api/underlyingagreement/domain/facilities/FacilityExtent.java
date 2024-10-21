package uk.gov.cca.api.underlyingagreement.domain.facilities;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#areActivitiesClaimed) == (#activitiesDescriptionFile != null)}", 
		message = "underlyingagreement.facilities.facilityextent.areActivitiesClaimed")
public class FacilityExtent {

	@NotNull
	private UUID manufacturingProcessFile;
	
	@NotNull
	private UUID processFlowFile;
	
	@NotNull
	private UUID annotatedSitePlansFile;
	
	@NotNull
	private UUID eligibleProcessFile;
	
	@NotNull
	private Boolean areActivitiesClaimed;
	
	private UUID activitiesDescriptionFile;
}
