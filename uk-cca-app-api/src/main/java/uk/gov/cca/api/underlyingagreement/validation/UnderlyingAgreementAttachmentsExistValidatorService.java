package uk.gov.cca.api.underlyingagreement.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.underlyingagreement.domain.UnderlyingAgreementContainer;
import uk.gov.netz.api.files.attachments.service.FileAttachmentService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UnderlyingAgreementAttachmentsExistValidatorService implements UnderlyingAgreementSectionContextValidator {

    private final FileAttachmentService fileAttachmentService;

    @Override
    public BusinessValidationResult validate(UnderlyingAgreementContainer container) {
        Optional<UnderlyingAgreementViolation> optViolation =
                validateFilesExist(
                        container.getUnderlyingAgreement().getUnderlyingAgreementSectionAttachmentIds(),
                        container.getUnderlyingAgreementAttachments().keySet()
                ).map(UnderlyingAgreementViolation::new);

        return optViolation.isEmpty()
                ? BusinessValidationResult.valid()
                : BusinessValidationResult.invalid(List.of(optViolation.get()));
    }

    private Optional<UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage> validateFilesExist(
            final Set<UUID> filesInSections, final Set<UUID> files) {

        Set<UUID> nonNullFiles =  filesInSections.stream().filter(Objects::nonNull).collect(Collectors.toSet());

        if (nonNullFiles.isEmpty()) {
            return Optional.empty();
        }

        if (!files.containsAll(nonNullFiles) ||
                !fileAttachmentService.fileAttachmentsExist(nonNullFiles.stream().map(UUID::toString).collect(Collectors.toSet()))) {
            return Optional.of(UnderlyingAgreementViolation.UnderlyingAgreementViolationMessage.ATTACHMENT_NOT_FOUND);
        } else {
            return Optional.empty();
        }
    }
}
