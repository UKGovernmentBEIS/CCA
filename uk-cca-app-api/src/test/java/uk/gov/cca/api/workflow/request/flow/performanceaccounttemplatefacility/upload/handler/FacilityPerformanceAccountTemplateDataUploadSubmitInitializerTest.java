package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationInfo;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.common.domain.FacilityPerformanceAccountTemplateDataUploadProcessingStatus;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataUploadRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataUploadResults;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestResource;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class FacilityPerformanceAccountTemplateDataUploadSubmitInitializerTest {

    @InjectMocks
    private FacilityPerformanceAccountTemplateDataUploadSubmitInitializer initializer;

    @Test
    void initializePayload() {
        final long sectorAssociationId = 1L;
        final SectorAssociationInfo sectorAssociationInfo = SectorAssociationInfo.builder()
                .id(sectorAssociationId)
                .build();
        final Request request = Request.builder()
                .requestResources(List.of(RequestResource.builder()
                        .resourceId(String.valueOf(sectorAssociationId))
                        .resourceType(CcaResourceType.SECTOR_ASSOCIATION)
                        .build())
                )
                .payload(FacilityPerformanceAccountTemplateDataUploadRequestPayload.builder()
                        .sectorAssociationInfo(sectorAssociationInfo)
                        .build())
                .build();

        final FacilityPerformanceAccountTemplateDataUploadResults results = FacilityPerformanceAccountTemplateDataUploadResults.builder()
                .totalFilesUploaded(0)
                .facilitiesSucceeded(0)
                .facilitiesFailed(0)
                .build();


        // Invoke
        RequestTaskPayload requestTaskPayload = initializer.initializePayload(request);

        // Verify
        assertThat(requestTaskPayload)
                .isNotNull()
                .isInstanceOf(FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload.class);
        assertThat(requestTaskPayload.getPayloadType()).isEqualTo(CcaRequestTaskPayloadType.FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_SUBMIT_PAYLOAD);
        assertThat(((FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload) requestTaskPayload).getSectorAssociationInfo())
                .isEqualTo(sectorAssociationInfo);
        assertThat(((FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload) requestTaskPayload).getProcessingStatus())
                .isEqualTo(FacilityPerformanceAccountTemplateDataUploadProcessingStatus.NOT_STARTED_YET);
        assertThat(((FacilityPerformanceAccountTemplateDataUploadSubmitRequestTaskPayload) requestTaskPayload).getResults())
                .isEqualTo(results);
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes()).containsExactly(CcaRequestTaskType.FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_SUBMIT);
    }
}
