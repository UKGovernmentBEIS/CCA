package uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.review.validation;

import org.springframework.stereotype.Service;
import uk.gov.cca.api.account.service.TargetUnitAccountQueryService;
import uk.gov.cca.api.common.validation.DataValidator;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationSchemeService;
import uk.gov.cca.api.workflow.request.flow.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.common.domain.review.UnderlyingAgreementPayloadType;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.common.validation.UnderlyingAgreementVariationTargetUnitDetailsValidatorService;
import uk.gov.cca.api.workflow.request.flow.underlyingagreementvariation.review.domain.UnderlyingAgreementVariationReviewRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

@Service
public class ProposedUnderlyingAgreementVariationTargetUnitDetailsValidatorService extends UnderlyingAgreementVariationTargetUnitDetailsValidatorService {
    public ProposedUnderlyingAgreementVariationTargetUnitDetailsValidatorService(DataValidator<UnderlyingAgreementTargetUnitDetails> validator, SectorAssociationSchemeService sectorAssociationSchemeService, TargetUnitAccountQueryService targetUnitAccountQueryService) {
        super(validator, sectorAssociationSchemeService, targetUnitAccountQueryService);
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
