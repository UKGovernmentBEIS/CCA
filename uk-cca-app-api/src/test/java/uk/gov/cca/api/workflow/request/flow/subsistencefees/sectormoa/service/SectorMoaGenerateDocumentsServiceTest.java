package uk.gov.cca.api.workflow.request.flow.subsistencefees.sectormoa.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.subsistencefees.domain.dto.EligibleFacilityDTO;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.sectormoa.domain.SectorMoaRequestPayload;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SectorMoaGenerateDocumentsServiceTest {

    @InjectMocks
    private SectorMoaGenerateDocumentsService sectorMoaGenerateDocumentsService;

    @Mock
    private SectorMoaCreateDocumentService sectorMoaCreateDocumentService;

    @Test
    void generateDocuments() {
        Request request = Request.builder()
                .payload(SectorMoaRequestPayload.builder().build())
                .build();
        List<EligibleFacilityDTO> facilities = List.of(EligibleFacilityDTO.builder().build());
        final UUID pdfUuid = UUID.randomUUID();
        final FileInfoDTO document = FileInfoDTO.builder()
                .name("sectorMoA.pdf")
                .uuid(pdfUuid.toString())
                .build();

        when(sectorMoaCreateDocumentService.create(request, facilities))
                .thenReturn(CompletableFuture.completedFuture(document));

        // invoke
        sectorMoaGenerateDocumentsService.generateDocuments(request, facilities);

        // verify
        verify(sectorMoaCreateDocumentService, times(1)).create(request, facilities);

        SectorMoaRequestPayload requestPayload = (SectorMoaRequestPayload) request.getPayload();
        assertThat(requestPayload.getSectorMoaDocument()).isEqualTo(document);
    }

    @Test
    void generateDocuments_throws_internal_server_error_exception() {
        Request request = Request.builder()
                .payload(SectorMoaRequestPayload.builder().build())
                .build();
        List<EligibleFacilityDTO> facilities = List.of(EligibleFacilityDTO.builder().build());

        when(sectorMoaCreateDocumentService.create(request, facilities)).thenAnswer(answer -> {
            CompletableFuture<?> future = new CompletableFuture<>();
            future.completeExceptionally(new RuntimeException("something unexpected happened"));
            return future;
        });

        // invoke
        BusinessException be = assertThrows(BusinessException.class,
                () -> sectorMoaGenerateDocumentsService.generateDocuments(request, facilities));

        // verify
        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.INTERNAL_SERVER);
        verify(sectorMoaCreateDocumentService, times(1)).create(request, facilities);

        SectorMoaRequestPayload requestPayload = (SectorMoaRequestPayload) request.getPayload();
        assertThat(requestPayload.getSectorMoaDocument()).isNull();
    }
}


