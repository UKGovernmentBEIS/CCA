package uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.processing.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.account.domain.dto.NoticeRecipientType;
import uk.gov.cca.api.user.sectoruser.service.SectorUserAuthorityInfoService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.cca2extensionnotice.processing.domain.Cca2ExtensionNoticeAccountProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.common.domain.DefaultNoticeRecipient;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.RequestDefaultNoticeRecipients;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.TargetUnitAccountNoticeRecipients;
import uk.gov.netz.api.workflow.request.core.domain.Request;

import java.util.List;

@Service
@RequiredArgsConstructor
public class Cca2ExtensionNoticeAccountProcessingDefaultNoticeRecipients implements RequestDefaultNoticeRecipients {

    private final TargetUnitAccountNoticeRecipients targetUnitAccountNoticeRecipients;
    private final SectorUserAuthorityInfoService sectorUserAuthorityInfoService;

    @Override
    public List<DefaultNoticeRecipient> getRecipients(final Request request) {
        final Cca2ExtensionNoticeAccountProcessingRequestPayload payload =
                (Cca2ExtensionNoticeAccountProcessingRequestPayload) request.getPayload();

        List<DefaultNoticeRecipient> recipients = targetUnitAccountNoticeRecipients
                .getDefaultNoticeRecipients(request.getAccountId());

        sectorUserAuthorityInfoService.getConsultantSectorUsersNoticeRecipients(payload.getSectorAssociationId()).stream()
                .map(sector ->
                        DefaultNoticeRecipient.builder()
                                .name(sector.getFirstName() + " " + sector.getLastName())
                                .email(sector.getEmail())
                                .recipientType(NoticeRecipientType.SECTOR_CONSULTANT)
                                .build()
                ).forEach(recipients::add);

        return recipients;
    }

    @Override
    public String getType() {
        return CcaRequestType.CCA2_EXTENSION_NOTICE_ACCOUNT_PROCESSING;
    }
}
