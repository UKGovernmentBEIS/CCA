package uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacilityTemplateData {

	private String id;
	private String name;
	private String uketsId;
	private String address;
}
