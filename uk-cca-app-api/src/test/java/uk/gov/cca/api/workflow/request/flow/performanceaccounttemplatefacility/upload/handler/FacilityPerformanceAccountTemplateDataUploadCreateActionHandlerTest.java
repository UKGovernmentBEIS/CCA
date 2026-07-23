package uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestMetadataType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationInfo;
import uk.gov.cca.api.workflow.request.core.service.SectorReferenceDetailsService;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaRequestParams;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.common.config.PerformanceAccountTemplateConfig;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataUploadRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.performanceaccounttemplatefacility.upload.domain.FacilityPerformanceAccountTemplateDataUploadRequestPayload;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.workflow.request.StartProcessRequestService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.flow.common.domain.RequestCreateActionEmptyPayload;

import java.time.Year;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FacilityPerformanceAccountTemplateDataUploadCreateActionHandlerTest {

    @InjectMocks
    private FacilityPerformanceAccountTemplateDataUploadCreateActionHandler handler;

    @Mock
    private SectorReferenceDetailsService sectorReferenceDetailsService;

    @Mock
    private StartProcessRequestService startProcessRequestService;

    @Mock
    private PerformanceAccountTemplateConfig performanceAccountTemplateConfig;

    @Test
    void process() {
        final long sectorAssociationId = 1L;
        final RequestCreateActionEmptyPayload payload = RequestCreateActionEmptyPayload.builder().build();
        final AppUser appUser = AppUser.builder().userId("sector").build();
        final Year targetYear = Year.of(2026);

        final SectorAssociationInfo sectorAssociationInfo = SectorAssociationInfo.builder()
                .id(sectorAssociationId)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .build();
        final CcaRequestParams requestParams = CcaRequestParams.builder()
                .type(CcaRequestType.FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD)
                .requestResources(Map.of(
                        CcaResourceType.SECTOR_ASSOCIATION, String.valueOf(sectorAssociationId),
                        ResourceType.CA, CompetentAuthorityEnum.ENGLAND.name()
                ))
                .requestMetadata(FacilityPerformanceAccountTemplateDataUploadRequestMetadata.builder()
                        .type(CcaRequestMetadataType.FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD)
                        .targetYear(targetYear)
                        .build())
                .requestPayload(FacilityPerformanceAccountTemplateDataUploadRequestPayload.builder()
                        .payloadType(CcaRequestPayloadType.FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD_PAYLOAD)
                        .sectorAssociationInfo(sectorAssociationInfo)
                        .sectorUserAssignee(appUser.getUserId())
                        .targetYear(targetYear)
                        .build())
                .build();

        when(performanceAccountTemplateConfig.getTargetYear()).thenReturn(targetYear);
        when(sectorReferenceDetailsService.getSectorAssociationInfo(sectorAssociationId)).thenReturn(sectorAssociationInfo);
        when(startProcessRequestService.startProcess(requestParams)).thenReturn(Request.builder().id("request-id").build());

        // Invoke
        handler.process(sectorAssociationId, payload, appUser);

        // Verify
        verify(performanceAccountTemplateConfig, times(1)).getTargetYear();
        verify(sectorReferenceDetailsService, times(1)).getSectorAssociationInfo(sectorAssociationId);
        verify(startProcessRequestService, times(1)).startProcess(requestParams);
    }

    @Test
    void getRequestType() {
        assertThat(handler.getRequestType()).isEqualTo(CcaRequestType.FACILITY_PERFORMANCE_ACCOUNT_TEMPLATE_DATA_UPLOAD);
    }
}
