package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.run.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;
import uk.gov.cca.api.files.attachments.service.CcaFileAttachmentService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.core.domain.constants.CcaRequestStatuses;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.domain.BuyOutSurplusAccountState;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.domain.BuyOutSurplusErrorType;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.domain.BuyOutSurplusRunSummary;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.domain.BuyOutSurplusRunRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.domain.BuyOutSurplusRunRequestPayload;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.run.domain.BuyOutSurplusRunCompletedRequestActionPayload;
import uk.gov.netz.api.files.common.domain.FileStatus;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuyOutSurplusRunServiceTest {

    @InjectMocks
    private BuyOutSurplusRunService buyOutSurplusRunService;

    @Mock
    private RequestService requestService;

    @Mock
    private CcaFileAttachmentService ccaFileAttachmentService;

    @Test
    void submit() {
        final String requestId = "requestId";
        final String submitterId = "regulator";

        final Request request = Request.builder()
                .payload(BuyOutSurplusRunRequestPayload.builder()
                        .submitterId(submitterId)
                        .build())
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        // Invoke
        buyOutSurplusRunService.submit(requestId);

        // Verify
        assertThat(request.getSubmissionDate()).isNotNull();
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestService, times(1)).addActionToRequest(
                request, null, CcaRequestActionType.BUY_OUT_SURPLUS_RUN_SUBMITTED, submitterId);
    }

    @Test
    void accountProcessingCompleted() {
        final String requestId = "requestId";
        final Long accountId = 1L;
        final BuyOutSurplusAccountState buyOutSurplusAccountState = BuyOutSurplusAccountState.builder()
                .accountId(accountId)
                .build();

        Request request = Request.builder()
                .id(requestId)
                .payload(BuyOutSurplusRunRequestPayload.builder()
                        .buyOutSurplusAccountStates(new HashMap<>())
                        .build())
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        // Invoke
        buyOutSurplusRunService.accountProcessingCompleted(requestId, accountId, buyOutSurplusAccountState);

        // Verify
        assertThat(((BuyOutSurplusRunRequestPayload) request.getPayload()).getBuyOutSurplusAccountStates())
                .containsExactlyEntriesOf(Map.of(accountId, buyOutSurplusAccountState));
        verify(requestService, times(1)).findRequestById(requestId);
    }

    @Test
    void createCsvFile() throws IOException {
        final String requestId = "requestId";
        final String submitterId = "regulator";

        BuyOutSurplusRunRequestPayload requestPayload = BuyOutSurplusRunRequestPayload.builder()
                .submitterId(submitterId)
                .buyOutSurplusAccountStates(Map.of(
                        1L, BuyOutSurplusAccountState.builder().businessId("account1").succeeded(true).build()
                ))
                .build();
        Request request = Request.builder()
                .id(requestId)
                .payload(requestPayload)
                .build();
        final String fileCsv = "fileCsv";

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(ccaFileAttachmentService.createSystemFileAttachment(any(), eq(FileStatus.SUBMITTED), eq(submitterId)))
                .thenReturn(fileCsv);

        // Invoke
        buyOutSurplusRunService.createCsvFile(requestId);

        // Verify
        assertThat(requestPayload.getErrorType()).isNull();
        assertThat(requestPayload.getCsvFile()).isNotNull();
        verify(requestService, times(1)).findRequestById(requestId);
        verify(ccaFileAttachmentService, times(1))
                .createSystemFileAttachment(any(), eq(FileStatus.SUBMITTED), eq(submitterId));
    }

    @Test
    void createCsvFile_no_accounts() {
        final String requestId = "requestId";
        final String submitterId = "regulator";

        BuyOutSurplusRunRequestPayload requestPayload = BuyOutSurplusRunRequestPayload.builder()
                .submitterId(submitterId)
                .buyOutSurplusAccountStates(Map.of())
                .build();
        Request request = Request.builder()
                .id(requestId)
                .payload(requestPayload)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        // Invoke
        buyOutSurplusRunService.createCsvFile(requestId);

        // Verify
        assertThat(requestPayload.getErrorType()).isNull();
        assertThat(requestPayload.getCsvFile()).isNull();
        verify(requestService, times(1)).findRequestById(requestId);
        verifyNoInteractions(ccaFileAttachmentService);
    }

    @Test
    void createCsvFile_throw_exception() throws IOException {
        final String requestId = "requestId";
        final String submitterId = "regulator";

        BuyOutSurplusRunRequestPayload requestPayload = BuyOutSurplusRunRequestPayload.builder()
                .submitterId(submitterId)
                .buyOutSurplusAccountStates(Map.of(
                        1L, BuyOutSurplusAccountState.builder().businessId("account1").succeeded(true).build()
                ))
                .build();
        Request request = Request.builder()
                .id(requestId)
                .payload(requestPayload)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(ccaFileAttachmentService.createSystemFileAttachment(any(), eq(FileStatus.SUBMITTED), eq(submitterId)))
                .thenThrow(new IOException("test"));

        // Invoke
        buyOutSurplusRunService.createCsvFile(requestId);

        // Verify
        assertThat(requestPayload.getErrorType()).isEqualTo(BuyOutSurplusErrorType.GENERATE_CSV_FAILED);
        assertThat(requestPayload.getCsvFile()).isNull();
        verify(requestService, times(1)).findRequestById(requestId);
        verify(ccaFileAttachmentService, times(1))
                .createSystemFileAttachment(any(), eq(FileStatus.SUBMITTED), eq(submitterId));
    }

    @Test
    void complete() {
        final String requestId = "requestId";
        final String submitterId = "regulator";

        final Map<Long, BuyOutSurplusAccountState> accountStateMap = Map.of(
                1L, BuyOutSurplusAccountState.builder().succeeded(true).build(),
                2L, BuyOutSurplusAccountState.builder().succeeded(true).paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_REFUND).transactionCode("tr2").build(),
                3L, BuyOutSurplusAccountState.builder().succeeded(true).paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT).transactionCode("tr3").build(),
                4L, BuyOutSurplusAccountState.builder().succeeded(true).paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT).transactionCode("tr4").build()
        );
        BuyOutSurplusRunRequestPayload requestPayload = BuyOutSurplusRunRequestPayload.builder()
                .submitterId(submitterId)
                .buyOutSurplusAccountStates(accountStateMap)
                .build();
        BuyOutSurplusRunRequestMetadata metadata = BuyOutSurplusRunRequestMetadata.builder().build();
        Request request = Request.builder()
                .id(requestId)
                .metadata(metadata)
                .payload(requestPayload)
                .build();
        final BuyOutSurplusRunSummary runSummary = BuyOutSurplusRunSummary.builder()
                .totalAccounts(4L)
                .failedAccounts(0L)
                .buyOutTransactions(2L)
                .refundedTransactions(1L)
                .build();
        final BuyOutSurplusRunCompletedRequestActionPayload actionPayload =
                BuyOutSurplusRunCompletedRequestActionPayload.builder()
                        .payloadType(CcaRequestActionPayloadType.BUY_OUT_SURPLUS_RUN_COMPLETED_PAYLOAD)
                        .runSummary(runSummary)
                        .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        // Invoke
        buyOutSurplusRunService.complete(requestId);

        // Verify
        assertThat(((BuyOutSurplusRunRequestPayload) request.getPayload()).getRunSummary()).isEqualTo(runSummary);
        assertThat(((BuyOutSurplusRunRequestMetadata) request.getMetadata()).getTotalAccounts()).isEqualTo(4L);
        assertThat(((BuyOutSurplusRunRequestMetadata) request.getMetadata()).getFailedAccounts()).isZero();
        verify(requestService, never()).updateRequestStatus(anyString(), anyString());
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestService, times(1)).addActionToRequest(
                request, actionPayload, CcaRequestActionType.BUY_OUT_SURPLUS_RUN_COMPLETED, submitterId);
    }

    @Test
    void complete_with_failures() {
        final String requestId = "requestId";
        final String submitterId = "regulator";

        final Map<Long, BuyOutSurplusAccountState> accountStateMap = Map.of(
                1L, BuyOutSurplusAccountState.builder().succeeded(true).build(),
                2L, BuyOutSurplusAccountState.builder().succeeded(false).build(),
                3L, BuyOutSurplusAccountState.builder().succeeded(true).paymentStatus(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT).transactionCode("tr").build()
        );
        BuyOutSurplusRunRequestPayload requestPayload = BuyOutSurplusRunRequestPayload.builder()
                .submitterId(submitterId)
                .buyOutSurplusAccountStates(accountStateMap)
                .build();
        BuyOutSurplusRunRequestMetadata metadata = BuyOutSurplusRunRequestMetadata.builder().build();
        Request request = Request.builder()
                .id(requestId)
                .metadata(metadata)
                .payload(requestPayload)
                .build();
        final BuyOutSurplusRunSummary runSummary = BuyOutSurplusRunSummary.builder()
                .totalAccounts(3L)
                .failedAccounts(1L)
                .buyOutTransactions(1L)
                .refundedTransactions(0L)
                .build();
        final BuyOutSurplusRunCompletedRequestActionPayload actionPayload =
                BuyOutSurplusRunCompletedRequestActionPayload.builder()
                        .payloadType(CcaRequestActionPayloadType.BUY_OUT_SURPLUS_RUN_COMPLETED_PAYLOAD)
                        .runSummary(runSummary)
                        .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        // Invoke
        buyOutSurplusRunService.complete(requestId);

        // Verify
        assertThat(((BuyOutSurplusRunRequestPayload) request.getPayload()).getRunSummary()).isEqualTo(runSummary);
        assertThat(((BuyOutSurplusRunRequestMetadata) request.getMetadata()).getTotalAccounts()).isEqualTo(3L);
        assertThat(((BuyOutSurplusRunRequestMetadata) request.getMetadata()).getFailedAccounts()).isEqualTo(1L);
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestService, times(1)).updateRequestStatus(requestId, CcaRequestStatuses.COMPLETED_WITH_FAILURES);
        verify(requestService, times(1)).addActionToRequest(
                request, actionPayload, CcaRequestActionType.BUY_OUT_SURPLUS_RUN_COMPLETED_WITH_FAILURES, submitterId);
    }
}
