package uk.gov.cca.api.migration.underlyingagreement.documents;

import java.util.ArrayList;
import java.util.List;

import lombok.experimental.UtilityClass;
import uk.gov.netz.api.files.documents.domain.FileDocument;

@UtilityClass
public class FileDocumentUtil {
    
    public List<FileDocument> startsWith(List<FileDocument> files, String prefix) {
        List<FileDocument> results = new ArrayList<>();
        files.stream().forEach(file -> {
            if (file.getFileName().toLowerCase().startsWith(prefix.toLowerCase())) {
                results.add(file);
            }
        });
        return results;
    }

}
