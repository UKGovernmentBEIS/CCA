package uk.gov.cca.api.files.evidences.service;

import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.files.evidences.domain.FileEvidence;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.files.common.domain.FileStatus;
import uk.gov.netz.api.token.FileToken;
import uk.gov.netz.api.token.UserFileTokenService;

import java.util.UUID;

public abstract class FileEvidenceTokenService {

    private final UserFileTokenService userFileTokenService;
    private final FileEvidenceService fileEvidenceService;

    protected FileEvidenceTokenService(UserFileTokenService userFileTokenService, FileEvidenceService fileEvidenceService) {
        this.userFileTokenService = userFileTokenService;
        this.fileEvidenceService = fileEvidenceService;
    }

    protected abstract void validateFileEvidenceResource(Long resourceId, UUID fileEvidenceUuid);

    public FileToken generateGetFileEvidenceToken(Long resourceId, UUID fileEvidenceUuid, AppUser submitter) {
        FileEvidence fileEvidence = fileEvidenceService.getFileEvidenceByUuid(fileEvidenceUuid.toString());

        if (fileEvidence.getStatus().equals(FileStatus.PENDING)) {
            // check that the file has been created by the submitter
            if (submitter.getUserId().equals(fileEvidence.getCreatedBy())) {
                return userFileTokenService.generateGetFileToken(fileEvidenceUuid.toString());
            } else {
                throw new BusinessException(CcaErrorCode.SUBMITTER_HAS_NO_ACCESS_TO_FILE_EVIDENCE);
            }
        }

        // validate if the file is related to the resource
        validateFileEvidenceResource(resourceId, fileEvidenceUuid);
        return userFileTokenService.generateGetFileToken(fileEvidenceUuid.toString());
    }
}
