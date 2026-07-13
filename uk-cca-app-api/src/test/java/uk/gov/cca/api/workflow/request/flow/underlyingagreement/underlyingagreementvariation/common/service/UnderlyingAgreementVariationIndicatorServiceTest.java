package uk.gov.cca.api.workflow.request.flow.underlyingagreement.underlyingagreementvariation.common.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.service.PerformanceDataFacilityService;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.service.PerformanceDataFacilityStatusService;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.facilities.ApplicationReasonType;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Cca3FacilityBaselineAndTargets;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityBaselineData;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityBaselineEnergyConsumption;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityDetails;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityItem;

@ExtendWith(MockitoExtension.class)
class UnderlyingAgreementVariationIndicatorServiceTest {

	@InjectMocks
    private UnderlyingAgreementVariationIndicatorService service;
    
    @Mock
    private PerformanceDataFacilityService performanceDataFacilityService;
    
    @Mock
    private PerformanceDataFacilityStatusService performanceDataFacilityStatusService;
    
    @Test
    void updateVariationIndicatorForPerformanceDataFacility() {
    	final SchemeVersion schemeVersion = SchemeVersion.CCA_3;
    	final Set<Year> targetYears = Set.of(Year.of(2026), Year.of(2027));

        final String facilityId1 = "facilityId1";
        final String facilityId2 = "facilityId2";


        FacilityItem facilityItem1 = FacilityItem.builder()
                .facilityId(facilityId1)
                .cca3BaselineAndTargets(Cca3FacilityBaselineAndTargets.builder()
                		.facilityBaselineEnergyConsumption(FacilityBaselineEnergyConsumption.builder()
                				.totalFixedEnergy(BigDecimal.ONE)
                				.build())
                		.baselineData(FacilityBaselineData.builder().baselineDate(LocalDate.now()).build())
                		.build())
                .facilityDetails(FacilityDetails.builder()
                        .previousFacilityId("Prv1")
                        .applicationReason(ApplicationReasonType.CHANGE_OF_OWNERSHIP)
                        .participatingSchemeVersions(Set.of(SchemeVersion.CCA_3))
                        .build())
                .build();
        FacilityItem currentFacilityItem1 = FacilityItem.builder()
                .facilityId(facilityId1)
                .cca3BaselineAndTargets(Cca3FacilityBaselineAndTargets.builder()
                		.facilityBaselineEnergyConsumption(FacilityBaselineEnergyConsumption.builder()
                				.totalFixedEnergy(BigDecimal.TWO)
                				.build())
                		.baselineData(FacilityBaselineData.builder().baselineDate(LocalDate.now()).build())
                		.build())
                .facilityDetails(FacilityDetails.builder()
                        .previousFacilityId("Prv1")
                        .applicationReason(ApplicationReasonType.CHANGE_OF_OWNERSHIP)
                        .participatingSchemeVersions(Set.of(SchemeVersion.CCA_3))
                        .build())
                .build();
        Facility facility1 = Facility.builder()
                .facilityItem(currentFacilityItem1)
                .build();     
        Facility currentFacility1 = Facility.builder()
                .facilityItem(facilityItem1)
                .build();
        FacilityItem facilityItem2 = FacilityItem.builder()
                .facilityId(facilityId2)
                .cca3BaselineAndTargets(Cca3FacilityBaselineAndTargets.builder()
                		.facilityBaselineEnergyConsumption(FacilityBaselineEnergyConsumption.builder()
                				.build())
                		.build())
                .facilityDetails(FacilityDetails.builder()
                        .previousFacilityId("Prv1")
                        .applicationReason(ApplicationReasonType.CHANGE_OF_OWNERSHIP)
                        .participatingSchemeVersions(Set.of(SchemeVersion.CCA_3))
                        .build())
                .build();
        FacilityItem currentFacilityItem2 = FacilityItem.builder()
                .facilityId(facilityId2)
                .cca3BaselineAndTargets(Cca3FacilityBaselineAndTargets.builder()
                		.facilityBaselineEnergyConsumption(FacilityBaselineEnergyConsumption.builder()
                				.totalFixedEnergy(BigDecimal.TWO)
                				.build())
                		.baselineData(FacilityBaselineData.builder().baselineDate(LocalDate.now()).build())
                		.build())
                .facilityDetails(FacilityDetails.builder()
                        .previousFacilityId("Prv1")
                        .applicationReason(ApplicationReasonType.CHANGE_OF_OWNERSHIP)
                        .participatingSchemeVersions(Set.of(SchemeVersion.CCA_2))
                        .build())
                .build();
        Facility facility2 = Facility.builder()
                .facilityItem(facilityItem2)
                .build();
        Facility currentFacility2 = Facility.builder()
                .facilityItem(currentFacilityItem2)
                .build();

        UnderlyingAgreementContainer originalUnderlyingAgreementContainer = UnderlyingAgreementContainer.builder()
				.underlyingAgreement(UnderlyingAgreement.builder().facilities(Set.of(facility1, facility2))
						.build())
				.build();
        UnderlyingAgreementContainer currentUnderlyingAgreementContainer = UnderlyingAgreementContainer.builder()
				.underlyingAgreement(UnderlyingAgreement.builder().facilities(Set.of(currentFacility1, currentFacility2))
						.build())
				.build();

        when(performanceDataFacilityService.getAvailableTargetPeriodYears(schemeVersion)).thenReturn(targetYears);

        // Invoke
        service.updateVariationIndicatorForPerformanceDataFacility(
        		originalUnderlyingAgreementContainer, currentUnderlyingAgreementContainer);

        // Verify
        verify(performanceDataFacilityService, times(1)).getAvailableTargetPeriodYears(schemeVersion);
        verify(performanceDataFacilityStatusService, times(1))
        		.updateFacilityPerformanceDataVariationIndicator(Set.of(facilityId1, facilityId2), Year.of(2026));
        verify(performanceDataFacilityStatusService, times(1))
				.updateFacilityPerformanceDataVariationIndicator(Set.of(facilityId1, facilityId2), Year.of(2027));

    }
}
