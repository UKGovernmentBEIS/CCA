package uk.gov.cca.api.workflow.request.flow.targetunitaccount.accountcreation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.targetunitaccount.accountcreation.domain.TargetUnitAccountCreationRequestPayload;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.netz.api.workflow.request.flow.common.service.AccountIdBasedRequestIdGenerator;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TargetUnitAccountCreationRequestIdGenerator extends AccountIdBasedRequestIdGenerator {

    @Override
    public String generate(RequestParams params) {
        TargetUnitAccountCreationRequestPayload payload = (TargetUnitAccountCreationRequestPayload) params.getRequestPayload();
        Objects.requireNonNull(payload.getBusinessId());
        return String.format("%s-%s", payload.getBusinessId(), getPrefix());
    }

    @Override
    public List<String> getTypes() {
        return List.of(CcaRequestType.TARGET_UNIT_ACCOUNT_CREATION);
    }

    @Override
    public String getPrefix() {
        return "ACC";
    }
}
