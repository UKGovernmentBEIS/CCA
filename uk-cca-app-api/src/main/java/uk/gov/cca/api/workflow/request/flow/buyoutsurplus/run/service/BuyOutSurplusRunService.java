package uk.gov.cca.api.workflow.request.flow.buyoutsurplus.run.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.files.attachments.service.CcaFileAttachmentService;
import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionType;
import uk.gov.cca.api.workflow.request.core.domain.constants.CcaRequestStatuses;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.domain.BuyOutSurplusAccountState;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.domain.BuyOutSurplusErrorType;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.domain.BuyOutSurplusRunSummary;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.domain.BuyOutSurplusRunRequestMetadata;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.common.domain.BuyOutSurplusRunRequestPayload;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.run.domain.BuyOutSurplusRunCompletedRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.run.transform.BuyOutSurplusRunMapper;
import uk.gov.cca.api.workflow.request.flow.buyoutsurplus.run.utils.BuyOutSurplusRunUtil;
import uk.gov.netz.api.files.common.domain.FileStatus;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.workflow.request.core.domain.Request;
import uk.gov.netz.api.workflow.request.core.service.RequestService;

import java.time.LocalDateTime;
import java.util.Map;

@Log4j2
@Service
@RequiredArgsConstructor
public class BuyOutSurplusRunService {

    private final RequestService requestService;
    private final CcaFileAttachmentService ccaFileAttachmentService;
    private static final BuyOutSurplusRunMapper MAPPER = Mappers.getMapper(BuyOutSurplusRunMapper.class);

    @Transactional
    public void submit(final String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final BuyOutSurplusRunRequestPayload requestPayload = (BuyOutSurplusRunRequestPayload) request.getPayload();

        LocalDateTime now = LocalDateTime.now();
        request.setSubmissionDate(now);

        requestService.addActionToRequest(request,
                null,
                CcaRequestActionType.BUY_OUT_SURPLUS_RUN_SUBMITTED,
                requestPayload.getSubmitterId());
    }

    @Transactional
    public void accountProcessingCompleted(final String requestId, final Long accountId, final BuyOutSurplusAccountState buyOutSurplusAccountState) {
        final Request request = requestService.findRequestById(requestId);
        final BuyOutSurplusRunRequestPayload requestPayload = (BuyOutSurplusRunRequestPayload) request.getPayload();

        requestPayload.getBuyOutSurplusAccountStates().put(accountId, buyOutSurplusAccountState);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createCsvFile(final String requestId) {
        final Request request = requestService.findRequestById(requestId);
        BuyOutSurplusRunRequestPayload requestPayload = (BuyOutSurplusRunRequestPayload) request.getPayload();
        final Map<Long, BuyOutSurplusAccountState> buyOutSurplusAccountStates = requestPayload.getBuyOutSurplusAccountStates();

        if(!buyOutSurplusAccountStates.isEmpty()) {
            try {
                // Write CSV
                final FileDTO csvFileDTO = BuyOutSurplusRunUtil.createCsvFileContent(requestId, buyOutSurplusAccountStates);

                // Save to DB
                final String uuid = ccaFileAttachmentService.createSystemFileAttachment(
                        csvFileDTO, FileStatus.SUBMITTED, requestPayload.getSubmitterId());

                FileInfoDTO csvFile = FileInfoDTO.builder().uuid(uuid).name(csvFileDTO.getFileName()).build();
                requestPayload.setCsvFile(csvFile);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                requestPayload.setErrorType(BuyOutSurplusErrorType.GENERATE_CSV_FAILED);
            }
        }
    }

    @Transactional
    public void complete(final String requestId) {
        final Request request = requestService.findRequestById(requestId);
        BuyOutSurplusRunRequestPayload requestPayload = (BuyOutSurplusRunRequestPayload) request.getPayload();
        BuyOutSurplusRunRequestMetadata metadata = (BuyOutSurplusRunRequestMetadata) request.getMetadata();
        final Map<Long, BuyOutSurplusAccountState> buyOutSurplusAccountStates = requestPayload.getBuyOutSurplusAccountStates();

        // Update payload
        BuyOutSurplusRunSummary runSummary = MAPPER.toBuyOutSurplusRunSummary(buyOutSurplusAccountStates);
        requestPayload.setRunSummary(runSummary);

        // Update metadata
        metadata.setTotalAccounts(runSummary.getTotalAccounts());
        metadata.setFailedAccounts(runSummary.getFailedAccounts());

        String actionType = CcaRequestActionType.BUY_OUT_SURPLUS_RUN_COMPLETED;
        // Update status
        if(runSummary.getFailedAccounts() > 0) {
            requestService.updateRequestStatus(requestId, CcaRequestStatuses.COMPLETED_WITH_FAILURES);
            actionType = CcaRequestActionType.BUY_OUT_SURPLUS_RUN_COMPLETED_WITH_FAILURES;
        }

        // Create timeline
        final BuyOutSurplusRunCompletedRequestActionPayload actionPayload = MAPPER
                .toBuyOutSurplusRunCompletedRequestActionPayload(requestPayload);
        requestService.addActionToRequest(request, actionPayload, actionType, requestPayload.getSubmitterId());
    }
}
