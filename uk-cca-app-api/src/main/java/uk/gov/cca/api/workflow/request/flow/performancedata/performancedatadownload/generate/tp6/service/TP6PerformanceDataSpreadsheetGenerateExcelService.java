package uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.generate.tp6.service;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbookFactory;
import org.jetbrains.annotations.NotNull;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.common.utils.ExcelCellUtils;
import uk.gov.cca.api.workflow.request.flow.performancedata.common.domain.TargetPeriodDocumentTemplate;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.generate.common.domain.PerformanceDataSpreadsheetGenerateRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.generate.common.service.PerformanceDataSpreadsheetGenerateExcelService;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.generate.tp6.domain.TP6ExtractExcelCellsReferenceEnum;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.generate.tp6.domain.TP6Data;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.generate.tp6.transform.TP6DataMapper;
import uk.gov.netz.api.files.common.FileType;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class TP6PerformanceDataSpreadsheetGenerateExcelService implements PerformanceDataSpreadsheetGenerateExcelService {

    private static final TP6DataMapper TP6_DATA_MAPPER = Mappers.getMapper(TP6DataMapper.class);

    static {
        WorkbookFactory.addProvider(new XSSFWorkbookFactory());
    }

    @Override
    public FileDTO generate(PerformanceDataSpreadsheetGenerateRequestMetadata metadata,
                            Long accountId) throws Exception {
        FileDTO template = metadata.getTemplate();
        String sectorAcronym = metadata.getSectorAssociationInfo().getAcronym();
        TP6Data tp6Data = TP6_DATA_MAPPER.toTP6Data(sectorAcronym, metadata.getUnderlyingAgreement(),
                metadata.getTargetUnitAccountDetails(), metadata.getReportVersion(), metadata.getSubmissionType(), metadata.getLastUploadedReport());

        Workbook workbook = loadTemplate(template.getFileContent());
        populateTemplate(workbook, tp6Data);
        workbook.setForceFormulaRecalculation(true);
        writeWorkbookToTemplate(workbook, template, tp6Data);

        return template;
    }

    @Override
    public TargetPeriodDocumentTemplate getTemplateType() {
        return TargetPeriodDocumentTemplate.REPORTING_SPREADSHEETS_DOWNLOAD_TP6;
    }

    private Workbook loadTemplate(byte[] fileContent) throws IOException {
        try (InputStream templateStream = new ByteArrayInputStream(fileContent)) {
            return WorkbookFactory.create(templateStream);
        }
    }

    private void populateTemplate(Workbook workbook, TP6Data tp6Data) {
        Sheet sheet = workbook.getSheetAt(1);
        for (TP6ExtractExcelCellsReferenceEnum cellReference : TP6ExtractExcelCellsReferenceEnum.values()) {
            Object value = cellReference.extractValue(tp6Data);
            if (value != null) {
                Row row = ExcelCellUtils.getRow(sheet, cellReference.getReferenceEnum().getRowIndex());
                Cell cell = ExcelCellUtils.getCell(row, cellReference.getReferenceEnum().getColumnIndex());
                ExcelCellUtils.setCellValue(cell, value, cellReference.toString());
            }
        }
    }

    private void writeWorkbookToTemplate(Workbook workbook, FileDTO template, TP6Data tp6Data)
            throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            workbook.write(out);
            template.setFileContent(out.toByteArray());
        } finally {
            workbook.close();
        }

        String filename = constructFilename(tp6Data);
        template.setFileName(filename);
    }

    private static @NotNull String constructFilename(TP6Data tp6Data) {
        StringBuilder sb = new StringBuilder();
        sb.append(tp6Data.getTargetUnitId())
                .append("_TPR_")
                .append(tp6Data.getTargetPeriod())
                .append("_V")
                .append(tp6Data.getReportVersion())
                .append(".")
                .append(FileType.XLSX.getSimpleType());

        return sb.toString();
    }
}
