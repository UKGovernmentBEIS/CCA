package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.service;

import org.springframework.stereotype.Service;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationQueryService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataUploadRequestPayload;
import uk.gov.cca.api.workflow.request.flow.common.service.SectorRequestSequenceRequestIdGenerator;
import uk.gov.netz.api.workflow.request.core.repository.RequestSequenceRepository;
import uk.gov.netz.api.workflow.request.core.repository.RequestTypeRepository;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;

import java.util.List;

@Service
public class FacilityPerformanceAccountTemplateDataUploadRequestIdGenerator extends SectorRequestSequenceRequestIdGenerator {
    public FacilityPerformanceAccountTemplateDataUploadRequestIdGenerator(RequestSequenceRepository repository,
                                                                          SectorAssociationQueryService sectorAssociationQueryService, RequestTypeRepository requestTypeRepository) {
        super(repository, sectorAssociationQueryService, requestTypeRepository);
    }

    @Override
    protected String generateRequestId(Long sequenceNo, RequestParams params) {
        FacilityPerformanceAccountTemplateDataUploadRequestPayload requestPayload = (FacilityPerformanceAccountTemplateDataUploadRequestPayload) params
                .getRequestPayload();
        String sectorAcronym = requestPayload.getSectorAssociationInfo().getAcronym();

        return String.format("%s-%s-%d", sectorAcronym, getPrefix(), sequenceNo);
    }

    @Override
    public String getPrefix() {
        return "PAT";
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestType.FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD);
    }
}
