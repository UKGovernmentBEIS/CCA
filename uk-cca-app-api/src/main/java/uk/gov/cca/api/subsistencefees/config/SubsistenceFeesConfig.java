package uk.gov.cca.api.subsistencefees.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;
import java.time.LocalDate;

@ConfigurationProperties(prefix = "subsistence-fees")
@Getter
@Setter
public class SubsistenceFeesConfig {

    private LocalDate triggerDate;
    private BigDecimal facilityFee;
}
