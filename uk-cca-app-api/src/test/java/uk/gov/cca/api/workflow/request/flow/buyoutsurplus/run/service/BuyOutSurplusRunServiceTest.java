package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.run.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.domain.BuyOutSurplusAccountState;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.domain.BuyOutSurplusRunRequestPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuyOutSurplusRunServiceTest {

    @InjectMocks
    private BuyOutSurplusRunService buyOutSurplusRunService;

    @Mock
    private RequestService requestService;

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
}
