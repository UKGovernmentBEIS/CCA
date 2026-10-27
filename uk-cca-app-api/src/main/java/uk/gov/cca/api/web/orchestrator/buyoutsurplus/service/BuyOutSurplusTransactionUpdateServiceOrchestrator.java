package uk.gov.cca.api.web.orchestrator.buyoutsurplus.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionDetailUpdateAmountDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionMarkPaymentStatusDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionUpdatePaymentStatusDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionUpdateReceivedAmountDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusTransactionService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.workflow.request.core.domain.constants.RequestStatuses;
import uk.gov.netz.api.workflow.request.core.service.RequestQueryService;

@Service
@RequiredArgsConstructor
public class BuyOutSurplusTransactionUpdateServiceOrchestrator {

    private final BuyOutSurplusTransactionService buyOutSurplusTransactionService;
    private final RequestQueryService requestQueryService;

    @Transactional
    public void updateTransactionDetailsPaymentStatus(Long transactionId, BuyOutSurplusTransactionUpdatePaymentStatusDTO paymentStatusDTO, AppUser appUser) {
        validateNoInProgressWorkflow(appUser);
        buyOutSurplusTransactionService.updateTransactionDetailsPaymentStatus(transactionId, paymentStatusDTO, appUser);
    }

    @Transactional
    public void updateTransactionDetailAmount(Long transactionDetailId, BuyOutSurplusTransactionDetailUpdateAmountDTO updateAmountDTO, AppUser appUser) {
        validateNoInProgressWorkflow(appUser);
        buyOutSurplusTransactionService.updateTransactionDetailAmount(transactionDetailId, updateAmountDTO, appUser);
    }

    @Transactional
    public void markTransactionDetailsPaymentStatus(Long transactionId, BuyOutSurplusTransactionMarkPaymentStatusDTO markDTO, AppUser appUser) {
        validateNoInProgressWorkflow(appUser);
        buyOutSurplusTransactionService.markTransactionDetailsPaymentStatus(transactionId, markDTO, appUser);
    }

    @Transactional
    public void updateTransactionReceivedAmount(Long transactionId, BuyOutSurplusTransactionUpdateReceivedAmountDTO updateAmountDTO, AppUser appUser) {
        validateNoInProgressWorkflow(appUser);
        buyOutSurplusTransactionService.updateTransactionReceivedAmount(transactionId, updateAmountDTO, appUser);
    }

    private void validateNoInProgressWorkflow(AppUser appUser) {
        boolean hasInProgressFacility = requestQueryService.existByRequestTypeAndRequestStatusAndCompetentAuthority(
                CcaRequestType.BUY_OUT_SURPLUS_FACILITY_RUN, RequestStatuses.IN_PROGRESS, appUser.getCompetentAuthority());
        if(hasInProgressFacility) {
            throw new BusinessException(CcaErrorCode.BUY_OUT_SURPLUS_IN_PROGRESS);
        }

        boolean hasInProgress = requestQueryService.existByRequestTypeAndRequestStatusAndCompetentAuthority(
                CcaRequestType.BUY_OUT_SURPLUS_RUN, RequestStatuses.IN_PROGRESS, appUser.getCompetentAuthority());
        if(hasInProgress) {
            throw new BusinessException(CcaErrorCode.BUY_OUT_SURPLUS_IN_PROGRESS);
        }
    }
}
