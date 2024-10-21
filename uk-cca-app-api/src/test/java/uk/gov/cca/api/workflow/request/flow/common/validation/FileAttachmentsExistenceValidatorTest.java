package uk.gov.cca.api.workflow.request.flow.common.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.netz.api.files.attachments.service.FileAttachmentService;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileAttachmentsExistenceValidatorTest {

    @InjectMocks
    private FileAttachmentsExistenceValidator validator;

    @Mock
    private FileAttachmentService fileAttachmentService;

    @Test
    void valid() {
        final UUID file1 = UUID.randomUUID();
        final UUID file2 = UUID.randomUUID();
        final Set<UUID> filesInSections = Set.of(file1, file2);
        final Set<UUID> files  = Set.of(file1, file2);
        final Set<String> filesInSectionsStrings = Set.of(file1.toString(), file2.toString());

        when(fileAttachmentService.fileAttachmentsExist(filesInSectionsStrings))
                .thenReturn(true);

        // Invoke
        boolean result = validator.valid(filesInSections, files);

        // Verify
        assertThat(result).isTrue();
        verify(fileAttachmentService, times(1))
                .fileAttachmentsExist(filesInSectionsStrings);
    }

    @Test
    void valid_not_in_file_sections_valid() {
        final UUID file1 = UUID.randomUUID();
        final UUID file2 = UUID.randomUUID();
        final Set<UUID> filesInSections = Set.of(file1);
        final Set<UUID> files  = Set.of(file1, file2);
        final Set<String> filesInSectionsStrings = Set.of(file1.toString());

        when(fileAttachmentService.fileAttachmentsExist(filesInSectionsStrings))
                .thenReturn(true);

        // Invoke
        boolean result = validator.valid(filesInSections, files);

        // Verify
        assertThat(result).isTrue();
        verify(fileAttachmentService, times(1))
                .fileAttachmentsExist(filesInSectionsStrings);
    }

    @Test
    void valid_not_in_attachments_not_valid() {
        final UUID file1 = UUID.randomUUID();
        final UUID file2 = UUID.randomUUID();
        final Set<UUID> filesInSections = Set.of(file1, file2);
        final Set<UUID> files  = Set.of(file1);

        // Invoke
        boolean result = validator.valid(filesInSections, files);

        // Verify
        assertThat(result).isFalse();
        verifyNoInteractions(fileAttachmentService);
    }

    @Test
    void valid_files_not_exist() {
        final UUID file1 = UUID.randomUUID();
        final UUID file2 = UUID.randomUUID();
        final Set<UUID> filesInSections = Set.of(file1, file2);
        final Set<UUID> files  = Set.of(file1, file2);
        final Set<String> filesInSectionsStrings = Set.of(file1.toString(), file2.toString());

        when(fileAttachmentService.fileAttachmentsExist(filesInSectionsStrings))
                .thenReturn(false);

        // Invoke
        boolean result = validator.valid(filesInSections, files);

        // Verify
        assertThat(result).isFalse();
        verify(fileAttachmentService, times(1))
                .fileAttachmentsExist(filesInSectionsStrings);
    }
}
