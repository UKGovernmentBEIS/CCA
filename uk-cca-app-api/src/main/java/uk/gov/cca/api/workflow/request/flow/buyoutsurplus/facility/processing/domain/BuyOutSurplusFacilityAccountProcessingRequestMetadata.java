package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.domain;

import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.domain.BuyOutSurplusAccountProcessingBaseRequestMetadata;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class BuyOutSurplusFacilityAccountProcessingRequestMetadata extends BuyOutSurplusAccountProcessingBaseRequestMetadata {

	private Map<TargetPeriodType, String> buyOutTransactionCodes;
	private Map<TargetPeriodType, String> refundTransactionCodes;
}
