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
import uk.gov.netz.api.common.domain.PagingRequest;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacilityBuyOutSurplusTransactionDetailsSearchCriteria {

    @Size(min = 3, max = 255)
    private String term;

    private BuyOutSurplusPaymentStatus paymentStatus;

    @Valid
    @NotNull
    @JsonUnwrapped
    private PagingRequest paging;
}
