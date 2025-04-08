package uk.gov.cca.api.workflow.request.flow.common.service;

import uk.gov.cca.api.sectorassociation.service.SectorAssociationQueryService;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaRequestParams;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.workflow.request.core.domain.RequestSequence;
import uk.gov.netz.api.workflow.request.core.domain.RequestType;
import uk.gov.netz.api.workflow.request.core.repository.RequestSequenceRepository;
import uk.gov.netz.api.workflow.request.core.repository.RequestTypeRepository;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.netz.api.workflow.request.flow.common.service.RequestSequenceRequestIdGenerator;

public abstract class SectorRequestSequenceRequestIdGenerator extends RequestSequenceRequestIdGenerator {

	private static final String REQUEST_ID_FORMATTER = "%s-%s-%d";
	private final SectorAssociationQueryService sectorAssociationQueryService;
    protected RequestTypeRepository requestTypeRepository;

    protected SectorRequestSequenceRequestIdGenerator(
    		RequestSequenceRepository repository, 
    		SectorAssociationQueryService sectorAssociationQueryService,
    		RequestTypeRepository requestTypeRepository) {
        super(repository);
        this.sectorAssociationQueryService = sectorAssociationQueryService;
        this.requestTypeRepository = requestTypeRepository;
    }

    protected RequestSequence resolveRequestSequence(RequestParams params) {
        final Long sectorId = ((CcaRequestParams) params).getSectorId();
        final String requestTypeCode = params.getType();
        
		final RequestType requestType = requestTypeRepository.findByCode(requestTypeCode)
				.orElseThrow(() -> new BusinessException(ErrorCode.REQUEST_TYPE_NOT_FOUND));

		return repository.findByBusinessIdentifierAndRequestType(String.valueOf(sectorId), requestType)
				.orElse(new RequestSequence(String.valueOf(sectorId), requestType));
    }
    
    protected String generateRequestId(Long sequenceNo, RequestParams params) {
    	String acronym = sectorAssociationQueryService.getSectorAssociationAcronymById(((CcaRequestParams) params).getSectorId());
        return String.format(REQUEST_ID_FORMATTER, acronym, getPrefix(), sequenceNo);
    }
}
