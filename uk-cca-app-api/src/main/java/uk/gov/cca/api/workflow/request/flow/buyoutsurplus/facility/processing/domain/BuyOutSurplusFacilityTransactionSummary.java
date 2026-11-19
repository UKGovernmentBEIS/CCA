package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusChargeType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuyOutSurplusFacilityTransactionSummary {

	@NotNull
    private TargetPeriodType targetPeriodType;
	
	@NotNull
    private BuyOutSurplusChargeType chargeType;

	@NotNull
    private BigDecimal targetPeriodBuyOutCost;

    @NotNull
    @Positive
    private Long totalFacilities;

    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 2)
    private BigDecimal invoicedBuyOutFee;

    private FileInfoDTO officialNotice;

    private LocalDate dueDate;

}
