package uk.gov.cca.api.common.converters;

import com.opencsv.exceptions.CsvDataTypeMismatchException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class CsvLocalDateConverterTest {

    @InjectMocks
    private CsvLocalDateConverter converter;

    @Test
    void convert() throws CsvDataTypeMismatchException {
        LocalDate result = converter.convert("12/2/2004");
        assertThat(result).isEqualTo(LocalDate.of(2004, 2, 12));
    }

    @Test
    void convert_null() throws CsvDataTypeMismatchException {
        LocalDate result = converter.convert(null);
        assertThat(result).isNull();
    }

    @Test
    void convert_blank() throws CsvDataTypeMismatchException {
        LocalDate result = converter.convert("  ");
        assertThat(result).isNull();
    }

    @Test
    void convert_with_spaces() throws CsvDataTypeMismatchException {
        LocalDate result = converter.convert(" 12/2/2004 ");
        assertThat(result).isEqualTo(LocalDate.of(2004, 2, 12));
    }

    @Test
    void convert_not_date() {
        CsvDataTypeMismatchException ex = assertThrows(CsvDataTypeMismatchException.class,
                () -> converter.convert("xxxx"));

        // Verify
        assertThat(ex).isInstanceOf(CsvDataTypeMismatchException.class);
    }
}
