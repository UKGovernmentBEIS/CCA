package uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.service.notification;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.account.domain.dto.AccountAddressDTO;
import uk.gov.cca.api.common.domain.MeasurementType;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.facility.domain.dto.FacilityAddressDTO;
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
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaDocumentTemplateCommonUnderlyingAgreementParamsProvider;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementTargetUnitDetails;
import uk.gov.cca.api.workflow.request.flow.underlyingagreement.common.domain.UnderlyingAgreementTargetUnitResponsiblePerson;

import java.math.BigDecimal;
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
    private CcaDocumentTemplateCommonUnderlyingAgreementParamsProvider ccaDocumentTemplateCommonUnderlyingAgreementParamsProvider;
    
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
                "targetUnitDetails", targetUnitDetailsParams
        );

        when(documentTemplateTransformationMapper.constructAccountAddressDTO(targetUnitDetails.getOperatorAddress()))
                .thenReturn("Op line1\nOp City\nOp postcode\nOp Country\n");
        when(documentTemplateTransformationMapper.constructAccountAddressDTO(targetUnitDetails.getResponsiblePersonDetails().getAddress()))
                .thenReturn("Res line1\nRes line2\nRes City\nRes postcode\nRes County\nRes Country\n");

        // Invoke
        Map<String, Object> actual = provider.constructTargetUnitDetailsTemplateParams(targetUnitDetails);

        // Verify
        assertThat(actual).isEqualTo(expected);
        verify(documentTemplateTransformationMapper, times(1))
                .constructAccountAddressDTO(targetUnitDetails.getOperatorAddress());
        verify(documentTemplateTransformationMapper, times(1))
                .constructAccountAddressDTO(targetUnitDetails.getResponsiblePersonDetails().getAddress());
    }

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
                                        .facilityId("facilityId2")
                                        .facilityDetails(FacilityDetails.builder()
                                                .name("name2")
                                                .uketsId("uketsId2")
                                                .facilityAddress(facilityAddress)
                                                .build())
                                        .build())
                                .build()))
                .build();

        when(ccaDocumentTemplateCommonUnderlyingAgreementParamsProvider.constructTemplateParams(una, null, SchemeVersion.CCA_3, 1))
        		.thenReturn(Map.of("params", una));

        // Invoke
        Map<String, Object> result = provider.constructTemplateParams(una, null, SchemeVersion.CCA_3, 1);

        // Verify
        assertThat(result).isEqualTo(Map.of("params", una));
        verify(ccaDocumentTemplateCommonUnderlyingAgreementParamsProvider, times(1))
        		.constructTemplateParams(una, null, SchemeVersion.CCA_3, 1);
    }
}
