package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.PerformanceAccountTemplateDataContainer;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.service.PerformanceAccountTemplateDataService;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.common.domain.AccountUploadReport;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain.PerformanceAccountTemplateProcessingRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain.PerformanceAccountTemplateViolation;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

@ExtendWith(MockitoExtension.class)
class PerformanceAccountTemplateProcessingServiceTest {
    
    @Mock
    private RequestService requestService;
    
    @Mock
    private PerformanceAccountTemplateProcessingExtractAndValidateService extractAndValidateService;
    
    @Mock
    private PerformanceAccountTemplateProcessingRequestActionService requestActionService;
    
    @Mock
    private PerformanceAccountTemplateDataService dataService;
    
    @InjectMocks
    private PerformanceAccountTemplateProcessingService performanceAccountTemplateProcessingService;
    
    private static final String REQUEST_ID = "testRequestId";
    private Request mockRequest;
    private PerformanceAccountTemplateProcessingRequestMetadata mockMetadata;
    private AccountUploadReport accountUploadReport;
    
    @BeforeEach
    void setUp() {
        mockRequest = spy(new Request());
        
        mockMetadata = new PerformanceAccountTemplateProcessingRequestMetadata();
        mockMetadata.setAccountId(1L);
        mockMetadata.setTargetPeriodType(TargetPeriodType.TP6);
        mockMetadata.setTargetPeriodYear(Year.now());
        mockMetadata.setReportVersion(1);
        
        doReturn(mockMetadata).when(mockRequest).getMetadata();
        
        accountUploadReport = new AccountUploadReport();
        accountUploadReport.setFile(new FileInfoDTO("test-file.xlsx", "uuid"));
        
        when(requestService.findRequestById(REQUEST_ID)).thenReturn(mockRequest);
    }
    
    @Test
    void testDoProcess_SuccessfulPath() throws Exception {
        PerformanceAccountTemplateDataContainer container =
                PerformanceAccountTemplateDataContainer.builder().build();
        
        when(extractAndValidateService.extractAndValidateData(any(AccountUploadReport.class), anyList()))
                .thenReturn(Optional.of(container));
        
        performanceAccountTemplateProcessingService.doProcess(REQUEST_ID, accountUploadReport);
        
        verify(dataService).submitPerformanceAccountTemplate(
                container,
                1L,
                TargetPeriodType.TP6,
                Year.now(),
                1
        );
        
        verify(requestActionService).addSubmittedAction(mockRequest, mockMetadata, container);
        
        ArgumentCaptor<LocalDateTime> submissionDateCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(mockRequest).setSubmissionDate(submissionDateCaptor.capture());
        LocalDateTime capturedDate = submissionDateCaptor.getValue();
        assertTrue(capturedDate.isBefore(LocalDateTime.now().plusSeconds(1)) &&
                        capturedDate.isAfter(LocalDateTime.now().minusSeconds(1)));
    }
    
    @Test
    void testDoProcess_ErrorPath_WhenNoContainerPresent() throws Exception {
        List<PerformanceAccountTemplateViolation> errors = new ArrayList<>();
        errors.add(new PerformanceAccountTemplateViolation(PerformanceAccountTemplateViolation.PerformanceAccountTemplateViolationMessage.PROCESS_EXCEL_FAILED));
        
        when(extractAndValidateService.extractAndValidateData(any(AccountUploadReport.class), anyList()))
                .thenReturn(Optional.empty());
        
        PerformanceAccountTemplateProcessingException exception = assertThrows(
                PerformanceAccountTemplateProcessingException.class,
                () -> performanceAccountTemplateProcessingService.doProcess(REQUEST_ID, accountUploadReport)
        );
        
        assertTrue(exception.getMessage().contains(PerformanceAccountTemplateViolation.PerformanceAccountTemplateViolationMessage.PROCESS_EXCEL_FAILED.getMessage()));
        
        assertTrue(accountUploadReport.getErrorFilenames().contains("test-file.xlsx"));
        
        verify(dataService, never()).submitPerformanceAccountTemplate(any(), anyLong(), any(TargetPeriodType.class), any(Year.class), anyInt());
        verify(requestActionService, never()).addSubmittedAction(any(), any(), any());
    }
    
    @Test
    void testDoProcess_IOException() throws Exception {
        when(extractAndValidateService.extractAndValidateData(any(AccountUploadReport.class), anyList()))
                .thenThrow(new IOException("I/O error while processing"));
        
        assertThrows(IOException.class,
                () -> performanceAccountTemplateProcessingService.doProcess(REQUEST_ID, accountUploadReport));
        
        verify(dataService, never()).submitPerformanceAccountTemplate(any(), anyLong(), any(TargetPeriodType.class), any(Year.class), anyInt());
        verify(requestActionService, never()).addSubmittedAction(any(), any(), any());
    }
}
