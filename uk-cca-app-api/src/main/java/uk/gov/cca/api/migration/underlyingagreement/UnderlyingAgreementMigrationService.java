package uk.gov.cca.api.migration.underlyingagreement;

import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.cca.api.account.domain.TargetUnitAccount;
import uk.gov.cca.api.account.domain.TargetUnitAccountStatus;
import uk.gov.cca.api.account.domain.dto.AccountAddressDTO;
import uk.gov.cca.api.account.repository.TargetUnitAccountRepository;
import uk.gov.cca.api.common.exception.CcaErrorCode;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.facility.domain.dto.FacilityDataCreationDTO;
import uk.gov.cca.api.facility.service.FacilityDataUpdateService;
import uk.gov.cca.api.migration.MigrationEndpoint;
import uk.gov.cca.api.migration.underlyingagreement.legacyattachment.LegacyFileAttachment;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementEntity;
import uk.gov.cca.api.underlyingagreement.domain.facilities.Facility;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityItem;
import uk.gov.cca.api.underlyingagreement.domain.facilities.FacilityStatus;
import uk.gov.cca.api.underlyingagreement.repository.UnderlyingAgreementRepository;
import uk.gov.cca.api.underlyingagreement.service.UnderlyingAgreementService;
import uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementValidatorService;
import uk.gov.cca.api.underlyingagreement.validation.UnderlyingAgreementViolation;
import uk.gov.netz.api.account.service.AccountSearchAdditionalKeywordService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.files.attachments.domain.FileAttachment;
import uk.gov.netz.api.files.attachments.repository.FileAttachmentRepository;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static uk.gov.netz.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@Service
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class UnderlyingAgreementMigrationService {

    private final UnderlyingAgreementRepository underlyingAgreementRepository;
    private final TargetUnitAccountRepository targetUnitAccountRepository;
    private final FileAttachmentRepository fileAttachmentRepository;
    private final AccountSearchAdditionalKeywordService accountSearchAdditionalKeywordService;
    private final UnderlyingAgreementValidatorService underlyingAgreementValidatorService;
    private final FacilityDataUpdateService facilityDataUpdateService;
    private final UnderlyingAgreementService underlyingAgreementService;


    @Transactional(propagation = Propagation.REQUIRED)
    public void migrateUnderlyingAgreement(String targetUnitId, UnderlyingAgreementMigrationContainer migrationContainer, List<String> migrationResults) {

        UnderlyingAgreementContainer unaContainer = migrationContainer.getUnderlyingAgreementContainer();

        final Long pesistentAccountId = migrationContainer.getPersistentAccountId();
        final FileInfoDTO underlyingAgreementDocument = migrationContainer.getFileDocument();

        try {
            validateUnderlyingAgreementDocument(underlyingAgreementDocument.getName());

            validateConsolidationNumber(migrationContainer.getConsolidationNumber(), targetUnitId, underlyingAgreementDocument.getName());

            validateFacilitiesCreatedDate(migrationContainer.getFacilitiesCreatedDate(), migrationContainer.getActivationDate());

            if (migrationContainer.getFileAttachments().size() != migrationContainer.getUnderlyingAgreementContainer().getUnderlyingAgreementAttachments().size()) {
                throw new BusinessException(CcaErrorCode.INVALID_UNDERLYING_AGREEMENT, "Invalid underlying agreement attachments");
            }

            // Persist blank file attachments before validating the underlying agreement
            saveBlankFileAttachments(migrationContainer.getFileAttachments());

            // Persist live facilities before validating the underlying agreement
            Set<Facility> liveFacilities = filterByStatus(unaContainer.getUnderlyingAgreement().getFacilities(), FacilityStatus.LIVE);
            saveFacilitiesData(liveFacilities, pesistentAccountId, migrationContainer.getFacilitiesCreatedDate());

            underlyingAgreementValidatorService.validate(unaContainer);

            Set<Facility> newFacilities = filterByStatus(unaContainer.getUnderlyingAgreement().getFacilities(), FacilityStatus.NEW);
            saveFacilitiesData(newFacilities, pesistentAccountId, migrationContainer.getFacilitiesCreatedDate());

            activateFacilities(unaContainer);

            removeFileAttachmentPrefixAndSave(unaContainer, migrationContainer.getFileAttachments());

            saveUnderlyingAgreement(migrationContainer, pesistentAccountId);

            updateAccountStatus(pesistentAccountId);

            saveFacilityKeywords(pesistentAccountId, unaContainer);

        } catch (BusinessException e) {
            Arrays.asList(e.getData()).forEach(violation -> {
                if (violation instanceof UnderlyingAgreementViolation underlyingAgreementViolation) {
                    migrationResults.add(UnderlyingAgreementMigrationUtil.constructErrorMessage(targetUnitId,
                            pesistentAccountId,
                            underlyingAgreementViolation.getSectionName(),
                            underlyingAgreementViolation.getMessage(),
                            getData(underlyingAgreementViolation)));
                } else {
                    migrationResults.add(UnderlyingAgreementMigrationUtil.constructErrorMessage(targetUnitId,
                            pesistentAccountId,
                            e.getMessage(),
                            Arrays.asList(e.getData()).stream().map(Object::toString).collect(Collectors.joining(","))));
                }
            });

            throw e;
        } catch (ConstraintViolationException e) {
            //run validators anyway to collect all errors. Validators should be made null safe
            e.getConstraintViolations().forEach((error -> {
                migrationResults.add(UnderlyingAgreementMigrationUtil.constructErrorMessage(targetUnitId,
                        pesistentAccountId,
                        e.getMessage()));
            }));

            List<BusinessValidationResult> validationResults = underlyingAgreementValidatorService.getValidationResults(unaContainer);

            boolean isValid = validationResults.stream().allMatch(BusinessValidationResult::isValid);

            if (!isValid) {
                validationResults.forEach(error -> error.getViolations().forEach(violation ->
                        migrationResults.add(UnderlyingAgreementMigrationUtil.constructErrorMessage(
                                targetUnitId,
                                pesistentAccountId,
                                ((UnderlyingAgreementViolation) violation).getSectionName(),
                                ((UnderlyingAgreementViolation) violation).getMessage(),
                                getData(((UnderlyingAgreementViolation) violation))))));
            }
            throw e;
        } catch (Exception e) {
            migrationResults.add(UnderlyingAgreementMigrationUtil.constructErrorMessage(targetUnitId, pesistentAccountId, e.getMessage()));
            throw e;
        }

        migrationResults.add(UnderlyingAgreementMigrationUtil.constructSuccessMessage(targetUnitId, pesistentAccountId));

    }

    private void saveUnderlyingAgreement(UnderlyingAgreementMigrationContainer unaMigrationContainer, final Long pesistentAccountId) {
        UnderlyingAgreementEntity entity = createUnderlyingAgreement(unaMigrationContainer, pesistentAccountId);
        underlyingAgreementRepository.save(entity);
    }

    private UnderlyingAgreementEntity createUnderlyingAgreement(UnderlyingAgreementMigrationContainer unaMigrationContainer, final Long pesistentAccountId) {
        UnderlyingAgreementEntity entity = UnderlyingAgreementEntity.createUnderlyingAgreementEntity(unaMigrationContainer.getUnderlyingAgreementContainer(), pesistentAccountId);
        entity.setConsolidationNumber(unaMigrationContainer.getConsolidationNumber());
        entity.setActivationDate(unaMigrationContainer.getActivationDate());
        entity.setFileDocumentUuid(unaMigrationContainer.getFileDocument().getUuid());
        return entity;
    }

    private void validateUnderlyingAgreementDocument(String fileDocumentName) {
        if (StringUtils.isEmpty(fileDocumentName)) {
            throw new BusinessException(CcaErrorCode.INVALID_UNDERLYING_AGREEMENT, "Underlying agreement document is not found");
        }
    }

    private void validateConsolidationNumber(int consolidationNumber, String targetUnitId, String fileDocumentName) {
        String regex = "^" + targetUnitId + " Underlying Agreement v(\\d+)\\.pdf$";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(fileDocumentName);
        boolean valid = matcher.matches() && matcher.group(1).equals(String.valueOf(consolidationNumber));

        if (!valid) {
            throw new BusinessException(CcaErrorCode.INVALID_UNDERLYING_AGREEMENT, "Mismatch consolidation number");
        }
    }

    private void validateFacilitiesCreatedDate(Map<String, LocalDateTime> facilitiesCreatedDate, LocalDateTime activationDate) {
        boolean nonValid = facilitiesCreatedDate.values().stream().anyMatch(createdDate -> createdDate == null || createdDate.isAfter(activationDate));
        if (nonValid) {
            throw new BusinessException(CcaErrorCode.INVALID_UNDERLYING_AGREEMENT, "Facility entry date is empty or after the activation date");
        }
    }

    private void saveBlankFileAttachments(final List<LegacyFileAttachment> fileAttachments) {
        List<FileAttachment> blankAttachments = fileAttachments.stream()
                .filter(file -> file.getFileAttachment().getId() == null)
                .map(LegacyFileAttachment::getFileAttachment)
                .collect(Collectors.toList());
        fileAttachmentRepository.saveAll(blankAttachments);
    }

    private void saveFacilitiesData(Set<Facility> facilities, Long accountId, Map<String, LocalDateTime> facilitiesCreatedDate) {
        Set<FacilityItem> facilityItems = facilities.stream()
                .map(Facility::getFacilityItem)
                .collect(Collectors.toSet());
        facilityDataUpdateService.createFacilitiesData(buildFacilitiesData(accountId, facilityItems, facilitiesCreatedDate));
    }

    private Set<Facility> filterByStatus(Set<Facility> facilities, FacilityStatus facilityStatus) {
        return facilities.stream()
                .filter(facility -> facilityStatus.equals(facility.getStatus()))
                .collect(Collectors.toSet());
    }

    private List<FacilityDataCreationDTO> buildFacilitiesData(Long accountId, Set<FacilityItem> facilities, Map<String, LocalDateTime> facilitiesCreatedDate) {
        return facilities.stream()
                .map(facility -> {
                    String facilityId = facility.getFacilityId();
                    String siteName = facility.getFacilityDetails().getName();
                    AccountAddressDTO address = facility.getFacilityDetails().getFacilityAddress();
                    return FacilityDataCreationDTO.builder()
                            .accountId(accountId)
                            .facilityId(facilityId)
                            .siteName(siteName)
                            .address(address)
                            .createdDate(facilitiesCreatedDate.get(facilityId))
                            .build();
                })
                .toList();
    }

    private void activateFacilities(UnderlyingAgreementContainer container) {
        container.getUnderlyingAgreement().getFacilities().forEach(f -> f.setStatus(FacilityStatus.LIVE));
    }

    private void removeFileAttachmentPrefixAndSave(UnderlyingAgreementContainer unaContainer, final List<LegacyFileAttachment> legacyFileAttachments) {
        legacyFileAttachments
                .forEach(legacyAttachment -> {
                    UUID uuid = UUID.fromString(legacyAttachment.getFileAttachment().getUuid());
                    String filename = legacyAttachment.getFileAttachment().getFileName();
                    unaContainer
                            .getUnderlyingAgreementAttachments()
                            .put(uuid, filename.replaceFirst("(?i)" + legacyAttachment.getPrefix(), "").trim());
                });

        Set<String> uuids = unaContainer
                .getUnderlyingAgreementAttachments()
                .keySet()
                .stream()
                .map(Object::toString)
                .collect(Collectors.toSet());

        List<FileAttachment> attachmentsToUpdate = fileAttachmentRepository.findAllByUuidIn(uuids);

        attachmentsToUpdate.forEach(file -> {
            String filename = unaContainer
                    .getUnderlyingAgreementAttachments()
                    .get(UUID.fromString(file.getUuid()));
            file.setFileName(filename);
        });
    }

    private void updateAccountStatus(Long accountId) {
        final TargetUnitAccount targetUnitAccount = targetUnitAccountRepository.findById(accountId)
                .orElseThrow(() -> new BusinessException(RESOURCE_NOT_FOUND));
        targetUnitAccount.setStatus(TargetUnitAccountStatus.LIVE);
    }

    private void saveFacilityKeywords(Long accountId, UnderlyingAgreementContainer unaContainer) {
        // Create search keywords
        final Map<String, String> searchKeywordsForAccount = underlyingAgreementService.createSearchKeywordsForAccount(unaContainer);

        // Store facility IDs and postcodes as search keywords
        accountSearchAdditionalKeywordService.storeKeywordsForAccount(accountId, searchKeywordsForAccount);
    }

    private String getData(UnderlyingAgreementViolation violation) {
        StringBuilder builder = new StringBuilder();
        List<Object> collect = Arrays.stream(violation.getData())
                .map(data -> data instanceof Map.Entry ? ((Map.Entry<?, ?>) data).getValue() : data)
                .toList();
        collect.forEach(element -> builder.append("[").append(element).append("]"));
        return builder.toString();
    }

}
