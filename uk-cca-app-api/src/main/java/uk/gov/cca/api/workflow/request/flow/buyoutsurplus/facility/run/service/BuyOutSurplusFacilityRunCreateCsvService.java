package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.run.service;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import uk.gov.cca.api.files.attachments.service.CcaFileAttachmentService;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.domain.BuyOutSurplusErrorType;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.domain.BuyOutSurplusFacilityAccountState;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.common.domain.BuyOutSurplusFacilityRunRequestPayload;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.facility.run.utils.BuyOutSurplusFacilityRunUtil;
import uk.gov.netz.api.files.common.domain.FileStatus;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

@Log4j2
@Service
@RequiredArgsConstructor
public class BuyOutSurplusFacilityRunCreateCsvService {
    
    private final RequestService requestService;
    private final CcaFileAttachmentService ccaFileAttachmentService;
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createCsvFile(final String requestId) {
        final Request request = requestService.findRequestById(requestId);
        BuyOutSurplusFacilityRunRequestPayload requestPayload = (BuyOutSurplusFacilityRunRequestPayload) request.getPayload();
        final Map<Long, BuyOutSurplusFacilityAccountState> accountStates = requestPayload.getAccountStates();

        if(!accountStates.isEmpty()) {
            try {
                // Write CSV
                final FileDTO csvFileDTO = BuyOutSurplusFacilityRunUtil
                        .createCsvFileContent(requestId, accountStates, requestPayload.getSubmitterId());

                // Save to DB
                final String uuid = ccaFileAttachmentService.createSystemFileAttachment(csvFileDTO, FileStatus.SUBMITTED);

                FileInfoDTO csvFile = FileInfoDTO.builder()
                        .uuid(uuid)
                        .name(csvFileDTO.getFileName())
                        .build();
                requestPayload.setCsvFile(csvFile);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                requestPayload.setErrorType(BuyOutSurplusErrorType.GENERATE_CSV_FAILED);
            }
        }
    }

}
