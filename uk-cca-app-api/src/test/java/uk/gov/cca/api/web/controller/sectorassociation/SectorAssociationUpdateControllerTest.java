package uk.gov.cca.api.web.controller.sectorassociation;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.cca.api.sectorassociation.domain.dto.AddressDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationContactDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationDetailsUpdateDTO;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationUpdateService;
import uk.gov.cca.api.web.controller.exception.ExceptionControllerAdvice;

@ExtendWith(MockitoExtension.class)
public class SectorAssociationUpdateControllerTest {

    private static final String CONTROLLER_PATH = "/v1.0/sector-association/";

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @InjectMocks
    private SectorAssociationUpdateController controller;

    @Mock
    private SectorAssociationUpdateService sectorAssociationUpdateService;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(new ExceptionControllerAdvice())
            .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void updateSectorAssociation_details() throws Exception {
        Long sectorAssociationId = 1L;
        SectorAssociationDetailsUpdateDTO sectorAssociationDetailsUpdateDTO = createSectorAssociationDetailsUpdateDTO();

        mockMvc.perform(
                MockMvcRequestBuilders
                    .post(CONTROLLER_PATH + sectorAssociationId + "/details")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(sectorAssociationDetailsUpdateDTO)))
            .andExpect(status().isNoContent());

        verify(sectorAssociationUpdateService, times(1))
            .updateSectorAssociationDetails(sectorAssociationId, sectorAssociationDetailsUpdateDTO);
    }

    @Test
    void updateSectorAssociation_contact() throws Exception {
        Long sectorAssociationId = 1L;
        SectorAssociationContactDTO sectorAssociationContactDTO = createSectorAssociationContactDTO();

        mockMvc.perform(
                MockMvcRequestBuilders
                    .post(CONTROLLER_PATH + sectorAssociationId + "/contact")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(sectorAssociationContactDTO)))
            .andExpect(status().isNoContent());

        verify(sectorAssociationUpdateService, times(1))
            .updateSectorAssociationContact(sectorAssociationId, sectorAssociationContactDTO);
    }

    private SectorAssociationDetailsUpdateDTO createSectorAssociationDetailsUpdateDTO() {
        AddressDTO addressDTO = AddressDTO.builder()
            .postcode("12345")
            .line1("123 Main St")
            .line2("124 Second St")
            .city("Springfield")
            .county("CountyName")
            .build();

        return SectorAssociationDetailsUpdateDTO.builder()
            .legalName("Some Association Legal")
            .commonName("Some Association")
            .noticeServiceAddress(addressDTO)
            .build();
    }

    private SectorAssociationContactDTO createSectorAssociationContactDTO() {
        AddressDTO addressDTO = AddressDTO.builder()
            .postcode("12345")
            .line1("123 Main St")
            .line2("124 Second St")
            .city("Springfield")
            .county("CountyName")
            .build();

        return SectorAssociationContactDTO.builder()
            .title("Mr.")
            .firstName("John")
            .lastName("Doe")
            .jobTitle("Director")
            .organisationName("Acme Corp")
            .phoneNumber("123456789")
            .email("john.doe@example.com")
            .address(addressDTO)
            .build();
    }
}
