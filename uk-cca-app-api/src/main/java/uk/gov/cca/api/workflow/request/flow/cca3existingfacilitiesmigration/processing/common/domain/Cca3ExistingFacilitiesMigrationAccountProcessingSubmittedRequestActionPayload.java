package uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.processing.common.domain;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.ObjectUtils;

import uk.gov.cca.api.workflow.request.core.domain.CcaRequestActionPayload;
import uk.gov.cca.api.workflow.request.flow.cca3existingfacilitiesmigration.common.domain.Cca3FacilityMigrationData;
import uk.gov.cca.api.workflow.request.flow.common.domain.DefaultNoticeRecipient;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Cca3ExistingFacilitiesMigrationAccountProcessingSubmittedRequestActionPayload extends CcaRequestActionPayload {

    @Builder.Default
    private List<Cca3FacilityMigrationData> facilityMigrationDataList = new ArrayList<>();

    @Valid
    private List<DefaultNoticeRecipient> defaultContacts;

    @Valid
    private FileInfoDTO officialNotice;

    @Valid
    private FileInfoDTO underlyingAgreementDocument;

    @Override
    public Map<UUID, String> getAttachments() {
        return this.getFacilityMigrationDataList().stream()
                .filter(data -> ObjectUtils.isNotEmpty(data.getCalculatorFileUuid()))
                .collect(Collectors
                        .toMap(data -> UUID.fromString(data.getCalculatorFileUuid()),
                                Cca3FacilityMigrationData::getCalculatorFileName));
    }

    @Override
    public Map<UUID, String> getFileDocuments() {
        Map<UUID, String> fileDocuments = new HashMap<>();

        Optional.ofNullable(officialNotice).ifPresent(file ->
                fileDocuments.put(UUID.fromString(file.getUuid()), file.getName()));

        Optional.ofNullable(underlyingAgreementDocument).ifPresent(file ->
                fileDocuments.put(UUID.fromString(file.getUuid()), file.getName()));

        return fileDocuments;
    }
}
