package uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.targetperiodreporting.performancedatafacility.domain.PerformanceDataReportType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskActionType;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationInfo;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.common.domain.FacilityUploadReport;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.domain.PerformanceDataFacilityDataUploadProcessingRequestTaskActionPayload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.domain.PerformanceDataFacilityDataUploadSubmitRequestTaskPayload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.domain.PerformanceDataFacilityUpload;
import uk.gov.cca.api.workflow.request.flow.performancedatafacility.csvform.upload.service.PerformanceDataFacilityDataUploadService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.workflow.request.StartProcessRequestService;
import uk.gov.netz.api.workflow.request.WorkflowService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;
import uk.gov.netz.api.workflow.request.core.service.RequestTaskService;
import uk.gov.netz.api.workflow.request.flow.common.constants.BpmnProcessConstants;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerformanceDataFacilityDataUploadProcessingActionHandlerTest {

    @InjectMocks
    private PerformanceDataFacilityDataUploadProcessingActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private PerformanceDataFacilityDataUploadService performanceDataFacilityDataUploadService;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private StartProcessRequestService startProcessRequestService;

    @Test
    void process() {
        final Long requestTaskId = 1L;
        final String requestTaskActionType = CcaRequestTaskActionType.PERFORMANCE_DATA_FACILITY_DATA_UPLOAD_PROCESSING;
        final String userId = "userId";
        final AppUser appUser = AppUser.builder().userId(userId).build();
        final PerformanceDataFacilityDataUploadProcessingRequestTaskActionPayload payload =
                PerformanceDataFacilityDataUploadProcessingRequestTaskActionPayload.builder()
                        .performanceDataUpload(PerformanceDataFacilityUpload.builder()
                                .targetPeriodType(TargetPeriodType.TP7)
                                .reportType(PerformanceDataReportType.FINAL)
                                .build())
                        .build();

        final String requestId = "requestId";
        final String processInstanceId = "processInstanceId";
        final SectorAssociationInfo sectorAssociation = SectorAssociationInfo.builder()
                .id(11L)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .build();
        final Map<Long, FacilityUploadReport> facilityReports = Map.of(22L, FacilityUploadReport.builder().build());
        final PerformanceDataFacilityDataUploadSubmitRequestTaskPayload taskPayload =
                PerformanceDataFacilityDataUploadSubmitRequestTaskPayload.builder()
                        .sectorAssociationInfo(sectorAssociation)
                        .facilityReports(facilityReports)
                        .build();
        final RequestTask requestTask = RequestTask.builder()
                .payload(taskPayload)
                .request(Request.builder()
                        .id(requestId)
                        .processInstanceId(processInstanceId)
                        .build())
                .build();
        final String uploadRequestBusinessKey = "uploadRequestBusinessKey";

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        when(workflowService.getVariable(processInstanceId, BpmnProcessConstants.BUSINESS_KEY)).thenReturn(uploadRequestBusinessKey);

        // Invoke
        handler.process(requestTaskId, requestTaskActionType, appUser, payload);

        // Verify
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(performanceDataFacilityDataUploadService, times(1)).process(eq(requestTask), eq(payload), any());
        verify(workflowService, times(1)).getVariable(processInstanceId, BpmnProcessConstants.BUSINESS_KEY);
        verify(startProcessRequestService, times(1)).startProcess(any());
    }

    @Test
    void getRequestType() {
        assertThat(handler.getTypes()).containsExactly(CcaRequestTaskActionType.PERFORMANCE_DATA_FACILITY_DATA_UPLOAD_PROCESSING);
    }
}
