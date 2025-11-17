package uk.gov.cca.api.common.converters;

import com.opencsv.exceptions.CsvDataTypeMismatchException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class CsvBooleanConverterTest {

    @InjectMocks
    private CsvBooleanConverter converter;

    @Test
    void convert_true() throws CsvDataTypeMismatchException {
        Boolean result = converter.convert("Yes");
        assertThat(result).isTrue();
    }

    @Test
    void convert_false() throws CsvDataTypeMismatchException {
        Boolean result = converter.convert("No");
        assertThat(result).isFalse();
    }

    @Test
    void convert_null() throws CsvDataTypeMismatchException {
        Boolean result = converter.convert(null);
        assertThat(result).isNull();
    }

    @Test
    void convert_blank() throws CsvDataTypeMismatchException {
        Boolean result = converter.convert("  ");
        assertThat(result).isNull();
    }

    @Test
    void convert_with_spaces() throws CsvDataTypeMismatchException {
        Boolean result = converter.convert(" Yes ");
        assertThat(result).isTrue();
    }

    @Test
    void convert_not_boolean() {
        CsvDataTypeMismatchException ex = assertThrows(CsvDataTypeMismatchException.class,
                () -> converter.convert("xxxx"));

        // Verify
        assertThat(ex).isInstanceOf(CsvDataTypeMismatchException.class);
    }
}
