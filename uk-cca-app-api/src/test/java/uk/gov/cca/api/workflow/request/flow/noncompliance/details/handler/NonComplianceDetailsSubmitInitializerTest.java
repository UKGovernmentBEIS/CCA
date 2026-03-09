package uk.gov.cca.api.workflow.request.flow.noncompliance.details.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.domain.NonComplianceRequestPayload;
import uk.gov.cca.api.workflow.request.flow.noncompliance.common.service.NonComplianceInfoService;
import uk.gov.cca.api.workflow.request.flow.noncompliance.details.domain.NonComplianceDetailsSubmitRequestTaskPayload;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestResource;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NonComplianceDetailsSubmitInitializerTest {

    @InjectMocks
    private NonComplianceDetailsSubmitInitializer nonComplianceDetailsSubmitInitializer;

    @Mock
    private NonComplianceInfoService nonComplianceInfoService;

    @Test
    void initializePayload() {

        final String accountId = "1";
        final Long aId = Long.valueOf(accountId);
        final Map<String, String> relevantWorkflows = Map.of("ADS_1-F00007-AUDT-3", "", "ADS_1-F00007-AUDT-2", "");
        final Map<String, String> relevantFacilities = Map.of("ADS_1-F00010", "site1", "ADS_1-F00011", "site2");

        final Request request = Request.builder()
                .id("ADS_1-T00003-NCOM-1")
                .payload(NonComplianceRequestPayload.builder().build())
                .requestResources(List.of(RequestResource.builder()
                        .resourceId(accountId)
                        .resourceType(ResourceType.ACCOUNT)
                        .build()))
                .build();

        final NonComplianceDetailsSubmitRequestTaskPayload expected = NonComplianceDetailsSubmitRequestTaskPayload.builder()
                .payloadType(CcaRequestTaskPayloadType.NON_COMPLIANCE_DETAILS_SUBMIT_PAYLOAD)
                .allRelevantWorkflows(relevantWorkflows)
                .allRelevantFacilities(relevantFacilities)
                .sectionsCompleted(Map.of())
                .build();

        when(nonComplianceInfoService.getAllRelevantWorkflows(aId, request.getId())).thenReturn(relevantWorkflows);
        when(nonComplianceInfoService.getAllRelevantFacilities(aId)).thenReturn(relevantFacilities);

        // Invoke
        RequestTaskPayload actual = nonComplianceDetailsSubmitInitializer.initializePayload(request);

        // Verify
        assertThat(actual).isInstanceOf(NonComplianceDetailsSubmitRequestTaskPayload.class).isEqualTo(expected);
        verify(nonComplianceInfoService, times(1)).getAllRelevantWorkflows(aId, request.getId());
        verify(nonComplianceInfoService, times(1)).getAllRelevantFacilities(aId);
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(nonComplianceDetailsSubmitInitializer.getRequestTaskTypes())
                .containsExactly(CcaRequestTaskType.NON_COMPLIANCE_DETAILS_SUBMIT);
    }
}
