package uk.gov.cca.api.migration.ftp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FtpFileDTOResult {

    private FileDTO fileDTO;
    private String errorReport;
    
}
