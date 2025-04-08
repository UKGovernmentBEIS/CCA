package uk.gov.cca.api.workflow.request.flow.common.service.notification;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.account.domain.dto.AccountAddressDTO;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.notification.template.domain.TargetUnitDetailsParams;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.common.domain.AgreementCompositionType;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.BaselineData;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetComposition;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetPeriod5Details;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetPeriod6Details;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.Targets;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityDetails;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityItem;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityStatus;
import uk.gov.cca.api.workflow.request.core.transform.DocumentTemplateTransformationMapper;
import uk.gov.cca.api.workflow.request.flow.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.common.domain.UnderlyingAgreementTargetUnitResponsiblePerson;
import uk.gov.cca.api.workflow.request.flow.common.domain.notification.BaselineAndTargetsTemplateData;
import uk.gov.cca.api.workflow.request.flow.common.domain.notification.FacilityTemplateData;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentTemplateUnderlyingAgreementParamsProviderTest {

    @InjectMocks
    private DocumentTemplateUnderlyingAgreementParamsProvider provider;

    @Mock
    private DocumentTemplateTransformationMapper documentTemplateTransformationMapper;

    @Test
    void constructTargetUnitDetailsTemplateParams() {
        final UnderlyingAgreementTargetUnitDetails targetUnitDetails = UnderlyingAgreementTargetUnitDetails.builder()
                .operatorName("operatorName")
                .operatorAddress(AccountAddressDTO.builder()
                        .line1("Op line1")
                        .postcode("Op postcode")
                        .city("Op City")
                        .country("Op Country")
                        .build())
                .isCompanyRegistrationNumber(Boolean.TRUE)
                .companyRegistrationNumber("companyRegistrationNumber")
                .responsiblePersonDetails(UnderlyingAgreementTargetUnitResponsiblePerson.builder()
                        .email("res@test.gr")
                        .firstName("ResFirst")
                        .lastName("ResLast")
                        .address(AccountAddressDTO.builder()
                                .line1("Res Line 1")
                                .line2("Res Line 2")
                                .city("Res City")
                                .county("Res County")
                                .postcode("Res code")
                                .country("Res Country")
                                .build())
                        .build())
                .build();

        final TargetUnitDetailsParams targetUnitDetailsParams = TargetUnitDetailsParams.builder()
                .name("operatorName")
                .companyRegistrationNumber("companyRegistrationNumber")
                .targetUnitAddress("Op line1\nOp City\nOp postcode\nOp Country\n")
                .primaryContact("ResFirst ResLast")
                .primaryContactEmail("res@test.gr")
                .location("Res line1\nRes line2\nRes City\nRes postcode\nRes County\nRes Country\n")
                .build();
        Map<String, Object> expected = Map.of(
                "targetUnitDetails", targetUnitDetailsParams,
                "version", "v1"
        );

        when(documentTemplateTransformationMapper.constructAccountAddressDTO(targetUnitDetails.getOperatorAddress()))
                .thenReturn("Op line1\nOp City\nOp postcode\nOp Country\n");
        when(documentTemplateTransformationMapper.constructAccountAddressDTO(targetUnitDetails.getResponsiblePersonDetails().getAddress()))
                .thenReturn("Res line1\nRes line2\nRes City\nRes postcode\nRes County\nRes Country\n");

        // Invoke
        Map<String, Object> actual = provider.constructTargetUnitDetailsTemplateParams(targetUnitDetails, 1);

        // Verify
        assertThat(actual).isEqualTo(expected);
        verify(documentTemplateTransformationMapper, times(1))
                .constructAccountAddressDTO(targetUnitDetails.getOperatorAddress());
        verify(documentTemplateTransformationMapper, times(1))
                .constructAccountAddressDTO(targetUnitDetails.getResponsiblePersonDetails().getAddress());
    }

    @Test
    void constructTemplateParams() {
        Set<String> rejectedFacilityIds = Set.of("facilityId");

        final AccountAddressDTO facilityAddress = AccountAddressDTO.builder()
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
                                .energy(BigDecimal.valueOf(1000))
                                .energyCarbonFactor(BigDecimal.valueOf(4000))
                                .throughput(BigDecimal.valueOf(1))
                                .usedReportingMechanism(Boolean.TRUE)
                                .build())
                        .targets(Targets.builder()
                                .improvement(BigDecimal.valueOf(99))
                                .target(BigDecimal.valueOf(10))
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
                                        .facilityId("facilityId2")
                                        .facilityDetails(FacilityDetails.builder()
                                                .name("name2")
                                                .uketsId("uketsId2")
                                                .facilityAddress(facilityAddress)
                                                .build())
                                        .build())
                                .build()))
                .build();

        final BaselineAndTargetsTemplateData tp6data = BaselineAndTargetsTemplateData.builder()
                .targetType("Relative")
                .energy(BigDecimal.valueOf(1000))
                .energyCarbonUnit("kWh")
                .throughput(BigDecimal.valueOf(1))
                .throughputUnit("unit")
                .improvement(BigDecimal.valueOf(99) + "%")
                .target(BigDecimal.valueOf(10.0).setScale(3, RoundingMode.HALF_UP))
                .usedReportingMechanism(Boolean.TRUE)
                .build();

        final List<FacilityTemplateData> facilities = List.of(FacilityTemplateData.builder()
                .id("facilityId2")
                .name("name2")
                .uketsId("uketsId2")
                .address("line\ncity\npostcode\ncounty\n")
                .build());

        when(documentTemplateTransformationMapper.constructAccountAddressDTO(facilityAddress))
                .thenReturn("line\ncity\npostcode\ncounty\n");

        // Invoke
        Map<String, Object> result = provider.constructTemplateParams(una, null, 1);

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
                .constructAccountAddressDTO(facilityAddress);
    }

    @Test
    void constructTemplateParams_variation() {
        Set<String> rejectedFacilityIds = Set.of("facilityId1");

        final AccountAddressDTO facilityAddress = AccountAddressDTO.builder()
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
                                .build())
                        .build())
                .build();

        final Set<Facility> originalFacilities = Set.of();

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
                                .energy(BigDecimal.valueOf(1000))
                                .energyCarbonFactor(BigDecimal.valueOf(4000))
                                .throughput(BigDecimal.valueOf(1))
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
                .energy(BigDecimal.valueOf(1000))
                .energyCarbonUnit("kWh")
                .throughput(BigDecimal.valueOf(1))
                .throughputUnit("unit")
                .improvement(BigDecimal.valueOf(99) + "%")
                .target(BigDecimal.valueOf(10.0).setScale(3))
                .usedReportingMechanism(Boolean.TRUE)
                .build();


        final List<FacilityTemplateData> facilities = List.of(FacilityTemplateData.builder()
                .id("facilityId2")
                .name("name2")
                .uketsId("uketsId2")
                .address("line\ncity\npostcode\ncounty\n")
                .build());

        when(documentTemplateTransformationMapper.constructAccountAddressDTO(facilityAddress))
                .thenReturn("line\ncity\npostcode\ncounty\n");

        // Invoke
        Map<String, Object> result = provider.constructTemplateParams(una, null, 2);

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
                .constructAccountAddressDTO(facilityAddress);
    }
}
