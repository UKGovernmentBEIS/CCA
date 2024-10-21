package uk.gov.cca.api.web.orchestrator.facility;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.account.service.TargetUnitAccountQueryService;
import uk.gov.cca.api.facility.domain.dto.FacilityDTO;
import uk.gov.cca.api.facility.service.FacilityIdentifierService;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationQueryService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FacilityIdGeneratorServiceOrchestratorTest {

    @InjectMocks
    private FacilityIdGeneratorServiceOrchestrator facilityIdGeneratorServiceOrchestrator;

    @Mock
    private FacilityIdentifierService facilityIdentifierService;

    @Mock
    private SectorAssociationQueryService sectorAssociationQueryService;

    @Mock
    private TargetUnitAccountQueryService targetUnitAccountQueryService;

    @Test
    void generateFacilityId() {
        final Long identifierId = 1L;
        final Long sectorAssociationId = 1L;
        final Long accountId = 1L;
        final String facilityId = "SA-F00001";
        final String acronym = "SA";

        FacilityDTO facilityDTOSaved = FacilityDTO.builder()
                .facilityId(facilityId)
                .build();

        when(targetUnitAccountQueryService.getAccountSectorAssociationId(accountId)).thenReturn(sectorAssociationId);
        when(facilityIdentifierService.incrementAndGet(sectorAssociationId)).thenReturn(identifierId);
        when(sectorAssociationQueryService.getSectorAssociationAcronymById(sectorAssociationId)).thenReturn(acronym);

        FacilityDTO facilityDTO = facilityIdGeneratorServiceOrchestrator.generateFacilityId(accountId);

        assertThat(facilityDTO).isEqualTo(facilityDTOSaved);
        verify(facilityIdentifierService, times(1)).incrementAndGet(sectorAssociationId);
        verify(sectorAssociationQueryService, times(1)).getSectorAssociationAcronymById(sectorAssociationId);
    }
}
