package uk.gov.cca.api.migration.cca2extensionnoticeemails;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.authorization.ccaauth.rules.domain.CcaResourceType;
import uk.gov.cca.api.migration.MigrationEndpoint;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayloadType;
import uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.processing.domain.Cca2ExtensionNoticeAccountProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.processing.domain.Cca2ExtensionNoticeAccountProcessingSubmittedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.processing.service.Cca2ExtensionNoticeAccountProcessingOfficialNoticeService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.domain.RequestAction;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@Log4j2
@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@RequiredArgsConstructor
public class Cca2ExtensionNoticeMigrationService {

    private final Cca2ExtensionNoticeAccountProcessingOfficialNoticeService cca2ExtensionNoticeAccountProcessingOfficialNoticeService;

    public String sendEmail(final RequestAction requestAction, final String accountBusinessId) {
        try {
            final Request request = requestAction.getRequest();
            final Cca2ExtensionNoticeAccountProcessingSubmittedRequestActionPayload actionPayload =
                    (Cca2ExtensionNoticeAccountProcessingSubmittedRequestActionPayload) requestAction.getPayload();
            final Long sectorAssociationId = request.getRequestResources().stream()
                    .filter(r -> r.getResourceType().equals(CcaResourceType.SECTOR_ASSOCIATION)).findFirst()
                    .map(r -> Long.parseLong(r.getResourceId()))
                    .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));

            final Request mockRequest = Request.builder()
                    .requestResources(request.getRequestResources())
                    .type(request.getType())
                    .payload(Cca2ExtensionNoticeAccountProcessingRequestPayload.builder()
                            .payloadType(CcaRequestActionPayloadType.CCA2_EXTENSION_NOTICE_ACCOUNT_PROCESSING_SUBMITTED_PAYLOAD)
                            .sectorAssociationId(sectorAssociationId)
                            .underlyingAgreementDocument(actionPayload.getUnderlyingAgreementDocument())
                            .officialNotice(actionPayload.getOfficialNotice())
                            .build())
                    .build();

            cca2ExtensionNoticeAccountProcessingOfficialNoticeService.sendOfficialNotice(mockRequest);

            return accountBusinessId;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return accountBusinessId + " (FAILED)";
        }
    }
}
