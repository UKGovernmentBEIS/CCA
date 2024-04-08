package uk.gov.cca.api.verificationbody.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.verificationbody.domain.dto.AddressDTO;
import uk.gov.cca.api.common.EmissionTradingScheme;
import uk.gov.cca.api.verificationbody.domain.dto.VerificationBodyDTO;
import uk.gov.cca.api.verificationbody.domain.verificationbodydetails.VerificationBodyDetails;
import uk.gov.cca.api.verificationbody.enumeration.VerificationBodyStatus;
import uk.gov.cca.api.verificationbody.service.VerificationBodyDetailsQueryService;
import uk.gov.cca.api.verificationbody.service.VerificationBodyQueryService;

import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VerificationBodyDetailsQueryServiceTest {

    @InjectMocks
    private VerificationBodyDetailsQueryService service;

    @Mock
    private VerificationBodyQueryService verificationBodyQueryService;

    @Test
    void getVerificationBodyDetails() {
        final Long vbId = 1L;
        final VerificationBodyDTO verificationBodyDTO = VerificationBodyDTO.builder()
                .id(vbId)
                .name("name")
                .accreditationReferenceNumber("accrRefNum")
                .status(VerificationBodyStatus.ACTIVE)
                .address(AddressDTO.builder()
                        .line1("line1")
                        .city("city")
                        .country("GR")
                        .postcode("postcode")
                        .build())
                .emissionTradingSchemes(Set.of(mock(EmissionTradingScheme.class)))
                .build();

        final VerificationBodyDetails expected = VerificationBodyDetails.builder()
                .name(verificationBodyDTO.getName())
                .accreditationReferenceNumber(verificationBodyDTO.getAccreditationReferenceNumber())
                .address(verificationBodyDTO.getAddress())
                .emissionTradingSchemes(verificationBodyDTO.getEmissionTradingSchemes())
                .build();

        when(verificationBodyQueryService.getVerificationBodyById(vbId)).thenReturn(verificationBodyDTO);

        // Invoke
        VerificationBodyDetails actual = service.getVerificationBodyDetails(vbId);

        // Verify
        verify(verificationBodyQueryService, times(1)).getVerificationBodyById(vbId);
        Assertions.assertEquals(expected, actual);
    }
}
