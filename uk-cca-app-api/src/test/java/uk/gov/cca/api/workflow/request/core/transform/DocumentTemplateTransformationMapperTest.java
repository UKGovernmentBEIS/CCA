package uk.gov.cca.api.workflow.request.core.transform;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.cca.api.account.domain.dto.AccountAddressDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.AddressDTO;
import uk.gov.netz.api.referencedata.domain.Country;
import uk.gov.netz.api.referencedata.service.CountryService;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentTemplateTransformationMapperTest {

    @InjectMocks
    private DocumentTemplateTransformationMapper documentTemplateTransformationMapper;

    @Mock
    private CountryService countryService;

    @Test
    void constructAccountAddressDTO() {
        final AccountAddressDTO address = AccountAddressDTO.builder()
                .line1("Line 1")
                .line2("Line 2")
                .city("City")
                .county("County")
                .postcode("code")
                .country("GR")
                .build();

        when(countryService.getReferenceData())
                .thenReturn(List.of(Country.builder().code("GR").name("Greece").build()));

        final String expected = "Line 1\nLine 2\nCity\ncode\nCounty\nGreece";

        // Invoke
        String actual = documentTemplateTransformationMapper.constructAccountAddressDTO(address);

        // Verify
        assertThat(actual).isEqualTo(expected);
        verify(countryService, times(1)).getReferenceData();
    }

    @Test
    void constructAddressDTO() {
        final AddressDTO address = AddressDTO.builder()
                .line1("Line 1")
                .line2("Line 2")
                .city("City")
                .county("County")
                .postcode("code")
                .build();

        final String expected = "Line 1\nLine 2\nCity\ncode\nCounty";

        // Invoke
        String actual = documentTemplateTransformationMapper.constructAddressDTO(address);

        // Verify
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void formatUmaDate() {
        final LocalDate umaDate = LocalDate.of(2024, 2, 3);

        final String expected = "03/02/2024";

        // Invoke
        String actual = documentTemplateTransformationMapper.formatUmaDate(umaDate);

        // Verify
        assertThat(actual).isEqualTo(expected);
    }
}
