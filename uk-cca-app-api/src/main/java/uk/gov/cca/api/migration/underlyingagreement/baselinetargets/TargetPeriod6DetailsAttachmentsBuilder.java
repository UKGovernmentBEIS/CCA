package uk.gov.cca.api.migration.underlyingagreement.baselinetargets;

import static java.util.stream.Collectors.toSet;
import static uk.gov.cca.api.migration.underlyingagreement.legacyattachment.LegacyFileAttachmentType.TARGET_PERIOD_DETAILS_CONVERSION_EVIDENCE;
import static uk.gov.cca.api.migration.underlyingagreement.legacyattachment.LegacyFileAttachmentType.TARGET_PERIOD_DETAILS_GREENFIELD_EVIDENCE;
import static uk.gov.cca.api.migration.underlyingagreement.legacyattachment.LegacyFileAttachmentType.TARGET_PERIOD_DETAILS_TARGET_CALCULATOR_FILE;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.collections4.CollectionUtils;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.cca.api.migration.MigrationEndpoint;
import uk.gov.cca.api.migration.underlyingagreement.attachments.FileAttachmentUtil;
import uk.gov.cca.api.migration.underlyingagreement.legacyattachment.LegacyFileAttachment;
import uk.gov.cca.api.migration.underlyingagreement.legacyattachment.LegacyFileAttachmentMapper;
import uk.gov.cca.api.migration.underlyingagreement.attachments.FileAttachmentMigrationRepository;
import uk.gov.cca.api.migration.underlyingagreement.attachments.FileAttachmentMigrationMapper;
import uk.gov.cca.api.migration.underlyingagreement.placeholderattachments.GreenfieldSiteEvidencePlaceholderAttachment;
import uk.gov.cca.api.migration.underlyingagreement.placeholderattachments.PlaceholderAttachment;
import uk.gov.cca.api.migration.underlyingagreement.placeholderattachments.TargetCalculatorPlaceholderAttachment;
import uk.gov.cca.api.migration.underlyingagreement.placeholderattachments.ThroughputConversionEvidencePlaceholderAttachment;
import uk.gov.cca.api.underlyingagreement.domain.baselinetargets.TargetPeriod6Details;
import uk.gov.netz.api.files.attachments.domain.FileAttachment;

@Service
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class TargetPeriod6DetailsAttachmentsBuilder {
    
    private static final FileAttachmentMigrationMapper fileAttachmentMapper = Mappers.getMapper(FileAttachmentMigrationMapper.class);
    private static final LegacyFileAttachmentMapper legacyFileAttachmentMapper = Mappers.getMapper(LegacyFileAttachmentMapper.class);

    private final FileAttachmentMigrationRepository fileAttachmentRepository;

    
    public void populateAttachments(String targetUnitId, TargetPeriod6Details section, boolean createCopy, List<LegacyFileAttachment> sectionAttachments) {
        
        final List<FileAttachment> attachments = fileAttachmentRepository.searchByNameLike(targetUnitId);
        
        //Target calculator file - mandatory
        final String calculatorFileIndex = String.join(" ", targetUnitId, TARGET_PERIOD_DETAILS_TARGET_CALCULATOR_FILE.getIndex());
        List<FileAttachment> calculatorFile = FileAttachmentUtil.startsWith(attachments, calculatorFileIndex);
        if (CollectionUtils.isEmpty(calculatorFile)) {
            PlaceholderAttachment placeholderAttachment = new TargetCalculatorPlaceholderAttachment(targetUnitId);
            calculatorFile.add(fileAttachmentMapper.toFileAttachment(placeholderAttachment));
        } else if (calculatorFile.size() != 1) {
            calculatorFile.clear();
        } else if(createCopy) {
            FileAttachment calculatorFileCopy = fileAttachmentMapper.toFileAttachmentCopy(calculatorFile.get(0));
            calculatorFile = new ArrayList<>(List.of(calculatorFileCopy));
        }
        if(CollectionUtils.isNotEmpty(calculatorFile)) {
            section.getTargetComposition().setCalculatorFile(UUID.fromString(calculatorFile.get(0).getUuid()));
            sectionAttachments.add(legacyFileAttachmentMapper.toLegacyFileAttachment(calculatorFile.get(0), calculatorFileIndex));
        }
        
        //Conversion Evidences - conditionally mandatory based on isTargetUnitThroughputMeasured
        final String conversionEvidenceIndex = String.join(" ", targetUnitId, TARGET_PERIOD_DETAILS_CONVERSION_EVIDENCE.getIndex()); 
        List<FileAttachment> conversionEvidences = FileAttachmentUtil.startsWith(attachments, conversionEvidenceIndex);
        if (Boolean.TRUE.equals(section.getTargetComposition().getIsTargetUnitThroughputMeasured())) {
            if (CollectionUtils.isEmpty(conversionEvidences)) {
                PlaceholderAttachment placeholderAttachment = new ThroughputConversionEvidencePlaceholderAttachment(targetUnitId);
                conversionEvidences.add(fileAttachmentMapper.toFileAttachment(placeholderAttachment));
            } else if(createCopy) {
                List<FileAttachment> conversionEvidencesCopy = fileAttachmentMapper.toFileAttachmentCopy(conversionEvidences);
                conversionEvidences = new ArrayList<>(conversionEvidencesCopy);
            }
            section.getTargetComposition().setConversionEvidences(getUuids(conversionEvidences));
            sectionAttachments.addAll(legacyFileAttachmentMapper.toLegacyFileAttachments(conversionEvidences, conversionEvidenceIndex));
        }      
               
        //Greenfield Evidences  - conditionally mandatory based on isTwelveMonths
        final String greenfieldEvidenceIndex = String.join(" ", targetUnitId, TARGET_PERIOD_DETAILS_GREENFIELD_EVIDENCE.getIndex());                
        List<FileAttachment> greenfieldEvidences = FileAttachmentUtil.startsWith(attachments, greenfieldEvidenceIndex);
        if (Boolean.FALSE.equals(section.getBaselineData().getIsTwelveMonths())) {
            if (CollectionUtils.isEmpty(greenfieldEvidences)) {
                PlaceholderAttachment placeholderAttachment = new GreenfieldSiteEvidencePlaceholderAttachment(targetUnitId);
                greenfieldEvidences.add(fileAttachmentMapper.toFileAttachment(placeholderAttachment));
            } else if(createCopy) {
                List<FileAttachment> greenfieldEvidencesCopy = fileAttachmentMapper.toFileAttachmentCopy(greenfieldEvidences);
                greenfieldEvidences = new ArrayList<>(greenfieldEvidencesCopy);
            }
            section.getBaselineData().setGreenfieldEvidences(getUuids(greenfieldEvidences));
            sectionAttachments.addAll(legacyFileAttachmentMapper.toLegacyFileAttachments(greenfieldEvidences, greenfieldEvidenceIndex));
        }
    }
    
    private Set<UUID> getUuids(List<FileAttachment> fileAttachments) {
        return fileAttachments.stream().map(fl -> UUID.fromString(fl.getUuid())).collect(toSet());
    }
}
