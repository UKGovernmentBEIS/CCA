package uk.gov.cca.api.subsistencefees.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.util.Pair;
import uk.gov.cca.api.files.evidences.service.FileEvidenceService;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationInfoNameDTO;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationQueryService;
import uk.gov.cca.api.subsistencefees.domain.FacilityPaymentStatus;
import uk.gov.cca.api.subsistencefees.domain.MoaType;
import uk.gov.cca.api.subsistencefees.domain.PaymentStatus;
import uk.gov.cca.api.subsistencefees.domain.SubsistenceFeesMoa;
import uk.gov.cca.api.subsistencefees.domain.SubsistenceFeesMoaReceivedAmountHistory;
import uk.gov.cca.api.subsistencefees.domain.SubsistenceFeesMoaReceivedAmountHistoryPayload;
import uk.gov.cca.api.subsistencefees.domain.SubsistenceFeesRun;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaDetailsDTO;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaReceivedAmountHistoryDTO;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaReceivedAmountInfoDTO;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaSearchCriteria;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaSearchResultInfoDTO;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaSearchResults;
import uk.gov.cca.api.subsistencefees.domain.dto.transform.SubsistenceFeesMoaDetails;
import uk.gov.cca.api.subsistencefees.domain.dto.transform.SubsistenceFeesMoaSearchResultInfo;
import uk.gov.cca.api.subsistencefees.domain.dto.transform.SubsistenceFeesMoaSearchResultsInfo;
import uk.gov.cca.api.subsistencefees.repository.SubsistenceFeesMoaReceivedAmountHistoryRepository;
import uk.gov.cca.api.subsistencefees.repository.SubsistenceFeesMoaRepository;
import uk.gov.netz.api.account.domain.dto.AccountInfoDTO;
import uk.gov.netz.api.account.service.AccountQueryService;
import uk.gov.netz.api.common.domain.PagingRequest;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.files.documents.service.FileDocumentService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubsistenceFeesMoaQueryServiceTest {

    @InjectMocks
    private SubsistenceFeesMoaQueryService subsistenceFeesMoaQueryService;

    @Mock
    private SubsistenceFeesMoaRepository subsistenceFeesMoaRepository;

    @Mock
    private FileDocumentService fileDocumentService;

    @Mock
    private SectorAssociationQueryService sectorAssociationQueryService;

    @Mock
    private AccountQueryService accountQueryService;

    @Mock
    private SubsistenceFeesMoaReceivedAmountHistoryRepository subsistenceFeesMoaReceivedAmountHistoryRepository;

    @Mock
    private FileEvidenceService fileEvidenceService;

    @Test
    void getSubsistenceFeesRunMoas() {
        final String businessId = "businessId";
        final String name = "name";
        final int page = 0;
        final int pageSize = 30;
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
        final int page = 0;
        final int pageSize = 30;
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

    @Test
    void getFileEvidenceFilesByMoaId() {

        Long moaId = 1L;
        UUID fileEvidenceUuid1 = UUID.randomUUID();
        String evidenceFileName1 = "EvidenceFile1";
        UUID fileEvidenceUuid2 = UUID.randomUUID();
        String evidenceFileName2 = "EvidenceFile2";
        UUID fileEvidenceUuid3 = UUID.randomUUID();
        String evidenceFileName3 = "EvidenceFile3";
        SubsistenceFeesMoaReceivedAmountHistory receivedAmountHistory1 = SubsistenceFeesMoaReceivedAmountHistory.builder()
                .submitterId("submitterId")
                .submitter("submitter")
                .subsistenceFeesMoa(SubsistenceFeesMoa.builder().id(moaId).build())
                .submissionDate(LocalDateTime.now())
                .payload(SubsistenceFeesMoaReceivedAmountHistoryPayload.builder()
                        .previousReceivedAmount(BigDecimal.valueOf(5000))
                        .transactionAmount(BigDecimal.valueOf(185))
                        .comments("bla bla bla")
                        .evidenceFiles(Map.of(fileEvidenceUuid1, evidenceFileName1, fileEvidenceUuid2, evidenceFileName2))
                        .build())
                .build();

        SubsistenceFeesMoaReceivedAmountHistory receivedAmountHistory2 = SubsistenceFeesMoaReceivedAmountHistory.builder()
                .submitterId("submitterId")
                .submitter("submitter")
                .subsistenceFeesMoa(SubsistenceFeesMoa.builder().id(moaId).build())
                .submissionDate(LocalDateTime.now())
                .payload(SubsistenceFeesMoaReceivedAmountHistoryPayload.builder()
                        .previousReceivedAmount(BigDecimal.valueOf(5000))
                        .transactionAmount(BigDecimal.valueOf(185))
                        .comments("bla bla bla")
                        .evidenceFiles(Map.of(fileEvidenceUuid3, evidenceFileName3))
                        .build())
                .build();

        when(subsistenceFeesMoaReceivedAmountHistoryRepository.findByMoaId(moaId)).thenReturn(List.of(receivedAmountHistory1, receivedAmountHistory2));

        // invoke
        Set<UUID> fileEvidenceUuids = subsistenceFeesMoaQueryService.getFileEvidenceFilesByMoaId(moaId);

        // verify
        verify(subsistenceFeesMoaReceivedAmountHistoryRepository, times(1)).findByMoaId(moaId);
        assertThat(fileEvidenceUuids).containsExactlyInAnyOrder(fileEvidenceUuid1, fileEvidenceUuid2, fileEvidenceUuid3);
    }

    @Test
    void getSubsistenceFeesMoaReceivedAmountInfo() {
        Long moaId = 1L;
        LocalDateTime submissionDate = LocalDateTime.of(2020, 1, 1, 1, 1);
        BigDecimal receivedAmount = BigDecimal.valueOf(4000);
        BigDecimal currentTotalAmount = BigDecimal.valueOf(6500);
        BigDecimal transactionAmount = BigDecimal.valueOf(185);
        BigDecimal previousReceivedAmount = BigDecimal.valueOf(3825);
        String submitter = "submitter";
        String comments = "bla bla bla";
        UUID fileEvidenceUuid1 = UUID.randomUUID();
        String evidenceFileName1 = "EvidenceFile1";
        Map<UUID, String> evidenceFiles = Map.of(fileEvidenceUuid1, evidenceFileName1);

        SubsistenceFeesMoaReceivedAmountHistory receivedAmountHistory = SubsistenceFeesMoaReceivedAmountHistory.builder()
                .submitterId("submitterId")
                .submitter(submitter)
                .subsistenceFeesMoa(SubsistenceFeesMoa.builder().id(moaId).build())
                .submissionDate(submissionDate)
                .payload(SubsistenceFeesMoaReceivedAmountHistoryPayload.builder()
                        .previousReceivedAmount(previousReceivedAmount)
                        .transactionAmount(transactionAmount)
                        .comments(comments)
                        .evidenceFiles(evidenceFiles)
                        .build())
                .build();

        SubsistenceFeesMoaReceivedAmountHistoryDTO moaReceivedAmountHistoryDTO = SubsistenceFeesMoaReceivedAmountHistoryDTO.builder()
                .transactionAmount(transactionAmount)
                .submitter(submitter)
                .transactionAmount(transactionAmount)
                .submissionDate(submissionDate)
                .comments(comments)
                .evidenceFiles(evidenceFiles)
                .build();

        final SectorAssociationInfoNameDTO sectorDTO = SectorAssociationInfoNameDTO.builder().name("name").acronym("acronym").build();
        SubsistenceFeesMoaDetails moaDetails = new SubsistenceFeesMoaDetails(moaId, "CCACM1200", MoaType.SECTOR_MOA, 1L,
                BigDecimal.ZERO, receivedAmount, "uuid", submissionDate, BigDecimal.ZERO, currentTotalAmount, 1L, 1L, null);

        when(subsistenceFeesMoaRepository.getMoaDetailsById(moaId)).thenReturn(Optional.of(moaDetails));
        when(sectorAssociationQueryService.getSectorAssociationInfoNameDTO(1L)).thenReturn(sectorDTO);
        when(subsistenceFeesMoaReceivedAmountHistoryRepository.findByMoaId(moaId)).thenReturn(List.of(receivedAmountHistory));

        // invoke
        SubsistenceFeesMoaReceivedAmountInfoDTO moaReceivedAmountDetails = subsistenceFeesMoaQueryService.getSubsistenceFeesMoaReceivedAmountInfo(moaId);

        // verify
        verify(fileEvidenceService, times(1)).cleanUpUnusedNoteFilesAsync();
        verify(subsistenceFeesMoaReceivedAmountHistoryRepository, times(1)).findByMoaId(moaId);
        verify(sectorAssociationQueryService, times(1)).getSectorAssociationInfoNameDTO(1L);
        verify(subsistenceFeesMoaRepository, times(1)).getMoaDetailsById(moaId);

        assertThat(moaReceivedAmountDetails.getReceivedAmount()).isEqualTo(receivedAmount);
        assertThat(moaReceivedAmountDetails.getCurrentTotalAmount()).isEqualTo(currentTotalAmount);
        assertThat(moaReceivedAmountDetails.getReceivedAmountHistoryList()).isEqualTo(List.of(moaReceivedAmountHistoryDTO));
    }
}
