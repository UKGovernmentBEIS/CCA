package uk.gov.cca.api.web.controller.reference;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.cca.api.web.controller.exception.ExceptionControllerAdvice;
import uk.gov.netz.api.referencedata.domain.Country;
import uk.gov.netz.api.referencedata.domain.County;
import uk.gov.netz.api.referencedata.domain.enumeration.ReferenceDataType;
import uk.gov.netz.api.referencedata.service.CountryService;
import uk.gov.netz.api.referencedata.service.CountyService;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class ReferenceDataControllerTest {

    public static final String REF_DATA_CONTROLLER_PATH = "/v1.0/data";

    @InjectMocks
    private ReferenceDataController referenceDataController;

    @Mock
    private ApplicationContext context;

    @Mock
    private CountryService countryService;
    @Mock
    private CountyService countyService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(referenceDataController)
                .setControllerAdvice(new ExceptionControllerAdvice())
                .build();
    }

    @Test
    void getReferenceData() throws Exception {
        when(context.getBean(CountryService.class)).thenReturn(countryService);
        when(context.getBean(CountyService.class)).thenReturn(countyService);

        List<Country> countries = buildCountries("GR", "IT");
        when(countryService.getReferenceData()).thenReturn(countries);
        List<County> counties = buildCounties("AAAA","BBBB");
        when(countyService.getReferenceData()).thenReturn(counties);

        mockMvc.perform(MockMvcRequestBuilders.get(REF_DATA_CONTROLLER_PATH)
                .param("types", ReferenceDataType.COUNTRIES.name(), ReferenceDataType.COUNTIES.name())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.COUNTRIES").exists())
                .andExpect(jsonPath("$.COUNTRIES").isArray())
                .andExpect(jsonPath("$.COUNTRIES").isNotEmpty())
                .andExpect(jsonPath("$.COUNTIES").exists())
                .andExpect(jsonPath("$.COUNTIES").isArray())
                .andExpect(jsonPath("$.COUNTIES").isNotEmpty());
    }

    @Test
    void getReferenceDataOnlyOne() throws Exception {
        when(context.getBean(CountryService.class)).thenReturn(countryService);
        when(context.getBean(CountyService.class)).thenReturn(countyService);

        List<Country> countries = buildCountries("GR", "IT");
        when(countryService.getReferenceData()).thenReturn(countries);

        mockMvc.perform(MockMvcRequestBuilders.get(REF_DATA_CONTROLLER_PATH)
                        .param("types", ReferenceDataType.COUNTRIES.name())
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.COUNTRIES").exists())
                        .andExpect(jsonPath("$.COUNTIES").doesNotExist());
    }

    private List<Country> buildCountries(String... countryCodes) {
        return Arrays.stream(countryCodes).map(countryCode -> Country.builder()
                .code(countryCode)
                .name(countryCode + "_name")
                .officialName(countryCode + "_official").build())
                .toList();
    }

    private List<County> buildCounties(String... countyNames) {
        return Arrays.stream(countyNames)
                .map(countyName -> new County((long)Objects.hash(countyName), countyName))
                .toList();
    }
}
