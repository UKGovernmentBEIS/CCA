package uk.gov.cca.api.migration.files;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileMigrationError {
    private String uuid;
    private String fileName;
    private String errorReport;
}
