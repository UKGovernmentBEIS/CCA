package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.ObjectUtils;

import uk.gov.cca.api.facility.service.FacilityDataQueryService;
import uk.gov.cca.api.notification.template.constants.CcaDocumentTemplateType;
import uk.gov.cca.api.targetperiodreporting.buyoutsurplus.domain.BuyOutSurplusChargeType;
import uk.gov.cca.api.targetperiodreporting.targetperiod.domain.TargetPeriodType;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.domain.BuyOutSurplusFacilityAccountProcessingRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.domain.BuyOutSurplusFacilityAccountProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaFileDocumentUnprocessedGeneratorService;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;

@ExtendWith(MockitoExtension.class)
class BuyOutSurplusFacilityAccountProcessingGenerateDocumentsServiceTest {
    
    @InjectMocks
    private BuyOutSurplusFacilityAccountProcessingGenerateDocumentsService service;

    @Mock
    private FacilityDataQueryService facilityDataQueryService;

    @Mock
    private BuyOutSurplusFacilityAccountProcessingCreateDocumentService createDocumentService;
    
    @Mock
    private Request request;
    
    @Mock
    private BuyOutSurplusFacilityAccountProcessingRequestPayload payload;
    
    @Mock
    private BuyOutSurplusFacilityAccountProcessingRequestMetadata metadata;

    @Mock
    private CcaFileDocumentUnprocessedGeneratorService ccaFileDocumentUnprocessedGeneratorService;
    
    @BeforeEach
    void setUp() {
        when(request.getPayload()).thenReturn(payload);
        when(request.getMetadata()).thenReturn(metadata);
    }
    
    @Test
    void generateDocuments_whenNoTransactionCodes() {
        when(metadata.getBuyOutTransactionCodes()).thenReturn(Collections.emptyMap());
        when(metadata.getRefundTransactionCodes()).thenReturn(Collections.emptyMap());
        when(facilityDataQueryService.getFacilityDataByIds(anyList())).thenReturn(Collections.emptyList());

        // When
        assertDoesNotThrow(() -> service.generateDocuments(request));

        // Then
        verifyNoInteractions(createDocumentService);
        verify(payload, never()).setBuyOutOfficialNotices(any());
        verify(payload, never()).setRefundOfficialNotices(any());
    }
    
    @Test
    void generateDocuments_whenBuyOutTransactionCodesExist() {
        FileInfoDTO fileInfo = FileInfoDTO.builder()
                .name("CCA070001")
                .uuid("uuid")
                .build();
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP7;
        
        when(facilityDataQueryService.getFacilityDataByIds(anyList())).thenReturn(Collections.emptyList());

        when(metadata.getBuyOutTransactionCodes()).thenReturn(Map.of(targetPeriodType, "CCA070001"));
        when(metadata.getRefundTransactionCodes()).thenReturn(Collections.emptyMap());
        
        when(payload.getBuyOutOfficialNotices()).thenReturn(new EnumMap<>(TargetPeriodType.class));
        when(payload.getRefundOfficialNotices()).thenReturn(null);
        
        when(payload.getFacilityPerformanceResults()).thenReturn(Collections.emptyList());

        when(createDocumentService.create(
                eq(request),
                eq(targetPeriodType),
                eq(BuyOutSurplusChargeType.FEE),
                anyMap()))
                .thenReturn(CompletableFuture.completedFuture(fileInfo));

        // When
        service.generateDocuments(request);

        // Then
        verify(createDocumentService).create(
                eq(request),
                eq(targetPeriodType),
                eq(BuyOutSurplusChargeType.FEE),
                anyMap());

        assertEquals(fileInfo, payload.getBuyOutOfficialNotices().get(targetPeriodType));
        assertTrue(ObjectUtils.isEmpty(payload.getRefundOfficialNotices()));
        verifyNoInteractions(ccaFileDocumentUnprocessedGeneratorService);
    }
    
    @Test
    void generateDocuments_whenRefundTransactionCodesExist() {
        FileInfoDTO fileInfo = FileInfoDTO.builder()
                .name("CCA070001")
                .uuid("uuid")
                .build();
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP7;
        
        FileInfoDTO refundClaimForm = mock(FileInfoDTO.class);
        
        when(facilityDataQueryService.getFacilityDataByIds(anyList())).thenReturn(Collections.emptyList());

        when(metadata.getBuyOutTransactionCodes()).thenReturn(Collections.emptyMap());
        when(metadata.getRefundTransactionCodes()).thenReturn(Map.of(targetPeriodType, "CCA070001"));
        
        when(payload.getBuyOutOfficialNotices()).thenReturn(null);
        when(payload.getRefundOfficialNotices()).thenReturn(new EnumMap<>(TargetPeriodType.class));
        
        when(payload.getFacilityPerformanceResults()).thenReturn(Collections.emptyList());
        
        when(request.getRequestResourcesMap()).thenReturn(Map.of(ResourceType.CA, CompetentAuthorityEnum.ENGLAND.toString()));

        when(createDocumentService.create(
                eq(request),
                eq(targetPeriodType),
                eq(BuyOutSurplusChargeType.REFUND),
                anyMap()))
                .thenReturn(CompletableFuture.completedFuture(fileInfo));
        
        when(ccaFileDocumentUnprocessedGeneratorService.copyTemplateAndSaveFileDocument(
                CcaDocumentTemplateType.REFUND_CLAIM_FORM_BUY_OUT,
                CompetentAuthorityEnum.valueOf(request.getRequestResourcesMap().get(ResourceType.CA)),
                "Buyout Refund Claim Form.docx"))
                .thenReturn(refundClaimForm);

        // When
        service.generateDocuments(request);

        // Then
        verify(createDocumentService).create(
                eq(request),
                eq(targetPeriodType),
                eq(BuyOutSurplusChargeType.REFUND),
                anyMap());
        
        verify(ccaFileDocumentUnprocessedGeneratorService, times(1)).copyTemplateAndSaveFileDocument(
                CcaDocumentTemplateType.REFUND_CLAIM_FORM_BUY_OUT,
                CompetentAuthorityEnum.valueOf(request.getRequestResourcesMap().get(ResourceType.CA)),
                "Buyout Refund Claim Form.docx");

        verify(payload).setRefundClaimForm(refundClaimForm);

        assertEquals(fileInfo, payload.getRefundOfficialNotices().get(targetPeriodType));
        assertTrue(ObjectUtils.isEmpty(payload.getBuyOutOfficialNotices()));
    }

    @Test
    void generateDocuments_whenBothTransactionTypesExist() {
        FileInfoDTO fileInfoBuyOut = FileInfoDTO.builder()
                .name("CCA070001")
                .uuid("uuid")
                .build();
        FileInfoDTO fileInfoRefund = FileInfoDTO.builder()
                .name("CCA070002")
                .uuid("uuid")
                .build();
        final TargetPeriodType targetPeriodType = TargetPeriodType.TP7;

        FileInfoDTO refundClaimForm = mock(FileInfoDTO.class);
        
        when(facilityDataQueryService.getFacilityDataByIds(anyList())).thenReturn(Collections.emptyList());

        when(metadata.getBuyOutTransactionCodes()).thenReturn(Map.of(targetPeriodType, "CCA070001"));
        when(metadata.getRefundTransactionCodes()).thenReturn(Map.of(targetPeriodType, "CCA070002"));
        
        when(payload.getBuyOutOfficialNotices()).thenReturn(new EnumMap<>(TargetPeriodType.class));
        when(payload.getRefundOfficialNotices()).thenReturn(new EnumMap<>(TargetPeriodType.class));
        
        when(payload.getFacilityPerformanceResults()).thenReturn(Collections.emptyList());
        
        when(request.getRequestResourcesMap()).thenReturn(Map.of(ResourceType.CA, CompetentAuthorityEnum.ENGLAND.toString()));

        when(createDocumentService.create(
                eq(request),
                eq(targetPeriodType),
                eq(BuyOutSurplusChargeType.FEE),
                anyMap()))
                .thenReturn(CompletableFuture.completedFuture(fileInfoBuyOut));
        when(createDocumentService.create(
                eq(request),
                eq(targetPeriodType),
                eq(BuyOutSurplusChargeType.REFUND),
                anyMap()))
                .thenReturn(CompletableFuture.completedFuture(fileInfoRefund));
        
        when(ccaFileDocumentUnprocessedGeneratorService.copyTemplateAndSaveFileDocument(
                CcaDocumentTemplateType.REFUND_CLAIM_FORM_BUY_OUT,
                CompetentAuthorityEnum.valueOf(request.getRequestResourcesMap().get(ResourceType.CA)),
                "Buyout Refund Claim Form.docx"))
                .thenReturn(refundClaimForm);

        // When
        service.generateDocuments(request);

        // Then
        verify(createDocumentService).create(
                eq(request),
                eq(targetPeriodType),
                eq(BuyOutSurplusChargeType.FEE),
                anyMap());
        verify(createDocumentService).create(
                eq(request),
                eq(targetPeriodType),
                eq(BuyOutSurplusChargeType.REFUND),
                anyMap());
        verify(ccaFileDocumentUnprocessedGeneratorService, times(1)).copyTemplateAndSaveFileDocument(
                CcaDocumentTemplateType.REFUND_CLAIM_FORM_BUY_OUT,
                CompetentAuthorityEnum.valueOf(request.getRequestResourcesMap().get(ResourceType.CA)),
                "Buyout Refund Claim Form.docx");
        verify(payload).setRefundClaimForm(refundClaimForm);

        assertEquals(fileInfoBuyOut, payload.getBuyOutOfficialNotices().get(targetPeriodType));
        assertEquals(fileInfoRefund, payload.getRefundOfficialNotices().get(targetPeriodType));

        verify(createDocumentService, times(2))
                .create(eq(request), eq(targetPeriodType), any(), anyMap());
    }
    
}
