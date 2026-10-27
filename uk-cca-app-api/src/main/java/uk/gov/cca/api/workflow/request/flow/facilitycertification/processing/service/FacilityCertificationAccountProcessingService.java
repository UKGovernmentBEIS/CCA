package uk.gov.cca.api.workflow.request.flow.facilitycertification.processing.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.targetperiodreporting.facilitycertification.service.FacilityCertificationService;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.CertificationPeriodDTO;
import uk.gov.cca.api.workflow.bpmn.exception.BpmnExecutionException;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.common.domain.FacilityCertificationAccountState;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.common.domain.FacilityCertificationRunRequestPayload;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.common.service.FacilityCertificationRunResourceTypeDelegatorService;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.processing.domain.FacilityCertificationAccountProcessingRequestMetadata;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.util.List;
import java.util.Set;

@Log4j2
@Service
@RequiredArgsConstructor
public class FacilityCertificationAccountProcessingService {

    private final RequestService requestService;
    private final FacilityCertificationRunResourceTypeDelegatorService facilityCertificationRunResourceTypeDelegatorService;
    private final FacilityCertificationService facilityCertificationService;

    @Transactional(rollbackFor = BpmnExecutionException.class, propagation = Propagation.REQUIRES_NEW)
    public void doProcess(Request request, FacilityCertificationAccountState accountState) throws BpmnExecutionException {
        try {
            accountState.setFacilitiesCertified(0L);

            if(accountState.getFacilityIds().isEmpty()) {
                throw new BusinessException(CcaErrorCode.NO_FACILITIES_FOR_ACCOUNT);
            }

            final FacilityCertificationAccountProcessingRequestMetadata metadata =
                    (FacilityCertificationAccountProcessingRequestMetadata) request.getMetadata();
            final CertificationPeriodDTO certificationPeriod = metadata.getCertificationPeriodDetails();

            Set<Long> facilitiesEligibleForCertification = facilityCertificationRunResourceTypeDelegatorService
                    .getFacilitiesEligibleForCertification(certificationPeriod, accountState);

            if(!facilitiesEligibleForCertification.isEmpty()) {
                // Certify facilities
                facilityCertificationService.certifyFacilities(facilitiesEligibleForCertification,
                        certificationPeriod.getId(), certificationPeriod.getStartDate());

                accountState.setFacilitiesCertified((long) facilitiesEligibleForCertification.size());
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BpmnExecutionException(e.getMessage(), List.of(e.getMessage()));
        }
    }

    @Transactional
    public void complete(String requestId, FacilityCertificationAccountState accountState) {
        try {
            final Request request = requestService.findRequestById(requestId);
            final FacilityCertificationAccountProcessingRequestMetadata metadata =
                    (FacilityCertificationAccountProcessingRequestMetadata) request.getMetadata();

            final Request parentRequest = requestService.findRequestById(metadata.getParentRequestId());
            FacilityCertificationRunRequestPayload parentRequestPayload =
                    (FacilityCertificationRunRequestPayload) parentRequest.getPayload();

            if(accountState.getErrors().isEmpty()) {
                accountState.setSucceeded(true);
            }
            else {
                accountState.setSucceeded(false);
                accountState.setFacilitiesCertified(0L);
            }

            parentRequestPayload.getFacilityCertificationAccountStates().put(accountState.getAccountId(), accountState);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
