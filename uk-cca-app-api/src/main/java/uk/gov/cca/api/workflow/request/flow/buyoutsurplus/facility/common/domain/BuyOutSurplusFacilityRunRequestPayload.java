package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.TargetPeriodInfoDTO;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.RequestPayload;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BuyOutSurplusFacilityRunRequestPayload extends RequestPayload {

	private String submitterId;
	private TargetPeriodInfoDTO targetPeriodDetails;
	private FileInfoDTO csvFile;
}
