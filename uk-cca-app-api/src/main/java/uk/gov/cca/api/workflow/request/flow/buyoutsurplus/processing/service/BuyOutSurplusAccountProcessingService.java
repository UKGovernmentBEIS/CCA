package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.workflow.bpmn.exception.BpmnExecutionException;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.domain.BuyOutSurplusAccountState;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.validation.BuyOutSurplusViolation;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.processing.domain.BuyOutSurplusAccountProcessingRequestMetadata;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class BuyOutSurplusAccountProcessingService {

    private final RequestService requestService;
    private final List<BuyOutSurplusAccountProcessingTargetPeriodService> buyOutSurplusAccountProcessingTargetPeriodServices;

    @Transactional(rollbackFor = BpmnExecutionException.class, propagation = Propagation.REQUIRES_NEW)
    public void doProcess(String requestId, BuyOutSurplusAccountState accountState) throws BpmnExecutionException {
        try {
            final Request request = requestService.findRequestById(requestId);
            final BuyOutSurplusAccountProcessingRequestMetadata metadata =
                    (BuyOutSurplusAccountProcessingRequestMetadata) request.getMetadata();

            // Process per Target Period
            BuyOutSurplusAccountProcessingTargetPeriodService buyOutService = buyOutSurplusAccountProcessingTargetPeriodServices.stream()
                    .filter(service -> service.getType().equals(metadata.getTargetPeriodType()))
                    .findFirst().orElseThrow();
            buyOutService.processBuyOutSurplus(accountState);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BpmnExecutionException(e.getMessage(), List.of(
                    BuyOutSurplusViolation.BuyOutSurplusViolationMessage.PROCESS_FAILED.getMessage()));
        }
    }
}
