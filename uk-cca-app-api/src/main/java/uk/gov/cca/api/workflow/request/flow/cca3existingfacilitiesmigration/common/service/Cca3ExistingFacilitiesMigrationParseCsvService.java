package uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.cca.api.common.utils.CsvUtils;
import uk.gov.cca.api.files.attachments.service.CcaFileAttachmentService;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.domain.Cca3FacilityMigrationData;
import uk.gov.netz.api.files.attachments.service.FileAttachmentService;
import uk.gov.netz.api.files.common.domain.FileStatus;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

import java.util.List;
import java.util.UUID;

import static uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.utils.Cca3ExistingFacilitiesMigrationUtil.CALCULATOR_PLACEHOLDER_FILE;
import static uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.utils.Cca3ExistingFacilitiesMigrationUtil.CCA3_CSV_FILE;
import static uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.utils.Cca3ExistingFacilitiesMigrationUtil.CCA3_EXISTING_FACILITIES_MIGRATION_PREFIX;

/**
 *  An actuator endpoint ('cca3-existing-facilities-migration-attachments')
 *  must be executed beforehand so that the csv and the available calculator files
 *  are stored in the file_attachment table.
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class Cca3ExistingFacilitiesMigrationParseCsvService {

    private final FileAttachmentService fileAttachmentService;
    private final CcaFileAttachmentService ccaFileAttachmentService;

    @Transactional
    public String submitAndGetSourceFile() {
        // If exists set CSV as SUBMITTED
        return ccaFileAttachmentService.updateMigrationFileStatusByName(CCA3_CSV_FILE, FileStatus.SUBMITTED);
    }

    @Transactional
    public List<Cca3FacilityMigrationData> parseSourceFile(String csvFile, List<String> errors) {
        // Get csv from server
        FileDTO fileDTO = fileAttachmentService.getFileDTO(csvFile);

        // Convert CSV to list of migration facilities
        List<Cca3FacilityMigrationData> facilities = CsvUtils.convertToModel(fileDTO, Cca3FacilityMigrationData.class, true, errors);

        // Get calculator files
        List<FileInfoDTO> calculatorFiles = ccaFileAttachmentService
                .getAllByFileNameLikeAndStatus(CCA3_EXISTING_FACILITIES_MIGRATION_PREFIX, FileStatus.PENDING_MIGRATION);

        // Update migration data with attachment info
        facilities.stream().filter(Cca3FacilityMigrationData::getParticipatingInCca3Scheme).forEach(facility -> {
            List<FileInfoDTO> facilityFiles = calculatorFiles.stream()
                    .filter(file -> file.getName().startsWith(facility.getFacilityBusinessId()))
                    .toList();

            if(facilityFiles.size() > 1) {
                errors.add(String.format("Facility file %s exist more than once", facility.getFacilityBusinessId()));
            }
            else if(facilityFiles.isEmpty()) {
                // Create a new UUID and set name of placeholder
                facility.setCalculatorFileUuid(UUID.randomUUID().toString());
                facility.setCalculatorFileName(CALCULATOR_PLACEHOLDER_FILE);
            }
            else {
                FileInfoDTO file = facilityFiles.getFirst();
                facility.setCalculatorFileProvided(true);
                facility.setCalculatorFileUuid(file.getUuid());
                facility.setCalculatorFileName(file.getName().replace(facility.getFacilityBusinessId() +
                        " " + CCA3_EXISTING_FACILITIES_MIGRATION_PREFIX, "").strip());
            }
        });

        return facilities;
    }
}
