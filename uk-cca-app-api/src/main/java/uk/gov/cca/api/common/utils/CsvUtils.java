package uk.gov.cca.api.common.utils;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvException;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;

import uk.gov.cca.api.common.domain.CsvErrorEntry;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@UtilityClass
public class CsvUtils {

    public static final String CSV_ROW_ERROR_PATTERN = "%s [row: %d] %s";
    public static final String CSV_ERROR_DELIMITER = " | ";

    @SuppressWarnings("unchecked")
    public static <T> List<T> convertToModel(FileDTO file, Class<T> responseType, boolean skipHeaders, List<String> errors) {
        List<T> models;
        CsvToBean<?> csvToBean;
        try (Reader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(file.getFileContent())))) {
            csvToBean = new CsvToBeanBuilder<>(reader)
                    .withSkipLines(skipHeaders ? 1 : 0)
                    .withType(responseType)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withIgnoreEmptyLine(true)
                    .withThrowExceptions(false)
                    .build();
            models = (List<T>) csvToBean.parse();
        } catch (Exception ex) {
            log.error("Error parsing csv file: ", ex);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER, ex.getCause().getMessage());
        }

        csvToBean.getCapturedExceptions().stream().collect(Collectors.groupingBy(
                CsvException::getLineNumber,
                Collectors.mapping(CsvException::getMessage, Collectors.toList())
        )).forEach((line, messages) ->
                errors.add(String.format(CSV_ROW_ERROR_PATTERN, file.getFileName(), line, String.join(CSV_ERROR_DELIMITER, messages))));

        return models;
    }

    public static CsvErrorEntry parseCsvError(String errorMessage) {
        return CsvErrorEntry.builder()
                .filename(errorMessage.substring(0, errorMessage.indexOf(' ')))
                .message(errorMessage.substring(errorMessage.indexOf(' ') + 1))
                .build();
    }
}
