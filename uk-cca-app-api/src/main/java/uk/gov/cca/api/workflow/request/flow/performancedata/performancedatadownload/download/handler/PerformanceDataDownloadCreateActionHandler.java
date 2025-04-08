package uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.download.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationInfo;
import uk.gov.cca.api.workflow.request.core.service.SectorReferenceDetailsService;
import uk.gov.cca.api.workflow.request.flow.common.actionhandler.RequestSectorCreateActionHandler;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaRequestParams;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedatadownload.download.domain.PerformanceDataDownloadRequestPayload;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.workflow.request.StartProcessRequestService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.flow.common.domain.RequestCreateActionEmptyPayload;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class PerformanceDataDownloadCreateActionHandler implements RequestSectorCreateActionHandler<RequestCreateActionEmptyPayload> {

    private final StartProcessRequestService startProcessRequestService;
    private final SectorReferenceDetailsService sectorReferenceDetailsService;

    @Override
    public String process(Long sectorId, RequestCreateActionEmptyPayload payload, AppUser appUser) {
        SectorAssociationInfo sectorAssociationInfo = sectorReferenceDetailsService.getSectorAssociationInfo(sectorId);

        // Create process for performance data download
        CcaRequestParams requestParams = CcaRequestParams.builder()
                .type(CcaRequestType.PERFORMANCE_DATA_DOWNLOAD)
                .requestResources(Map.of(
                        CcaResourceType.SECTOR_ASSOCIATION, sectorId.toString(),
                        ResourceType.CA, sectorAssociationInfo.getCompetentAuthority().name()
                ))
                .requestPayload(PerformanceDataDownloadRequestPayload.builder()
                        .payloadType(CcaRequestPayloadType.PERFORMANCE_DATA_DOWNLOAD_PAYLOAD)
                        .sectorAssociationInfo(sectorAssociationInfo)
                        .sectorUserAssignee(appUser.getUserId())
                        .build())
                .build();

        final Request request = startProcessRequestService.startProcess(requestParams);

        return request.getId();
    }

    @Override
    public String getRequestType() {
        return CcaRequestType.PERFORMANCE_DATA_DOWNLOAD;
    }
}
