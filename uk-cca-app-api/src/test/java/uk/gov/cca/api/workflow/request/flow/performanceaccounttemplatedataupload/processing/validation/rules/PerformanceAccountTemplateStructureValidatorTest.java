package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.validation.rules;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain.TargetUnitIdentityAndPerformanceCellsReferenceEnum;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatedataupload.processing.domain.PerformanceAccountTemplateViolation;

@ExtendWith(MockitoExtension.class)
class PerformanceAccountTemplateStructureValidatorTest {
    
    private PerformanceAccountTemplateStructureValidator validator;
    private Sheet mockSheet;
    
    @BeforeEach
    void setUp() {
        validator = new PerformanceAccountTemplateStructureValidator();
        mockSheet = mock(Sheet.class);
        
        // Default mock for any row not specifically stubbed
        lenient().when(mockSheet.getRow(anyInt())).thenReturn(null);
    }
    
    @Test
    void validate_shouldReturnViolationWhenRowIsMissing() {
        // Arrange
        TargetUnitIdentityAndPerformanceCellsReferenceEnum firstCellRef = TargetUnitIdentityAndPerformanceCellsReferenceEnum.values()[0];
        when(mockSheet.getRow(firstCellRef.getCellAddress().getRow())).thenReturn(null);
        
        // Act
        List<PerformanceAccountTemplateViolation> violations = validator.validate(mockSheet);
        
        // Assert
        assertEquals(9, violations.size());
        assertEquals(firstCellRef.getCellAddress(), violations.get(0).getCellAddress());
        assertEquals(PerformanceAccountTemplateViolation.PerformanceAccountTemplateViolationMessage.CELL_ADDRESS_NOT_FOUND.getMessage(),
                violations.get(0).getMessage());
    }
    
    @Test
    void validate_shouldReturnBothRowMissingAndTotalKeywordViolations() {
        // Arrange
        for (TargetUnitIdentityAndPerformanceCellsReferenceEnum cellRef : TargetUnitIdentityAndPerformanceCellsReferenceEnum.values()) {
            when(mockSheet.getRow(cellRef.getCellAddress().getRow())).thenReturn(null);
        }
        
        // Act
        List<PerformanceAccountTemplateViolation> violations = validator.validate(mockSheet);
        
        // Assert
        assertEquals(9, violations.size());
        boolean hasRowViolation = violations.stream()
                .anyMatch(v -> v.getMessage().equals(PerformanceAccountTemplateViolation.PerformanceAccountTemplateViolationMessage.CELL_ADDRESS_NOT_FOUND.getMessage()));
        boolean hasTotalViolation = violations.stream()
                .anyMatch(v -> v.getMessage().equals(PerformanceAccountTemplateViolation.PerformanceAccountTemplateViolationMessage.TOTAL_KEYWORD_NOT_FOUND.getMessage()));
        
        assertTrue(hasRowViolation);
        assertTrue(hasTotalViolation);
    }
    
    @Test
    void validate_shouldReturnNoViolations_whenAllRowsExist() {
        Row mockRow = mock(Row.class);
        RichTextString richTextString = new XSSFRichTextString("Total");
        Cell mockTotalCell = mock(Cell.class);
        when(mockSheet.getRow(anyInt())).thenReturn(mockRow);
        when(mockRow.getCell(1)).thenReturn(mockTotalCell);
        when(mockTotalCell.getCellType()).thenReturn(CellType.STRING);
        when(mockTotalCell.getRichStringCellValue()).thenReturn(richTextString);
        List<PerformanceAccountTemplateViolation> violations = validator.validate(mockSheet);
        
        assertTrue(violations.isEmpty());
    }
    
    @Test
    void validate_shouldReturnViolation_whenTotalKeywordNotFound() {
        Row mockRow = mock(Row.class);
        when(mockSheet.getRow(anyInt())).thenReturn(mockRow);
        when(mockRow.getCell(anyInt())).thenReturn(null);
        
        List<PerformanceAccountTemplateViolation> violations = validator.validate(mockSheet);
        
        assertEquals(1, violations.size());
        assertEquals(PerformanceAccountTemplateViolation.PerformanceAccountTemplateViolationMessage.TOTAL_KEYWORD_NOT_FOUND.getMessage(),
                violations.get(0).getMessage());
    }
}
