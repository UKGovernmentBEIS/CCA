package uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.processing.service;

import org.springframework.stereotype.Service;

import uk.gov.cca.api.sectorassociation.service.SectorAssociationQueryService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.common.service.SectorRequestSequenceRequestIdGenerator;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.common.domain.PerformanceDataFacilityDataProcessingRequestPayload;
import uk.gov.netz.api.workflow.request.core.repository.RequestSequenceRepository;
import uk.gov.netz.api.workflow.request.core.repository.RequestTypeRepository;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;

import java.util.List;

@Service
public class PerformanceDataFacilityDataProcessingRequestIdGenerator extends SectorRequestSequenceRequestIdGenerator {

    public PerformanceDataFacilityDataProcessingRequestIdGenerator(RequestSequenceRepository repository,
                                                                   SectorAssociationQueryService sectorAssociationQueryService,
                                                                   RequestTypeRepository requestTypeRepository) {
        super(repository, sectorAssociationQueryService, requestTypeRepository);
    }

    @Override
    protected String generateRequestId(Long sequenceNo, RequestParams params) {
        PerformanceDataFacilityDataProcessingRequestPayload requestPayload =
                (PerformanceDataFacilityDataProcessingRequestPayload) params.getRequestPayload();
        String sectorAcronym = requestPayload.getSectorAssociationInfo().getAcronym();

        return String.format("%s-%s-%d", sectorAcronym, getPrefix(), sequenceNo);
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestType.PERFORMANCE_DATA_FACILITY_DATA_PROCESSING);
    }

    @Override
    public String getPrefix() {
        return "TPRFPC";
    }
}
