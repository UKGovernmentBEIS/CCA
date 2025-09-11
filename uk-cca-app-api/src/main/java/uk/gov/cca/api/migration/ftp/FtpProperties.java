package uk.gov.cca.api.migration.ftp;

import jakarta.validation.Valid;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import uk.gov.cca.api.migration.MigrationEndpoint;

@Validated
@Component
@ConfigurationProperties(prefix = "migration-ftp")
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@Getter
@Setter
public class FtpProperties {

    @Valid
    @NotBlank
    private String url;

    @Valid
    @NotBlank
    private String username;

    @Valid
    @Positive
    private int port;

    @Valid
    private Resource keyPath;

    @Valid
    @NotBlank
    private String serverSectorUmbrellaAgreementsDirectory;

    @Valid
    @NotBlank
    private String serverUnderlyingAgreementsDirectory;

    @Valid
    @NotBlank
    private String serverUnaAttachmentsDirectory;

    @Valid
    @NotBlank
    private String serverFacilityCertificationDirectory;
    
    @Valid
    @NotBlank
    private String serverFacilityCertificationSourceFile;
}
