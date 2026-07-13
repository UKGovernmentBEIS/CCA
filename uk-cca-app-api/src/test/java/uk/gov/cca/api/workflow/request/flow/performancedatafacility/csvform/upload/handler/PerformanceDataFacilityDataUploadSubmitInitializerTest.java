package uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationInfo;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.common.domain.PerformanceDataFacilityDataUploadProcessingStatus;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.domain.PerformanceDataFacilityDataUploadRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.domain.PerformanceDataFacilityDataUploadSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.domain.PerformanceDataFacilityUploadResults;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestResource;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PerformanceDataFacilityDataUploadSubmitInitializerTest {

    @InjectMocks
    private PerformanceDataFacilityDataUploadSubmitInitializer initializer;

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
                .payload(PerformanceDataFacilityDataUploadRequestPayload.builder()
                        .sectorAssociationInfo(sectorAssociationInfo)
                        .build())
                .build();

        final PerformanceDataFacilityUploadResults results = PerformanceDataFacilityUploadResults.builder()
                .totalFilesUploaded(0)
                .facilitiesSucceeded(0)
                .facilitiesFailed(0)
                .build();

        // Invoke
        RequestTaskPayload requestTaskPayload = initializer.initializePayload(request);

        // Verify
        assertThat(requestTaskPayload)
                .isNotNull()
                .isInstanceOf(PerformanceDataFacilityDataUploadSubmitRequestTaskPayload.class);
        assertThat(requestTaskPayload.getPayloadType()).isEqualTo(CcaRequestTaskPayloadType.PERFORMANCE_DATA_FACILITY_DATA_UPLOAD_SUBMIT_PAYLOAD);
        assertThat(((PerformanceDataFacilityDataUploadSubmitRequestTaskPayload) requestTaskPayload).getSectorAssociationInfo())
                .isEqualTo(sectorAssociationInfo);
        assertThat(((PerformanceDataFacilityDataUploadSubmitRequestTaskPayload) requestTaskPayload).getProcessingStatus())
                .isEqualTo(PerformanceDataFacilityDataUploadProcessingStatus.NOT_STARTED_YET);
        assertThat(((PerformanceDataFacilityDataUploadSubmitRequestTaskPayload) requestTaskPayload).getResults())
                .isEqualTo(results);
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes()).containsExactly(CcaRequestTaskType.PERFORMANCE_DATA_FACILITY_DATA_UPLOAD_SUBMIT);
    }
}
