package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.transform;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransaction;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionAmountChangedHistoryPayload;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionChangeType;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionHistory;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusTransactionPaymentStatusChangedHistoryPayload;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionCreateDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionInfoDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionUpdateAmountDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionUpdatePaymentStatusDTO;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.config.MapperConfig;

@Mapper(componentModel = "spring", config = MapperConfig.class, imports = {BuyOutSurplusTransactionChangeType.class})
public interface BuyOutSurplusTransactionMapper {

    BuyOutSurplusTransaction toBuyOutSurplusTransaction(BuyOutSurplusTransactionCreateDTO dto);

    BuyOutSurplusTransactionInfoDTO toBuyOutSurplusTransactionInfoDTO(BuyOutSurplusTransaction entity);

    @Mapping(target = "changeType", expression = "java(BuyOutSurplusTransactionChangeType.PAYMENT_STATUS_CHANGED)")
    @Mapping(target = "submitter", source = "appUser.fullName")
    @Mapping(target = "submitterId", source = "appUser.userId")
    @Mapping(target = "payload", expression = "java(mapTransactionPaymentStatusChangedHistoryPayload(paymentStatusDTO))")
    BuyOutSurplusTransactionHistory toBuyOutSurplusTransactionPaymentStatusChangedHistory(BuyOutSurplusTransactionUpdatePaymentStatusDTO paymentStatusDTO, AppUser appUser);

    @Mapping(target = "submitter", source = "appUser.fullName")
    @Mapping(target = "submitterId", source = "appUser.userId")
    @Mapping(target = "changeType", expression = "java(BuyOutSurplusTransactionChangeType.AMOUNT_CHANGED)")
    @Mapping(target = "payload", expression = "java(mapTransactionAmountChangedHistoryPayload(updateAmountDTO))")
    BuyOutSurplusTransactionHistory toBuyOutSurplusTransactionAmountChangedHistory(BuyOutSurplusTransactionUpdateAmountDTO updateAmountDTO, AppUser appUser);

    default BuyOutSurplusTransactionPaymentStatusChangedHistoryPayload mapTransactionPaymentStatusChangedHistoryPayload(BuyOutSurplusTransactionUpdatePaymentStatusDTO paymentStatusDTO) {
        return BuyOutSurplusTransactionPaymentStatusChangedHistoryPayload.builder()
                .paymentStatus(paymentStatusDTO.getStatus())
                .type(BuyOutSurplusTransactionChangeType.PAYMENT_STATUS_CHANGED)
                .evidenceFiles(paymentStatusDTO.getEvidenceFiles())
                .comments(paymentStatusDTO.getComments())
                .paymentDate(paymentStatusDTO.getPaymentDate())
                .build();
    }

    BuyOutSurplusTransactionDTO toBuyOutSurplusTransactionDTO(BuyOutSurplusTransaction entity);


    default BuyOutSurplusTransactionAmountChangedHistoryPayload mapTransactionAmountChangedHistoryPayload(BuyOutSurplusTransactionUpdateAmountDTO updateAmountDTO) {
        return BuyOutSurplusTransactionAmountChangedHistoryPayload.builder()
                .amount(updateAmountDTO.getAmount())
                .type(BuyOutSurplusTransactionChangeType.AMOUNT_CHANGED)
                .evidenceFiles(updateAmountDTO.getEvidenceFiles())
                .comments(updateAmountDTO.getComments())
                .build();
    }
}
