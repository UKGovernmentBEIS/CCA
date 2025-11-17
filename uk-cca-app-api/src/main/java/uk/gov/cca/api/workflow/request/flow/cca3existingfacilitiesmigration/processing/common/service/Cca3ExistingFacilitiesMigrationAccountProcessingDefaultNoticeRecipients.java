package uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.account.domain.dto.NoticeRecipientType;
import uk.gov.cca.api.user.sectoruser.service.SectorUserAuthorityInfoService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.common.domain.Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.common.domain.DefaultNoticeRecipient;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.RequestDefaultNoticeRecipients;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.TargetUnitAccountNoticeRecipients;
import uk.gov.netz.api.workflow.request.core.domain.Request;

import java.util.List;

@Service
@RequiredArgsConstructor
public class Cca3ExistingFacilitiesMigrationAccountProcessingDefaultNoticeRecipients implements RequestDefaultNoticeRecipients {

    private final TargetUnitAccountNoticeRecipients targetUnitAccountNoticeRecipients;
    private final SectorUserAuthorityInfoService sectorUserAuthorityInfoService;

    @Override
    public List<DefaultNoticeRecipient> getRecipients(final Request request) {
        final Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload payload =
                (Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload) request.getPayload();

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
        return CcaRequestType.CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING;
    }
}
