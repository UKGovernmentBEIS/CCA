package uk.gov.cca.api.workflow.request.flow.common.service.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.domain.AgreementCompositionType;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.facility.domain.dto.FacilityAddressDTO;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.BaselineData;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetComposition;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetPeriod5Details;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetPeriod6Details;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.Targets;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Cca3FacilityBaselineAndTargets;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityDetails;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityItem;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityStatus;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityTargets;
import uk.gov.cca.api.workflow.request.core.transform.DocumentTemplateTransformationMapper;
import uk.gov.cca.api.workflow.request.flow.common.domain.notification.BaselineAndTargetsTemplateData;
import uk.gov.cca.api.workflow.request.flow.common.domain.notification.FacilityTemplateData;

@ExtendWith(MockitoExtension.class)
class CcaDocumentTemplateCommonUnderlyingAgreementParamsProviderTest {

	@InjectMocks
    private CcaDocumentTemplateCommonUnderlyingAgreementParamsProvider provider;
    
    @Mock
    private DocumentTemplateTransformationMapper documentTemplateTransformationMapper;
	
	@Test
    void constructTemplateParams() {

        final FacilityAddressDTO facilityAddress = FacilityAddressDTO.builder()
                .country("GR")
                .line1("line")
                .county("county")
                .city("city")
                .postcode("postcode")
                .build();
        final UnderlyingAgreement una = UnderlyingAgreement.builder()
                .targetPeriod5Details(TargetPeriod5Details.builder().exist(Boolean.FALSE).build())
                .targetPeriod6Details(TargetPeriod6Details.builder()
                        .targetComposition(TargetComposition.builder()
                                .agreementCompositionType(AgreementCompositionType.RELATIVE)
                                .isTargetUnitThroughputMeasured(Boolean.TRUE)
                                .throughputUnit("unit")
                                .measurementType(MeasurementType.ENERGY_KWH)
                                .build())
                        .baselineData(BaselineData.builder()
                                .energy(new BigDecimal("1000.0000000"))
                                .energyCarbonFactor(BigDecimal.valueOf(4000))
                                .throughput(BigDecimal.valueOf(1.0000001))
                                .usedReportingMechanism(Boolean.TRUE)
                                .build())
                        .targets(Targets.builder()
                                .improvement(new BigDecimal("0.0000000"))
                                .target(BigDecimal.valueOf(10.001))
                                .build())
                        .build())
                .facilities(Set.of(Facility.builder()
                                .status(FacilityStatus.EXCLUDED)
                                .facilityItem(FacilityItem
                                        .builder()
                                        .facilityId("facilityId")
                                        .facilityDetails(FacilityDetails.builder().name("name").uketsId("uketsId").build())
                                        .build()).build(),
                        Facility.builder()
                        		.status(FacilityStatus.NEW)
                        		.facilityItem(FacilityItem
                        				.builder()
                        				.facilityId("facilityId")
                        				.facilityDetails(FacilityDetails.builder()
                        						.name("name")
                        						.uketsId("uketsId")
                        						.participatingSchemeVersions(Set.of(SchemeVersion.CCA_2))
                        						.build())
                        				.build()).build(),
                        Facility.builder()
                                .status(FacilityStatus.NEW)
                                .facilityItem(FacilityItem
                                        .builder()
                                        .facilityId("facilityId2")
                                        .facilityDetails(FacilityDetails.builder()
                                                .name("name2")
                                                .uketsId("uketsId2")
                                                .facilityAddress(facilityAddress)
                                                .participatingSchemeVersions(Set.of(SchemeVersion.CCA_3))
                                                .build())
                                        .cca3BaselineAndTargets(Cca3FacilityBaselineAndTargets.builder()
                                        		.facilityTargets(FacilityTargets.builder()
                                        				.improvements(Map.of())
                                        				.build())
                                        		.build())
                                        .build())
                                .build()))
                .build();

        final BaselineAndTargetsTemplateData tp6data = BaselineAndTargetsTemplateData.builder()
                .targetType("Relative")
                .energy(new BigDecimal("1000.000"))
                .energyCarbonUnit("kWh")
                .throughput(new BigDecimal("1.000"))
                .throughputUnit("unit")
                .improvement(BigDecimal.ZERO)
                .target(BigDecimal.valueOf(10.001))
                .usedReportingMechanism(Boolean.TRUE)
                .build();

        final List<FacilityTemplateData> facilities = List.of(FacilityTemplateData.builder()
                .id("facilityId2")
                .name("name2")
                .uketsId("uketsId2")
                .address("line\ncity\npostcode\ncounty\n")
                .build());

        when(documentTemplateTransformationMapper.constructFacilityAddressDTO(facilityAddress))
                .thenReturn("line\ncity\npostcode\ncounty\n");

        // Invoke
        Map<String, Object> result = provider.constructTemplateParams(una, null, SchemeVersion.CCA_3, 1);

        // Verify
        Map<String, Object> paramMap = new HashMap<>(Map.of(
                "facilities", facilities,
                "baselineTargetTP5", BaselineAndTargetsTemplateData.builder().build(),
                "baselineTargetTP6", tp6data,
                "version", "v1"
        ));
        paramMap.put("activationDate", null);
        assertThat(result).isEqualTo(paramMap);

        verify(documentTemplateTransformationMapper, times(1))
                .constructFacilityAddressDTO(facilityAddress);
    }

    @Test
    void constructTemplateParams_variation() {

        final FacilityAddressDTO facilityAddress = FacilityAddressDTO.builder()
                .country("GR")
                .line1("line")
                .county("county")
                .city("city")
                .postcode("postcode")
                .build();

        final Facility facility1 = Facility.builder()
                .status(FacilityStatus.EXCLUDED)
                .facilityItem(FacilityItem
                        .builder()
                        .facilityId("facilityId1")
                        .facilityDetails(FacilityDetails.builder()
                                .name("name")
                                .uketsId("uketsId")
                                .facilityAddress(facilityAddress)
                                .build())
                        .build())
                .build();
        final Facility facility2 = Facility.builder()
                .status(FacilityStatus.LIVE)
                .facilityItem(FacilityItem
                        .builder()
                        .facilityId("facilityId2")
                        .facilityDetails(FacilityDetails.builder()
                                .name("name2")
                                .uketsId("uketsId2")
                                .facilityAddress(facilityAddress)
                                .participatingSchemeVersions(Set.of(SchemeVersion.CCA_3))
                                .build())
                        .cca3BaselineAndTargets(Cca3FacilityBaselineAndTargets.builder()
                        		.facilityTargets(FacilityTargets.builder()
                        				.improvements(Map.of())
                        				.build())
                        		.build())
                        .build())
                .build();

        UnderlyingAgreement una = UnderlyingAgreement.builder()
                .targetPeriod5Details(TargetPeriod5Details.builder().exist(Boolean.FALSE).build())
                .targetPeriod6Details(TargetPeriod6Details.builder()
                        .targetComposition(TargetComposition.builder()
                                .agreementCompositionType(AgreementCompositionType.RELATIVE)
                                .isTargetUnitThroughputMeasured(Boolean.TRUE)
                                .throughputUnit("unit")
                                .measurementType(MeasurementType.ENERGY_KWH)
                                .build())
                        .baselineData(BaselineData.builder()
                                .energy(new BigDecimal("1000.000000"))
                                .energyCarbonFactor(BigDecimal.valueOf(4000))
                                .throughput(BigDecimal.valueOf(1.3459))
                                .usedReportingMechanism(Boolean.TRUE)
                                .build())
                        .targets(Targets.builder()
                                .improvement(BigDecimal.valueOf(99))
                                .target(BigDecimal.valueOf(10))
                                .build())
                        .build())
                .facilities(Set.of(facility1, facility2))
                .build();


        BaselineAndTargetsTemplateData tp6data = BaselineAndTargetsTemplateData.builder()
                .targetType("Relative")
                .energy(new BigDecimal("1000.000"))
                .energyCarbonUnit("kWh")
                .throughput(BigDecimal.valueOf(1.346))
                .throughputUnit("unit")
                .improvement(new BigDecimal("99.000"))
                .target(new BigDecimal("10.000"))
                .usedReportingMechanism(Boolean.TRUE)
                .build();


        final List<FacilityTemplateData> facilities = List.of(FacilityTemplateData.builder()
                .id("facilityId2")
                .name("name2")
                .uketsId("uketsId2")
                .address("line\ncity\npostcode\ncounty\n")
                .build());

        when(documentTemplateTransformationMapper.constructFacilityAddressDTO(facilityAddress))
                .thenReturn("line\ncity\npostcode\ncounty\n");

        // Invoke
        Map<String, Object> result = provider.constructTemplateParams(una, null, SchemeVersion.CCA_3, 2);

        // Verify
        Map<String, Object> paramMap = new HashMap<>(Map.of(
                "facilities", facilities,
                "baselineTargetTP5", BaselineAndTargetsTemplateData.builder().build(),
                "baselineTargetTP6", tp6data,
                "version", "v2"
        ));
        paramMap.put("activationDate", null);
        assertThat(result).isEqualTo(paramMap);

        verify(documentTemplateTransformationMapper, times(1))
                .constructFacilityAddressDTO(facilityAddress);
    }
}
