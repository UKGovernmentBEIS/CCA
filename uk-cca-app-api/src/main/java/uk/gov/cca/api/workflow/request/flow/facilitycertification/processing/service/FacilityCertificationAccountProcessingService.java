package uk.gov.cca.api.workflow.request.flow.facilitycertification.processing.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusPaymentStatus;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusProcessedDataQueryService;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service.BuyOutSurplusQueryService;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.service.FacilityCertificationService;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.CertificationPeriodDTO;
import uk.gov.cca.api.workflow.bpmn.exception.BpmnExecutionException;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.common.domain.FacilityCertificationAccountState;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.processing.domain.FacilityCertificationAccountProcessingRequestMetadata;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.util.HashSet;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class FacilityCertificationAccountProcessingService {

    private final RequestService requestService;
    private final BuyOutSurplusProcessedDataQueryService buyOutSurplusProcessedDataQueryService;
    private final BuyOutSurplusQueryService buyOutSurplusQueryService;
    private final FacilityCertificationService facilityCertificationService;

    @Transactional(rollbackFor = BpmnExecutionException.class, propagation = Propagation.REQUIRES_NEW)
    public void doProcess(String requestId, FacilityCertificationAccountState accountState) throws BpmnExecutionException {
        try {
            accountState.setFacilitiesCertified(0L);

            if(accountState.getFacilityIds().isEmpty()) {
                throw new BusinessException(CcaErrorCode.NO_FACILITIES_FOR_ACCOUNT);
            }

            final Request request = requestService.findRequestById(requestId);
            final FacilityCertificationAccountProcessingRequestMetadata metadata =
                    (FacilityCertificationAccountProcessingRequestMetadata) request.getMetadata();
            final Long performanceDataId = accountState.getLastPerformanceDataId();

            // Find if buy out surplus has been run for account's last performance data
            buyOutSurplusProcessedDataQueryService.getBuyOutSurplusProcessedDataByPerformanceData(performanceDataId)
                    .ifPresent(processedData ->
                        // If it has no transaction or transaction as not AWAITING_PAYMENT then certify facilities
                        buyOutSurplusQueryService.getBuyOutSurplusTransactionByPerformanceData(accountState.getLastPerformanceDataId())
                                .ifPresentOrElse(info -> {
                                    // If it has transaction as not AWAITING_PAYMENT then certify facilities
                                    if(!info.getPaymentStatus().equals(BuyOutSurplusPaymentStatus.AWAITING_PAYMENT)) {
                                        certifyFacilities(accountState, metadata);
                                    }
                                }, () -> certifyFacilities(accountState, metadata))
                    );
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BpmnExecutionException(e.getMessage(), List.of(e.getMessage()));
        }
    }

    private void certifyFacilities(FacilityCertificationAccountState accountState,
                                   final FacilityCertificationAccountProcessingRequestMetadata metadata) {
        final CertificationPeriodDTO certificationPeriod = metadata.getCertificationPeriodDetails();

        facilityCertificationService.certifyFacilities(new HashSet<>(accountState.getFacilityIds()),
                certificationPeriod.getId(), certificationPeriod.getStartDate());

        accountState.setFacilitiesCertified((long) accountState.getFacilityIds().size());
    }
}
