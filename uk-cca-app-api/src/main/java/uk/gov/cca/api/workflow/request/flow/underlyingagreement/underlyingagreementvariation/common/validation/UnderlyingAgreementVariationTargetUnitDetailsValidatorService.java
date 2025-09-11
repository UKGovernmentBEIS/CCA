package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.validation;

import org.springframework.stereotype.Service;
import uk.gov.cca.api.account.service.TargetUnitAccountQueryService;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.sectorassociation.service.SubsectorAssociationService;
import uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.validation.TargetUnitDetailsValidatorService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationPayload;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.util.List;

@Service
public abstract class UnderlyingAgreementVariationTargetUnitDetailsValidatorService extends TargetUnitDetailsValidatorService {
    public UnderlyingAgreementVariationTargetUnitDetailsValidatorService(DataValidator<UnderlyingAgreementTargetUnitDetails> validator, SubsectorAssociationService subsectorAssociationService, TargetUnitAccountQueryService targetUnitAccountQueryService) {
        super(validator, subsectorAssociationService, targetUnitAccountQueryService);
    }

    public abstract UnderlyingAgreementVariationPayload getUnderlyingAgreementPayload(RequestTask requestTask);

    @Override
    public BusinessValidationResult validate(RequestTask requestTask) {
        final Long accountId = requestTask.getRequest().getAccountId();
        UnderlyingAgreementTargetUnitDetails underlyingAgreementTargetUnitDetails = this.getUnderlyingAgreementPayload(requestTask).getUnderlyingAgreementTargetUnitDetails();

        final List<UnderlyingAgreementViolation> violations = validateTargetUnitDetails(accountId, underlyingAgreementTargetUnitDetails);

        return BusinessValidationResult.builder()
                .valid(violations.isEmpty())
                .violations(violations)
                .build();
    }
}
