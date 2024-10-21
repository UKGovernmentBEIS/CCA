package uk.gov.cca.api.workflow.request.flow.targetunitaccount.accountcreation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.account.service.TargetUnitAccountService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.repository.RequestRepository;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

@Service
@RequiredArgsConstructor
public class TargetUnitAccountCreationRollbackService {

    private final RequestService requestService;
    private final RequestRepository requestRepository;
    private final TargetUnitAccountService targetUnitAccountService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void rollback(final String requestId) {
        Request request = requestService.findRequestById(requestId);
        requestRepository.delete(request);

        Long accountId = request.getAccountId();
        targetUnitAccountService.deleteTargetUnitAccount(accountId);
    }
}
