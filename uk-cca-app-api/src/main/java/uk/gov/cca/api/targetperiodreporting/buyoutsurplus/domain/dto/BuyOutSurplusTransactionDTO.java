package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusChargeType;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class BuyOutSurplusTransactionDTO {
    private Long id;
    private String transactionCode;
    private String accountBusinessId;
    private String operatorName;
    private TargetPeriodType targetPeriodType;
    private BuyOutSurplusChargeType chargeType;
    private FileInfoDTO fileInfoDTO;
    private LocalDateTime creationDate;
    private LocalDate dueDate;
    private BigDecimal invoicedBuyOutFee;
    private BuyOutSurplusPaymentStatus paymentStatus;
}
