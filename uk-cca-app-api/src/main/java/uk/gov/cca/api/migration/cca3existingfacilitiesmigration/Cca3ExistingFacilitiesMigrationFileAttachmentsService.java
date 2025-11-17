package uk.gov.cca.api.migration.cca3existingfacilitiesmigration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.files.attachments.service.CcaFileAttachmentService;
import uk.gov.cca.api.migration.MigrationBaseService;
import uk.gov.cca.api.migration.MigrationEndpoint;
import uk.gov.cca.api.migration.ftp.FtpFileService;
import uk.gov.cca.api.migration.ftp.FtpProperties;
import uk.gov.cca.api.migration.ftp.GenericFtpResult;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.files.common.domain.FileStatus;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@RequiredArgsConstructor
public class Cca3ExistingFacilitiesMigrationFileAttachmentsService extends MigrationBaseService {

    private final FtpProperties ftpProperties;
    private final FtpFileService ftpService;
    private final CcaFileAttachmentService ccaFileAttachmentService;
    private final Cca3ExistingFacilitiesMigrationFileAttachmentService cca3ExistingFacilitiesMigrationFileAttachmentService;
    private static final String FORWARD_SLASH = "/";
    private static final int BATCH_SIZE = 100;

    public static final String CCA3_EXISTING_FACILITIES_MIGRATION_PREFIX = "CCA3 Migration";

    @Override
    public List<String> migrate(String fileNames) {
        List<String> inputFiles = StringUtils.isBlank(fileNames)
                ? null : Arrays.stream(fileNames.split(",")).toList();

        // Get all files from directory
        final String sftpDirectory = ftpProperties.getServerCca3ExistingFacilitiesMigrationDirectory();
        GenericFtpResult<List<String>> files = ftpService.listFiles(sftpDirectory);

		if (files.getErrorReport() != null) {
			throw new BusinessException(ErrorCode.UPLOAD_FILE_FAILED_ERROR, files.getErrorReport());
		}

		if (!files.getData().contains(ftpProperties.getServerCca3ExistingFacilitiesMigrationSourceFile())) {
			throw new BusinessException(ErrorCode.UPLOAD_FILE_FAILED_ERROR, "No source file found");
		}

		if (inputFiles != null && !new HashSet<>(files.getData()).containsAll(inputFiles)) {
			throw new BusinessException(ErrorCode.UPLOAD_FILE_FAILED_ERROR, "Files not found in FTP server");
		}

		final List<FileInfoDTO> filesMigrated = ccaFileAttachmentService
				.getAllByFileNameLikeAndStatus(CCA3_EXISTING_FACILITIES_MIGRATION_PREFIX, FileStatus.PENDING_MIGRATION);
		List<String> filesToMigrate = Optional.ofNullable(inputFiles).orElse(files.getData()).stream()
				.filter(name -> filesMigrated.stream().noneMatch(fm -> fm.getName().equals(name)))
				.map(name -> sftpDirectory + FORWARD_SLASH + name)
				.toList();

        List<String> errors = new ArrayList<>();

		ListUtils.partition(filesToMigrate, BATCH_SIZE).forEach(batch -> {
			try {
				errors.addAll(cca3ExistingFacilitiesMigrationFileAttachmentService.transferFilesToDatabase(batch));
			} catch (Exception e) {
				errors.add(String.format("Files'%s' could not be uploaded: %s", batch, e.getMessage()));
			}
		});

        return errors;
    }

    @Override
    public String getResource() {
        return "cca3-existing-facilities-migration-attachments";
    }
}
