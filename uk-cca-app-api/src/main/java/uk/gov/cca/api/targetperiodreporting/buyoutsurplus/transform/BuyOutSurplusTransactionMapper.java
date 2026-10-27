package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.transform;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import uk.gov.cca.api.account.domain.dto.TargetUnitAccountBusinessInfoDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.AccountBuyOutSurplusTransactionCreate;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransaction;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionCreate;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionDetail;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionDetailCreate;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.AccountBuyOutSurplusTransactionDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionDetailInfoDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionMarkPaymentStatusDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionUpdatePaymentStatusDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.FacilityBuyOutSurplusTransactionDTO;
import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataResourceType;
import uk.gov.cca.api.targetperiodreporting.performancedata.domain.dto.PerformanceDataDetailsInfoDTO;
import uk.gov.netz.api.common.config.MapperConfig;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

@Mapper(componentModel = "spring", config = MapperConfig.class, imports = {PerformanceDataResourceType.class, BigDecimal.class})
public interface BuyOutSurplusTransactionMapper {

    @Mapping(target = "invoicedBuyOutFee", source = "dto.buyOutFee")
    @Mapping(target = "performanceDataResourceType", expression = "java(PerformanceDataResourceType.ACCOUNT)")
    BuyOutSurplusTransaction toBuyOutSurplusTransaction(AccountBuyOutSurplusTransactionCreate dto);
    
    @Mapping(target = "receivedAmount", expression = "java(BigDecimal.ZERO)")
    @Mapping(target = "invoicedBuyOutFee", ignore = true)
    BuyOutSurplusTransaction toBuyOutSurplusTransaction(BuyOutSurplusTransactionCreate dto, PerformanceDataResourceType performanceDataResourceType);
    
    @AfterMapping
    default void setTransactionData(@MappingTarget BuyOutSurplusTransaction transaction, BuyOutSurplusTransactionCreate dto) {
        BigDecimal invoicedBuyOutFee = dto.getDetails().stream()
                .map(BuyOutSurplusTransactionDetailCreate::getBuyOutFee)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        transaction.setInvoicedBuyOutFee(invoicedBuyOutFee);
        
        dto.getDetails().forEach(det -> {
            transaction.addDetail(toBuyOutSurplusTransactionDetail(det));
        });
    }
    
    @AfterMapping
    default void setTransactionDetail(@MappingTarget BuyOutSurplusTransaction transaction, AccountBuyOutSurplusTransactionCreate dto) {
        transaction.addDetail(toBuyOutSurplusTransactionDetail(dto));
    }

    BuyOutSurplusTransactionDetail toBuyOutSurplusTransactionDetail(AccountBuyOutSurplusTransactionCreate dto);
    
    BuyOutSurplusTransactionDetail toBuyOutSurplusTransactionDetail(BuyOutSurplusTransactionDetailCreate dto);

    BuyOutSurplusTransactionDetailInfoDTO toBuyOutSurplusTransactionDetailInfoDTO(BuyOutSurplusTransactionDetail entity);

    @Mapping(target = "id", source = "transaction.id")
    @Mapping(target = "detailId", source = "transactionDetail.id")
    @Mapping(target = "dueDate", source = "transaction.invoicedPaymentDeadline")
    @Mapping(target = "priBuyOutCost", source = "transactionDetail.buyOutSurplusContainer.priBuyOutCost")
    @Mapping(target = "invoicedBuyOutFee", source = "transactionDetail.buyOutSurplusContainer.invoicedBuyOutFee")
    @Mapping(target = "invoicedSurplusGained", source = "transactionDetail.buyOutSurplusContainer.invoicedSurplusGained")
    @Mapping(target = "invoicedPreviousPaidFees", source = "transactionDetail.buyOutSurplusContainer.invoicedPreviousPaidFees")
    AccountBuyOutSurplusTransactionDTO toAccountBuyOutSurplusTransactionDTO(BuyOutSurplusTransaction transaction,
            BuyOutSurplusTransactionDetail transactionDetail, PerformanceDataDetailsInfoDTO performanceData, FileInfoDTO fileInfoDTO);

    @Mapping(target = "accountBusinessId", source = "account.businessId")
    @Mapping(target = "operatorName", source = "account.name")
    @Mapping(target = "dueDate", source = "transaction.invoicedPaymentDeadline")
    @Mapping(target = "targetPeriodType", source = "transaction.targetPeriod.businessId")
    FacilityBuyOutSurplusTransactionDTO toFacilityBuyOutSurplusTransactionDTO(
            BuyOutSurplusTransaction transaction, TargetUnitAccountBusinessInfoDTO account, FileInfoDTO fileInfoDTO);

    @AfterMapping
    default void setFacilityTransactionReconciliation(@MappingTarget FacilityBuyOutSurplusTransactionDTO dto, BuyOutSurplusTransaction transaction) {
        Set<BuyOutSurplusPaymentStatus> detailStatuses = transaction.getTransactionDetailList().stream()
                .map(BuyOutSurplusTransactionDetail::getPaymentStatus)
                .collect(Collectors.toSet());

        Set<BuyOutSurplusPaymentStatus> expectedStatuses = BuyOutSurplusPaymentStatus
                .getExpectedPaymentStatusesByChargeType(transaction.getChargeType());
        Set<BuyOutSurplusPaymentStatus> completedStatuses = BuyOutSurplusPaymentStatus.getCompletedPayments();
        Set<BuyOutSurplusPaymentStatus> awaitingStatuses = BuyOutSurplusPaymentStatus.getAwaitingPayments();

        BuyOutSurplusPaymentStatus paymentStatus = detailStatuses.stream()
                .min(Comparator.comparingInt(BuyOutSurplusPaymentStatus::getPriority))
                .orElse(null);
        BigDecimal expectedAmount = transaction.getTransactionDetailList().stream()
                .filter(d -> expectedStatuses.contains(d.getPaymentStatus()))
                .map(BuyOutSurplusTransactionDetail::getBuyOutFee).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal amountAllocated = transaction.getTransactionDetailList().stream()
                .filter(d -> completedStatuses.contains(d.getPaymentStatus()))
                .map(BuyOutSurplusTransactionDetail::getBuyOutFee).reduce(BigDecimal.ZERO, BigDecimal::add);
        int expectedMarkedFacilities = (int) transaction.getTransactionDetailList().stream()
                .filter(d -> expectedStatuses.contains(d.getPaymentStatus()))
                .count();
        int actualMarkedFacilities = (int) transaction.getTransactionDetailList().stream()
                .filter(d -> completedStatuses.contains(d.getPaymentStatus()))
                .count();
        boolean hasBulkAction = transaction.getTransactionDetailList().stream()
                .anyMatch(d -> awaitingStatuses.contains(d.getPaymentStatus()));

        dto.setPaymentStatus(paymentStatus);
        dto.setExpectedAmount(expectedAmount);
        dto.setAmountAllocated(amountAllocated);
        dto.setExpectedMarkedFacilities(expectedMarkedFacilities);
        dto.setActualMarkedFacilities(actualMarkedFacilities);
        dto.setHasBulkAction(hasBulkAction);
    }

    BuyOutSurplusTransactionUpdatePaymentStatusDTO toBuyOutSurplusTransactionUpdatePaymentStatusDTO(BuyOutSurplusTransactionMarkPaymentStatusDTO markDTO);
}
