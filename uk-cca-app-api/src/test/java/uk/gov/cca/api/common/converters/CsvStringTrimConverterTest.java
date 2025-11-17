package uk.gov.cca.api.common.converters;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CsvStringTrimConverterTest {

    @InjectMocks
    private CsvStringTrimConverter converter;

    @Test
    void convert() {
        String result = converter.convert("test");
        assertThat(result).isEqualTo("test");
    }

    @Test
    void convert_null() {
        String result = converter.convert(null);
        assertThat(result).isNull();
    }

    @Test
    void convert_blank() {
        String result = converter.convert("  ");
        assertThat(result).isNull();
    }

    @Test
    void convert_with_spaces() {
        String result = converter.convert(" test ");
        assertThat(result).isEqualTo("test");
    }
}
