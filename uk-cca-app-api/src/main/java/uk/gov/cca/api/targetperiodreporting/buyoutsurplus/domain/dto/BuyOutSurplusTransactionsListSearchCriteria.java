package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.netz.api.common.domain.PagingRequest;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuyOutSurplusTransactionsListSearchCriteria {
    
    @Size(min = 3, max = 255)
    private String term;
    
    @NotNull
    private TargetPeriodType targetPeriodType;
    
    private BuyOutSurplusPaymentStatus buyOutSurplusPaymentStatus;
    
    @Valid
    @NotNull
    @JsonUnwrapped
    private PagingRequest paging;
}
