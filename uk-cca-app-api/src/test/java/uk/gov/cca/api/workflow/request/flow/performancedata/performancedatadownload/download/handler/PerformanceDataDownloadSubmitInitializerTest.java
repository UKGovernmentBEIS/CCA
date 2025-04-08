package uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.download.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationInfo;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.download.domain.PerformanceDataDownloadRequestPayload;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.download.domain.PerformanceDataDownloadSubmitRequestTaskPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestResource;
import uk.gov.netz.api.workflow.request.core.domain.RequestTaskPayload;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PerformanceDataDownloadSubmitInitializerTest {

    @InjectMocks
    private PerformanceDataDownloadSubmitInitializer initializer;

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
                .payload(PerformanceDataDownloadRequestPayload.builder()
                        .sectorAssociationInfo(sectorAssociationInfo)
                        .build())
                .build();

        // Invoke
        RequestTaskPayload requestTaskPayload = initializer.initializePayload(request);

        // Verify
        assertThat(requestTaskPayload)
                .isNotNull()
                .isInstanceOf(PerformanceDataDownloadSubmitRequestTaskPayload.class);
        assertThat(requestTaskPayload.getPayloadType()).isEqualTo(CcaRequestTaskPayloadType.PERFORMANCE_DATA_DOWNLOAD_SUBMIT_PAYLOAD);
        assertThat(((PerformanceDataDownloadSubmitRequestTaskPayload) requestTaskPayload).getSectorAssociationInfo())
                .isEqualTo(sectorAssociationInfo);
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes()).containsExactly(CcaRequestTaskType.PERFORMANCE_DATA_DOWNLOAD_SUBMIT);
    }
}
