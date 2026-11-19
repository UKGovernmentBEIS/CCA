package uk.gov.cca.api.migration.transactionhistoryfiles;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.migration.MigrationBaseService;
import uk.gov.cca.api.migration.MigrationEndpoint;
import uk.gov.cca.api.migration.ftp.FtpFileService;
import uk.gov.cca.api.migration.ftp.FtpProperties;
import uk.gov.cca.api.migration.ftp.GenericFtpResult;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@RequiredArgsConstructor
public class TransactionHistoryFilesMigrationService extends MigrationBaseService {

    private final FtpProperties ftpProperties;
    private final FtpFileService ftpService;
    private final TransactionHistoryFileMigrationService transactionHistoryFileEvidenceMigrationService;

    private static final String FORWARD_SLASH = "/";

    @Override
    public List<String> migrate(String fileNames) {
        List<String> inputFiles = StringUtils.isBlank(fileNames)
                ? null : Arrays.stream(fileNames.split(",")).toList();

        // Get all files from directory
        final String sftpDirectory = ftpProperties.getServerTransactionHistoryFileMigrationDirectory();
        GenericFtpResult<List<String>> files = ftpService.listFiles(sftpDirectory);

        if (files.getErrorReport() != null) {
            throw new BusinessException(ErrorCode.UPLOAD_FILE_FAILED_ERROR, files.getErrorReport());
        }

        if (inputFiles != null && !new HashSet<>(files.getData()).containsAll(inputFiles)) {
            throw new BusinessException(ErrorCode.UPLOAD_FILE_FAILED_ERROR, "Files not found in FTP server");
        }

        // Create files to migrate
        List<String> filesToMigrate = Optional.ofNullable(inputFiles).orElse(files.getData()).stream()
                .map(name -> sftpDirectory + FORWARD_SLASH + name)
                .toList();

        // Add files to DB
        return transactionHistoryFileEvidenceMigrationService.transferFilesToDatabase(filesToMigrate);
    }

    @Override
    public String getResource() {
        return "transaction-history-file-evidences";
    }
}
