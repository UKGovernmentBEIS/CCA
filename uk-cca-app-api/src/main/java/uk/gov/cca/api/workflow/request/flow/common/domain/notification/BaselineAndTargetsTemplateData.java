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
public class BaselineAndTargetsTemplateData {

	private String targetType;
	private BigDecimal throughput;
	private BigDecimal energy;
	private Boolean usedReportingMechanism;
	private String throughputUnit;
	private String energyCarbonUnit;
	private BigDecimal target;
	private BigDecimal improvement;
}
