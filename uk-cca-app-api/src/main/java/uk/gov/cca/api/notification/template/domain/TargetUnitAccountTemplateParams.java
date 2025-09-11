package uk.gov.cca.api.notification.template.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.netz.api.documenttemplate.domain.templateparams.AccountTemplateParams;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class TargetUnitAccountTemplateParams extends AccountTemplateParams {
    private String targetUnitIdentifier;
    private String targetUnitAddress;
    private String companyRegistrationNumber;
}
