package uk.gov.cca.api.workflow.request.flow.facilitycertification.common.service;

import uk.gov.cca.api.targetperiodreporting.common.domain.PerformanceDataResourceType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.dto.CertificationPeriodDTO;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.common.domain.FacilityCertificationAccountState;

import java.util.Map;
import java.util.Set;

public interface FacilityCertificationRunResourceTypeService {

    Map<Long, FacilityCertificationAccountState> getEligibleAccounts(CertificationPeriodDTO certificationPeriodDetails);

    Set<Long> getFacilitiesEligibleForCertification(CertificationPeriodDTO certificationPeriodDetails, FacilityCertificationAccountState accountState);

    PerformanceDataResourceType getResourceType();
}
