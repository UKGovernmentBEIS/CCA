package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.transform;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionChangeType;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionDetailAmountChangedHistoryPayload;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionDetailChangeType;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionDetailHistory;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionDetailPaymentStatusChangedHistoryPayload;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionHistory;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionReceivedAmountHistoryPayload;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionDetailHistoryDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionDetailUpdateAmountDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionReceivedAmountHistoryDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionUpdatePaymentStatusDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionUpdateReceivedAmountDTO;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.config.MapperConfig;

import java.math.BigDecimal;

@Mapper(componentModel = "spring", config = MapperConfig.class, imports = {BuyOutSurplusTransactionDetailChangeType.class, BuyOutSurplusTransactionChangeType.class})
public interface BuyOutSurplusTransactionHistoryMapper {

    @Mapping(target = "evidenceFiles", source = "history.payload.evidenceFiles")
    @Mapping(target = "comments", source = "history.payload.comments")
    BuyOutSurplusTransactionReceivedAmountHistoryDTO toBuyOutSurplusTransactionReceivedAmountHistoryDTO(BuyOutSurplusTransactionHistory history);

    BuyOutSurplusTransactionDetailHistoryDTO toBuyOutSurplusTransactionDetailHistoryDTO(BuyOutSurplusTransactionDetailHistory history);

    @Mapping(target = "changeType", expression = "java(BuyOutSurplusTransactionDetailChangeType.PAYMENT_STATUS_CHANGED)")
    @Mapping(target = "submitter", source = "appUser.fullName")
    @Mapping(target = "submitterId", source = "appUser.userId")
    @Mapping(target = "payload", expression = "java(mapTransactionPaymentStatusChangedHistoryPayload(paymentStatusDTO))")
    BuyOutSurplusTransactionDetailHistory toBuyOutSurplusTransactionPaymentStatusChangedHistory(BuyOutSurplusTransactionUpdatePaymentStatusDTO paymentStatusDTO, AppUser appUser);

    @Mapping(target = "submitter", source = "appUser.fullName")
    @Mapping(target = "submitterId", source = "appUser.userId")
    @Mapping(target = "changeType", expression = "java(BuyOutSurplusTransactionDetailChangeType.AMOUNT_CHANGED)")
    @Mapping(target = "payload", expression = "java(mapTransactionDetailAmountChangedHistoryPayload(updateAmountDTO))")
    BuyOutSurplusTransactionDetailHistory toBuyOutSurplusTransactionAmountChangedHistory(BuyOutSurplusTransactionDetailUpdateAmountDTO updateAmountDTO, AppUser appUser);

    @Mapping(target = "submitter", source = "appUser.fullName")
    @Mapping(target = "submitterId", source = "appUser.userId")
    @Mapping(target = "changeType", expression = "java(BuyOutSurplusTransactionChangeType.RECEIVED_AMOUNT_CHANGED)")
    @Mapping(target = "payload", expression = "java(mapTransactionAmountChangedHistoryPayload(updateAmountDTO, previousReceivedAmount))")
    BuyOutSurplusTransactionHistory toBuyOutSurplusTransactionReceivedAmountHistory(BuyOutSurplusTransactionUpdateReceivedAmountDTO updateAmountDTO, BigDecimal previousReceivedAmount, AppUser appUser);

    default BuyOutSurplusTransactionDetailPaymentStatusChangedHistoryPayload mapTransactionPaymentStatusChangedHistoryPayload(BuyOutSurplusTransactionUpdatePaymentStatusDTO paymentStatusDTO) {
        return BuyOutSurplusTransactionDetailPaymentStatusChangedHistoryPayload.builder()
                .paymentStatus(paymentStatusDTO.getStatus())
                .type(BuyOutSurplusTransactionDetailChangeType.PAYMENT_STATUS_CHANGED)
                .evidenceFiles(paymentStatusDTO.getEvidenceFiles())
                .comments(paymentStatusDTO.getComments())
                .paymentDate(paymentStatusDTO.getPaymentDate())
                .dateOfAppeal(paymentStatusDTO.getDateOfAppeal())
                .build();
    }

    default BuyOutSurplusTransactionDetailAmountChangedHistoryPayload mapTransactionDetailAmountChangedHistoryPayload(BuyOutSurplusTransactionDetailUpdateAmountDTO updateAmountDTO) {
        return BuyOutSurplusTransactionDetailAmountChangedHistoryPayload.builder()
                .amount(updateAmountDTO.getAmount())
                .type(BuyOutSurplusTransactionDetailChangeType.AMOUNT_CHANGED)
                .evidenceFiles(updateAmountDTO.getEvidenceFiles())
                .comments(updateAmountDTO.getComments())
                .build();
    }

    default BuyOutSurplusTransactionReceivedAmountHistoryPayload mapTransactionAmountChangedHistoryPayload(BuyOutSurplusTransactionUpdateReceivedAmountDTO updateAmountDTO, BigDecimal previousReceivedAmount) {
        return BuyOutSurplusTransactionReceivedAmountHistoryPayload.builder()
                .adjustmentAmount(updateAmountDTO.getAdjustmentAmount())
                .previousReceivedAmount(previousReceivedAmount)
                .type(BuyOutSurplusTransactionChangeType.RECEIVED_AMOUNT_CHANGED)
                .evidenceFiles(updateAmountDTO.getEvidenceFiles())
                .comments(updateAmountDTO.getComments())
                .paymentDate(updateAmountDTO.getPaymentDate())
                .build();
    }

    @AfterMapping
    default void setReceivedAmountHistoryInfo(@MappingTarget BuyOutSurplusTransactionReceivedAmountHistoryDTO dto, BuyOutSurplusTransactionHistory history) {
        if (!history.getPayload().getType().equals(BuyOutSurplusTransactionChangeType.RECEIVED_AMOUNT_CHANGED)) {
            throw new IllegalArgumentException("Not expected payload type");
        }

        BuyOutSurplusTransactionReceivedAmountHistoryPayload receivedAmountHistoryPayload =
                (BuyOutSurplusTransactionReceivedAmountHistoryPayload) history.getPayload();

        dto.setAdjustmentAmount(receivedAmountHistoryPayload.getAdjustmentAmount());
        dto.setPaymentDate(receivedAmountHistoryPayload.getPaymentDate());
    }
}
