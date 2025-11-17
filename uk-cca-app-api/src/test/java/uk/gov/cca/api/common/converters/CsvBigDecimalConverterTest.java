package uk.gov.cca.api.common.converters;

import com.opencsv.exceptions.CsvDataTypeMismatchException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class CsvBigDecimalConverterTest {

    @InjectMocks
    private CsvBigDecimalConverter converter;

    @Test
    void convert() throws CsvDataTypeMismatchException {
        BigDecimal result = converter.convert("105.560");
        assertThat(result).isEqualTo(BigDecimal.valueOf(105.56).setScale(7, RoundingMode.HALF_DOWN));
    }

    @Test
    void convert_negative() throws CsvDataTypeMismatchException {
        BigDecimal result = converter.convert("-105.560");
        assertThat(result).isEqualTo(BigDecimal.valueOf(105.56).negate().setScale(7, RoundingMode.HALF_DOWN));
    }

    @Test
    void convert_null() throws CsvDataTypeMismatchException {
        BigDecimal result = converter.convert(null);
        assertThat(result).isNull();
    }

    @Test
    void convert_blank() throws CsvDataTypeMismatchException {
        BigDecimal result = converter.convert(" ");
        assertThat(result).isNull();
    }

    @Test
    void convert_with_spaces() throws CsvDataTypeMismatchException {
        BigDecimal result = converter.convert(" 105.560 ");
        assertThat(result).isEqualTo(BigDecimal.valueOf(105.56).setScale(7, RoundingMode.HALF_DOWN));
    }

    @Test
    void convert_nan() {
        CsvDataTypeMismatchException ex = assertThrows(CsvDataTypeMismatchException.class,
                () -> converter.convert("xxxx"));

        // Verify
        assertThat(ex).isInstanceOf(CsvDataTypeMismatchException.class);
    }
}
