package uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.utils;

import lombok.experimental.UtilityClass;

import uk.gov.netz.api.common.domain.ResourceFile;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.files.common.utils.ResourceFileUtils;

import java.io.File;
import java.io.IOException;

@UtilityClass
public class Cca3ExistingFacilitiesMigrationUtil {

    public static final String CCA3_CSV_FILE = "cca3_facility_migration.csv";
    public static final String CALCULATOR_PLACEHOLDER_FILE = "Facility base year and target data (Placeholder).xlsx";
    public static final String CCA3_EXISTING_FACILITIES_MIGRATION_PREFIX = "CCA3 Migration";

    public FileDTO getPlaceholderFile() throws IOException {
        String resourcePath = "migration" + File.separator + "attachments" + File.separator + CALCULATOR_PLACEHOLDER_FILE;
        ResourceFile placeHolderFile = ResourceFileUtils.getResourceFile(resourcePath);

        return FileDTO.builder()
                .fileName(CALCULATOR_PLACEHOLDER_FILE)
                .fileType(placeHolderFile.getFileType())
                .fileContent(placeHolderFile.getFileContent())
                .fileSize(placeHolderFile.getFileSize())
                .build();
    }
}
