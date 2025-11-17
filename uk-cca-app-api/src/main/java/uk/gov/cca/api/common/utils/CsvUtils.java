package uk.gov.cca.api.common.utils;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;

import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

@Log4j2
@UtilityClass
public class CsvUtils {

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

        csvToBean.getCapturedExceptions().forEach(error ->
                errors.add(String.format("[%d] %s", error.getLineNumber(), error.getMessage())));

        return models;
    }
}
