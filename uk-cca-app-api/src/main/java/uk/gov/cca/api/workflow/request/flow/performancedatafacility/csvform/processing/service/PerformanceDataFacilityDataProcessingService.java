package uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.processing.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementQueryService;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.common.domain.FacilityUploadReport;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.common.domain.PerformanceDataFacilityDataProcessingRequestPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PerformanceDataFacilityDataProcessingService {

    private final RequestService requestService;
    private final UnderlyingAgreementQueryService underlyingAgreementQueryService;

    @Transactional
    public void setAdditionalRequestPayloadData(final String requestId, final Map<Long, FacilityUploadReport> facilityReports){
        final Request request = requestService.findRequestById(requestId);
        PerformanceDataFacilityDataProcessingRequestPayload requestPayload =
                (PerformanceDataFacilityDataProcessingRequestPayload) request.getPayload();

        // Get UNAs data
        Set<Long> accountIds = facilityReports.values().stream()
                .map(FacilityUploadReport::getAccountId).collect(Collectors.toSet());
        final Map<Long, UnderlyingAgreementContainer> underlyingAgreementAccountMap = underlyingAgreementQueryService
                .getUnderlyingAgreementContainersByAccounts(accountIds);

        // Set to payload
        requestPayload.setUnderlyingAgreementAccountMap(underlyingAgreementAccountMap);
    }
}
