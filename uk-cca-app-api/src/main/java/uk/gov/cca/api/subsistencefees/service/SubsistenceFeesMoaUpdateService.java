package uk.gov.cca.api.subsistencefees.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.files.evidences.service.FileEvidenceService;
import uk.gov.cca.api.subsistencefees.domain.SubsistenceFeesMoa;
import uk.gov.cca.api.subsistencefees.domain.SubsistenceFeesMoaReceivedAmountHistory;
import uk.gov.cca.api.subsistencefees.domain.SubsistenceFeesMoaReceivedAmountHistoryPayload;
import uk.gov.cca.api.subsistencefees.domain.dto.SubsistenceFeesMoaReceivedAmountDetailsDTO;
import uk.gov.cca.api.subsistencefees.repository.SubsistenceFeesMoaReceivedAmountHistoryRepository;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;

import java.math.BigDecimal;

@Log4j2
@Service
@RequiredArgsConstructor
public class SubsistenceFeesMoaUpdateService {

    private final SubsistenceFeesMoaReceivedAmountHistoryRepository receivedAmountHistoryRepository;
    private final SubsistenceFeesMoaQueryService subsistenceFeesMoaQueryService;
    private final FileEvidenceService fileEvidenceService;

    @Transactional
    public void updateSubsistenceFeesMoaReceivedAmount(Long moaId, SubsistenceFeesMoaReceivedAmountDetailsDTO detailsDTO, AppUser submitter) {

        BigDecimal transactionAmount = detailsDTO.getTransactionAmount();

        if (transactionAmount.compareTo(BigDecimal.ZERO) == 0) {
            throw new BusinessException(CcaErrorCode.ZERO_SUBSISTENCE_FEES_MOA_TRANSACTION_AMOUNT);
        }

        SubsistenceFeesMoa subsistenceFeesMoa = subsistenceFeesMoaQueryService.getSubsistenceFeesMoaByIdPessimistic(moaId);
        BigDecimal previousReceivedAmount = subsistenceFeesMoa.getRegulatorReceivedAmount();
        BigDecimal currentTotalReceivedAmount = previousReceivedAmount.add(transactionAmount);

        // validate that the current total received amount is not negative
        if (currentTotalReceivedAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(CcaErrorCode.NEGATIVE_SUBSISTENCE_FEES_MOA_RECEIVED_AMOUNT);
        }

        // set the new total received amount
        subsistenceFeesMoa.setRegulatorReceivedAmount(currentTotalReceivedAmount);

        // submit evidence files
        detailsDTO.getEvidenceFiles().keySet()
                .forEach(uuid -> fileEvidenceService.submitFileEvidence(uuid.toString()));

        // create received amount history
        SubsistenceFeesMoaReceivedAmountHistory receivedAmountHistory = SubsistenceFeesMoaReceivedAmountHistory.builder()
                .subsistenceFeesMoa(subsistenceFeesMoa)
                .payload(SubsistenceFeesMoaReceivedAmountHistoryPayload.builder()
                        .transactionAmount(transactionAmount)
                        .previousReceivedAmount(previousReceivedAmount)
                        .evidenceFiles(detailsDTO.getEvidenceFiles())
                        .comments(detailsDTO.getComments())
                        .build())
                .submitter(submitter.getFullName())
                .submitterId(submitter.getUserId())
                .build();

        receivedAmountHistoryRepository.save(receivedAmountHistory);
    }
}
