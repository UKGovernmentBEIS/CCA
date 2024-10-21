package uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.validation;

import org.springframework.stereotype.Service;
import uk.gov.cca.api.account.service.TargetUnitAccountQueryService;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationSchemeService;
import uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation;
import uk.gov.cca.api.workflow.request.flow.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.common.validation.TargetUnitDetailsValidatorService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.util.List;

@Service
public class UnderlyingAgreementTargetUnitDetailsValidatorService extends TargetUnitDetailsValidatorService {

    public UnderlyingAgreementTargetUnitDetailsValidatorService(DataValidator<UnderlyingAgreementTargetUnitDetails> validator, SectorAssociationSchemeService sectorAssociationSchemeService, TargetUnitAccountQueryService targetUnitAccountQueryService) {
        super(validator, sectorAssociationSchemeService, targetUnitAccountQueryService);
    }

    @Override
    public BusinessValidationResult validate(final RequestTask requestTask) {

        final UnderlyingAgreementRequestTaskPayload taskPayload = (UnderlyingAgreementRequestTaskPayload) requestTask.getPayload();
        final Long accountId = requestTask.getRequest().getAccountId();
        UnderlyingAgreementTargetUnitDetails underlyingAgreementTargetUnitDetails = taskPayload.getUnderlyingAgreement().getUnderlyingAgreementTargetUnitDetails();

        final List<UnderlyingAgreementViolation> violations = validateTargetUnitDetails(accountId, underlyingAgreementTargetUnitDetails);

        return BusinessValidationResult.builder()
                .valid(violations.isEmpty())
                .violations(violations)
                .build();
    }
}
