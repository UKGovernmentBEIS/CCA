package uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.service.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.notification.template.domain.TargetUnitDetailsParams;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.common.domain.AgreementCompositionType;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetPeriod5Details;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetPeriod6Details;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityStatus;
import uk.gov.cca.api.workflow.request.core.transform.DocumentTemplateTransformationMapper;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementTargetUnitResponsiblePerson;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.notification.BaselineAndTargetsTemplateData;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.notification.FacilityTemplateData;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentTemplateUnderlyingAgreementParamsProvider {

    private final DocumentTemplateTransformationMapper documentTemplateTransformationMapper;

    public Map<String, Object> constructTargetUnitDetailsTemplateParams(UnderlyingAgreementTargetUnitDetails targetUnitDetails, int version) {
        Map<String, Object> params = constructTargetUnitDetailsTemplateParams(targetUnitDetails);
        params.put("version", "v" + version);

        return params;
    }

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

    public Map<String, Object> constructTemplateParams(final UnderlyingAgreement underlyingAgreement, String activationDate, int version) {

        // get only the active facilities in case of Variation
        final List<FacilityTemplateData> activeFacilities = underlyingAgreement.getFacilities().stream()
                .filter(facility -> !facility.getStatus().equals(FacilityStatus.EXCLUDED))
                .map(this::getFacilityTemplateData)
                .collect(Collectors.toList());

        final BaselineAndTargetsTemplateData baselineTargetTP5 = getBaselineAndTargetsDataTP5(underlyingAgreement.getTargetPeriod5Details());
        final BaselineAndTargetsTemplateData baselineTargetTP6 = getBaselineAndTargetsData(underlyingAgreement.getTargetPeriod6Details());

        Map<String, Object> paramMap = new HashMap<>(Map.of(
                "facilities", activeFacilities,
                "baselineTargetTP5", baselineTargetTP5,
                "baselineTargetTP6", baselineTargetTP6,
                "version", "v" + version
        ));
        paramMap.put("activationDate", activationDate);
        return paramMap;

    }

    private BaselineAndTargetsTemplateData getBaselineAndTargetsDataTP5(TargetPeriod5Details targetPeriod5Details) {
        return Boolean.TRUE.equals(targetPeriod5Details.getExist())
                ? getBaselineAndTargetsData(targetPeriod5Details.getDetails())
                : BaselineAndTargetsTemplateData.builder().build();
    }

    private BaselineAndTargetsTemplateData getBaselineAndTargetsData(TargetPeriod6Details targetPeriod6Details) {
        AgreementCompositionType targetType = targetPeriod6Details.getTargetComposition().getAgreementCompositionType();
        return BaselineAndTargetsTemplateData.builder()
                .targetType(targetType.getDescription())
                .throughput(!AgreementCompositionType.NOVEM.equals(targetType)
                        ? convertToThreeDecimals(targetPeriod6Details.getBaselineData().getThroughput())
                        : null)
                .energy(convertToThreeDecimals(targetPeriod6Details.getBaselineData().getEnergy()))
                .usedReportingMechanism(targetPeriod6Details.getBaselineData().getUsedReportingMechanism())
                .throughputUnit(targetPeriod6Details.getTargetComposition().getThroughputUnit())
                .energyCarbonUnit(targetPeriod6Details.getTargetComposition().getMeasurementType().getUnit())
                .target(!AgreementCompositionType.NOVEM.equals(targetType)
                        ? convertToThreeDecimals(targetPeriod6Details.getTargets().getTarget())
                        : null)
                .improvement(convertToThreeDecimals(targetPeriod6Details.getTargets().getImprovement()))
                .build();
    }
    
    private BigDecimal convertToThreeDecimals(BigDecimal value) {
    	return value.compareTo(BigDecimal.ZERO) != 0 ? value.setScale(3, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    private FacilityTemplateData getFacilityTemplateData(Facility facility) {
        return FacilityTemplateData.builder()
                .id(facility.getFacilityItem().getFacilityId())
                .name(facility.getFacilityItem().getFacilityDetails().getName())
                .address(documentTemplateTransformationMapper.constructAccountAddressDTO(facility.getFacilityItem().getFacilityDetails().getFacilityAddress()))
                .uketsId(facility.getFacilityItem().getFacilityDetails().getUketsId())
                .build();
    }
}
