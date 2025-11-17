package uk.gov.cca.api.workflow.request.flow.common.service.notification;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.common.domain.AgreementCompositionType;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetPeriod5Details;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetPeriod6Details;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityStatus;
import uk.gov.cca.api.underlyingagreement.domain.facilities.TargetImprovementType;
import uk.gov.cca.api.workflow.request.core.transform.DocumentTemplateTransformationMapper;
import uk.gov.cca.api.workflow.request.flow.common.domain.notification.BaselineAndTargetsTemplateData;
import uk.gov.cca.api.workflow.request.flow.common.domain.notification.FacilityTemplateData;

@Service
@RequiredArgsConstructor
public class CcaDocumentTemplateCommonUnderlyingAgreementParamsProvider {

	private final DocumentTemplateTransformationMapper documentTemplateTransformationMapper;
	
	public Map<String, Object> constructTemplateParams(
			final UnderlyingAgreement underlyingAgreement, 
			String activationDate, 
			SchemeVersion schemeVersion, 
			int version) {

        // Get only the active facilities in case of Variation, for given scheme version
        final List<FacilityTemplateData> activeFacilities = underlyingAgreement.getFacilities().stream()
                .filter(facility -> !facility.getStatus().equals(FacilityStatus.EXCLUDED))
                .filter(facility -> facility.getFacilityItem().getFacilityDetails().getParticipatingSchemeVersions().contains(schemeVersion))
                .map(this::getFacilityTemplateData)
                .toList();

        Map<String, Object> paramMap = new HashMap<>(Map.of(
                "facilities", activeFacilities,
                "version", "v" + version
        ));

        final BaselineAndTargetsTemplateData baselineTargetTP5 = getBaselineAndTargetsDataTP5(underlyingAgreement.getTargetPeriod5Details());
        paramMap.put("baselineTargetTP5", baselineTargetTP5);

        final BaselineAndTargetsTemplateData baselineTargetTP6 = getBaselineAndTargetsData(underlyingAgreement.getTargetPeriod6Details());
        paramMap.put("baselineTargetTP6", baselineTargetTP6);

        paramMap.put("activationDate", activationDate);

        return paramMap;
    }

    private BaselineAndTargetsTemplateData getBaselineAndTargetsDataTP5(TargetPeriod5Details targetPeriod5Details) {
        if(ObjectUtils.isEmpty(targetPeriod5Details)) {
            return null;
        }

        return Boolean.TRUE.equals(targetPeriod5Details.getExist())
                ? getBaselineAndTargetsData(targetPeriod5Details.getDetails())
                : BaselineAndTargetsTemplateData.builder().build();
    }

    private BaselineAndTargetsTemplateData getBaselineAndTargetsData(TargetPeriod6Details targetPeriod6Details) {
        if(ObjectUtils.isEmpty(targetPeriod6Details)) {
            return null;
        }

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
    	Map<TargetImprovementType, BigDecimal> improvements = facility.getFacilityItem().getFacilityDetails().getParticipatingSchemeVersions().contains(SchemeVersion.CCA_3)
    			? facility.getFacilityItem().getCca3BaselineAndTargets().getFacilityTargets().getImprovements()
                : Map.of();
    	
        return FacilityTemplateData.builder()
                .id(facility.getFacilityItem().getFacilityId())
                .name(facility.getFacilityItem().getFacilityDetails().getName())
                .address(documentTemplateTransformationMapper.constructFacilityAddressDTO(facility.getFacilityItem().getFacilityDetails().getFacilityAddress()))
                .uketsId(facility.getFacilityItem().getFacilityDetails().getUketsId())
                .tp7Target(improvements.get(TargetImprovementType.TP7))
                .tp8Target(improvements.get(TargetImprovementType.TP8))
                .tp9Target(improvements.get(TargetImprovementType.TP9))
                .build();
    }
}
