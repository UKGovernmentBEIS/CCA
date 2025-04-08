package uk.gov.cca.api.buyoutsurplus.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import uk.gov.cca.api.buyoutsurplus.domain.BuyOutPaymentStatus;
import uk.gov.cca.api.buyoutsurplus.domain.BuyOutSurplusEntity;
import uk.gov.cca.api.buyoutsurplus.domain.dto.BuyOutSurplusDTO;
import uk.gov.cca.api.buyoutsurplus.repository.BuyOutSurplusRepository;
import uk.gov.cca.api.buyoutsurplus.transform.BuyOutSurplusMapper;

import java.util.List;
import java.util.Set;

@Validated
@Service
@RequiredArgsConstructor
public class BuyOutSurplusService {

    private final BuyOutSurplusRepository buyOutSurplusRepository;
    private static final BuyOutSurplusMapper BUY_OUT_SURPLUS_MAPPER = Mappers.getMapper(BuyOutSurplusMapper.class);

    @Transactional
    public BuyOutSurplusEntity createBuyOutSurplus(@NotNull @Valid BuyOutSurplusDTO buyOutSurplusDTO) {

        // Submit
        BuyOutSurplusEntity entity = BUY_OUT_SURPLUS_MAPPER.toBuyOutFeeTransactionEntity(buyOutSurplusDTO);

        return buyOutSurplusRepository.save(entity);
    }

    @Transactional
    public void updateBuyOutSurplusToTerminated(Long accountId) {
        BuyOutPaymentStatus terminatedStatus = BuyOutPaymentStatus.TERMINATED;
        Set<BuyOutPaymentStatus> paymentStatuses = Set.of(BuyOutPaymentStatus.AWAITING_PAYMENT,
                BuyOutPaymentStatus.AWAITING_REFUND);

        List<BuyOutSurplusEntity> entities = buyOutSurplusRepository
                .findAllByAccountIdAndPaymentStatusIn(accountId, paymentStatuses);

        // TODO add history change

        // Update status to TERMINATED
        entities.forEach(entity -> {
            entity.setPaymentStatus(terminatedStatus);
            entity.getBuyOutSurplusContainer().setInvoicedPaymentDeadline(null);
        });
        buyOutSurplusRepository.saveAll(entities);
    }
}
