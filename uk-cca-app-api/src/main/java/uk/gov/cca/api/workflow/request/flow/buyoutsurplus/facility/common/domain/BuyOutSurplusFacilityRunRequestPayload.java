package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.domain;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
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
	private LocalDate creationDate;
	private List<TargetPeriodInfoDTO> targetPeriodsDetails;
	private boolean applyPrimaryRules;
	private FileInfoDTO csvFile;
	
	@Builder.Default
    private Map<Long, BuyOutSurplusFacilityAccountState> accountStates = new HashMap<>();
}
