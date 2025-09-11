package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.review.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.account.service.TargetUnitAccountUpdateService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementRejectedService {

    private final RequestService requestService;
    private final TargetUnitAccountUpdateService updateService;

    public void reject(final String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final Long accountId = request.getAccountId();

        updateService.handleTargetUnitAccountRejected(accountId);
    }

}
