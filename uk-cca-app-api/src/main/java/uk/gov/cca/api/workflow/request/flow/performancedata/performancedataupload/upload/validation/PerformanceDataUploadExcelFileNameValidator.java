package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.upload.validation;

import org.springframework.stereotype.Service;

import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.workflow.request.core.domain.SectorAssociationInfo;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.common.validation.PerformanceDataUploadViolation;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.upload.domain.PerformanceDataUpload;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.common.utils.PerformanceDataUploadUtility;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.common.utils.PerformanceDataUploadUtility.XLSX_FILE_NAME_REGEX;

@Service
public class PerformanceDataUploadExcelFileNameValidator {

    public BusinessValidationResult validate(final String filename, final SectorAssociationInfo sectorAssociationInfo,
                                             final PerformanceDataUpload performanceDataUpload, final Map<String, Long> accountsMap) {
        // Validate filename
        final String regex = String.format(XLSX_FILE_NAME_REGEX, performanceDataUpload.getPerformanceDataTargetPeriodType().name());
        Matcher matcher = Pattern.compile(regex).matcher(filename);
        if (!matcher.matches()) {
            return BusinessValidationResult.invalid(List.of(
                    new PerformanceDataUploadViolation(PerformanceDataUploadViolation.PerformanceDataUploadViolationMessage.FILE_NAME_NOT_VALID)));
        }

        // Validate sector
        String sector = filename.substring(0, matcher.start(1));
        if(!sectorAssociationInfo.getAcronym().equals(sector)) {
            return BusinessValidationResult.invalid(List.of(
                    new PerformanceDataUploadViolation(PerformanceDataUploadViolation.PerformanceDataUploadViolationMessage.SECTOR_NOT_VALID)));
        }

        // Validate active account
        final String accountBusinessId = PerformanceDataUploadUtility.extractBusinessAccountIdFromReportFilename(filename);
        if(!accountsMap.containsKey(accountBusinessId)) {
            return BusinessValidationResult.invalid(List.of(
                    new PerformanceDataUploadViolation(PerformanceDataUploadViolation.PerformanceDataUploadViolationMessage.TU_NOT_VALID)));
        }

        return BusinessValidationResult.valid();
    }
}
