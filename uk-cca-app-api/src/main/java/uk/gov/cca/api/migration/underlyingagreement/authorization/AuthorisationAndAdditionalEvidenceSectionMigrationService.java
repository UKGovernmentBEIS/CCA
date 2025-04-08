package uk.gov.cca.api.migration.underlyingagreement.authorization;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static uk.gov.cca.api.migration.underlyingagreement.legacyattachment.LegacyFileAttachmentType.AUTHORISATION_AND_ADDITIONAL_EVIDENCE_ADDITIONAL_EVIDENCE;
import static uk.gov.cca.api.migration.underlyingagreement.legacyattachment.LegacyFileAttachmentType.AUTHORISATION_AND_ADDITIONAL_EVIDENCE_AUTHORISATION;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.migration.MigrationEndpoint;
import uk.gov.cca.api.migration.underlyingagreement.UnderlyingAgreementMigrationContainer;
import uk.gov.cca.api.migration.underlyingagreement.UnderlyingAgreementSectionMigrationService;
import uk.gov.cca.api.migration.underlyingagreement.attachments.FileAttachmentUtil;
import uk.gov.cca.api.migration.underlyingagreement.legacyattachment.LegacyFileAttachment;
import uk.gov.cca.api.migration.underlyingagreement.legacyattachment.LegacyFileAttachmentMapper;
import uk.gov.cca.api.migration.underlyingagreement.attachments.FileAttachmentMigrationRepository;
import uk.gov.cca.api.migration.underlyingagreement.attachments.FileAttachmentMigrationMapper;
import uk.gov.cca.api.migration.underlyingagreement.placeholderattachments.AuthorisationPlaceholderAttachment;
import uk.gov.cca.api.migration.underlyingagreement.placeholderattachments.PlaceholderAttachment;
import uk.gov.cca.api.underlyingagreement.domain.authorisation.AuthorisationAndAdditionalEvidence;
import uk.gov.netz.api.files.attachments.domain.FileAttachment;

@Service
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class AuthorisationAndAdditionalEvidenceSectionMigrationService implements UnderlyingAgreementSectionMigrationService<AuthorisationAndAdditionalEvidence>{
        
    private static final FileAttachmentMigrationMapper fileAttachmentMapper = Mappers.getMapper(FileAttachmentMigrationMapper.class);
    private static final LegacyFileAttachmentMapper legacyFileAttachmentMapper = Mappers.getMapper(LegacyFileAttachmentMapper.class);
    
    private final FileAttachmentMigrationRepository fileAttachmentRepository;
    
    public void populateSection(List<String> eligibleTargetUnitIds, Map<String, UnderlyingAgreementMigrationContainer> migrationContainerMap) {

        for (Entry<String, UnderlyingAgreementMigrationContainer> migrationContainerPerTargetUnit : migrationContainerMap.entrySet()) {

            String targetUnitId = migrationContainerPerTargetUnit.getKey();
            UnderlyingAgreementMigrationContainer migrationContainer = migrationContainerPerTargetUnit.getValue();
          
            AuthorisationAndAdditionalEvidence section = new AuthorisationAndAdditionalEvidence();
            List<LegacyFileAttachment> sectionAttachments = new ArrayList<>();

            final List<FileAttachment> attachments = fileAttachmentRepository.searchByNameLike(targetUnitId);

            // Authorization - mandatory
            final String authorisationFileIndex = String.join(" ", targetUnitId, AUTHORISATION_AND_ADDITIONAL_EVIDENCE_AUTHORISATION.getIndex());
            List<FileAttachment> authorisationAttachments = FileAttachmentUtil.startsWith(attachments, authorisationFileIndex);
            if (CollectionUtils.isEmpty(authorisationAttachments)) {
                PlaceholderAttachment placeholderAttachment = new AuthorisationPlaceholderAttachment(targetUnitId);
                authorisationAttachments.add(fileAttachmentMapper.toFileAttachment(placeholderAttachment));
            }
            section.setAuthorisationAttachmentIds(getUuids(authorisationAttachments));
            sectionAttachments.addAll(legacyFileAttachmentMapper.toLegacyFileAttachments(authorisationAttachments, authorisationFileIndex));

            // Additional Evidence - optional
            final String additionalEvidenceFileIndex = String.join(" ", targetUnitId, AUTHORISATION_AND_ADDITIONAL_EVIDENCE_ADDITIONAL_EVIDENCE.getIndex());      
            List<FileAttachment> additionalEvidences = FileAttachmentUtil.startsWith(attachments, additionalEvidenceFileIndex);
            if (CollectionUtils.isNotEmpty(additionalEvidences)) {
                section.setAdditionalEvidenceAttachmentIds(getUuids(additionalEvidences));
                sectionAttachments.addAll(legacyFileAttachmentMapper.toLegacyFileAttachments(additionalEvidences, additionalEvidenceFileIndex));
            }

            // Section Attachments
            migrationContainer.getUnderlyingAgreementContainer().getUnderlyingAgreement().setAuthorisationAndAdditionalEvidence(section);
            migrationContainer.getUnderlyingAgreementContainer().getUnderlyingAgreementAttachments()
                    .putAll(sectionAttachments.stream().collect(toMap(file -> UUID.fromString(file.getFileAttachment().getUuid()), file -> file.getFileAttachment().getFileName())));
            migrationContainer.getFileAttachments().addAll(sectionAttachments);
        }
    }

    @Override
    public Map<String, AuthorisationAndAdditionalEvidence> querySection(List<String> eligibleTargetUnitIds) {
        return null;
    }

    private Set<UUID> getUuids(List<FileAttachment> fileAttachments) {
        return fileAttachments.stream().map(fl -> UUID.fromString(fl.getUuid())).collect(toSet());
    }
}
