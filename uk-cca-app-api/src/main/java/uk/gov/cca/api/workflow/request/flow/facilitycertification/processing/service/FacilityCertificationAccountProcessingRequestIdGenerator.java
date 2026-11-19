package uk.gov.cca.api.workflow.request.flow.facilitycertification.processing.service;

import org.springframework.stereotype.Service;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.facilitycertification.processing.domain.FacilityCertificationAccountProcessingRequestPayload;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.netz.api.workflow.request.flow.common.service.RequestIdGenerator;

import java.util.List;

@Service
public class FacilityCertificationAccountProcessingRequestIdGenerator implements RequestIdGenerator {

    private static final String REQUEST_ID_FORMATTER = "%s-%s";

    @Override
    public String generate(RequestParams params) {
        final FacilityCertificationAccountProcessingRequestPayload payload =
                (FacilityCertificationAccountProcessingRequestPayload) params.getRequestPayload();

        final String accountAcronym = payload.getAccountBusinessId();
        final String parentRequestId = payload.getParentRequestId();

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
