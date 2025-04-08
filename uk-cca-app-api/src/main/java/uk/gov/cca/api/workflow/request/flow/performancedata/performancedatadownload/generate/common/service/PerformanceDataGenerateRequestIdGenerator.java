package uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.generate.common.service;

import org.springframework.stereotype.Service;

import uk.gov.cca.api.sectorassociation.service.SectorAssociationQueryService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.common.service.SectorRequestSequenceRequestIdGenerator;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.common.domain.PerformanceDataGenerateRequestPayload;
import uk.gov.netz.api.workflow.request.core.repository.RequestSequenceRepository;
import uk.gov.netz.api.workflow.request.core.repository.RequestTypeRepository;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;

import java.util.List;

@Service
public class PerformanceDataGenerateRequestIdGenerator extends SectorRequestSequenceRequestIdGenerator {

	protected PerformanceDataGenerateRequestIdGenerator(RequestSequenceRepository repository,
														SectorAssociationQueryService sectorAssociationQueryService,
														RequestTypeRepository requestTypeRepository) {
		super(repository, sectorAssociationQueryService, requestTypeRepository);
	}

	@Override
	protected String generateRequestId(Long sequenceNo, RequestParams params) {
		PerformanceDataGenerateRequestPayload requestPayload =
				(PerformanceDataGenerateRequestPayload) params.getRequestPayload();
		String sectorAcronym = requestPayload.getSectorAssociationInfo().getAcronym();

		return String.format("%s-%s-%d", sectorAcronym, getPrefix(), sequenceNo);
	}

	@Override
	public String getPrefix() {
		return "TPRGN";
	}

	@Override
	public List<String> getTypes() {
		return List.of(CcaRequestType.PERFORMANCE_DATA_GENERATE);
	}
}
