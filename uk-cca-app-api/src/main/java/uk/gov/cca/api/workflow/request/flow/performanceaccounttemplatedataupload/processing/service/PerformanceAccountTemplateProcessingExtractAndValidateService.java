package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.Workbook;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.targetperiodreporting.performanceaccounttemplatedata.domain.PerformanceAccountTemplateDataContainer;
import uk.gov.cca.api.workflow.request.flow.common.configuration.WorkbookFactoryConfiguration.CustomWorkbookFactory;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.common.domain.AccountUploadReport;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain.PerformanceAccountTemplateReportData;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain.PerformanceAccountTemplateViolation;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.transform.PerformanceAccountTemplateDataContainerMapper;
import uk.gov.netz.api.files.attachments.service.FileAttachmentService;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class PerformanceAccountTemplateProcessingExtractAndValidateService {
    
    private final CustomWorkbookFactory workbookFactory;
    private final FileAttachmentService fileAttachmentService;
    private final PerformanceAccountTemplateProcessingValidationService validationService;
    private final PerformanceAccountTemplateProcessingExtractDataService dataExtractionService;
    private static final PerformanceAccountTemplateDataContainerMapper PERFORMANCE_ACCOUNT_TEMPLATE_DATA_CONTAINER_MAPPER
            = Mappers.getMapper(PerformanceAccountTemplateDataContainerMapper.class);
    
    public Optional<PerformanceAccountTemplateDataContainer> extractAndValidateData(AccountUploadReport report, List<PerformanceAccountTemplateViolation> errors) throws IOException {
        FileDTO accountReportFile =
                fileAttachmentService.getFileDTO(report.getFile().getUuid());
        
        Workbook workbook = loadTemplate(accountReportFile.getFileContent());
        
        List<PerformanceAccountTemplateViolation> structureValidationResult =
                validationService.validateTemplateStructure(workbook);
        
        if (!structureValidationResult.isEmpty()) {
            errors.addAll(structureValidationResult);
            return Optional.empty();
        }
        
        PerformanceAccountTemplateReportData reportData = dataExtractionService.extractData(workbook);
        
        List<PerformanceAccountTemplateViolation> dataValidationResult =
                validationService.validateData(reportData, report.getAccountBusinessId());
        
        if (!dataValidationResult.isEmpty()) {
            errors.addAll(dataValidationResult);
            return Optional.empty();
        }
        
        return Optional.of(PERFORMANCE_ACCOUNT_TEMPLATE_DATA_CONTAINER_MAPPER
                .toPerformanceAccountTemplateDataContainer(reportData, report.getFile()));
    }
    
    private Workbook loadTemplate(byte[] fileContent) throws IOException {
        try (InputStream templateStream = new ByteArrayInputStream(fileContent)) {
            return workbookFactory.create(templateStream);
        }
    }
}

