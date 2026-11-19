package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service;

import org.springframework.stereotype.Service;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.files.evidences.service.FileEvidenceService;
import uk.gov.cca.api.files.evidences.service.FileEvidenceTokenService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.token.UserFileTokenService;

import java.util.Set;
import java.util.UUID;

@Service
public class BuyOutSurplusTransactionFileEvidenceTokenService extends FileEvidenceTokenService {

    private final BuyOutSurplusTransactionQueryService buyOutSurplusTransactionQueryService;

    public BuyOutSurplusTransactionFileEvidenceTokenService(UserFileTokenService userFileTokenService,
                                                            FileEvidenceService fileEvidenceService,
                                                            BuyOutSurplusTransactionQueryService buyOutSurplusTransactionQueryService) {
        super(userFileTokenService, fileEvidenceService);
        this.buyOutSurplusTransactionQueryService = buyOutSurplusTransactionQueryService;
    }

    @Override
    public void validateFileEvidenceResource(Long resourceId, UUID fileEvidenceUuid) {

        Set<UUID> evidences = buyOutSurplusTransactionQueryService.getBuyOutSurplusTransactionHistoryEvidenceFiles(resourceId);

        if (!evidences.contains(fileEvidenceUuid)) {
            throw new BusinessException(CcaErrorCode.FILE_EVIDENCE_IS_NOT_RELATED_TO_TRANSACTION);
        }
    }
}
