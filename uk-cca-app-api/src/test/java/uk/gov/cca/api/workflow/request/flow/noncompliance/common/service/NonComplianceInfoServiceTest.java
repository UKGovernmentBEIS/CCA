package uk.gov.cca.api.workflow.request.flow.noncompliance.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.facility.domain.dto.FacilityBaseInfoDTO;
import uk.gov.cca.api.facility.service.FacilityDataQueryService;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.workflow.request.application.taskview.RequestInfoDTO;
import uk.gov.netz.api.workflow.request.core.service.RequestQueryService;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NonComplianceInfoServiceTest {

    @InjectMocks
    private NonComplianceInfoService nonComplianceInfoService;

    @Mock
    private FacilityDataQueryService facilityDataQueryService;

    @Mock
    private RequestQueryService requestQueryService;

    @Test
    void getAllRelevantWorkflows() {

        final Long accountId = 1L;
        final String requestId = "ADS_1-T00003-NCOM-2";
        final String relevantRequestId = "ADS_1-F00007-AUDT-3";
        final RequestInfoDTO relevantRequest = RequestInfoDTO.builder()
                .id(relevantRequestId)
                .type("FACILITY_AUDIT")
                .resources(Map.of("1", "ACCOUNT"))
                .build();

        when(requestQueryService.findByResourceTypeAndResourceIdAndTypeNotIn(Collections.emptyList(), ResourceType.ACCOUNT, String.valueOf(accountId)))
                .thenReturn(List.of(relevantRequest));

        // invoke
        Map<String, String> result = nonComplianceInfoService.getAllRelevantWorkflows(accountId, requestId);

        // verify
        assertThat(result).hasSize(1);
        assertThat(result.containsKey(relevantRequestId)).isTrue();
    }

    @Test
    void getAllRelevantFacilities() {
        final Long accountId = 1L;
        final FacilityBaseInfoDTO facility = FacilityBaseInfoDTO.builder()
                .facilityBusinessId("ADS_1-F00014")
                .siteName("site1")
                .build();

        when(facilityDataQueryService.getFacilitiesByAccountId(accountId)).thenReturn(List.of(facility));

        // invoke
        Map<String, String> result = nonComplianceInfoService.getAllRelevantFacilities(accountId);

        // verify
        assertThat(result).hasSize(1);
        assertThat(result.get(facility.getFacilityBusinessId())).isEqualTo(facility.getSiteName());
    }
}
