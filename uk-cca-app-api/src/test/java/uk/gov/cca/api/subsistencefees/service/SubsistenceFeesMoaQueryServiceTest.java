package uk.gov.cca.api.subsistencefees.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.util.Pair;

import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationInfoNameDTO;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationQueryService;
import uk.gov.cca.api.subsistencefees.domain.FacilityPaymentStatus;
import uk.gov.cca.api.subsistencefees.domain.MoaType;
import uk.gov.cca.api.subsistencefees.domain.PaymentStatus;
import uk.gov.cca.api.subsistencefees.domain.SubsistenceFeesMoa;
import uk.gov.cca.api.subsistencefees.domain.SubsistenceFeesRun;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaSearchCriteria;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaSearchResultInfoDTO;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaSearchResults;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaDetailsDTO;
import uk.gov.cca.api.subsistencefees.domain.dto.transform.SubsistenceFeesMoaSearchResultInfo;
import uk.gov.cca.api.subsistencefees.domain.dto.transform.SubsistenceFeesMoaSearchResultsInfo;
import uk.gov.cca.api.subsistencefees.domain.dto.transform.SubsistenceFeesMoaDetails;
import uk.gov.cca.api.subsistencefees.repository.SubsistenceFeesMoaRepository;
import uk.gov.cca.api.subsistencefees.transform.SubsistenceFeesMapper;
import uk.gov.netz.api.account.domain.dto.AccountInfoDTO;
import uk.gov.netz.api.account.service.AccountQueryService;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.files.documents.service.FileDocumentService;

@ExtendWith(MockitoExtension.class)
class SubsistenceFeesMoaQueryServiceTest {

	@InjectMocks
    private SubsistenceFeesMoaQueryService subsistenceFeesMoaQueryService;

    @Mock
    private SubsistenceFeesMoaRepository subsistenceFeesMoaRepository;

    @Mock
    private SubsistenceFeesMapper subsistenceFeesMapper;
    
    @Mock
    private FileDocumentService fileDocumentService;
    
    @Mock
    private SectorAssociationQueryService sectorAssociationQueryService;
    
    @Mock
    private AccountQueryService accountQueryService;
    
    @Test
    void getSubsistenceFeesRunMoas() {
    	final String businessId = "businessId";
    	final String name = "name";
    	final long page = 0;
        final long pageSize = 30;
        final LocalDateTime submissionDate = LocalDateTime.now();
        PagingRequest pagingRequest = PagingRequest.builder().pageNumber(page).pageSize(pageSize).build();
        SubsistenceFeesMoaSearchCriteria criteria = SubsistenceFeesMoaSearchCriteria.builder()
        		.paging(pagingRequest)
        		.moaType(MoaType.SECTOR_MOA)
        		.build();
        final BigDecimal amount = BigDecimal.valueOf(1000L);
        final SubsistenceFeesMoaSearchResultInfo resultInfo = new SubsistenceFeesMoaSearchResultInfo(
        		1L, "CCACM1200", businessId, name, amount, amount, amount, submissionDate);
        final SubsistenceFeesMoaSearchResultsInfo resultsInfo = SubsistenceFeesMoaSearchResultsInfo.builder()
        		.subsistenceFeesMoaSearchResultInfo(List.of(resultInfo))
        		.total(1L)
        		.build();
        final SubsistenceFeesMoaSearchResultInfoDTO resultInfoDto = new SubsistenceFeesMoaSearchResultInfoDTO(
        		1L, "CCACM1200", businessId, name, PaymentStatus.PAID, FacilityPaymentStatus.IN_PROGRESS, amount, BigDecimal.ZERO, submissionDate);
        final SubsistenceFeesMoaSearchResults expectedResults = SubsistenceFeesMoaSearchResults.builder()
                .subsistenceFeesMoas(List.of(resultInfoDto))
                .total(1L)
                .build();

        when(subsistenceFeesMoaRepository.findBySearchCriteriaForCAView(1L, criteria)).thenReturn(resultsInfo);

        // invoke
        final SubsistenceFeesMoaSearchResults results = subsistenceFeesMoaQueryService.getSubsistenceFeesRunMoas(1L, criteria);

        // verify
        verify(subsistenceFeesMoaRepository, times(1)).findBySearchCriteriaForCAView(1L, criteria);
        assertThat(results).isEqualTo(expectedResults);
    }
    
    @Test
    void getSectorSubsistenceFeesMoas() {
    	final String businessId = "businessId";
    	final String name = "name";
    	final long page = 0;
        final long pageSize = 30;
        final LocalDateTime submissionDate = LocalDateTime.now();
        PagingRequest pagingRequest = PagingRequest.builder().pageNumber(page).pageSize(pageSize).build();
        SubsistenceFeesMoaSearchCriteria criteria = SubsistenceFeesMoaSearchCriteria.builder()
        		.paging(pagingRequest)
        		.moaType(MoaType.SECTOR_MOA)
        		.build();
        final BigDecimal amount = BigDecimal.valueOf(1000L);
        final SubsistenceFeesMoaSearchResultInfo resultInfo = new SubsistenceFeesMoaSearchResultInfo(
        		1L, "CCACM1200", businessId, name, amount, amount, amount, submissionDate);
        final SubsistenceFeesMoaSearchResultsInfo resultsInfo = SubsistenceFeesMoaSearchResultsInfo.builder()
        		.subsistenceFeesMoaSearchResultInfo(List.of(resultInfo))
        		.total(1L)
        		.build();
        final SubsistenceFeesMoaSearchResultInfoDTO resultInfoDto = new SubsistenceFeesMoaSearchResultInfoDTO(
        		1L, "CCACM1200", businessId, name, PaymentStatus.PAID, FacilityPaymentStatus.IN_PROGRESS, amount, BigDecimal.ZERO, submissionDate);
        final SubsistenceFeesMoaSearchResults expectedResults = SubsistenceFeesMoaSearchResults.builder()
                .subsistenceFeesMoas(List.of(resultInfoDto))
                .total(1L)
                .build();

        when(subsistenceFeesMoaRepository.findBySearchCriteriaForSectorAssociationView(1L, criteria)).thenReturn(resultsInfo);

        // invoke
        final SubsistenceFeesMoaSearchResults results = subsistenceFeesMoaQueryService.getSectorSubsistenceFeesMoas(1L, criteria);

        // verify
        verify(subsistenceFeesMoaRepository, times(1)).findBySearchCriteriaForSectorAssociationView(1L, criteria);
        assertThat(results).isEqualTo(expectedResults);
    }
    
    @Test
    void getSubsistenceFeesRunMoaDetailsById_sector_moa() {
        final long moaId = 1L;
        final LocalDateTime date = LocalDateTime.now();
        final FileInfoDTO fileInfoDTO = FileInfoDTO.builder().build();
        final SectorAssociationInfoNameDTO sectorDTO = SectorAssociationInfoNameDTO.builder().name("name").acronym("acronym").build();
        SubsistenceFeesMoaDetails moaDetails = new SubsistenceFeesMoaDetails(1L, "CCACM1200", MoaType.SECTOR_MOA, 1L, 
        		BigDecimal.ZERO, BigDecimal.ZERO, "uuid", date, BigDecimal.ZERO, BigDecimal.ZERO, 1L, 1L, null);
        
        SubsistenceFeesMoaDetailsDTO expectedDTO = new SubsistenceFeesMoaDetailsDTO(1L, "CCACM1200", "acronym", "name", fileInfoDTO, 
        		date, PaymentStatus.CANCELLED, 1L, 1L, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, null);

        when(subsistenceFeesMoaRepository.getMoaDetailsById(moaId)).thenReturn(Optional.of(moaDetails));
        when(sectorAssociationQueryService.getSectorAssociationInfoNameDTO(1L)).thenReturn(sectorDTO);
        when(fileDocumentService.getFileInfoDTO("uuid")).thenReturn(fileInfoDTO);

        // invoke
        SubsistenceFeesMoaDetailsDTO subsistenceFeesRunMoaDetailsDTO =
        		subsistenceFeesMoaQueryService.getSubsistenceFeesMoaDetailsById(moaId);

        // verify
        verify(subsistenceFeesMoaRepository, times(1)).getMoaDetailsById(moaId);
        verify(sectorAssociationQueryService, times(1)).getSectorAssociationInfoNameDTO(1L);
        verify(fileDocumentService, times(1)).getFileInfoDTO("uuid");
        verifyNoInteractions(accountQueryService);
        assertThat(subsistenceFeesRunMoaDetailsDTO).isEqualTo(expectedDTO);
    }
    
    @Test
    void getSubsistenceFeesRunMoaDetailsById_target_unit_moa() {
        final long moaId = 1L;
        final LocalDateTime date = LocalDateTime.now();
        final FileInfoDTO fileInfoDTO = FileInfoDTO.builder().build();
        final AccountInfoDTO accountDTO = AccountInfoDTO.builder().name("name").businessId("businessId").build();
        SubsistenceFeesMoaDetails moaDetails = new SubsistenceFeesMoaDetails(1L, "CCACM1200", MoaType.TARGET_UNIT_MOA, 1L, 
        		BigDecimal.ZERO, BigDecimal.ZERO, "uuid", date, BigDecimal.ZERO, BigDecimal.ZERO, 1L, 1L, 1L);
        
        SubsistenceFeesMoaDetailsDTO expectedDTO = new SubsistenceFeesMoaDetailsDTO(1L, "CCACM1200", "businessId", "name", fileInfoDTO, 
        		date, PaymentStatus.CANCELLED, 1L, 1L, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 1L);

        when(subsistenceFeesMoaRepository.getMoaDetailsById(moaId)).thenReturn(Optional.of(moaDetails));
        when(accountQueryService.getAccountInfoDTOById(1L)).thenReturn(accountDTO);
        when(fileDocumentService.getFileInfoDTO("uuid")).thenReturn(fileInfoDTO);

        // invoke
        SubsistenceFeesMoaDetailsDTO subsistenceFeesRunMoaDetailsDTO =
        		subsistenceFeesMoaQueryService.getSubsistenceFeesMoaDetailsById(moaId);

        // verify
        verify(subsistenceFeesMoaRepository, times(1)).getMoaDetailsById(moaId);
        verify(accountQueryService, times(1)).getAccountInfoDTOById(1L);
        verify(fileDocumentService, times(1)).getFileInfoDTO("uuid");
        verifyNoInteractions(sectorAssociationQueryService);
        assertThat(subsistenceFeesRunMoaDetailsDTO).isEqualTo(expectedDTO);
    }
    
    @Test
    void getSubsistenceFeesMoaResourceIdById() {
        final Pair<String, Long> moaResourceIdPair = Pair.of(MoaType.SECTOR_MOA.name(), 1L);
        final long moaId = 1L;
        
        SubsistenceFeesMoa moa = SubsistenceFeesMoa.builder()
        		.id(1L)
        		.moaType(MoaType.SECTOR_MOA)
        		.resourceId(1L)
        		.build();

        when(subsistenceFeesMoaRepository.findById(moaId)).thenReturn(Optional.of(moa));

        // invoke
        Pair<String, Long> result = subsistenceFeesMoaQueryService.getSubsistenceFeesMoaResourceIdById(moaId);

        // verify
        verify(subsistenceFeesMoaRepository, times(1)).findById(moaId);
        assertThat(result).isEqualTo(moaResourceIdPair);
    }

    @Test
    void getSubsistenceFeesMoaById() {
        final Long moaId = 1L;

        final SubsistenceFeesMoa moa = SubsistenceFeesMoa.builder()
        		.id(1L)
        		.subsistenceFeesRun(SubsistenceFeesRun.builder()
        				.id(1L)
        				.build())
        		.build();


        when(subsistenceFeesMoaRepository.findById(moaId)).thenReturn(Optional.of(moa));

        // invoke
        SubsistenceFeesMoa result = subsistenceFeesMoaQueryService.getSubsistenceFeesMoaById(moaId);

        // verify
        verify(subsistenceFeesMoaRepository, times(1)).findById(moaId);
        assertThat(result).isEqualTo(moa);
    }
    
    @Test
    void getSubsistenceFeesMoaByIdAndFileDocumentUuid() {
        final long moaId = 1L;
        final String documentUuid = "uuid";
        final SubsistenceFeesMoa moa = SubsistenceFeesMoa.builder()
        		.id(1L)
        		.subsistenceFeesRun(SubsistenceFeesRun.builder()
        				.id(1L)
        				.build())
        		.build();


        when(subsistenceFeesMoaRepository.findByIdAndFileDocumentUuid(moaId, documentUuid)).thenReturn(Optional.of(moa));

        // invoke
        subsistenceFeesMoaQueryService.getSubsistenceFeesMoaByIdAndFileDocumentUuid(moaId, documentUuid);

        // verify
        verify(subsistenceFeesMoaRepository, times(1)).findByIdAndFileDocumentUuid(moaId, documentUuid);
    }
}
