package uk.gov.cca.api.web.orchestrator.buyoutsurplus.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionDetailUpdateAmountDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionMarkPaymentStatusDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionUpdatePaymentStatusDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.dto.BuyOutSurplusTransactionUpdateReceivedAmountDTO;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusTransactionService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.netz.api.authorization.core.domain.AppAuthority;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.workflow.request.core.domain.constants.RequestStatuses;
import uk.gov.netz.api.workflow.request.core.service.RequestQueryService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuyOutSurplusTransactionUpdateServiceOrchestratorTest {

    @InjectMocks
    private BuyOutSurplusTransactionUpdateServiceOrchestrator serviceOrchestrator;

    @Mock
    private BuyOutSurplusTransactionService buyOutSurplusTransactionService;

    @Mock
    private RequestQueryService requestQueryService;

    @Test
    void updateTransactionDetailsPaymentStatus() {
        final Long transactionId = 1L;
        final BuyOutSurplusTransactionUpdatePaymentStatusDTO paymentStatusDTO = BuyOutSurplusTransactionUpdatePaymentStatusDTO.builder()
                .detailIds(Set.of(1L))
                .build();
        final CompetentAuthorityEnum competent = CompetentAuthorityEnum.ENGLAND;
        final AppUser appUser = AppUser.builder()
                .userId("userId")
                .authorities(List.of(AppAuthority.builder()
                        .competentAuthority(competent)
                        .build()))
                .build();

        when(requestQueryService.existByRequestTypeAndRequestStatusAndCompetentAuthority(CcaRequestType.BUY_OUT_SURPLUS_FACILITY_RUN, RequestStatuses.IN_PROGRESS, competent))
                .thenReturn(false);
        when(requestQueryService.existByRequestTypeAndRequestStatusAndCompetentAuthority(CcaRequestType.BUY_OUT_SURPLUS_RUN, RequestStatuses.IN_PROGRESS, competent))
                .thenReturn(false);

        // Invoke
        serviceOrchestrator.updateTransactionDetailsPaymentStatus(transactionId, paymentStatusDTO, appUser);

        // Verify
        verify(requestQueryService, times(1))
                .existByRequestTypeAndRequestStatusAndCompetentAuthority(CcaRequestType.BUY_OUT_SURPLUS_FACILITY_RUN, RequestStatuses.IN_PROGRESS, competent);
        verify(requestQueryService, times(1))
                .existByRequestTypeAndRequestStatusAndCompetentAuthority(CcaRequestType.BUY_OUT_SURPLUS_RUN, RequestStatuses.IN_PROGRESS, competent);
        verify(buyOutSurplusTransactionService, times(1))
                .updateTransactionDetailsPaymentStatus(transactionId, paymentStatusDTO, appUser);
    }

    @Test
    void updateTransactionDetailsPaymentStatus_BUY_OUT_SURPLUS_RUN_exist() {
        final Long transactionId = 1L;
        final BuyOutSurplusTransactionUpdatePaymentStatusDTO paymentStatusDTO = BuyOutSurplusTransactionUpdatePaymentStatusDTO.builder()
                .detailIds(Set.of(1L))
                .build();
        final CompetentAuthorityEnum competent = CompetentAuthorityEnum.ENGLAND;
        final AppUser appUser = AppUser.builder()
                .userId("userId")
                .authorities(List.of(AppAuthority.builder()
                        .competentAuthority(competent)
                        .build()))
                .build();

        when(requestQueryService.existByRequestTypeAndRequestStatusAndCompetentAuthority(CcaRequestType.BUY_OUT_SURPLUS_FACILITY_RUN, RequestStatuses.IN_PROGRESS, competent))
                .thenReturn(false);
        when(requestQueryService.existByRequestTypeAndRequestStatusAndCompetentAuthority(CcaRequestType.BUY_OUT_SURPLUS_RUN, RequestStatuses.IN_PROGRESS, competent))
                .thenReturn(true);

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class,
                () -> serviceOrchestrator.updateTransactionDetailsPaymentStatus(transactionId, paymentStatusDTO, appUser));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(CcaErrorCode.BUY_OUT_SURPLUS_IN_PROGRESS);
        verify(requestQueryService, times(1))
                .existByRequestTypeAndRequestStatusAndCompetentAuthority(CcaRequestType.BUY_OUT_SURPLUS_FACILITY_RUN, RequestStatuses.IN_PROGRESS, competent);
        verify(requestQueryService, times(1))
                .existByRequestTypeAndRequestStatusAndCompetentAuthority(CcaRequestType.BUY_OUT_SURPLUS_RUN, RequestStatuses.IN_PROGRESS, competent);
        verifyNoInteractions(buyOutSurplusTransactionService);
    }

    @Test
    void updateTransactionDetailsPaymentStatus_BUY_OUT_SURPLUS_FACILITY_RUN_exist() {
        final Long transactionId = 1L;
        final BuyOutSurplusTransactionUpdatePaymentStatusDTO paymentStatusDTO = BuyOutSurplusTransactionUpdatePaymentStatusDTO.builder()
                .detailIds(Set.of(1L))
                .build();
        final CompetentAuthorityEnum competent = CompetentAuthorityEnum.ENGLAND;
        final AppUser appUser = AppUser.builder()
                .userId("userId")
                .authorities(List.of(AppAuthority.builder()
                        .competentAuthority(competent)
                        .build()))
                .build();

        when(requestQueryService.existByRequestTypeAndRequestStatusAndCompetentAuthority(CcaRequestType.BUY_OUT_SURPLUS_FACILITY_RUN, RequestStatuses.IN_PROGRESS, competent))
                .thenReturn(true);

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class,
                () -> serviceOrchestrator.updateTransactionDetailsPaymentStatus(transactionId, paymentStatusDTO, appUser));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(CcaErrorCode.BUY_OUT_SURPLUS_IN_PROGRESS);
        verify(requestQueryService, times(1))
                .existByRequestTypeAndRequestStatusAndCompetentAuthority(CcaRequestType.BUY_OUT_SURPLUS_FACILITY_RUN, RequestStatuses.IN_PROGRESS, competent);
        verifyNoMoreInteractions(requestQueryService);
        verifyNoInteractions(buyOutSurplusTransactionService);
    }

    @Test
    void updateTransactionDetailAmount() {
        final Long transactionDetailId = 1L;
        final BuyOutSurplusTransactionDetailUpdateAmountDTO buyOutFeeUpdateDTO = BuyOutSurplusTransactionDetailUpdateAmountDTO.builder()
                .comments("comments")
                .build();
        final CompetentAuthorityEnum competent = CompetentAuthorityEnum.ENGLAND;
        final AppUser appUser = AppUser.builder()
                .userId("userId")
                .authorities(List.of(AppAuthority.builder()
                        .competentAuthority(competent)
                        .build()))
                .build();

        when(requestQueryService.existByRequestTypeAndRequestStatusAndCompetentAuthority(CcaRequestType.BUY_OUT_SURPLUS_FACILITY_RUN, RequestStatuses.IN_PROGRESS, competent))
                .thenReturn(false);
        when(requestQueryService.existByRequestTypeAndRequestStatusAndCompetentAuthority(CcaRequestType.BUY_OUT_SURPLUS_RUN, RequestStatuses.IN_PROGRESS, competent))
                .thenReturn(false);

        // Invoke
        serviceOrchestrator.updateTransactionDetailAmount(transactionDetailId, buyOutFeeUpdateDTO, appUser);

        // Verify
        verify(requestQueryService, times(1))
                .existByRequestTypeAndRequestStatusAndCompetentAuthority(CcaRequestType.BUY_OUT_SURPLUS_FACILITY_RUN, RequestStatuses.IN_PROGRESS, competent);
        verify(requestQueryService, times(1))
                .existByRequestTypeAndRequestStatusAndCompetentAuthority(CcaRequestType.BUY_OUT_SURPLUS_RUN, RequestStatuses.IN_PROGRESS, competent);
        verify(buyOutSurplusTransactionService, times(1))
                .updateTransactionDetailAmount(transactionDetailId, buyOutFeeUpdateDTO, appUser);
    }

    @Test
    void markTransactionDetailsPaymentStatus() {
        final Long transactionDetailId = 1L;
        final BuyOutSurplusTransactionMarkPaymentStatusDTO markDTO = BuyOutSurplusTransactionMarkPaymentStatusDTO.builder()
                .comments("comments")
                .build();
        final CompetentAuthorityEnum competent = CompetentAuthorityEnum.ENGLAND;
        final AppUser appUser = AppUser.builder()
                .userId("userId")
                .authorities(List.of(AppAuthority.builder()
                        .competentAuthority(competent)
                        .build()))
                .build();

        when(requestQueryService.existByRequestTypeAndRequestStatusAndCompetentAuthority(CcaRequestType.BUY_OUT_SURPLUS_FACILITY_RUN, RequestStatuses.IN_PROGRESS, competent))
                .thenReturn(false);
        when(requestQueryService.existByRequestTypeAndRequestStatusAndCompetentAuthority(CcaRequestType.BUY_OUT_SURPLUS_RUN, RequestStatuses.IN_PROGRESS, competent))
                .thenReturn(false);

        // Invoke
        serviceOrchestrator.markTransactionDetailsPaymentStatus(transactionDetailId, markDTO, appUser);

        // Verify
        verify(requestQueryService, times(1))
                .existByRequestTypeAndRequestStatusAndCompetentAuthority(CcaRequestType.BUY_OUT_SURPLUS_FACILITY_RUN, RequestStatuses.IN_PROGRESS, competent);
        verify(requestQueryService, times(1))
                .existByRequestTypeAndRequestStatusAndCompetentAuthority(CcaRequestType.BUY_OUT_SURPLUS_RUN, RequestStatuses.IN_PROGRESS, competent);
        verify(buyOutSurplusTransactionService, times(1))
                .markTransactionDetailsPaymentStatus(transactionDetailId, markDTO, appUser);
    }

    @Test
    void updateTransactionReceivedAmount() {
        final Long transactionId = 99L;
        final BigDecimal adjustmentAmount = BigDecimal.valueOf(400);
        final LocalDate paymentDate = LocalDate.of(2026, 5, 25);
        final String comments = "bla bla";
        final Map<UUID, String> evidenceFiles = Map.of(UUID.randomUUID(), "fileName");
        final CompetentAuthorityEnum competent = CompetentAuthorityEnum.ENGLAND;
        final AppUser appUser = AppUser.builder()
                .userId("userId")
                .authorities(List.of(AppAuthority.builder()
                        .competentAuthority(competent)
                        .build()))
                .build();
        final BuyOutSurplusTransactionUpdateReceivedAmountDTO updateReceivedAmountDTO = BuyOutSurplusTransactionUpdateReceivedAmountDTO.builder()
                .adjustmentAmount(adjustmentAmount)
                .comments(comments)
                .paymentDate(paymentDate)
                .evidenceFiles(evidenceFiles)
                .build();

        // invoke
        serviceOrchestrator.updateTransactionReceivedAmount(transactionId, updateReceivedAmountDTO, appUser);

        // verify
        verify(requestQueryService, times(1))
                .existByRequestTypeAndRequestStatusAndCompetentAuthority(CcaRequestType.BUY_OUT_SURPLUS_FACILITY_RUN, RequestStatuses.IN_PROGRESS, competent);
        verify(requestQueryService, times(1))
                .existByRequestTypeAndRequestStatusAndCompetentAuthority(CcaRequestType.BUY_OUT_SURPLUS_RUN, RequestStatuses.IN_PROGRESS, competent);
        verify(buyOutSurplusTransactionService, times(1)).updateTransactionReceivedAmount(transactionId, updateReceivedAmountDTO, appUser);
    }
}
