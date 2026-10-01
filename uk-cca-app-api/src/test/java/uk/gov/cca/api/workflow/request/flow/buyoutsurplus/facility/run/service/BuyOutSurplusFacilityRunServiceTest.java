package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.run.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.domain.BuyOutSurplusFacilityRunRequestPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

@ExtendWith(MockitoExtension.class)
public class BuyOutSurplusFacilityRunServiceTest {

	@InjectMocks
    private BuyOutSurplusFacilityRunService buyOutSurplusFacilityRunService;

    @Mock
    private RequestService requestService;

    @Test
    void submit() {
        final String requestId = "requestId";
        final String submitterId = "regulator";

        final Request request = Request.builder()
                .payload(BuyOutSurplusFacilityRunRequestPayload.builder()
                        .submitterId(submitterId)
                        .build())
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        // Invoke
        buyOutSurplusFacilityRunService.submit(requestId);

        // Verify
        assertThat(request.getSubmissionDate()).isNotNull();
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestService, times(1)).addActionToRequest(
                request, null, CcaRequestActionType.BUY_OUT_SURPLUS_FACILITY_RUN_SUBMITTED, submitterId);
    }
}
