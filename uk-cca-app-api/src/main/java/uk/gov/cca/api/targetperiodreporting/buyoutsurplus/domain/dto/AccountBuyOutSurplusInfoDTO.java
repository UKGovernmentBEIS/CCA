package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountBuyOutSurplusInfoDTO {

	private boolean excluded;

	private List<SurplusGainedDTO> surplusGainedDTOList;

}
