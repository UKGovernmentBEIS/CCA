package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.processing.service;

import org.springframework.stereotype.Service;
import uk.gov.cca.api.facility.service.FacilityDataQueryService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.common.service.FacilityRequestSequenceRequestIdGenerator;
import uk.gov.netz.api.workflow.request.core.repository.RequestSequenceRepository;
import uk.gov.netz.api.workflow.request.core.repository.RequestTypeRepository;

import java.util.List;

@Service
public class FacilityPerformanceAccountTemplateProcessingRequestIdGenerator extends FacilityRequestSequenceRequestIdGenerator {
    public FacilityPerformanceAccountTemplateProcessingRequestIdGenerator(RequestSequenceRepository repository, FacilityDataQueryService facilityDataQueryService, RequestTypeRepository requestTypeRepository) {
        super(repository, facilityDataQueryService, requestTypeRepository);
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestType.FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_PROCESSING);
    }

    @Override
    public String getPrefix() {
        return "PAT";
    }
}
