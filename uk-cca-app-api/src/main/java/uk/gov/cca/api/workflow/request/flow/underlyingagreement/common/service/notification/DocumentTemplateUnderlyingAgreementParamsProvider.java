package uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.service.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.notification.template.domain.TargetUnitDetailsParams;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.workflow.request.core.transform.DocumentTemplateTransformationMapper;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaDocumentTemplateCommonUnderlyingAgreementParamsProvider;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementTargetUnitResponsiblePerson;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DocumentTemplateUnderlyingAgreementParamsProvider {

    private final DocumentTemplateTransformationMapper documentTemplateTransformationMapper;
    private final CcaDocumentTemplateCommonUnderlyingAgreementParamsProvider ccaDocumentTemplateCommonUnderlyingAgreementParamsProvider;

    public Map<String, Object> constructTargetUnitDetailsTemplateParams(UnderlyingAgreementTargetUnitDetails targetUnitDetails) {
        UnderlyingAgreementTargetUnitResponsiblePerson responsiblePerson = targetUnitDetails.getResponsiblePersonDetails();

        TargetUnitDetailsParams targetUnitDetailsParams = TargetUnitDetailsParams.builder()
                .name(targetUnitDetails.getOperatorName())
                .companyRegistrationNumber(targetUnitDetails.getCompanyRegistrationNumber())
                .targetUnitAddress(documentTemplateTransformationMapper.constructAccountAddressDTO(targetUnitDetails.getOperatorAddress()))
                .primaryContact(responsiblePerson.getFirstName() + " " + responsiblePerson.getLastName())
                .primaryContactEmail(responsiblePerson.getEmail())
                .location(documentTemplateTransformationMapper.constructAccountAddressDTO(responsiblePerson.getAddress()))
                .build();

        return new HashMap<>(Map.of("targetUnitDetails", targetUnitDetailsParams));
    }

    public Map<String, Object> constructTemplateParams(
    		final UnderlyingAgreement underlyingAgreement, 
    		String activationDate, 
    		SchemeVersion schemeVersion,
    		int version) {
        return ccaDocumentTemplateCommonUnderlyingAgreementParamsProvider
        		.constructTemplateParams(underlyingAgreement, activationDate, schemeVersion, version);
    }
}
