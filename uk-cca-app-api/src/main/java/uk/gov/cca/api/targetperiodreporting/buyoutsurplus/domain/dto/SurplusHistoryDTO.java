package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SurplusHistoryDTO {

	private BigDecimal surplusGained;

	private String comments;

	private String submitter;

	private LocalDateTime submissionDate;
}
