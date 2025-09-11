package uk.gov.cca.api.subsistencefees.service;

import org.springframework.stereotype.Service;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.files.evidences.service.FileEvidenceService;
import uk.gov.cca.api.files.evidences.service.FileEvidenceTokenService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.token.UserFileTokenService;

import java.util.Set;
import java.util.UUID;

@Service
public class SubsistenceFeesMoaFileEvidenceTokenService extends FileEvidenceTokenService {

    private final SubsistenceFeesMoaQueryService subsistenceFeesMoaQueryService;

    public SubsistenceFeesMoaFileEvidenceTokenService(UserFileTokenService userFileTokenService,
                                                      FileEvidenceService fileEvidenceService,
                                                      SubsistenceFeesMoaQueryService subsistenceFeesMoaQueryService) {
        super(userFileTokenService, fileEvidenceService);
        this.subsistenceFeesMoaQueryService = subsistenceFeesMoaQueryService;
    }

    @Override
    public void validateFileEvidenceResource(Long moaId, UUID fileEvidenceUuid) {
        Set<UUID> moaEvidenceFiles = subsistenceFeesMoaQueryService.getFileEvidenceFilesByMoaId(moaId);

        if (!moaEvidenceFiles.contains(fileEvidenceUuid)) {
            throw new BusinessException(CcaErrorCode.FILE_EVIDENCE_IS_NOT_RELATED_TO_SUBSISTENCE_FEES_MOA);
        }
    }
}
