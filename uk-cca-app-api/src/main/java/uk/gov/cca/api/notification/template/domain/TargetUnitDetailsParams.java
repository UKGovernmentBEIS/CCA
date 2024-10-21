package uk.gov.cca.api.notification.template.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TargetUnitDetailsParams {

    private String name;
    private String companyRegistrationNumber;
    private String targetUnitAddress;
    private String primaryContact;
    private String primaryContactEmail;
    private String location;
}
