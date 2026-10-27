package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.processing.domain.BuyOutSurplusFacilityAccountProcessingRequestPayload;
import uk.gov.cca.api.workflow.request.flow.common.service.notification.CcaOfficialNoticeSendService;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;

@Service
@RequiredArgsConstructor
public class BuyOutSurplusFacilityAccountProcessingOfficialNoticeService {

    private final CcaOfficialNoticeSendService ccaOfficialNoticeSendService;
    
    @Transactional
    public void sendOfficialNotice(Request request) {
        final BuyOutSurplusFacilityAccountProcessingRequestPayload payload = (BuyOutSurplusFacilityAccountProcessingRequestPayload) request.getPayload();
        
        List<FileInfoDTO> attachments = new ArrayList<>();

        attachments.addAll(Optional.ofNullable(payload.getBuyOutOfficialNotices())
                .orElse(Collections.emptyMap()).values());

        attachments.addAll(Optional.ofNullable(payload.getRefundOfficialNotices())
                .orElse(Collections.emptyMap()).values());

        Optional.ofNullable(payload.getRefundClaimForm())
                .ifPresent(attachments::add);
        
        ccaOfficialNoticeSendService.sendOfficialNotice(attachments, request, new ArrayList<>());
    }    
}