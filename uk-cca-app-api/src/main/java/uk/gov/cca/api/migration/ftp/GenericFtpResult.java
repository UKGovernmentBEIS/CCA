package uk.gov.cca.api.migration.ftp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GenericFtpResult<T> {
    private String errorReport;
    private T data;

}
