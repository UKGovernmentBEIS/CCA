package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.service;

import java.util.List;

import org.springframework.stereotype.Service;

import uk.gov.cca.api.sectorassociation.service.SectorAssociationQueryService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.common.service.SectorRequestSequenceRequestIdGenerator;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.upload.domain.PerformanceAccountTemplateDataUploadRequestPayload;
import uk.gov.netz.api.workflow.request.core.repository.RequestSequenceRepository;
import uk.gov.netz.api.workflow.request.core.repository.RequestTypeRepository;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;

@Service
public class PerformanceAccountTemplateDataUploadRequestIdGenerator extends SectorRequestSequenceRequestIdGenerator {

	public PerformanceAccountTemplateDataUploadRequestIdGenerator(RequestSequenceRepository repository,
			SectorAssociationQueryService sectorAssociationQueryService, RequestTypeRepository requestTypeRepository) {
		super(repository, sectorAssociationQueryService, requestTypeRepository);
	}

	@Override
	protected String generateRequestId(Long sequenceNo, RequestParams params) {
		PerformanceAccountTemplateDataUploadRequestPayload requestPayload = (PerformanceAccountTemplateDataUploadRequestPayload) params
				.getRequestPayload();
		String sectorAcronym = requestPayload.getSectorAssociationInfo().getAcronym();

		return String.format("%s-%s-%d", sectorAcronym, getPrefix(), sequenceNo);
	}

	@Override
	public String getPrefix() {
		return "PATUL";
	}

	@Override
	public List<String> getTypes() {
		return List.of(CcaRequestType.PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD);
	}
}