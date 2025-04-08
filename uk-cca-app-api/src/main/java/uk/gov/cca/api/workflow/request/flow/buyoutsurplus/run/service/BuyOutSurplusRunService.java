package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.run.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.domain.BuyOutSurplusAccountState;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.domain.BuyOutSurplusRunRequestPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class BuyOutSurplusRunService {

    private final RequestService requestService;

    @Transactional
    public void submit(final String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final BuyOutSurplusRunRequestPayload requestPayload = (BuyOutSurplusRunRequestPayload) request.getPayload();

        LocalDateTime now = LocalDateTime.now();
        request.setSubmissionDate(now);

        requestService.addActionToRequest(request,
                null,
                CcaRequestActionType.BUY_OUT_SURPLUS_RUN_SUBMITTED,
                requestPayload.getSubmitterId());
    }

    @Transactional
    public void accountProcessingCompleted(String requestId, Long accountId, BuyOutSurplusAccountState buyOutSurplusAccountState) {
        final Request request = requestService.findRequestById(requestId);
        final BuyOutSurplusRunRequestPayload requestPayload = (BuyOutSurplusRunRequestPayload) request.getPayload();

        requestPayload.getBuyOutSurplusAccountStates().put(accountId, buyOutSurplusAccountState);
    }
}
