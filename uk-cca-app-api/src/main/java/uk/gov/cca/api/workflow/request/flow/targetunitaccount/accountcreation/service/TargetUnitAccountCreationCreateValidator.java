package uk.gov.cca.api.workflow.request.flow.targetunitaccount.accountcreation.service;

import org.springframework.stereotype.Service;
import uk.gov.cca.api.workflow.request.flow.common.service.RequestCreateBySectorAssociationValidator;
import uk.gov.netz.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;

import static uk.gov.cca.api.workflow.request.core.domain.CcaRequestType.TARGET_UNIT_ACCOUNT_CREATION;

@Service
public class TargetUnitAccountCreationCreateValidator implements RequestCreateBySectorAssociationValidator {

    @Override
    public RequestCreateValidationResult validateAction(Long sectorAssociationId, Long accountId) {
        return RequestCreateValidationResult.builder().valid(accountId == null).build();
    }

    @Override
    public String getRequestType() {
        return TARGET_UNIT_ACCOUNT_CREATION;
    }
}
