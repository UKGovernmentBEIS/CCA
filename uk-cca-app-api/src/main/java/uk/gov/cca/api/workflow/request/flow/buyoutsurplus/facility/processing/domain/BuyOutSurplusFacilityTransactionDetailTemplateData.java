package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuyOutSurplusFacilityTransactionDetailTemplateData {
    private String facilityBusinessId;
    private String siteName;
    private String facilityAddress;
    private LocalDate performanceDataSubmissionDate;
    private BigDecimal buyOutRequired;
    private BigDecimal surplusUsed;
    private BigDecimal netShortFall;
    private BigDecimal invoicedBuyOutFee;
    private BigDecimal previousPaidFees;
    private BigDecimal performanceDataBuyOutFee;
    private BigDecimal surplusGained;
}
