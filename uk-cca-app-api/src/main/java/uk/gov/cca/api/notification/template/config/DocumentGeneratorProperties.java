package uk.gov.cca.api.notification.template.config;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "document-generator")
@Data
public class DocumentGeneratorProperties {

    @NotEmpty @URL
    private String url;
}
