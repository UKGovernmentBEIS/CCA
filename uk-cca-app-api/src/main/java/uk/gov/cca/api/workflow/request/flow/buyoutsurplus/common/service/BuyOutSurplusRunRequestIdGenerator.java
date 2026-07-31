package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.service;

import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.domain.BuyOutSurplusRunRequestMetadata;
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
public class BuyOutSurplusRunRequestIdGenerator extends RequestSequenceRequestIdGenerator {

    protected RequestTypeRepository requestTypeRepository;
    private static final String REQUEST_ID_FORMATTER = "%s-%s%03d";

    protected BuyOutSurplusRunRequestIdGenerator(RequestSequenceRepository repository, RequestTypeRepository requestTypeRepository) {
        super(repository);
        this.requestTypeRepository = requestTypeRepository;
    }

    @Override
    public RequestSequence resolveRequestSequence(RequestParams params) {
    	final String requestTypeCode = CcaRequestType.BUY_OUT_SURPLUS_RUN;
        final RequestType requestType = requestTypeRepository.findByCode(requestTypeCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.REQUEST_TYPE_NOT_FOUND));
        final BuyOutSurplusRunRequestMetadata metaData = (BuyOutSurplusRunRequestMetadata) params.getRequestMetadata();

        return repository.findByBusinessIdentifierAndRequestType(metaData.getTargetPeriodType().name(), requestType)
                .orElse(new RequestSequence(metaData.getTargetPeriodType().name(), requestType));
    }

    @Override
    public String generateRequestId(Long sequenceNo, RequestParams params) {
        final BuyOutSurplusRunRequestMetadata metaData = (BuyOutSurplusRunRequestMetadata) params.getRequestMetadata();

        return String.format(REQUEST_ID_FORMATTER, getPrefix(), metaData.getTargetPeriodType().name(), sequenceNo);
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestType.BUY_OUT_SURPLUS_RUN, CcaRequestType.BUY_OUT_SURPLUS_FACILITY_RUN);
    }

    @Override
    public String getPrefix() {
        return "BOS";
    }
}
