package uk.gov.cca.api.workflow.request.flow.facilityaudit.common.service;

import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.common.service.CcaRequestCreateValidatorService;
import uk.gov.cca.api.workflow.request.flow.common.service.RequestCreateFacilityRelatedValidator;

import java.util.Set;

@Service
public class FacilityAuditCreateValidator extends RequestCreateFacilityRelatedValidator {

    public FacilityAuditCreateValidator(CcaRequestCreateValidatorService ccaRequestCreateValidatorService) {
        super(ccaRequestCreateValidatorService);
    }

    @Override
    protected Set<String> getMutuallyExclusiveRequests() {
        return Set.of(CcaRequestType.FACILITY_AUDIT);
    }

    @Override
    public String getRequestType() {
        return CcaRequestType.FACILITY_AUDIT;
    }
}
