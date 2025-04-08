package uk.gov.cca.api.workflow.request.flow.subsistencefees.sectormoa.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.account.domain.dto.NoticeRecipientType;
import uk.gov.cca.api.sectorassociation.domain.dto.SectorAssociationContactDTO;
import uk.gov.cca.api.sectorassociation.service.SectorAssociationInfoService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestType;
import uk.gov.cca.api.workflow.request.flow.common.domain.DefaultNoticeRecipient;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.RequestDefaultNoticeRecipients;
import uk.gov.cca.api.workflow.request.flow.subsistencefees.sectormoa.domain.SectorMoaRequestPayload;
import uk.gov.netz.api.workflow.request.core.domain.Request;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SectorMoaDefaultNoticeRecipients implements RequestDefaultNoticeRecipients {

    private final SectorAssociationInfoService sectorAssociationInfoService;

    @Override
    public List<DefaultNoticeRecipient> getRecipients(Request request) {

        final SectorMoaRequestPayload payload = (SectorMoaRequestPayload) request.getPayload();
        final Long sectorAssociationId = payload.getSectorAssociationId();

        SectorAssociationContactDTO recipient = sectorAssociationInfoService.getSectorAssociationContact(sectorAssociationId);

        return List.of(DefaultNoticeRecipient.builder()
                .name(recipient.getFullName())
                .email(recipient.getEmail())
                .recipientType(NoticeRecipientType.SECTOR_CONTACT)
                .build());
    }

    @Override
    public String getType() {
        return CcaRequestType.SECTOR_MOA;
    }
}
