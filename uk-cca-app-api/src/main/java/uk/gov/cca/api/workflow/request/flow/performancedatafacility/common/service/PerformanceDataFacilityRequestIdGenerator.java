package uk.gov.cca.api.workflow.request.flow.performancedatafacility.common.service;

import org.springframework.stereotype.Service;

import uk.gov.cca.api.facility.service.FacilityDataQueryService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaRequestParams;
import uk.gov.cca.api.workflow.request.flow.common.service.FacilityRequestSequenceRequestIdGenerator;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.workflow.request.core.domain.RequestSequence;
import uk.gov.netz.api.workflow.request.core.domain.RequestType;
import uk.gov.netz.api.workflow.request.core.repository.RequestSequenceRepository;
import uk.gov.netz.api.workflow.request.core.repository.RequestTypeRepository;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;

import java.util.List;

@Service
public class PerformanceDataFacilityRequestIdGenerator extends FacilityRequestSequenceRequestIdGenerator {

    protected PerformanceDataFacilityRequestIdGenerator(RequestSequenceRepository repository,
                                                        FacilityDataQueryService facilityDataQueryService,
                                                        RequestTypeRepository requestTypeRepository) {
        super(repository, facilityDataQueryService, requestTypeRepository);
    }

    @Override
    public RequestSequence resolveRequestSequence(RequestParams params) {
        final Long facilityId = ((CcaRequestParams) params).getFacilityId();
        final String requestTypeCode = CcaRequestType.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM;

        final RequestType requestType = requestTypeRepository.findByCode(requestTypeCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.REQUEST_TYPE_NOT_FOUND));

        return repository.findByBusinessIdentifierAndRequestType(String.valueOf(facilityId), requestType)
                .orElse(new RequestSequence(String.valueOf(facilityId), requestType));
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestType.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM, CcaRequestType.PERFORMANCE_DATA_FACILITY_PROCESSING);
    }

    @Override
    public String getPrefix() {
        return "TPR";
    }
}
