package uk.gov.cca.api.workflow.request.flow.common.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.netz.api.documenttemplate.domain.templateparams.AccountTemplateParams;
import uk.gov.netz.api.workflow.request.flow.common.service.notification.DocumentTemplateAccountData;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class TargetUnitAccountTemplateParams extends AccountTemplateParams implements DocumentTemplateAccountData {
    private String targetUnitIdentifier;
    private String targetUnitAddress;
    private String companyRegistrationNumber;
}
