package uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.submit.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.facility.domain.dto.FacilityDTO;
import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataReportType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreement;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Cca3FacilityBaselineAndTargets;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityBaselineData;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityItem;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementQueryService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.common.domain.PerformanceDataFacilityDigitalFormRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.digitalform.submit.domain.PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestResource;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;

import java.time.Year;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerformanceDataFacilityDigitalFormSubmitInitializerTest {

    @InjectMocks
    private PerformanceDataFacilityDigitalFormSubmitInitializer initializer;

    @Mock
    private UnderlyingAgreementQueryService underlyingAgreementQueryService;

    @Test
    void initializePayload() {
        final Long accountId = 1L;
        final String facilityBusinessId = "facilityBusinessId";
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP7;
        final PerformanceDataReportType reportType = PerformanceDataReportType.FINAL;
        final Year reportYear = Year.of(2018);
        final FacilityDTO facility = FacilityDTO.builder().facilityBusinessId(facilityBusinessId).build();
        final Request request = Request.builder()
                .requestResources(List.of(RequestResource.builder()
                        .resourceType(ResourceType.ACCOUNT)
                        .resourceId(accountId.toString())
                        .build())
                )
                .payload(PerformanceDataFacilityDigitalFormRequestPayload.builder()
                        .targetPeriodType(targetPeriodType)
                        .reportType(reportType)
                        .targetPeriodYear(reportYear)
                        .facility(facility)
                        .build())
                .build();

        final Cca3FacilityBaselineAndTargets baselineData = Cca3FacilityBaselineAndTargets.builder()
                .baselineData(FacilityBaselineData.builder().explanation("explanation").build())
                .build();
        final UnderlyingAgreementContainer underlyingAgreement = UnderlyingAgreementContainer.builder()
                .underlyingAgreement(UnderlyingAgreement.builder()
                        .facilities(Set.of(Facility.builder()
                                .facilityItem(FacilityItem.builder()
                                        .facilityId(facilityBusinessId)
                                        .cca3BaselineAndTargets(baselineData)
                                        .build())
                                .build()))
                        .build())
                .build();
        final PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload expected =
                PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload.builder()
                        .payloadType(CcaRequestTaskPayloadType.PERFORMANCE_DATA_FACILITY_DIGITAL_FORM_SUBMIT_PAYLOAD)
                        .targetPeriodType(targetPeriodType)
                        .reportType(reportType)
                        .targetPeriodYear(reportYear)
                        .facility(facility)
                        .originalBaselineData(baselineData)
                        .build();

        when(underlyingAgreementQueryService.getUnderlyingAgreementContainerByAccountId(accountId))
                .thenReturn(underlyingAgreement);

        // Invoke
        RequestTaskPayload result = initializer.initializePayload(request);

        // Verify
        assertThat(result)
                .isInstanceOf(PerformanceDataFacilityDigitalFormSubmitRequestTaskPayload.class)
                .isEqualTo(expected);
        verify(underlyingAgreementQueryService, times(1))
                .getUnderlyingAgreementContainerByAccountId(accountId);
    }
}
