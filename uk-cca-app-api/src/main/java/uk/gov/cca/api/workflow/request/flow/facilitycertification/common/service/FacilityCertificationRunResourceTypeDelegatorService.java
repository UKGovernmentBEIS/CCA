package uk.gov.cca.api.workflow.request.flow.facilitycertification.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.CertificationPeriodDTO;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.common.domain.FacilityCertificationAccountState;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.processing.domain.FacilityCertificationAccountProcessingRequestPayload;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FacilityCertificationRunResourceTypeDelegatorService {

    private final List<FacilityCertificationRunResourceTypeService> facilityCertificationRunResourceTypeServices;

    public Map<Long, FacilityCertificationAccountState> getEligibleAccounts(final CertificationPeriodDTO certificationPeriodDetails) {
        return facilityCertificationRunResourceTypeServices.stream()
                .filter(service -> service.getResourceType().equals(certificationPeriodDetails.getSchemeVersion().getResourceType()))
                .findFirst().map(service -> service.getEligibleAccounts(certificationPeriodDetails))
                .orElse(new HashMap<>());
    }

    public Set<Long> getFacilitiesEligibleForCertification(final FacilityCertificationAccountProcessingRequestPayload payload, final FacilityCertificationAccountState accountState) {
        final CertificationPeriodDTO certificationPeriodDetails = payload.getCertificationPeriodDetails();

        return facilityCertificationRunResourceTypeServices.stream()
                .filter(service -> service.getResourceType().equals(certificationPeriodDetails.getSchemeVersion().getResourceType()))
                .findFirst().map(service -> service.getFacilitiesEligibleForCertification(payload, accountState))
                .orElse(new HashSet<>());
    }
}
