package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.domain.BuyOutSurplusRunRequestMetadata;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class BuyOutSurplusFacilityRunRequestMetadata extends BuyOutSurplusRunRequestMetadata {

}
