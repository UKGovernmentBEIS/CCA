package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.run.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.domain.BuyOutSurplusFacilityRunRequestPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

@Service
@RequiredArgsConstructor
public class BuyOutSurplusFacilityRunService {

	private final RequestService requestService;

    @Transactional
    public void submit(final String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final BuyOutSurplusFacilityRunRequestPayload requestPayload = (BuyOutSurplusFacilityRunRequestPayload) request.getPayload();

        LocalDateTime now = LocalDateTime.now();
        request.setSubmissionDate(now);

        requestService.addActionToRequest(request,
                null,
                CcaRequestActionType.BUY_OUT_SURPLUS_FACILITY_RUN_SUBMITTED,
                requestPayload.getSubmitterId());
    }
}
