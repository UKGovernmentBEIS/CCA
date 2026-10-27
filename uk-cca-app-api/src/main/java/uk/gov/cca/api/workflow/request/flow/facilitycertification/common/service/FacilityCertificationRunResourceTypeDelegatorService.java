package uk.gov.cca.api.workflow.request.flow.facilitycertification.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.CertificationPeriodDTO;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.common.domain.FacilityCertificationAccountState;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FacilityCertificationRunResourceTypeDelegatorService {

    private final List<FacilityCertificationRunResourceTypeService> facilityCertificationRunResourceTypeServices;

    public Map<Long, FacilityCertificationAccountState> getEligibleAccounts(CertificationPeriodDTO certificationPeriodDetails) {
        return facilityCertificationRunResourceTypeServices.stream()
                .filter(service -> service.getResourceType().equals(certificationPeriodDetails.getSchemeVersion().getResourceType()))
                .findFirst().map(service -> service.getEligibleAccounts(certificationPeriodDetails))
                .orElse(new HashMap<>());
    }

    public Set<Long> getFacilitiesEligibleForCertification(CertificationPeriodDTO certificationPeriodDetails, FacilityCertificationAccountState accountState) {
        return facilityCertificationRunResourceTypeServices.stream()
                .filter(service -> service.getResourceType().equals(certificationPeriodDetails.getSchemeVersion().getResourceType()))
                .findFirst().map(service -> service.getFacilitiesEligibleForCertification(certificationPeriodDetails, accountState))
                .orElse(new HashSet<>());
    }
}
