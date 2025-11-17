package uk.gov.cca.api.workflow.request.flow.common.domain.notification;

import java.math.BigDecimal;

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
	private BigDecimal tp7Target;
	private BigDecimal tp8Target;
	private BigDecimal tp9Target;
}
