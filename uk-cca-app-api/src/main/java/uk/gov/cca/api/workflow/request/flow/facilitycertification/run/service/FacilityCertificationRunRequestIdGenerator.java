package uk.gov.cca.api.workflow.request.flow.facilitycertification.run.service;

import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.common.domain.FacilityCertificationRunRequestMetadata;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.workflow.request.core.domain.RequestSequence;
import uk.gov.netz.api.workflow.request.core.domain.RequestType;
import uk.gov.netz.api.workflow.request.core.repository.RequestSequenceRepository;
import uk.gov.netz.api.workflow.request.core.repository.RequestTypeRepository;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.netz.api.workflow.request.flow.common.service.RequestSequenceRequestIdGenerator;

import java.util.List;

@Service
public class FacilityCertificationRunRequestIdGenerator extends RequestSequenceRequestIdGenerator {

    protected RequestTypeRepository requestTypeRepository;
    private static final String REQUEST_ID_FORMATTER = "%s-%s%03d";

    public FacilityCertificationRunRequestIdGenerator(RequestSequenceRepository repository, RequestTypeRepository requestTypeRepository) {
        super(repository);
        this.requestTypeRepository = requestTypeRepository;
    }

    @Override
    protected RequestSequence resolveRequestSequence(RequestParams params) {
        final String requestTypeCode = params.getType();
        final RequestType requestType = requestTypeRepository.findByCode(requestTypeCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.REQUEST_TYPE_NOT_FOUND));
        final FacilityCertificationRunRequestMetadata metaData = (FacilityCertificationRunRequestMetadata) params.getRequestMetadata();

        return repository.findByBusinessIdentifierAndRequestType(metaData.getCertificationPeriodType().name(), requestType)
                .orElse(new RequestSequence(metaData.getCertificationPeriodType().name(), requestType));
    }

    @Override
    protected String generateRequestId(Long sequenceNo, RequestParams params) {
        final FacilityCertificationRunRequestMetadata metaData = (FacilityCertificationRunRequestMetadata) params.getRequestMetadata();

        return String.format(REQUEST_ID_FORMATTER, getPrefix(), metaData.getCertificationPeriodType().name(), sequenceNo);
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestType.FACILITY_CERTIFICATION_RUN);
    }

    @Override
    public String getPrefix() {
        return "CRT";
    }
}
