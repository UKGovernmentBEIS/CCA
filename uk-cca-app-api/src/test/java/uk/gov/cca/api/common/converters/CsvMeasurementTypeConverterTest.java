package uk.gov.cca.api.common.converters;

import com.opencsv.exceptions.CsvDataTypeMismatchException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.common.domain.MeasurementType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class CsvMeasurementTypeConverterTest {

    @InjectMocks
    private CsvMeasurementTypeConverter converter;

    @Test
    void convert_by_description() throws CsvDataTypeMismatchException {
        MeasurementType result = converter.convert("Energy (kWh)");
        assertThat(result).isEqualTo(MeasurementType.ENERGY_KWH);
    }

    @Test
    void convert_null() throws CsvDataTypeMismatchException {
        MeasurementType result = converter.convert(null);
        assertThat(result).isNull();
    }

    @Test
    void convert_blank() throws CsvDataTypeMismatchException {
        MeasurementType result = converter.convert("  ");
        assertThat(result).isNull();
    }

    @Test
    void convert_with_spaces() throws CsvDataTypeMismatchException {
        MeasurementType result = converter.convert(" Energy (kWh) ");
        assertThat(result).isEqualTo(MeasurementType.ENERGY_KWH);
    }

    @Test
    void convert_not_type() {
        CsvDataTypeMismatchException ex = assertThrows(CsvDataTypeMismatchException.class,
                () -> converter.convert("xxxx"));

        // Verify
        assertThat(ex).isInstanceOf(CsvDataTypeMismatchException.class);
    }
}
