package uk.gov.cca.api.web.orchestrator.account.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.SurplusGainedDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountBuyOutSurplusInfoDTO {

	private boolean excluded;

	private SurplusGainedDTO surplusDetails;
}
