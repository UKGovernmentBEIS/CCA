package uk.gov.cca.api.workflow.request.flow.subsistencefees.subsistencefeesrun.service;

import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.common.domain.SubsistenceFeesRunRequestMetadata;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.workflow.request.core.domain.RequestSequence;
import uk.gov.netz.api.workflow.request.core.domain.RequestType;
import uk.gov.netz.api.workflow.request.core.repository.RequestSequenceRepository;
import uk.gov.netz.api.workflow.request.core.repository.RequestTypeRepository;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.netz.api.workflow.request.flow.common.service.RequestSequenceRequestIdGenerator;

@Service
public class SubsistenceFeesRunRequestIdGenerator extends RequestSequenceRequestIdGenerator {

	private static final String REQUEST_ID_FORMATTER = "%s%s%02d";
    protected RequestTypeRepository requestTypeRepository;
    
	public SubsistenceFeesRunRequestIdGenerator(RequestSequenceRepository repository, RequestTypeRepository requestTypeRepository) {
		super(repository);
		this.requestTypeRepository = requestTypeRepository;
	}

	@Override
	protected RequestSequence resolveRequestSequence(RequestParams params) {
        final String requestTypeCode = params.getType();
        final SubsistenceFeesRunRequestMetadata metaData = (SubsistenceFeesRunRequestMetadata) params.getRequestMetadata();
        final RequestType requestType = requestTypeRepository.findByCode(requestTypeCode)
				.orElseThrow(() -> new BusinessException(ErrorCode.REQUEST_TYPE_NOT_FOUND));
        String yearPrefix = getYearPrefix(metaData.getChargingYear());
        
		return repository.findByBusinessIdentifierAndRequestType(yearPrefix, requestType)
				.orElse(new RequestSequence(yearPrefix, requestType));
	}

	@Override
	protected String generateRequestId(Long sequenceNo, RequestParams params) {
		final SubsistenceFeesRunRequestMetadata metaData = (SubsistenceFeesRunRequestMetadata) params.getRequestMetadata();
		return String.format(REQUEST_ID_FORMATTER, getPrefix(), getYearPrefix(metaData.getChargingYear()), sequenceNo);
	}
	
	@Override
	public String getPrefix() {
		return "S";
	}

	@Override
	public List<String> getTypes() {
		return List.of(CcaRequestType.SUBSISTENCE_FEES_RUN);
	}
	
	public String getYearPrefix(Year year) {
		return year.format(DateTimeFormatter.ofPattern("yy"));
	}
}
