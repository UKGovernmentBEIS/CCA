package uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.upload.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import uk.gov.cca.api.common.validation.BusinessValidationResult;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.common.validation.PerformanceDataUploadViolation;
import uk.gov.cca.api.workflow.request.flow.performancedata.performancedataupload.upload.domain.PerformanceDataUploadSubmitRequestTaskPayload;
import uk.gov.netz.api.files.attachments.service.FileAttachmentService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PerformanceDataUploadAttachmentsExistValidatorService {

    private final FileAttachmentService fileAttachmentService;

    public BusinessValidationResult validate(PerformanceDataUploadSubmitRequestTaskPayload taskPayload) {
        Optional<PerformanceDataUploadViolation> optViolation =
                validateFilesExist(
                        taskPayload.getPerformanceDataUpload().getReportPackages(),
                        taskPayload.getPerformanceDataUploadAttachments().keySet()
                ).map(PerformanceDataUploadViolation::new);

        return optViolation.isEmpty()
                ? BusinessValidationResult.valid()
                : BusinessValidationResult.invalid(List.of(optViolation.get()));
    }

    private Optional<PerformanceDataUploadViolation.PerformanceDataUploadViolationMessage> validateFilesExist(
            final Set<UUID> zipFiles, final Set<UUID> files) {

        Set<UUID> nonNullFiles =  zipFiles.stream().filter(Objects::nonNull).collect(Collectors.toSet());

        if (nonNullFiles.isEmpty()) {
            return Optional.empty();
        }

        if (!files.containsAll(nonNullFiles) ||
                !fileAttachmentService.fileAttachmentsExist(nonNullFiles.stream().map(UUID::toString).collect(Collectors.toSet()))) {
            return Optional.of(PerformanceDataUploadViolation.PerformanceDataUploadViolationMessage.ATTACHMENT_NOT_FOUND);
        } else {
            return Optional.empty();
        }
    }
}
