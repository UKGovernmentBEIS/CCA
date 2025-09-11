package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.submit.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityStatus;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.common.domain.UnderlyingAgreementPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.submit.domain.UnderlyingAgreementSaveRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementissuance.submit.domain.UnderlyingAgreementSubmitRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementApplyService {

    @Transactional
    public void applySaveAction(UnderlyingAgreementSaveRequestTaskActionPayload actionPayload, RequestTask requestTask) {
        UnderlyingAgreementSubmitRequestTaskPayload taskPayload =
                (UnderlyingAgreementSubmitRequestTaskPayload) requestTask.getPayload();

        if(!ObjectUtils.isEmpty(actionPayload.getUnderlyingAgreement())) {
            UnderlyingAgreementPayload agreementPayload = taskPayload.getUnderlyingAgreement();
            UnderlyingAgreementTargetUnitDetails saveTargetUnitDetails = actionPayload.getUnderlyingAgreement().getUnderlyingAgreementTargetUnitDetails();

            agreementPayload.setUnderlyingAgreementTargetUnitDetails(saveTargetUnitDetails);

            UnderlyingAgreement saveUnderlyingAgreement = UnderlyingAgreement.builder()
                    .facilities(actionPayload.getUnderlyingAgreement().getFacilities().stream()
                            .map(facilityItem -> Facility.builder().status(FacilityStatus.NEW).facilityItem(facilityItem).build())
                            .collect(Collectors.toSet()))
                    .authorisationAndAdditionalEvidence(actionPayload.getUnderlyingAgreement().getAuthorisationAndAdditionalEvidence())
                    .build();
            agreementPayload.setUnderlyingAgreement(saveUnderlyingAgreement);

            taskPayload.setUnderlyingAgreement(agreementPayload);
        }

        taskPayload.setSectionsCompleted(actionPayload.getSectionsCompleted());
    }
}
