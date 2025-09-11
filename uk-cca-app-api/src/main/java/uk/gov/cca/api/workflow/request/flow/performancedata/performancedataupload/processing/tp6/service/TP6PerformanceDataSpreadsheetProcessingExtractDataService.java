package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbookFactory;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.common.utils.ExcelCellUtils;
import uk.gov.cca.api.workflow.request.flow.performancedata.common.domain.TargetPeriodDocumentTemplate;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceDataCalculatedMetrics;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceDataCalculationParameters;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.domain.PerformanceDataSpreadsheetProcessingRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.common.service.PerformanceDataSpreadsheetProcessingExtractDataService;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain.TP6ParseExcelCellsReferenceEnum;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.domain.TP6PerformanceData;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.processing.tp6.transform.TP6CalculatedPerformanceDataMapper;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;

@Service
@RequiredArgsConstructor
public class TP6PerformanceDataSpreadsheetProcessingExtractDataService implements PerformanceDataSpreadsheetProcessingExtractDataService<TP6PerformanceData> {

    static {
        WorkbookFactory.addProvider(new XSSFWorkbookFactory());
      }

    private static final TP6CalculatedPerformanceDataMapper TP6_CALCULATED_PERFORMANCE_DATA_MAPPER = Mappers.getMapper(TP6CalculatedPerformanceDataMapper.class);

    @Override
    public TP6PerformanceData extractData(PerformanceDataSpreadsheetProcessingRequestMetadata metadata, FileDTO file) throws Exception {
        TP6PerformanceData tp6PerformanceData = TP6PerformanceData.builder()
                .type(metadata.getPerformanceDataTargetPeriodType())
                .submissionType(metadata.getSubmissionType())
                .build();

        Workbook workbook = loadTemplate(file.getFileContent());
        Map<TP6ParseExcelCellsReferenceEnum, String> extractedValues = processTemplate(workbook);

        applyValues(tp6PerformanceData, extractedValues);
        
        applyRequestedChangesToExtractedData(tp6PerformanceData);

        return tp6PerformanceData;
    }

    @Override
    public PerformanceDataCalculatedMetrics extractCalculatedData(TP6PerformanceData performanceData) {
        if(ObjectUtils.isEmpty(performanceData) || ObjectUtils.isEmpty(performanceData.getTargetUnitDetails())
                || ObjectUtils.isEmpty(performanceData.getActualTargetPeriodPerformance()) || ObjectUtils.isEmpty(performanceData.getPerformanceResult())
                || ObjectUtils.isEmpty(performanceData.getPrimaryDetermination()) || ObjectUtils.isEmpty(performanceData.getSecondaryDetermination())) {
            return PerformanceDataCalculatedMetrics.builder().build();
        }

        PerformanceDataCalculationParameters parameters = TP6_CALCULATED_PERFORMANCE_DATA_MAPPER
                .toPrepopulatedAndInputPerformanceData(performanceData);

        return TP6_CALCULATED_PERFORMANCE_DATA_MAPPER.toCalculatedPerformanceData(parameters);
    }

    @Override
    public TargetPeriodDocumentTemplate getDocumentTemplateType() {
        return TargetPeriodDocumentTemplate.REPORTING_SPREADSHEETS_DOWNLOAD_TP6;
    }

    private Workbook loadTemplate(byte[] fileContent) throws IOException {
        try (InputStream templateStream = new ByteArrayInputStream(fileContent)) {
            return WorkbookFactory.create(templateStream);
        }
    }

    private Map<TP6ParseExcelCellsReferenceEnum, String> processTemplate(Workbook workbook) {
        Map<TP6ParseExcelCellsReferenceEnum, String> extractedValues = new EnumMap<>(TP6ParseExcelCellsReferenceEnum.class);
        Sheet sheet = workbook.getSheetAt(1);
        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        for (TP6ParseExcelCellsReferenceEnum cellRef : TP6ParseExcelCellsReferenceEnum.values()) {
            int rowIndex = cellRef.getReferenceEnum().getRowIndex();
            int columnIndex = cellRef.getReferenceEnum().getColumnIndex();

            Row row = sheet.getRow(rowIndex);
            if (row != null) {
                Cell cell = row.getCell(columnIndex);
                if (cell != null) {
                    String cellValue = ExcelCellUtils.getCellValueAsString(cell, evaluator);
                    if (cellValue != null && !cellValue.isEmpty()) {
                        extractedValues.put(cellRef, cellValue);
                    }
                }
            }
        }
        return extractedValues;
    }

    /**
     * Parses and applies values to the given TP6PerformanceData object.
     * Calls the setter method only if the provided value is not null or empty.
     */
    private void applyValues(TP6PerformanceData data, Map<TP6ParseExcelCellsReferenceEnum, String> values) {
        values.forEach((enumReference, value) -> {
            if (value != null && !value.isEmpty()) {
                enumReference.parseValue(data, value);
            }
        });
    }
    
    /**
     * Changes requested by the client that override values read from the Excel file
     * 
     */
	private void applyRequestedChangesToExtractedData(TP6PerformanceData data) {
		if (data.getActualTargetPeriodPerformance().getTpChpDeliveredElectricity() == null) {
			data.getActualTargetPeriodPerformance().setTpChpDeliveredElectricity(BigDecimal.ZERO);
		}
	}
}