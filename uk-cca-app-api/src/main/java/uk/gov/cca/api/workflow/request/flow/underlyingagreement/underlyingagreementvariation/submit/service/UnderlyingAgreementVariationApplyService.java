package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.submit.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.domain.UnderlyingAgreementVariationPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.submit.domain.UnderlyingAgreementVariationApplySaveTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.submit.domain.UnderlyingAgreementVariationSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.submit.domain.UnderlyingAgreementVariationSubmitRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementVariationApplyService {

    @Transactional
    public void applySaveAction(UnderlyingAgreementVariationSaveRequestTaskActionPayload actionPayload, RequestTask requestTask) {
        UnderlyingAgreementVariationSubmitRequestTaskPayload taskPayload =
                (UnderlyingAgreementVariationSubmitRequestTaskPayload) requestTask.getPayload();

        if(!ObjectUtils.isEmpty(actionPayload.getUnderlyingAgreement())) {
            UnderlyingAgreementVariationPayload agreementPayload = taskPayload.getUnderlyingAgreement();
            UnderlyingAgreementTargetUnitDetails targetUnitDetails = agreementPayload.getUnderlyingAgreementTargetUnitDetails();
            UnderlyingAgreementVariationApplySaveTargetUnitDetails saveTargetUnitDetails =
                    actionPayload.getUnderlyingAgreement().getUnderlyingAgreementTargetUnitDetails();

            targetUnitDetails.setOperatorName(saveTargetUnitDetails.getOperatorName());
            targetUnitDetails.setOperatorAddress(saveTargetUnitDetails.getOperatorAddress());
            targetUnitDetails.setResponsiblePersonDetails(saveTargetUnitDetails.getResponsiblePersonDetails());

            agreementPayload.setUnderlyingAgreementTargetUnitDetails(targetUnitDetails);
            agreementPayload.setUnderlyingAgreementVariationDetails(actionPayload.getUnderlyingAgreement().getUnderlyingAgreementVariationDetails());

            UnderlyingAgreement saveUnderlyingAgreement = UnderlyingAgreement.builder()
                    .facilities(actionPayload.getUnderlyingAgreement().getFacilities())
                    .targetPeriod5Details(actionPayload.getUnderlyingAgreement().getTargetPeriod5Details())
                    .targetPeriod6Details(actionPayload.getUnderlyingAgreement().getTargetPeriod6Details())
                    .authorisationAndAdditionalEvidence(actionPayload.getUnderlyingAgreement().getAuthorisationAndAdditionalEvidence())
                    .build();
            agreementPayload.setUnderlyingAgreement(saveUnderlyingAgreement);

            taskPayload.setUnderlyingAgreement(agreementPayload);
        }

        taskPayload.setSectionsCompleted(actionPayload.getSectionsCompleted());
        taskPayload.setReviewGroupDecisions(actionPayload.getReviewGroupDecisions());
        taskPayload.setFacilitiesReviewGroupDecisions(actionPayload.getFacilitiesReviewGroupDecisions());
        taskPayload.setReviewSectionsCompleted(actionPayload.getReviewSectionsCompleted());
    }
}
