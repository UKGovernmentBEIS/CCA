package uk.gov.cca.api.sectorassociation.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.sectorassociation.domain.SubsectorAssociation;
import uk.gov.cca.api.sectorassociation.domain.dto.SubsectorAssociationDTO;
import uk.gov.cca.api.sectorassociation.domain.dto.SubsectorAssociationInfoDTO;
import uk.gov.cca.api.sectorassociation.repository.SubsectorAssociationRepository;
import uk.gov.netz.api.common.exception.BusinessException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@ExtendWith(MockitoExtension.class)
class SubsectorAssociationServiceTest {

    @InjectMocks
    private SubsectorAssociationService subsectorAssociationService;

    @Mock
    private SubsectorAssociationRepository subsectorAssociationRepository;

    @Test
    void getSubsectorById() {
        final long subsectorId = 1L;
        final SubsectorAssociation subsectorAssociation = SubsectorAssociation.builder()
                .id(subsectorId)
                .name("Name")
                .build();

        final SubsectorAssociationDTO expected = SubsectorAssociationDTO.builder()
                .name("Name")
                .build();

        when(subsectorAssociationRepository.findById(subsectorId))
                .thenReturn(Optional.of(subsectorAssociation));

        // Invoke
        SubsectorAssociationDTO actual = subsectorAssociationService.getSubsectorById(subsectorId);

        // Verify
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getSubsectorById_not_exist() {
        final long subsectorId = 1L;

        when(subsectorAssociationRepository.findById(subsectorId))
                .thenReturn(Optional.empty());

        BusinessException businessException = assertThrows(BusinessException.class,
                () -> subsectorAssociationService.getSubsectorById(subsectorId));

        // Verify
        assertThat(businessException.getErrorCode()).isEqualTo(RESOURCE_NOT_FOUND);
        verify(subsectorAssociationRepository, times(1)).findById(subsectorId);
    }
    
    @Test
    void getSubsectorAssociationIdsBySectorAssociationId() {
    	final long sectorId = 100L;
        final long subsectorId = 1L;
        final SubsectorAssociation subsectorAssociation = SubsectorAssociation.builder()
                .id(subsectorId)
                .name("Name")
                .build();

        final List<Long> expectedIds = List.of(subsectorId);

        when(subsectorAssociationRepository.findAllBySectorAssociationId(sectorId))
                .thenReturn(List.of(subsectorAssociation));

        // Invoke
        List<Long> actualIds = subsectorAssociationService.getSubsectorAssociationIdsBySectorAssociationId(sectorId);

        // Verify
        assertThat(actualIds).isEqualTo(expectedIds);
        verify(subsectorAssociationRepository, times(1)).findAllBySectorAssociationId(sectorId);
    }
    
    @Test
    void getSubsectorAssociationInfoDTOBySectorAssociationId() {
    	final long sectorId = 100L;
        final long subsectorId = 1L;
        final SubsectorAssociation subsectorAssociation = SubsectorAssociation.builder()
                .id(subsectorId)
                .name("Name")
                .build();

        final List<SubsectorAssociationInfoDTO> expected = List.of(SubsectorAssociationInfoDTO.builder()
        		.id(subsectorId)
        		.name("Name")
        		.build());

        when(subsectorAssociationRepository.findAllBySectorAssociationId(sectorId))
                .thenReturn(List.of(subsectorAssociation));

        // Invoke
        List<SubsectorAssociationInfoDTO> actual = subsectorAssociationService.getSubsectorAssociationInfoDTOBySectorAssociationId(sectorId);

        // Verify
        assertThat(actual).isEqualTo(expected);
        verify(subsectorAssociationRepository, times(1)).findAllBySectorAssociationId(sectorId);
    }

}
