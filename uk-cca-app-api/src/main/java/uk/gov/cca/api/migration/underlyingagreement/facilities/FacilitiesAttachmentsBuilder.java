package uk.gov.cca.api.migration.underlyingagreement.facilities;

import static uk.gov.cca.api.migration.underlyingagreement.legacyattachment.LegacyFileAttachmentType.MANAGE_FACILITIES_ANNOTATED_SITE_PLANS;
import static uk.gov.cca.api.migration.underlyingagreement.legacyattachment.LegacyFileAttachmentType.MANAGE_FACILITIES_DIRECTLY_ASSOCIATED_ACTIVITIES_DESCRIPTION;
import static uk.gov.cca.api.migration.underlyingagreement.legacyattachment.LegacyFileAttachmentType.MANAGE_FACILITIES_ELIGIBLE_PROCESS_DESCRIPTION;
import static uk.gov.cca.api.migration.underlyingagreement.legacyattachment.LegacyFileAttachmentType.MANAGE_FACILITIES_EVIDENCE;
import static uk.gov.cca.api.migration.underlyingagreement.legacyattachment.LegacyFileAttachmentType.MANAGE_FACILITIES_MANUFACTURING_PROCESS_DESCRIPTION;
import static uk.gov.cca.api.migration.underlyingagreement.legacyattachment.LegacyFileAttachmentType.MANAGE_FACILITIES_PERMIT_FILE;
import static uk.gov.cca.api.migration.underlyingagreement.legacyattachment.LegacyFileAttachmentType.MANAGE_FACILITIES_PROCESS_FLOW_MAPS;

import java.util.List;
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
import uk.gov.cca.api.migration.underlyingagreement.placeholderattachments.AnnotatedSitePlanPlaceholderAttachment;
import uk.gov.cca.api.migration.underlyingagreement.placeholderattachments.EligibleProcessDescriptionPlaceholderAttachment;
import uk.gov.cca.api.migration.underlyingagreement.placeholderattachments.EprLaapcPermitPlaceholderAttachment;
import uk.gov.cca.api.migration.underlyingagreement.placeholderattachments.ManufacturingProcessDescriptionPlaceholderAttachment;
import uk.gov.cca.api.migration.underlyingagreement.placeholderattachments.PlaceholderAttachment;
import uk.gov.cca.api.migration.underlyingagreement.placeholderattachments.ProcessFlowMapPlaceholderAttachment;
import uk.gov.cca.api.migration.underlyingagreement.placeholderattachments.Rule70EvidencePlaceholderAttachment;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.netz.api.files.attachments.domain.FileAttachment;
import uk.gov.netz.api.files.common.FileType;

@Service
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class FacilitiesAttachmentsBuilder {
    
    private static final FileAttachmentMigrationMapper fileAttachmentMapper = Mappers.getMapper(FileAttachmentMigrationMapper.class);
    private static final LegacyFileAttachmentMapper legacyFileAttachmentMapper = Mappers.getMapper(LegacyFileAttachmentMapper.class);
    
    private final FileAttachmentMigrationRepository fileAttachmentRepository;

    public void populateAttachments(List<LegacyFileAttachment> sectionAttachments, Facility facility) {
        final String facilityBusinessId = facility.getFacilityItem().getFacilityId();
        
        final List<FileAttachment> attachments = fileAttachmentRepository.searchByNameLike(facilityBusinessId);
        
        //Permit File - conditionally mandatory based on erpAuthorisationExists
        final String permitFileIndex = String.join(" ", facilityBusinessId, MANAGE_FACILITIES_PERMIT_FILE.getIndex());
        List<FileAttachment> permitFile = FileAttachmentUtil.startsWith(attachments, permitFileIndex);
        if (Boolean.TRUE.equals(facility.getFacilityItem().getEligibilityDetailsAndAuthorisation().getErpAuthorisationExists())) {
            if (CollectionUtils.isEmpty(permitFile)) {
                PlaceholderAttachment placeholderAttachment = new EprLaapcPermitPlaceholderAttachment(facilityBusinessId);
                permitFile.add(fileAttachmentMapper.toFileAttachment(placeholderAttachment));
            } else if (permitFile.size() != 1) {
                permitFile.clear();
            }
            if(CollectionUtils.isNotEmpty(permitFile)) {
                facility.getFacilityItem().getEligibilityDetailsAndAuthorisation().setPermitFile(getUuid(permitFile.get(0)));
                sectionAttachments.add(legacyFileAttachmentMapper.toLegacyFileAttachment(permitFile.get(0), permitFileIndex));
            }
        }
        
        //Manufacturing process description - mandatory
        final String manufacturingProcessFileIndex = String.join(" ", facilityBusinessId, MANAGE_FACILITIES_MANUFACTURING_PROCESS_DESCRIPTION.getIndex());                
        List<FileAttachment> manufacturingProcessFile = FileAttachmentUtil.startsWith(attachments, manufacturingProcessFileIndex);
        if (CollectionUtils.isEmpty(manufacturingProcessFile)) {
            PlaceholderAttachment placeholderAttachment = new ManufacturingProcessDescriptionPlaceholderAttachment(facilityBusinessId);
            manufacturingProcessFile.add(fileAttachmentMapper.toFileAttachment(placeholderAttachment));
        } else if (manufacturingProcessFile.size() != 1) {
            manufacturingProcessFile.clear();
        }
        if(CollectionUtils.isNotEmpty(manufacturingProcessFile)) {
            facility.getFacilityItem().getFacilityExtent().setManufacturingProcessFile(getUuid(manufacturingProcessFile.get(0)));
            sectionAttachments.add(legacyFileAttachmentMapper.toLegacyFileAttachment(manufacturingProcessFile.get(0), manufacturingProcessFileIndex));
        }
        
        //Process flow maps - mandatory
        final String processFlowFileIndex = String.join(" ", facilityBusinessId, MANAGE_FACILITIES_PROCESS_FLOW_MAPS.getIndex());
        List<FileAttachment> processFlowFile = FileAttachmentUtil.startsWith(attachments, processFlowFileIndex);
        if (CollectionUtils.isEmpty(processFlowFile)) {
            PlaceholderAttachment placeholderAttachment = new ProcessFlowMapPlaceholderAttachment(facilityBusinessId);
            processFlowFile.add(fileAttachmentMapper.toFileAttachment(placeholderAttachment));
        } else if (processFlowFile.size() != 1) {
            processFlowFile.clear();
        }
        if(CollectionUtils.isNotEmpty(processFlowFile)) {
            facility.getFacilityItem().getFacilityExtent().setProcessFlowFile(getUuid(processFlowFile.get(0)));
            sectionAttachments.add(legacyFileAttachmentMapper.toLegacyFileAttachment(processFlowFile.get(0), processFlowFileIndex));
        }
        
        //Annotated site plans - mandatory
        final String annotatedSitePlansFileIndex = String.join(" ", facilityBusinessId, MANAGE_FACILITIES_ANNOTATED_SITE_PLANS.getIndex());
        List<FileAttachment> annotatedSitePlansFile = FileAttachmentUtil.startsWith(attachments, annotatedSitePlansFileIndex);
        if (CollectionUtils.isEmpty(annotatedSitePlansFile)) {
            PlaceholderAttachment placeholderAttachment = new AnnotatedSitePlanPlaceholderAttachment(facilityBusinessId);
            annotatedSitePlansFile.add(fileAttachmentMapper.toFileAttachment(placeholderAttachment));
        } else if (annotatedSitePlansFile.size() != 1) {
            annotatedSitePlansFile.clear();
        }
        if(CollectionUtils.isNotEmpty(annotatedSitePlansFile)) {
            facility.getFacilityItem().getFacilityExtent().setAnnotatedSitePlansFile(getUuid(annotatedSitePlansFile.get(0)));
            sectionAttachments.add(legacyFileAttachmentMapper.toLegacyFileAttachment(annotatedSitePlansFile.get(0), annotatedSitePlansFileIndex));
        }
        
        //Eligible process description - mandatory
        final String eligibleProcessFileIndex = String.join(" ", facilityBusinessId, MANAGE_FACILITIES_ELIGIBLE_PROCESS_DESCRIPTION.getIndex());
        List<FileAttachment> eligibleProcessFile = FileAttachmentUtil.startsWith(attachments, eligibleProcessFileIndex);
        if (CollectionUtils.isEmpty(eligibleProcessFile)) {
            PlaceholderAttachment placeholderAttachment = new EligibleProcessDescriptionPlaceholderAttachment(facilityBusinessId);
            eligibleProcessFile.add(fileAttachmentMapper.toFileAttachment(placeholderAttachment));
        } else if (eligibleProcessFile.size() != 1) {
            eligibleProcessFile.clear();
        }
        if(CollectionUtils.isNotEmpty(eligibleProcessFile)) {
            facility.getFacilityItem().getFacilityExtent().setEligibleProcessFile(getUuid(eligibleProcessFile.get(0)));
            sectionAttachments.add(legacyFileAttachmentMapper.toLegacyFileAttachment(eligibleProcessFile.get(0), eligibleProcessFileIndex));
        }

        //Directly associated activities - conditionally mandatory based on areActivitiesClaimed
        final String activitiesDescriptionFileIndex = String.join(" ", facilityBusinessId, MANAGE_FACILITIES_DIRECTLY_ASSOCIATED_ACTIVITIES_DESCRIPTION.getIndex());                
        List<FileAttachment> activitiesDescriptionFile = FileAttachmentUtil.startsWith(attachments, activitiesDescriptionFileIndex);
        if (CollectionUtils.isNotEmpty(activitiesDescriptionFile) && activitiesDescriptionFile.size() == 1) {
            facility.getFacilityItem().getFacilityExtent().setAreActivitiesClaimed(Boolean.TRUE);
            facility.getFacilityItem().getFacilityExtent().setActivitiesDescriptionFile(getUuid(activitiesDescriptionFile.get(0)));
            sectionAttachments.add(legacyFileAttachmentMapper.toLegacyFileAttachment(activitiesDescriptionFile.get(0), activitiesDescriptionFileIndex));
        }
        
        //Evidence File for 70% rule - mandatory
        final String evidenceFileIndex = String.join(" ", facilityBusinessId, MANAGE_FACILITIES_EVIDENCE.getIndex());
        List<FileAttachment> evidenceFile = FileAttachmentUtil.startsWith(attachments, evidenceFileIndex);
        if (CollectionUtils.isEmpty(evidenceFile)) {
            PlaceholderAttachment placeholderAttachment = new Rule70EvidencePlaceholderAttachment(facilityBusinessId);
            evidenceFile.add(fileAttachmentMapper.toFileAttachment(placeholderAttachment));
        } else if (evidenceFile.size() != 1) {
            evidenceFile.clear();
        } else if (!FileType.XLSX.getMimeTypes().contains(evidenceFile.get(0).getFileType())
                && !FileType.XLS.getMimeTypes().contains(evidenceFile.get(0).getFileType())) {
            evidenceFile.clear();
            PlaceholderAttachment placeholderAttachment = new Rule70EvidencePlaceholderAttachment(facilityBusinessId);
            evidenceFile.add(fileAttachmentMapper.toFileAttachment(placeholderAttachment));
        }
        if(CollectionUtils.isNotEmpty(evidenceFile)) {
            facility.getFacilityItem().getApply70Rule().setEvidenceFile(getUuid(evidenceFile.get(0)));
            sectionAttachments.add(legacyFileAttachmentMapper.toLegacyFileAttachment(evidenceFile.get(0), evidenceFileIndex));
        }
    }

    private UUID getUuid(FileAttachment fileAttachment) {
        return UUID.fromString(fileAttachment.getUuid());
    }
}
