package uk.gov.cca.api.workflow.request.flow.common.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import uk.gov.netz.api.files.attachments.service.FileAttachmentService;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileAttachmentsExistenceValidator {

    private final FileAttachmentService fileAttachmentService;

    public boolean valid(final Set<UUID> filesInSections, final Set<UUID> files) {
        Set<UUID> nonNullFiles =  filesInSections.stream().filter(Objects::nonNull).collect(Collectors.toSet());

        if (nonNullFiles.isEmpty()) {
            return true;
        }

        return files.containsAll(nonNullFiles) &&
                fileAttachmentService.fileAttachmentsExist(nonNullFiles.stream().map(UUID::toString).collect(Collectors.toSet()));
    }
}
