package uk.gov.cca.api.workflow.request.flow.facilitycertification.processing.service;

import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.processing.domain.FacilityCertificationAccountProcessingRequestMetadata;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.netz.api.workflow.request.flow.common.service.RequestIdGenerator;

import java.util.List;

@Service
public class FacilityCertificationAccountProcessingRequestIdGenerator implements RequestIdGenerator {

    private static final String REQUEST_ID_FORMATTER = "%s-%s";

    @Override
    public String generate(RequestParams params) {
        final FacilityCertificationAccountProcessingRequestMetadata metadata =
                (FacilityCertificationAccountProcessingRequestMetadata) params.getRequestMetadata();

        final String accountAcronym = metadata.getAccountBusinessId();
        final String parentRequestId = metadata.getParentRequestId();

        return String.format(REQUEST_ID_FORMATTER, accountAcronym, parentRequestId);
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestType.FACILITY_CERTIFICATION_ACCOUNT_PROCESSING);
    }

    @Override
    public String getPrefix() {
        return "";
    }
}
