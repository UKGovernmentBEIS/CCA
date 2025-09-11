package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain;

import java.time.Year;
import java.util.Map;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.PerformanceAccountTemplateDataContainer;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PerformanceAccountTemplateProcessingSubmittedRequestActionPayload extends CcaRequestActionPayload {

	private TargetPeriodType targetPeriodType;
	private Year targetPeriodYear;
	private PerformanceAccountTemplateDataContainer data;

	@Override
	public Map<UUID, String> getAttachments() {
		return Map.of(UUID.fromString(data.getFile().getUuid()), data.getFile().getName());
	}

}
