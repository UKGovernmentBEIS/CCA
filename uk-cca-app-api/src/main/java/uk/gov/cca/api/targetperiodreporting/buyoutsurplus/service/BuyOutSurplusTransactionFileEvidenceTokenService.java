package uk.gov.cca.api.targetperiodreporting.buyoutsurplus.service;

import org.springframework.stereotype.Service;

import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.files.evidences.service.FileEvidenceService;
import uk.gov.cca.api.files.evidences.service.FileEvidenceTokenService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.token.UserFileTokenService;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BuyOutSurplusTransactionFileEvidenceTokenService extends FileEvidenceTokenService {

    private final BuyOutSurplusQueryService buyOutSurplusQueryService;

    public BuyOutSurplusTransactionFileEvidenceTokenService(UserFileTokenService userFileTokenService,
                                                            FileEvidenceService fileEvidenceService,
                                                            BuyOutSurplusQueryService buyOutSurplusQueryService) {
        super(userFileTokenService, fileEvidenceService);
        this.buyOutSurplusQueryService = buyOutSurplusQueryService;
    }

    @Override
    public void validateFileEvidenceResource(Long resourceId, UUID fileEvidenceUuid) {
        Set<UUID> evidences = buyOutSurplusQueryService.getBuyOutSurplusTransactionHistory(resourceId).stream()
                .map(history -> history.getPayload().getEvidenceFiles().keySet())
                .flatMap(Collection::stream).collect(Collectors.toSet());

        if (!evidences.contains(fileEvidenceUuid)) {
            throw new BusinessException(CcaErrorCode.FILE_EVIDENCE_IS_NOT_RELATED_TO_TRANSACTION);
        }
    }
}
