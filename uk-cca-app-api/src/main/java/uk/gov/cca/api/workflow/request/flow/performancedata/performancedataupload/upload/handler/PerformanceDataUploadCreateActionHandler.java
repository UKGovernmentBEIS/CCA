package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.upload.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestPayloadType;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationInfo;
import uk.gov.cca.api.workflow.request.core.service.SectorReferenceDetailsService;
import uk.gov.cca.api.workflow.request.flow.common.actionhandler.RequestSectorCreateActionHandler;
import uk.gov.cca.api.workflow.request.flow.common.domain.CcaRequestParams;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.upload.domain.PerformanceDataUploadRequestPayload;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.workflow.request.StartProcessRequestService;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.flow.common.domain.RequestCreateActionEmptyPayload;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class PerformanceDataUploadCreateActionHandler implements RequestSectorCreateActionHandler<RequestCreateActionEmptyPayload> {

    private final StartProcessRequestService startProcessRequestService;
    private final SectorReferenceDetailsService sectorReferenceDetailsService;

    @Override
    public String process(Long sectorId, RequestCreateActionEmptyPayload payload, AppUser appUser) {
        SectorAssociationInfo sectorAssociationInfo = sectorReferenceDetailsService.getSectorAssociationInfo(sectorId);

        // Create process for performance data upload
        CcaRequestParams requestParams = CcaRequestParams.builder()
                .type(CcaRequestType.PERFORMANCE_DATA_UPLOAD)
                .requestResources(Map.of(
                        CcaResourceType.SECTOR_ASSOCIATION, sectorId.toString(),
                        ResourceType.CA, sectorAssociationInfo.getCompetentAuthority().name()
                ))
                .requestPayload(PerformanceDataUploadRequestPayload.builder()
                        .payloadType(CcaRequestPayloadType.PERFORMANCE_DATA_UPLOAD_PAYLOAD)
                        .sectorAssociationInfo(sectorAssociationInfo)
                        .sectorUserAssignee(appUser.getUserId())
                        .build())
                .build();

        final Request request = startProcessRequestService.startProcess(requestParams);

        return request.getId();
    }

    @Override
    public String getRequestType() {
        return CcaRequestType.PERFORMANCE_DATA_UPLOAD;
    }
}
