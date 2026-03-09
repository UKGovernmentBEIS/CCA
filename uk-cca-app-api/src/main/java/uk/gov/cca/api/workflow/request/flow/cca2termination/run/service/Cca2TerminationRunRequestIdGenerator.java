package uk.gov.cca.api.workflow.request.flow.cca2termination.run.service;

import java.util.List;

import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.workflow.request.core.domain.RequestSequence;
import uk.gov.netz.api.workflow.request.core.domain.RequestType;
import uk.gov.netz.api.workflow.request.core.repository.RequestSequenceRepository;
import uk.gov.netz.api.workflow.request.core.repository.RequestTypeRepository;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.netz.api.workflow.request.flow.common.service.RequestSequenceRequestIdGenerator;

@Service
public class Cca2TerminationRunRequestIdGenerator extends RequestSequenceRequestIdGenerator {
	
	protected RequestTypeRepository requestTypeRepository;
    private static final String REQUEST_ID_FORMATTER = "%s-%d";

    public Cca2TerminationRunRequestIdGenerator(RequestSequenceRepository repository, RequestTypeRepository requestTypeRepository) {
        super(repository);
        this.requestTypeRepository = requestTypeRepository;
    }

    @Override
    protected RequestSequence resolveRequestSequence(RequestParams params) {
        final String requestTypeCode = params.getType();
        final RequestType requestType = requestTypeRepository.findByCode(requestTypeCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.REQUEST_TYPE_NOT_FOUND));

        return repository.findByRequestType(requestType).orElse(new RequestSequence(requestType));
    }

    @Override
    protected String generateRequestId(Long sequenceNo, RequestParams params) {
        return String.format(REQUEST_ID_FORMATTER, getPrefix(), sequenceNo);
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestType.CCA2_TERMINATION_RUN);
    }

    @Override
    public String getPrefix() {
        return "CCA2-END";
    }
}
