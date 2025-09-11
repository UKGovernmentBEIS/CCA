package uk.gov.cca.api.common.utils;

import java.math.BigDecimal;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

@Log4j2
@UtilityClass
public class ExcelCellUtils {

  public static Row getRow(Sheet sheet, int rowIndex) {
    Row row = sheet.getRow(rowIndex);
    if (row == null) {
      row = sheet.createRow(rowIndex);
    }
    return row;
  }

  public static Cell getCell(Row row, int columnIndex) {
    Cell cell = row.getCell(columnIndex);
    if (cell == null) {
      cell = row.createCell(columnIndex);
    }
    return cell;
  }


  public void setCellValue(Cell cell, Object value, String cellReferenceName) {
    switch (value) {
      case String s -> cell.setCellValue(s);
      case Number number -> cell.setCellValue(number.doubleValue());
      case LocalDate localDate -> cell.setCellValue(localDate);
      default ->
          throw new IllegalArgumentException("Unsupported data type for cell:" + cellReferenceName);
    }
  }

  public static String getCellValueAsString(Cell cell, FormulaEvaluator evaluator) {
    if (cell == null) {
      return null;
    }
    return switch (cell.getCellType()) {
      case STRING -> StringUtils.trim(cell.getStringCellValue());
      case NUMERIC -> DateUtil.isCellDateFormatted(cell)
          ? cell.getLocalDateTimeCellValue().toLocalDate().toString()
          : String.valueOf(cell.getNumericCellValue());
      case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
      case FORMULA -> evaluator != null ? evaluateFormula(cell, evaluator) : null;
      default -> null;
    };
  }

  public static boolean isBigDecimal(String value) {
    try {
      new BigDecimal(value);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  private static String evaluateFormula(Cell cell, FormulaEvaluator evaluator) {
    try {
      CellValue cellValue = evaluator.evaluate(cell);
      if (cellValue == null) {
        return null;
      }
      return switch (cellValue.getCellType()) {
        case STRING -> StringUtils.trim(cellValue.getStringValue());
        case NUMERIC -> String.valueOf(cellValue.getNumberValue());
        case BOOLEAN -> String.valueOf(cellValue.getBooleanValue());
        default -> null;
      };
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return null;
    }
  }
}
