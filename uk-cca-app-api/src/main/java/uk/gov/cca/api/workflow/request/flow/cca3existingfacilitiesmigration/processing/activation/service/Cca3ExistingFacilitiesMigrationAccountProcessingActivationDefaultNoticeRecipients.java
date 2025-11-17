package uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.activation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.account.domain.dto.NoticeRecipientDTO;
import uk.gov.cca.api.account.domain.dto.NoticeRecipientType;
import uk.gov.cca.api.user.sectoruser.service.SectorUserAuthorityInfoService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestTaskType;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.common.domain.Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.RequestTaskDefaultNoticeRecipients;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.TargetUnitAccountNoticeRecipients;
import uk.gov.netz.api.workflow.request.core.domain.RequestTask;

import java.util.List;

@Service
@RequiredArgsConstructor
public class Cca3ExistingFacilitiesMigrationAccountProcessingActivationDefaultNoticeRecipients implements RequestTaskDefaultNoticeRecipients {

    private final TargetUnitAccountNoticeRecipients targetUnitAccountNoticeRecipients;
    private final SectorUserAuthorityInfoService sectorUserAuthorityInfoService;

    @Override
    public List<NoticeRecipientDTO> getRecipients(RequestTask requestTask) {
        final Long accountId = requestTask.getRequest().getAccountId();
        final Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload requestPayload =
                (Cca3ExistingFacilitiesMigrationAccountProcessingRequestPayload) requestTask.getRequest().getPayload();
        final Long sectorId = requestPayload.getSectorAssociationId();

        List<NoticeRecipientDTO> recipients = targetUnitAccountNoticeRecipients.getNoticeRecipients(accountId);

        sectorUserAuthorityInfoService
                .getConsultantSectorUsersNoticeRecipients(sectorId).stream()
                .map(sector ->
                        NoticeRecipientDTO.builder()
                                .firstName(sector.getFirstName())
                                .lastName(sector.getLastName())
                                .email(sector.getEmail())
                                .type(NoticeRecipientType.SECTOR_CONSULTANT)
                                .build())
                .forEach(recipients::add);

        return recipients;
    }

    @Override
    public String getType() {
        return CcaRequestTaskType.CCA3_EXISTING_FACILITIES_MIGRATION_ACCOUNT_PROCESSING_ACTIVATION;
    }
}
