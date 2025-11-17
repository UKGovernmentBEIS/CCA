package uk.gov.cca.api.workflow.request.flow.common.service;

import uk.gov.cca.api.facility.service.FacilityDataQueryService;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaRequestParams;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.workflow.request.core.domain.RequestSequence;
import uk.gov.netz.api.workflow.request.core.domain.RequestType;
import uk.gov.netz.api.workflow.request.core.repository.RequestSequenceRepository;
import uk.gov.netz.api.workflow.request.core.repository.RequestTypeRepository;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.netz.api.workflow.request.flow.common.service.RequestSequenceRequestIdGenerator;

public abstract class FacilityRequestSequenceRequestIdGenerator extends RequestSequenceRequestIdGenerator {

	private static final String REQUEST_ID_FORMATTER = "%s-%s-%d";
	private final FacilityDataQueryService facilityDataQueryService;
    protected RequestTypeRepository requestTypeRepository;

    protected FacilityRequestSequenceRequestIdGenerator(
    		RequestSequenceRepository repository, 
    		FacilityDataQueryService facilityDataQueryService,
    		RequestTypeRepository requestTypeRepository) {
        super(repository);
        this.facilityDataQueryService = facilityDataQueryService;
        this.requestTypeRepository = requestTypeRepository;
    }

    protected RequestSequence resolveRequestSequence(RequestParams params) {
        final Long facilityId = ((CcaRequestParams) params).getFacilityId();
        final String requestTypeCode = params.getType();
        
		final RequestType requestType = requestTypeRepository.findByCode(requestTypeCode)
				.orElseThrow(() -> new BusinessException(ErrorCode.REQUEST_TYPE_NOT_FOUND));

		return repository.findByBusinessIdentifierAndRequestType(String.valueOf(facilityId), requestType)
				.orElse(new RequestSequence(String.valueOf(facilityId), requestType));
    }
    
    protected String generateRequestId(Long sequenceNo, RequestParams params) {
    	String businessId = facilityDataQueryService.getFacilityBusinessIdById(((CcaRequestParams) params).getFacilityId());
        return String.format(REQUEST_ID_FORMATTER, businessId, getPrefix(), sequenceNo);
    }
}
