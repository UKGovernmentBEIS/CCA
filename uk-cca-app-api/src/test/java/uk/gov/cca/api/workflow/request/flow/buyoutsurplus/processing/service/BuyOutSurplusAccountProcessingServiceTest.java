package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.domain.BuyOutSurplusResult;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.workflow.bpmn.exception.BpmnExecutionException;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.domain.BuyOutSurplusAccountProcessingException;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.domain.BuyOutSurplusAccountState;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.validation.BuyOutSurplusViolation;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.domain.BuyOutSurplusAccountProcessingRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.domain.BuyOutSurplusAccountProcessingRequestPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuyOutSurplusAccountProcessingServiceTest {

    @InjectMocks
    private BuyOutSurplusAccountProcessingService buyOutSurplusAccountProcessingService;

    @Mock
    private RequestService requestService;

    @Spy
    private ArrayList<BuyOutSurplusAccountProcessingTargetPeriodService> buyOutSurplusAccountProcessingTargetPeriodServices;

    @Mock
    private TP6BuyOutSurplusAccountProcessingTargetPeriodService tp6BuyOutSurplusAccountProcessingTargetPeriodService;

    @BeforeEach
    void setUp() {
        buyOutSurplusAccountProcessingTargetPeriodServices.add(tp6BuyOutSurplusAccountProcessingTargetPeriodService);
    }

    @Test
    void doProcess() throws Exception {
        final String requestId = "request-id";
        final BuyOutSurplusAccountState accountState = BuyOutSurplusAccountState.builder().accountId(1L).build();

        final Request request = Request.builder()
                .metadata(BuyOutSurplusAccountProcessingRequestMetadata.builder()
                        .targetPeriodType(TargetPeriodType.TP6)
                        .build())
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(tp6BuyOutSurplusAccountProcessingTargetPeriodService.getType()).thenReturn(TargetPeriodType.TP6);

        // Invoke
        buyOutSurplusAccountProcessingService.doProcess(requestId, accountState);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(tp6BuyOutSurplusAccountProcessingTargetPeriodService, times(1)).getType();
        verify(tp6BuyOutSurplusAccountProcessingTargetPeriodService, times(1))
                .processBuyOutSurplus(request, accountState);
    }

    @Test
    void doProcess_throws_exception() {
        final String requestId = "request-id";
        final BuyOutSurplusAccountState accountState = BuyOutSurplusAccountState.builder().accountId(1L).build();

        final Request request = Request.builder()
                .metadata(BuyOutSurplusAccountProcessingRequestMetadata.builder()
                        .targetPeriodType(TargetPeriodType.TP5)
                        .build())
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(tp6BuyOutSurplusAccountProcessingTargetPeriodService.getType()).thenReturn(TargetPeriodType.TP6);

        // Invoke
        BpmnExecutionException ex = assertThrows(BpmnExecutionException.class, () ->
                buyOutSurplusAccountProcessingService.doProcess(requestId, accountState));

        // Verify
        assertThat(ex.getErrors()).containsExactly(BuyOutSurplusViolation.BuyOutSurplusViolationMessage.PROCESS_FAILED.getMessage());
        verify(requestService, times(1)).findRequestById(requestId);
        verify(tp6BuyOutSurplusAccountProcessingTargetPeriodService, times(1)).getType();
    }

    @Test
    void doProcess_throws_business_exception() throws Exception {
        final String requestId = "request-id";
        final BuyOutSurplusAccountState accountState = BuyOutSurplusAccountState.builder().accountId(1L).build();

        final Request request = Request.builder()
                .metadata(BuyOutSurplusAccountProcessingRequestMetadata.builder()
                        .targetPeriodType(TargetPeriodType.TP6)
                        .build())
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(tp6BuyOutSurplusAccountProcessingTargetPeriodService.getType()).thenReturn(TargetPeriodType.TP6);
        doThrow(new BuyOutSurplusAccountProcessingException(BuyOutSurplusViolation.BuyOutSurplusViolationMessage.PREVIOUS_PAID_FEES_FAILED))
                .when(tp6BuyOutSurplusAccountProcessingTargetPeriodService).processBuyOutSurplus(request, accountState);

        // Invoke
        BpmnExecutionException ex = assertThrows(BpmnExecutionException.class, () ->
                buyOutSurplusAccountProcessingService.doProcess(requestId, accountState));

        // Verify
        assertThat(ex.getErrors()).containsExactly(BuyOutSurplusViolation.BuyOutSurplusViolationMessage.PREVIOUS_PAID_FEES_FAILED.getMessage());
        verify(requestService, times(1)).findRequestById(requestId);
        verify(tp6BuyOutSurplusAccountProcessingTargetPeriodService, times(1))
                .processBuyOutSurplus(request, accountState);
    }

    @Test
    void complete() {
        final String requestId = "request-id";
        final BuyOutSurplusAccountState accountState = BuyOutSurplusAccountState.builder().accountId(1L).build();

        final Request request = Request.builder()
                .payload(BuyOutSurplusAccountProcessingRequestPayload.builder()
                        .buyOutSurplus(BuyOutSurplusResult.builder()
                                .transactionCode("transaction")
                                .paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)
                                .build())
                        .build())
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        // Invoke
        buyOutSurplusAccountProcessingService.complete(requestId, accountState);

        // Verify
        assertThat(accountState.isSucceeded()).isTrue();
        assertThat(accountState.getTransactionCode()).isEqualTo("transaction");
        assertThat(accountState.getPaymentStatus()).isEqualTo(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT);
        verify(requestService, times(1)).findRequestById(requestId);
    }
}
