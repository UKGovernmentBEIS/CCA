package uk.gov.cca.api.sectorassociation.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.common.domain.SchemeVersion;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociation;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociationScheme;
import uk.gov.cca.api.sectorassociation.domain.SectorAssociationSchemeDocument;
import uk.gov.cca.api.sectorassociation.domain.TargetCommitment;
import uk.gov.cca.api.sectorassociation.domain.TargetSet;
import uk.gov.cca.api.sectorassociation.repository.SectorAssociationSchemeDocumentRepository;
import uk.gov.cca.api.sectorassociation.repository.SectorAssociationSchemeRepository;
import uk.gov.cca.api.sectorassociation.transform.SectorAssociationSchemeDocumentMapper;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.utils.DateService;
import uk.gov.netz.api.files.common.domain.FileStatus;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.files.common.service.FileScanValidatorService;
import uk.gov.netz.api.files.common.service.FileValidatorService;
import uk.gov.netz.api.token.FileToken;
import uk.gov.netz.api.token.UserFileTokenService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SectorAssociationSchemeDocumentServiceTest {

    @InjectMocks
    private SectorAssociationSchemeDocumentService sectorAssociationSchemeDocumentService;

    @Mock
    private SectorAssociationSchemeRepository sectorAssociationSchemeRepository;

    @Mock
    private SectorAssociationSchemeDocumentRepository sectorAssociationSchemeDocumentRepository;

    @Mock
    private UserFileTokenService userFileTokenService;

    @Mock
    private SectorAssociationSchemeDocumentMapper sectorAssociationSchemeDocumentMapper;

    @Mock
    private FileScanValidatorService fileScanValidator;

    @Spy
    private ArrayList<FileValidatorService> fileValidators;

    @Mock
    private DateService dateService;

    @BeforeEach
    void setUp() {
        fileValidators.add(fileScanValidator);
    }

    @Test
    void cleanUpUnusedFiles() {

        final LocalDateTime today = LocalDateTime.of(2022, 1, 2, 3, 4);
        final LocalDateTime yesterday = LocalDateTime.of(2022, 1, 1, 3, 4);

        when(dateService.getLocalDateTime()).thenReturn(today);

        sectorAssociationSchemeDocumentService.cleanUpUnusedFiles();

        verify(dateService, times(1)).getLocalDateTime();
        verify(sectorAssociationSchemeDocumentRepository, times(1)).deleteSchemeDocumentsByStatusAndDateBefore(FileStatus.PENDING, yesterday);
    }

    @Test
    void createSectorAssociationSchemeDocument() {
        AppUser user = AppUser.builder().firstName("firstName").userId("userId").lastName("lastName").build();
        byte[] contentBytes = "dummycontent".getBytes();
        FileDTO fileDTO = FileDTO.builder()
                .fileName("name")
                .fileSize(contentBytes.length)
                .fileType("application/pdf")
                .fileContent(contentBytes)
                .build();
        FileStatus status = FileStatus.PENDING;
        SectorAssociationSchemeDocument document = SectorAssociationSchemeDocument.builder()
                .fileName("name")
                .fileSize(contentBytes.length)
                .fileType("application/pdf")
                .fileContent(contentBytes)
                .build();

        when(sectorAssociationSchemeDocumentMapper.toSectorAssociationSchemeDocument(fileDTO)).thenReturn(document);

        String fileEvidenceUuid = sectorAssociationSchemeDocumentService.createSectorAssociationSchemeDocument(fileDTO, user);

        assertThat(fileEvidenceUuid).isNotNull();
        ArgumentCaptor<SectorAssociationSchemeDocument> evidenceCaptor = ArgumentCaptor.forClass(SectorAssociationSchemeDocument.class);
        verify(sectorAssociationSchemeDocumentRepository, times(1)).save(evidenceCaptor.capture());
        SectorAssociationSchemeDocument schemeDocument = evidenceCaptor.getValue();
        assertThat(schemeDocument.getFileName()).isEqualTo(fileDTO.getFileName());
        assertThat(schemeDocument.getFileSize()).isEqualTo(fileDTO.getFileSize());
        assertThat(schemeDocument.getFileType()).isEqualTo(fileDTO.getFileType());
        assertThat(schemeDocument.getFileContent()).isEqualTo(contentBytes);
        assertThat(schemeDocument.getCreatedBy()).isEqualTo("userId");
        assertThat(schemeDocument.getStatus()).isEqualTo(status);
        assertThat(schemeDocument.getUuid()).isEqualTo(fileEvidenceUuid);

        verify(fileScanValidator, times(1)).validate(fileDTO);
    }

    @Test
    void generateDocumentFileToken() {
        AppUser user = AppUser.builder().firstName("firstName").userId("userId").lastName("lastName").build();
        Long sectorId = 1L;
        Long sectorSchemeId = 1L;
        FileToken fileDocumentToken = FileToken.builder().build();
        UUID fileDocumentUuid = UUID.fromString("8b6ff5b7-2afe-4987-850e-0c28aad15fcf");
        SectorAssociationSchemeDocument schemeDocument = SectorAssociationSchemeDocument.builder()
                .uuid(fileDocumentUuid.toString())
                .fileName("file")
                .status(FileStatus.SUBMITTED)
                .fileContent("content".getBytes())
                .fileSize(1L)
                .fileType("type")
                .build();
        SectorAssociationScheme sectorAssociationScheme = getSectorAssociationScheme(sectorId, sectorSchemeId, fileDocumentUuid.toString());

        when(sectorAssociationSchemeDocumentRepository.findByUuid(fileDocumentUuid.toString())).thenReturn(Optional.of(schemeDocument));
        when(sectorAssociationSchemeRepository.findSectorAssociationSchemesBySectorAssociationId(sectorId))
                .thenReturn(List.of(sectorAssociationScheme));
        when(userFileTokenService.generateGetFileToken(fileDocumentUuid.toString())).thenReturn(fileDocumentToken);

        sectorAssociationSchemeDocumentService.generateDocumentFileToken(sectorId, fileDocumentUuid, user);

        verify(sectorAssociationSchemeDocumentRepository, times(1)).findByUuid(fileDocumentUuid.toString());
        verify(sectorAssociationSchemeRepository, times(1)).findSectorAssociationSchemesBySectorAssociationId(sectorId);
        verify(userFileTokenService, times(1)).generateGetFileToken(fileDocumentUuid.toString());
    }

    @Test
    void generateDocumentFileToken_pending() {
        AppUser user = AppUser.builder().firstName("firstName").userId("userId").lastName("lastName").build();
        Long sectorId = 1L;
        FileToken fileDocumentToken = FileToken.builder().build();
        UUID fileDocumentUuid = UUID.fromString("8b6ff5b7-2afe-4987-850e-0c28aad15fcf");
        SectorAssociationSchemeDocument schemeDocument = SectorAssociationSchemeDocument.builder()
                .uuid(fileDocumentUuid.toString())
                .fileName("file")
                .status(FileStatus.PENDING)
                .fileContent("content".getBytes())
                .fileSize(1L)
                .fileType("type")
                .createdBy(user.getUserId())
                .build();

        when(sectorAssociationSchemeDocumentRepository.findByUuid(fileDocumentUuid.toString())).thenReturn(Optional.of(schemeDocument));
        when(userFileTokenService.generateGetFileToken(fileDocumentUuid.toString())).thenReturn(fileDocumentToken);

        sectorAssociationSchemeDocumentService.generateDocumentFileToken(sectorId, fileDocumentUuid, user);

        verify(sectorAssociationSchemeRepository, never()).findSectorAssociationSchemesBySectorAssociationId(sectorId);
        verify(userFileTokenService, times(1)).generateGetFileToken(fileDocumentUuid.toString());
    }

    @Test
    void getSectorAssociationSchemeDocumentByUuid() {
        String uuid = "uuid";
        SectorAssociationSchemeDocument schemeDocument = SectorAssociationSchemeDocument.builder()
                .fileName("file")
                .fileContent("content".getBytes())
                .fileSize(1L)
                .fileType("type")
                .build();

        when(sectorAssociationSchemeDocumentRepository.findByUuid(uuid)).thenReturn(Optional.of(schemeDocument));

        SectorAssociationSchemeDocument documentByUuid = sectorAssociationSchemeDocumentService.getSectorAssociationSchemeDocumentByUuid(uuid);

        assertThat(documentByUuid.getFileName()).isEqualTo(schemeDocument.getFileName());
        assertThat(documentByUuid.getFileType()).isEqualTo(schemeDocument.getFileType());
        assertThat(documentByUuid.getFileContent()).isEqualTo(schemeDocument.getFileContent());
    }

    @Test
    void submitSectorAssociationSchemeDocument() {
        String uuid = "uuid";
        SectorAssociationSchemeDocument schemeDocument = SectorAssociationSchemeDocument.builder()
                .fileName("file")
                .fileContent("content".getBytes())
                .fileSize(1L)
                .fileType("type")
                .build();

        when(sectorAssociationSchemeDocumentRepository.findByUuid(uuid)).thenReturn(Optional.of(schemeDocument));

        sectorAssociationSchemeDocumentService.submitSectorAssociationSchemeDocument(uuid);

        verify(sectorAssociationSchemeDocumentRepository, times(1)).findByUuid(uuid);
    }

    private SectorAssociationScheme getSectorAssociationScheme(Long sectorAssociationId, Long sectorAssociationSchemeId, String fileUuid) {
        return SectorAssociationScheme.builder()
                .id(sectorAssociationSchemeId)
                .sectorAssociation(SectorAssociation.builder()
                        .id(sectorAssociationId)
                        .build())
                .schemeVersion(SchemeVersion.CCA_3)
                .sectorDefinition("SectorDef")
                .umaDate(LocalDate.of(2026, 1, 1))
                .umbrellaAgreement(SectorAssociationSchemeDocument.builder()
                        .fileName("file")
                        .createdBy("createdBy")
                        .id(1L)
                        .fileSize(100L)
                        .fileContent("fileContent".getBytes())
                        .uuid(fileUuid)
                        .build())
                .targetSet(TargetSet.builder()
                        .targetCurrencyType("Novem")
                        .energyOrCarbonUnit("kWh")
                        .targetCommitments(List.of(TargetCommitment.builder()
                                        .id(1L)
                                        .targetImprovement(BigDecimal.valueOf(15.000))
                                        .targetPeriod("TP7")
                                        .build(),
                                TargetCommitment.builder()
                                        .id(2L)
                                        .targetImprovement(BigDecimal.valueOf(20.000))
                                        .targetPeriod("TP8")
                                        .build()))
                        .build())
                .build();
    }
}
