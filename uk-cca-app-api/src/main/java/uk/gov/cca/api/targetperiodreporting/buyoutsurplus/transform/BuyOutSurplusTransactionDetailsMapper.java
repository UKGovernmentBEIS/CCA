package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransaction;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionDetailsDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionSummaryDTO;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.PerformanceDataDetailsInfoDTO;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface BuyOutSurplusTransactionDetailsMapper {
    
    @Mapping(target = "id", source = "buyOutSurplusTransactionDTO.id")
    @Mapping(target = "transactionCode", source = "buyOutSurplusTransactionDTO.transactionCode")
    @Mapping(target = "accountBusinessId", source = "performanceDataDetailsInfoDTO.accountBusinessId")
    @Mapping(target = "operatorName", source = "performanceDataDetailsInfoDTO.operatorName")
    @Mapping(target = "targetPeriodType", source = "performanceDataDetailsInfoDTO.targetPeriodType")
    @Mapping(target = "targetPeriodResultType", source = "performanceDataDetailsInfoDTO.targetPeriodResultType")
    @Mapping(target = "reportVersion", source = "performanceDataDetailsInfoDTO.reportVersion")
    @Mapping(target = "submissionType", source = "performanceDataDetailsInfoDTO.submissionType")
    @Mapping(target = "creationDate", source = "buyOutSurplusTransactionDTO.creationDate")
    @Mapping(target = "dueDate", source = "buyOutSurplusTransactionDTO.buyOutSurplusContainer.invoicedPaymentDeadline")
    @Mapping(target = "paymentStatus", source = "buyOutSurplusTransactionDTO.paymentStatus")
    @Mapping(target = "chargeType", source = "buyOutSurplusTransactionDTO.buyOutSurplusContainer.chargeType")
    @Mapping(target = "priBuyOutCarbon", source = "performanceDataDetailsInfoDTO.priBuyOutCarbon")
    @Mapping(target = "priBuyOutCost", source = "buyOutSurplusTransactionDTO.buyOutSurplusContainer.priBuyOutCost")
    @Mapping(target = "invoicedBuyOutFee", source = "buyOutSurplusTransactionDTO.buyOutSurplusContainer.invoicedBuyOutFee")
    @Mapping(target = "invoicedSurplusGained", source = "buyOutSurplusTransactionDTO.buyOutSurplusContainer.invoicedSurplusGained")
    @Mapping(target = "invoicedPreviousPaidFees", source = "buyOutSurplusTransactionDTO.buyOutSurplusContainer.invoicedPreviousPaidFees")
    @Mapping(target = "buyOutFee", source = "buyOutSurplusTransactionDTO.buyOutFee")
    BuyOutSurplusTransactionDetailsDTO toBuyOutSurplusTransactionDetailsDTO(
            BuyOutSurplusTransactionDTO buyOutSurplusTransactionDTO,
            PerformanceDataDetailsInfoDTO performanceDataDetailsInfoDTO,
            FileInfoDTO fileInfoDTO
    );

    @Mapping(target = "initialAmount", source = "entity.buyOutSurplusContainer.invoicedBuyOutFee")
    @Mapping(target = "currentAmount", source = "entity.buyOutFee")
    BuyOutSurplusTransactionSummaryDTO toBuyOutSurplusTransactionSummaryDTO(BuyOutSurplusTransaction entity);
}
