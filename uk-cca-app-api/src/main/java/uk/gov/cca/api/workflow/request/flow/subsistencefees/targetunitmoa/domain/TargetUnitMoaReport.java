package uk.gov.cca.api.workflow.request.flow.subsistencefees.targetunitmoa.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain.MoaReport;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class TargetUnitMoaReport extends MoaReport {

	private Long accountId;
	private String operatorName;
}
