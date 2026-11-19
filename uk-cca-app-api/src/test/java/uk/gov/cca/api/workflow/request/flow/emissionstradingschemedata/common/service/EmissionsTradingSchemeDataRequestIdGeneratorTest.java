package uk.gov.cca.api.workflow.request.flow.emissionstradingschemedata.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class EmissionsTradingSchemeDataRequestIdGeneratorTest {

    @InjectMocks
    private EmissionsTradingSchemeDataRequestIdGenerator generator;

    @Test
    void getTypes() {
        assertThat(generator.getTypes()).containsExactly(CcaRequestType.EMISSIONS_TRADING_SCHEME_DATA);
    }

    @Test
    void getPrefix() {
        assertThat(generator.getPrefix()).isEqualTo("ETS");
    }
}
