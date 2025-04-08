package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.upload.service;

import org.springframework.stereotype.Service;

import uk.gov.cca.api.sectorassociation.service.SectorAssociationQueryService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.common.service.SectorRequestSequenceRequestIdGenerator;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.upload.domain.PerformanceDataUploadRequestPayload;
import uk.gov.netz.api.workflow.request.core.repository.RequestSequenceRepository;
import uk.gov.netz.api.workflow.request.core.repository.RequestTypeRepository;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;

import java.util.List;

@Service
public class PerformanceDataUploadRequestIdGenerator extends SectorRequestSequenceRequestIdGenerator {

    public PerformanceDataUploadRequestIdGenerator(RequestSequenceRepository repository,
                                                   SectorAssociationQueryService sectorAssociationQueryService,
                                                   RequestTypeRepository requestTypeRepository) {
        super(repository, sectorAssociationQueryService, requestTypeRepository);
    }

    @Override
    protected String generateRequestId(Long sequenceNo, RequestParams params) {
        PerformanceDataUploadRequestPayload requestPayload =
                (PerformanceDataUploadRequestPayload) params.getRequestPayload();
        String sectorAcronym = requestPayload.getSectorAssociationInfo().getAcronym();

        return String.format("%s-%s-%d", sectorAcronym, getPrefix(), sequenceNo);
    }

    @Override
    public String getPrefix() {
        return "TPRUL";
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestType.PERFORMANCE_DATA_UPLOAD);
    }
}
