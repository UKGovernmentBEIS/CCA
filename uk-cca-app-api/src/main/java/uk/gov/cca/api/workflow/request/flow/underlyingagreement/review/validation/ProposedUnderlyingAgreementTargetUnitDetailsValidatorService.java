package uk.gov.cca.api.workflow.request.flow.underlyingagreement.review.validation;

import org.springframework.stereotype.Service;
import uk.gov.cca.api.account.service.TargetUnitAccountQueryService;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationSchemeService;
import uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation;
import uk.gov.cca.api.workflow.request.flow.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.common.validation.TargetUnitDetailsValidatorService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.review.domain.UnderlyingAgreementReviewRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.util.List;

import static uk.gov.cca.api.workflow.request.flow.common.domain.review.UnderlyingAgreementPayloadType.PROPOSED;

@Service
public class ProposedUnderlyingAgreementTargetUnitDetailsValidatorService extends TargetUnitDetailsValidatorService {

    public ProposedUnderlyingAgreementTargetUnitDetailsValidatorService(DataValidator<UnderlyingAgreementTargetUnitDetails> validator, SectorAssociationSchemeService sectorAssociationSchemeService, TargetUnitAccountQueryService targetUnitAccountQueryService) {
        super(validator, sectorAssociationSchemeService, targetUnitAccountQueryService);
    }

    @Override
    public BusinessValidationResult validate(RequestTask requestTask) {
        final UnderlyingAgreementReviewRequestTaskPayload taskPayload = (UnderlyingAgreementReviewRequestTaskPayload) requestTask.getPayload();
        final Long accountId = requestTask.getRequest().getAccountId();
        UnderlyingAgreementTargetUnitDetails underlyingAgreementTargetUnitDetails =
                taskPayload.getUnderlyingAgreementProposed().getUnderlyingAgreementTargetUnitDetails();

        final List<UnderlyingAgreementViolation> violations = validateTargetUnitDetails(accountId, underlyingAgreementTargetUnitDetails);

        return BusinessValidationResult.builder()
                .valid(violations.isEmpty())
                .violations(violations)
                .build();
    }

    @Override
    public String getPayloadType() {
        return PROPOSED.toString();
    }
}
