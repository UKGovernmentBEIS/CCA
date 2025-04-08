package uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.service;

import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementRequestPayload;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.netz.api.workflow.request.flow.common.service.AccountIdBasedRequestIdGenerator;

import java.util.List;
import java.util.Objects;

@Service
public class UnderlyingAgreementRequestIdGenerator extends AccountIdBasedRequestIdGenerator {

    @Override
    public String generate(RequestParams params) {
        UnderlyingAgreementRequestPayload payload = (UnderlyingAgreementRequestPayload) params.getRequestPayload();
        Objects.requireNonNull(payload.getBusinessId());
        return String.format("%s-%s", payload.getBusinessId(), getPrefix());
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestType.UNDERLYING_AGREEMENT);
    }

    @Override
    public String getPrefix() {
        return "UNA";
    }
}
