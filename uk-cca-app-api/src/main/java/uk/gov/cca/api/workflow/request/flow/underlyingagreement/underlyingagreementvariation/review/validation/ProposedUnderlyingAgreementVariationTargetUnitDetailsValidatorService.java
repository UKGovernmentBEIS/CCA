package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.validation;

import org.springframework.stereotype.Service;
import uk.gov.cca.api.account.service.TargetUnitAccountQueryService;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.sectorassociation.service.SubsectorAssociationService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.review.UnderlyingAgreementPayloadType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.validation.UnderlyingAgreementVariationTargetUnitDetailsValidatorService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.review.domain.UnderlyingAgreementVariationReviewRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

@Service
public class ProposedUnderlyingAgreementVariationTargetUnitDetailsValidatorService extends UnderlyingAgreementVariationTargetUnitDetailsValidatorService {
    public ProposedUnderlyingAgreementVariationTargetUnitDetailsValidatorService(DataValidator<UnderlyingAgreementTargetUnitDetails> validator, SubsectorAssociationService subsectorAssociationService, TargetUnitAccountQueryService targetUnitAccountQueryService) {
        super(validator, subsectorAssociationService, targetUnitAccountQueryService);
    }

    @Override
    public UnderlyingAgreementVariationPayload getUnderlyingAgreementPayload(RequestTask requestTask) {
        return ((UnderlyingAgreementVariationReviewRequestTaskPayload) requestTask.getPayload()).getProposedUnderlyingAgreement();
    }

    @Override
    public String getPayloadType() {
        return UnderlyingAgreementPayloadType.PROPOSED.toString();
    }
}
